package cool.mobile.account.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.tencent.qq.QQ
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.mob.MobSDK
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import cool.dingstock.appbase.R
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.exception.DcException
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.share.DcPlatform
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.SharePlatform
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import cool.dingstock.lib_base.util.AppUtils
import cool.dingstock.lib_base.util.FileUtils
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.lib_base.util.ToastUtil
import cool.mobile.account.dagger.AccountApiHelper
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableOnSubscribe
import javax.inject.Inject

class ShareHelper {
    @Inject
    lateinit var commonApi: CommonApi

    companion object {
        const val APP_URL = "https://dingstock.com.cn"
    }

    init {
        AccountApiHelper.apiAccountComponent.inject(this)
    }

    fun shareToMiniProgram(
        path: String,
        title: String,
        content: String,
        imageUrl: String,
        context: Context
    ) {
        val type = ShareType.Mp
        val params = ShareParams()
        params.title = title
        params.content = content
        params.imageUrl = imageUrl
        params.mpPath = path
        type.params = params
        val shareHelper = ShareHelper()
        shareHelper.share(context, type, SharePlatform.WeChat)
    }

    fun share(context: Context, type: ShareType, target: SharePlatform, utEventId: String) {
        val params = type.params
        when (target) {
            SharePlatform.WeChat -> {
                UTHelper.commonEvent(utEventId, "channel", "??????")
            }
            SharePlatform.QQ -> {
                UTHelper.commonEvent(utEventId, "channel", "QQ")
            }
            SharePlatform.WeChatMoments -> {
                UTHelper.commonEvent(utEventId, "channel", "?????????")
            }
            SharePlatform.Copy -> {
                UTHelper.commonEvent(utEventId, "channel", "????????????")
            }
            else -> {

            }
        }
        // ???????????????
        if (null == target.platform) {
            if (target == SharePlatform.Copy) {
                copyToClipBoard(context, params?.link ?: "")
                return
            }
            if (target == SharePlatform.PWD) {
                if (params != null) {
                    obtainPwdCode(context, params.pwdUrl ?: "")
                }
                return
            }
            return
        }
        var utLink = ""
        var UtPlat = ""
        UtPlat = target.shareName
        val onekeyShare = OnekeyShare()
        when (target.platform) {
            DcPlatform.Wechat -> {
                onekeyShare.setPlatform(Wechat.NAME)
            }
            DcPlatform.QQ -> {
                onekeyShare.setPlatform(QQ.NAME)
            }
            DcPlatform.WechatMoments -> {
                onekeyShare.setPlatform(WechatMoments.NAME)
            }
        }
        onekeyShare.setShareContentCustomizeCallback { platform, shareParams ->
            params?.title?.let { shareParams.title = it }
            params?.content.let { shareParams.text = it }
            shareParams.imageUrl = params?.imageUrl ?: ""
            if (StringUtils.isEmpty(params?.imageUrl)) {
                if (params?.imageBitmap == null) {
                    val logoBitmap =
                        BitmapFactory.decodeResource(
                            AppUtils.applicationContext?.resources,
                            R.drawable.wechat_share_default_img
                        )
                    shareParams.imageData = logoBitmap
                } else {
                    shareParams.imageData = params.imageBitmap
                }
            }
            params?.link.let {
                shareParams.url = it
                shareParams.titleUrl = it
            }
            params?.imageBitmap?.let { shareParams.imageData = it }
            if (params?.imageBitmap != null || params?.imagePath != null) {
                shareParams.shareType = Platform.SHARE_IMAGE
            }
            //?????????????????????
            if (params?.imageBitmap == null && StringUtils.isEmpty(params?.imageUrl)) {
                try {
                    shareParams.imageData =
                        context.resources?.getDrawable(R.drawable.wechat_share_default_img)
                            ?.toBitmap()
                } catch (e: Exception) {
                }
            }
            when (type) {
                ShareType.ImageText -> {
                    if (params?.imageBitmap != null) {
                        shareParams.shareType = Platform.SHARE_IMAGE
                        if (target == SharePlatform.QQ) {
                            shareParams.imagePath = params.imagePath
                        } else {
                            shareParams.imageData = params.imageBitmap
                        }
                    } else if (!TextUtils.isEmpty(params?.imageUrl)) {
                        shareParams.shareType = Platform.SHARE_IMAGE
                        shareParams.imageUrl = params!!.imageUrl
                    } else {
                        shareParams.shareType = Platform.SHARE_IMAGE
                        try {
                            shareParams.imageData =
                                context.resources?.getDrawable(R.drawable.wechat_share_default_img)
                                    ?.toBitmap()
                        } catch (e: Exception) {
                        }
                    }
                }
                ShareType.Link -> {
                    shareParams.shareType = Platform.SHARE_WEBPAGE
                    shareParams.imageUrl = params?.imageUrl ?: ""
                    shareParams.title = params?.title ?: "??????APP"
                    shareParams.text = params?.content ?: ""
                    shareParams.url = params?.link ?: ""
                    shareParams.titleUrl = params?.link ?: ""
                    utLink = params?.link ?: ""
                }
                ShareType.Mp -> {
                    shareParams.wxUserName = "gh_994f32006aae"
                    shareParams.url = APP_URL
                    shareParams.wxPath = params?.mpPath ?: ""
                    val shareMpEnv =
                        BaseLibrary.getInstance().context.getSharedPreferences(
                            "mpEnv",
                            Context.MODE_PRIVATE
                        )
                    val isRelease = shareMpEnv.getBoolean("isRelease", true)
                    shareParams.wxMiniProgramType =
                        if (isRelease) WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE else WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
                    // ????????????????????????????????????????????????
                    if (target == SharePlatform.WeChat) {
                        shareParams.shareType = Platform.SHARE_WXMINIPROGRAM
                    } else if (null != params?.link) {
                        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????
                        shareParams.shareType = Platform.SHARE_WEBPAGE
                        shareParams.url = params.link ?: APP_URL
                        shareParams.title = params.title ?: "??????APP"
                        shareParams.text = params.content ?: ""
                        shareParams.imageUrl = params.imageUrl ?: ""
                    }
                }
                ShareType.CONFIG -> {
                    shareParams.wxUserName = "gh_994f32006aae"
                    shareParams.url = APP_URL
                    shareParams.wxPath = params?.mpPath ?: ""
                    val shareMpEnv =
                        BaseLibrary.getInstance().context.getSharedPreferences(
                            "mpEnv",
                            Context.MODE_PRIVATE
                        )
                    val isRelease = shareMpEnv.getBoolean("isRelease", true)
                    shareParams.wxMiniProgramType =
                        if (isRelease) WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE else WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
                    // ????????????????????????????????????????????????
                    if (target == SharePlatform.WeChatMini) {
                        shareParams.shareType = Platform.SHARE_WXMINIPROGRAM
                    } else if (null != params?.link) {
                        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????
                        shareParams.shareType = Platform.SHARE_WEBPAGE
                        shareParams.url = params.link ?: APP_URL
                        shareParams.title = params.title ?: "??????APP"
                        shareParams.text = params.content ?: ""
                        shareParams.imageUrl = params.imageUrl ?: ""
                    }
                }
                ShareType.Image -> {
                    if (params?.imageBitmap != null) {
                        shareParams.shareType = Platform.SHARE_IMAGE
                        if (target == SharePlatform.QQ) {
                            shareParams.imagePath = params.imagePath
                        } else {
                            shareParams.imageData = params.imageBitmap
                        }
                    } else if (!TextUtils.isEmpty(params?.imageUrl)) {
                        shareParams.shareType = Platform.SHARE_IMAGE
                        shareParams.imageUrl = params!!.imageUrl
                    } else {
                        shareParams.shareType = Platform.SHARE_IMAGE
                        try {
                            shareParams.imageData =
                                context.resources?.getDrawable(R.drawable.wechat_share_default_img)
                                    ?.toBitmap()
                        } catch (e: Exception) {
                        }
                    }
                }
            }
            utEvent(utLink, UtPlat)
        }
        onekeyShare.callback = object : PlatformActionListener {
            override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
                Log.e("share", "????????????")
                ToastUtil.getInstance().makeTextAndShow(context, "????????????", Toast.LENGTH_SHORT)
            }

            override fun onCancel(p0: Platform?, p1: Int) {
                Log.e("share", "????????????")
                ToastUtil.getInstance().makeTextAndShow(context, "???????????????", Toast.LENGTH_SHORT)
            }

            override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
                Log.e("share", "????????????")
                ToastUtil.getInstance().makeTextAndShow(context, "????????????", Toast.LENGTH_SHORT)
            }
        }
        onekeyShare.show(MobSDK.getContext())
    }


    fun share(context: Context, type: ShareType, target: SharePlatform) {
        val params = type.params
        // ???????????????
        if (null == target.platform) {
            if (target == SharePlatform.Copy) {
                copyToClipBoard(context, params?.link ?: "")
                return
            }
            if (target == SharePlatform.PWD) {
                if (params != null) {
                    obtainPwdCode(context, params.pwdUrl ?: "")
                }
                return
            }
            return
        }
        var utLink = ""
        var UtPlat = ""
        UtPlat = target.shareName
        val onekeyShare = OnekeyShare()
        when (target.platform) {
            DcPlatform.Wechat -> {
                onekeyShare.setPlatform(Wechat.NAME)
            }
            DcPlatform.QQ -> {
                onekeyShare.setPlatform(QQ.NAME)
            }
            DcPlatform.WechatMoments -> {
                onekeyShare.setPlatform(WechatMoments.NAME)
            }
        }
        onekeyShare.setShareContentCustomizeCallback { platform, shareParams ->
            params?.title?.let { shareParams.title = it }
            params?.content.let { shareParams.text = it }
            shareParams.imageUrl = params?.imageUrl ?: ""
            if (StringUtils.isEmpty(params?.imageUrl)) {
                if (params?.imageBitmap == null) {
                    val logoBitmap =
                        BitmapFactory.decodeResource(
                            AppUtils.applicationContext?.resources,
                            R.drawable.wechat_share_default_img
                        )
                    shareParams.imageData = logoBitmap
                } else {
                    shareParams.imageData = params.imageBitmap
                }
            }
            params?.link.let {
                shareParams.url = it
                shareParams.titleUrl = it
            }
            params?.imageBitmap?.let { shareParams.imageData = it }
            if (params?.imageBitmap != null || params?.imagePath != null) {
                shareParams.shareType = Platform.SHARE_IMAGE
            }
            //?????????????????????
            if (params?.imageBitmap == null && StringUtils.isEmpty(params?.imageUrl)) {
                try {
                    shareParams.imageData =
                        context.resources?.getDrawable(R.drawable.wechat_share_default_img)
                            ?.toBitmap()
                } catch (e: Exception) {
                }
            }
            when (type) {
                ShareType.Link -> {
                    shareParams.shareType = Platform.SHARE_WEBPAGE
                    shareParams.imageUrl = params?.imageUrl ?: ""
                    shareParams.title = params?.title ?: "??????APP"
                    shareParams.text = params?.content ?: ""
                    shareParams.url = params?.link ?: ""
                    shareParams.titleUrl = params?.link ?: ""
                    utLink = params?.link ?: ""
                }
                ShareType.Mp -> {
                    shareParams.wxUserName = params?.mpId ?: "gh_994f32006aae"
                    shareParams.url = APP_URL
                    shareParams.wxPath = params?.mpPath ?: ""
                    val shareMpEnv =
                        BaseLibrary.getInstance().context.getSharedPreferences(
                            "mpEnv",
                            Context.MODE_PRIVATE
                        )
                    val isRelease = shareMpEnv.getBoolean("isRelease", true)
                    shareParams.wxMiniProgramType =
                        if (isRelease) WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE else WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
                    // ????????????????????????????????????????????????
                    if (target == SharePlatform.WeChat) {
                        shareParams.shareType = Platform.SHARE_WXMINIPROGRAM
                    } else if (null != params?.link) {
                        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????
                        shareParams.shareType = Platform.SHARE_WEBPAGE
                        shareParams.url = params.link ?: APP_URL
                        shareParams.title = params.title ?: "??????APP"
                        shareParams.text = params.content ?: ""
                        shareParams.imageUrl = params.imageUrl ?: ""
                    }
                }
                ShareType.Image -> {
                    if (params?.imageBitmap != null) {
                        shareParams.shareType = Platform.SHARE_IMAGE
                        if (target == SharePlatform.QQ) {
                            shareParams.imagePath = params.imagePath
                        } else {
                            shareParams.imageData = params.imageBitmap
                        }
                    } else if (!TextUtils.isEmpty(params?.imageUrl)) {
                        shareParams.shareType = Platform.SHARE_IMAGE
                        shareParams.imageUrl = params!!.imageUrl
                    } else {
                        shareParams.shareType = Platform.SHARE_IMAGE
                        try {
                            shareParams.imageData =
                                context.resources?.getDrawable(R.drawable.wechat_share_default_img)
                                    ?.toBitmap()
                        } catch (e: Exception) {
                        }
                    }
                }
            }
            utEvent(utLink, UtPlat)
        }
        onekeyShare.callback = object : PlatformActionListener {
            override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
                Log.e("share", "????????????")
                ToastUtil.getInstance().makeTextAndShow(context, "????????????", Toast.LENGTH_SHORT)
            }

            override fun onCancel(p0: Platform?, p1: Int) {
                Log.e("share", "????????????")
                ToastUtil.getInstance().makeTextAndShow(context, "???????????????", Toast.LENGTH_SHORT)
            }

            override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
                Log.e("share", "????????????")
                ToastUtil.getInstance().makeTextAndShow(context, "????????????", Toast.LENGTH_SHORT)
            }
        }
        onekeyShare.show(MobSDK.getContext())
    }

    private fun utEvent(link: String?, plat: String?) {
        if (link.isNullOrEmpty()) {
            return
        }
        UTHelper.commonEvent(UTConstant.Share.SHARE_LINK, "value", link, "channel", plat)
    }

    /**
     * ????????????????????????
     */
    private fun copyToClipBoard(context: Context, shareLink: String?) {
        if (shareLink == null || shareLink.isNullOrEmpty()) {
            return
        }
        ClipboardHelper.copyMsg(context, shareLink) {
            ToastUtil.getInstance().makeTextAndShow(context, "?????????????????????", Toast.LENGTH_SHORT)
        }
    }

    /**
     * ????????????????????????
     */
    private fun obtainPwdCode(context: Context, pwdUrl: String?) {
        if (pwdUrl == null || pwdUrl.isNullOrEmpty()) {
            return
        }
        commonApi.obtainParityWord(pwdUrl)
            .subscribe({
                if (!it.err && !StringUtils.isEmpty(it.res)) {
                    Log.e("obtainPwdCode", it?.res ?: "")
                    ClipboardHelper.copyMsg(context, it?.res ?: "") {
                        ToastUtil.getInstance()
                            .makeTextAndShow(context, "???????????????", Toast.LENGTH_SHORT)
                    }
                } else {
                    ToastUtil.getInstance().makeTextAndShow(context, it.msg, Toast.LENGTH_SHORT)
                }
            }, {
                ToastUtil.getInstance()
                    .makeTextAndShow(context, (it as DcException).msg, Toast.LENGTH_SHORT)
            })
    }

    private fun sendDynamic() {
//        entity?.fortuneShareImageUrl?.let {
//            val fileName = it.substring(it.lastIndexOf("/") + 1)
//            UTHelper.commonEvent(
//                    UTConstant.Score.IntegralP_click_ShareFortune,
//                    "channel",
//                    "????????????",
//                    "content",
//                    fileName,
//                    "source",
//                    if (!isShowShare.get()) "????????????" else "???????????????"
//            )
//            DcUriRequest(context, CircleConstant.Uri.CIRCLE_DYNAMIC_EDIT)
//                    .putUriParameter(CircleConstant.UriParams.IMAGE_URL, it)
//                    .start()
    }
}


//    private fun savePictureToPhone() {
//        entity?.fortuneShareImageUrl?.let {
//            //???????????????????????????????????????
//            saveImage(it)
//            val fileName = it.substring(it.lastIndexOf("/") + 1)
//
//        }
//    }
//
//    private fun saveImage(url: String) {
//        if (!PermissionX.isGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            PermissionX.init(activity)
//                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    .request(RequestCallback { allGranted, grantedList, deniedList ->
//                        downImg(url)
//                    })
//        } else {
//            downImg(url)
//        }
//    }

private fun downImg(url: String) {
    Flowable.create<String?>(FlowableOnSubscribe {
        Glide.with(BaseLibrary.getInstance().context)
            .asBitmap()
            .load(url)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    it.onNext(FileUtils.saveBitmapToPathAndRefreshPhoto(BaseLibrary.getInstance().context, resource))
                    it.onComplete()
                }
            })
    }, BackpressureStrategy.BUFFER)
        .compose(RxSchedulers.netio_main())
        .subscribe({
            //ceshi
            ToastUtil.getInstance()._short(BaseLibrary.getInstance().context, "????????????")
        }, {
            ToastUtil.getInstance()._short(BaseLibrary.getInstance().context, "????????????")
        })


}

