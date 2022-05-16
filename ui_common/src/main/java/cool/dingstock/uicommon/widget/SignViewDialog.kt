package cool.dingstock.uicommon.widget

import android.Manifest
import android.animation.Animator
import android.graphics.Bitmap
import android.view.*
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.mrrun.screenshotsshare.ScreenShotFileObserver
import com.mrrun.screenshotsshare.ScreenShotFileObserverManager
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.dialog.BaseCenterBindingDialog
import cool.dingstock.appbase.entity.bean.score.Fortune
import cool.dingstock.appbase.entity.bean.score.ScoreIndexUserInfoEntity
import cool.dingstock.appbase.ext.*
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.SharePlatform
import cool.dingstock.appbase.share.ShareServiceHelper
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import cool.dingstock.lib_base.util.*
import cool.dingstock.uicommon.R
import cool.dingstock.uicommon.databinding.SignViewLayoutBinding
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableOnSubscribe
import java.util.concurrent.atomic.AtomicBoolean


class SignViewDialog(val activity: FragmentActivity, val isHome: Boolean) :
    BaseCenterBindingDialog<SignViewLayoutBinding>(activity, R.style.CommonDialog) {

    private val screenShotListenManager by lazy {
        ScreenShotListenManager.newInstance(activity)
    }
    var entity: ScoreIndexUserInfoEntity? = null
    var enableClose = true
    var lottieAnimationView = LottieAnimationView(context)
    var signListener: SignListener? = null
    var isShowShare = AtomicBoolean(false)

    init {
        window?.let {
            StatusBarUtil.transparentStatus(it)
        }
        viewBinding.rootView.setOnClickListener {

        }
        viewBinding.closeIv.setOnClickListener {
            if (!enableClose) {
                return@setOnClickListener
            }
            dismiss()
        }

        viewBinding.shareDynamicLayer.setOnShakeClickListener {
            entity?.fortuneShareImageUrl?.let {
                val fileName = it.substring(it.lastIndexOf("/") + 1)
                UTHelper.commonEvent(
                    UTConstant.Score.IntegralP_click_ShareFortune,
                    "channel",
                    "盯链动态",
                    "content",
                    fileName,
                    "source",
                    if (!isShowShare.get()) "正常分享" else "截屏后分享"
                )



                DcUriRequest(context, CircleConstant.Uri.CIRCLE_DYNAMIC_EDIT)
                    .putUriParameter(CircleConstant.UriParams.IMAGE_URL, it)
                    .start()
            }
        }
        viewBinding.shareWechatLayer.setOnShakeClickListener {
            val type = ShareType.Image
            val params = ShareParams()
            params.imageUrl = entity?.fortuneShareImageUrl
            type.params = params
            params.title = ""
            params.content = ""
            ShareServiceHelper.share(context, type, SharePlatform.WeChat)
            entity?.fortuneShareImageUrl?.let {
                val fileName = it.substring(it.lastIndexOf("/") + 1)
                UTHelper.commonEvent(
                    UTConstant.Score.IntegralP_click_ShareFortune,
                    "channel",
                    "微信",
                    "content",
                    fileName,
                    "source",
                    if (!isShowShare.get()) "正常分享" else "截屏后分享"
                )
            }

        }
        viewBinding.shareMomentsLayer.setOnShakeClickListener {
            val type = ShareType.Image
            val params = ShareParams()
            params.imageUrl = entity?.fortuneShareImageUrl
            params.title = ""
            params.content = ""
            type.params = params
            ShareServiceHelper.share(context, type, SharePlatform.WeChatMoments)
            entity?.fortuneShareImageUrl?.let {
                val fileName = it.substring(it.lastIndexOf("/") + 1)
                UTHelper.commonEvent(
                    UTConstant.Score.IntegralP_click_ShareFortune,
                    "channel",
                    "朋友圈",
                    "content",
                    fileName,
                    "source",
                    if (!isShowShare.get()) "正常分享" else "截屏后分享"
                )
            }
        }
        viewBinding.shareSaveLayer.setOnShakeClickListener {
            entity?.fortuneShareImageUrl?.let {
                //保存一张网络图片到本地相册
                saveImage(it)
                val fileName = it.substring(it.lastIndexOf("/") + 1)
                UTHelper.commonEvent(
                    UTConstant.Score.IntegralP_click_ShareFortune,
                    "channel",
                    "保存",
                    "content",
                    fileName,
                    "source",
                    if (!isShowShare.get()) "正常分享" else "截屏后分享"
                )
            }
        }
        //适配屏幕高度问题
        val ratio = 375f / 433f
        val maxHeight = SizeUtils.getHeight() - (162 + 60).dp - 40.dp
        if (maxHeight < 433f.dp) {
            val lp = viewBinding.theDrawLayer.layoutParams
            lp.height = maxHeight.toInt()
            lp.width = (maxHeight * ratio).toInt()
            viewBinding.theDrawLayer.layoutParams = lp

            val lp1 = viewBinding.resultImg.layoutParams as? ConstraintLayout.LayoutParams
            lp1?.width = (lp.width * (180f / 375)).toInt()
            lp1?.height = ((lp1?.width ?: 0) * (210f / 180f)).toInt()
            lp1?.bottomMargin = (9 * maxHeight / 433f).toInt()
            viewBinding.resultImg.layoutParams = lp1
        }
        val lp =viewBinding.bottomBarV.layoutParams
        lp.height = SizeUtils.getStatusBarHeight(context)
        viewBinding.bottomBarV.layoutParams = lp
    }


    fun startSign(userInfoEntity: ScoreIndexUserInfoEntity) {
        show()

        closeable(false)
        entity = userInfoEntity
        viewBinding.dataTv.text = TimeUtils.formatTimestamp(System.currentTimeMillis(), "MM/dd")
        viewBinding.resultImg.hide(true)
        viewBinding.signLine.invisible()
        viewBinding.shareLayer.invisible()
        viewBinding.closeIv.hide(true)
        viewBinding.dataTv.hide(true)
        viewBinding.signTv.text = userInfoEntity.thisTimeCreditsStr
        viewBinding.signNext.text = userInfoEntity.nextTimeCreditsStr
        setLotAni(userInfoEntity)
        viewBinding.bgAniView.progress = 0f
        viewBinding.bgAniView.playAnimation()
        Glide.with(context).load(userInfoEntity.fortuneShareImageUrl).into(ImageView(context))
        viewBinding.shareImage.load(userInfoEntity.fortuneShareImageUrl, false)
        viewBinding.resultImg.load(userInfoEntity.fortuneDetailImageUrl)
        viewBinding.screenshots.hide(true)
        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                if (viewBinding.resultImg != null) {
                    viewBinding.resultImg.aniAlphaShow()
                    viewBinding.signLine.aniAlphaShow()
                    viewBinding.shareLayer.aniAlphaShow()
                    viewBinding.closeIv.aniAlphaShow()
                    viewBinding.dataTv.aniAlphaShow()
                }
                closeable(true)
                entity?.let {
                    signListener?.onSignComplete(it)
                }
                Logger.e("onAnimationEnd")
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        lottieAnimationView.playAnimation()
    }

    fun setData(userInfoEntity: ScoreIndexUserInfoEntity): SignViewDialog {
        entity = userInfoEntity
        viewBinding.dataTv.text = TimeUtils.formatTimestamp(System.currentTimeMillis(), "MM/dd")
        viewBinding.closeIv.hide(!userInfoEntity.isSign)
        viewBinding.dataTv.hide(!userInfoEntity.isSign)
        viewBinding.shareLayer.hide(!userInfoEntity.isSign)
        viewBinding.signLine.hide(true)
        viewBinding.signTv.text = userInfoEntity.thisTimeCreditsStr
        viewBinding.signNext.text = userInfoEntity.nextTimeCreditsStr
        viewBinding.resultImg.hide(!userInfoEntity.isSign)
        viewBinding.bgAniView.progress = if (userInfoEntity.isSign) 1f else 0f
        viewBinding.resultImg.load(userInfoEntity.fortuneDetailImageUrl)
        viewBinding.shareImage.load(userInfoEntity.fortuneShareImageUrl, false)
        viewBinding.screenshots.hide(true)
        if (userInfoEntity.isSign) {
            setLotAni(userInfoEntity)
            lottieAnimationView.progress = 1f
        }

        return this
    }

    private fun setLotAni(entity: ScoreIndexUserInfoEntity) {
        lottieAnimationView = LottieAnimationView(context)
        lottieAnimationView.repeatCount = 0
        viewBinding.theDrawResult.removeAllViews()
        viewBinding.theDrawResult.addView(
            lottieAnimationView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        when (entity.fortuneId) {
            Fortune.superLucky -> {
                lottieAnimationView.imageAssetsFolder = "upup_sign_images/"
                lottieAnimationView.setAnimation("upup_sign.json")
            }
            Fortune.lucky -> {
                lottieAnimationView.imageAssetsFolder = "up_sign_images/"
                lottieAnimationView.setAnimation("up_sign.json")
            }
            Fortune.common -> {
                lottieAnimationView.imageAssetsFolder = "middle_images/"
                lottieAnimationView.setAnimation("middle_sign.json")
            }
            Fortune.unlucky -> {
                lottieAnimationView.imageAssetsFolder = "down_sign_images/"
                lottieAnimationView.setAnimation("down_sign.json")
            }
        }
    }

    fun closeable(enableClose: Boolean) {
        setCanceledOnTouchOutside(enableClose)
        setCancelable(enableClose)
        this.enableClose = enableClose
    }

    override fun show() {
        super.show()
        UTHelper.commonEvent(UTConstant.Score.IntegralP_exposure_SigninPopup)
    }


    fun showShare() {
        if (!enableClose) {
            return
        }
        if (isShowShare.get()) {
            return
        }
        isShowShare.set(true)
        UTHelper.commonEvent(
            UTConstant.Score.Sign_Screenshot,
            "source",
            if (isHome) "首页" else "积分页"
        )

        viewBinding.screenshots?.post {
            viewBinding.screenshots.aniAlphaShow()
            viewBinding.bgAniView.aniAlphaHide()
            viewBinding.theDrawResult.aniAlphaHide()

            val defaultHeight = viewBinding.rootView.height - (111 + 152).azDp
            val defaultWidth = viewBinding.rootView.width - 40.azDp
            //根据比列来缩放
            val ratio = 548f / 335f
            val lp = viewBinding.shareCard.layoutParams
            val height = (defaultWidth * ratio).toInt()
            if (height > defaultHeight) {
                lp.width = (defaultHeight / ratio).toInt()
                lp.height = defaultHeight.toInt()
            } else {
                lp.height = height
                lp.width = defaultWidth.toInt()
            }
            viewBinding.shareCard.layoutParams = lp
            val lp1 = viewBinding.shareImage.layoutParams
            lp1.width = lp.width
            lp1.height = lp.height
            viewBinding.shareImage.layoutParams = lp1
        }
    }

    override fun onStart() {
        super.onStart()
        if (PermissionX.isGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            screenShotListenManager.setListener {
                showShare()
            }
            screenShotListenManager.startListen()

            ScreenShotFileObserverManager.registerScreenShotFileObserver(object :
                ScreenShotFileObserver.ScreenShotLister {
                override fun finshScreenShot(path: String?) {
                    Logger.d("ScreenShotFileObserverManager", "finshScreenShot path = $path")

                }

                override fun beganScreenShot(path: String?) {
                    Logger.d("ScreenShotFileObserverManager", "beganScreenShot path = $path")
                    //截屏开始了
                    showShare()
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        ScreenShotFileObserverManager.unregisteScreenShotFileObserver()
        screenShotListenManager.stopListen()
    }

    private fun saveImage(url: String) {
        if (!PermissionX.isGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionX.init(activity)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(RequestCallback { allGranted, grantedList, deniedList ->
                    downImg(url)
                })
        } else {
            downImg(url)
        }
    }

    private fun downImg(url: String) {
        Flowable.create<String?>(FlowableOnSubscribe {
            Glide.with(context)
                .asBitmap()
                .load(url)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        it.onNext(FileUtils.saveBitmapToPathAndRefreshPhoto(context, resource))
                        it.onComplete()
                    }
                })
        }, BackpressureStrategy.BUFFER)
            .compose(RxSchedulers.netio_main())
            .subscribe({
                //ceshi
                ToastUtil.getInstance()._short(context, "保存成功")
            }, {
                ToastUtil.getInstance()._short(context, "保存失败")
            })


    }


    interface SignListener {
        fun onSignComplete(entity: ScoreIndexUserInfoEntity)
    }

}