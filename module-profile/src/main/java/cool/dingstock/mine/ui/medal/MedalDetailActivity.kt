package cool.dingstock.mine.ui.medal

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.viewpager.widget.ViewPager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.CacheViewPagerAdapter
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.circle.MedalBtnStr
import cool.dingstock.appbase.entity.bean.circle.MedalEntity
import cool.dingstock.appbase.entity.bean.mine.MedalStatus
import cool.dingstock.appbase.entity.event.update.EventMedalStateChange
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ext.loadAvatar
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.util.ScreenUtils
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.ActivityMedalDetailBinding
import cool.dingstock.mine.databinding.MineMedalItemBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.abs
import kotlin.math.max


@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MineConstant.Path.MEDAL_DETAIL]
)
class MedalDetailActivity : VMBindingActivity<MedalDetailViewModel, ActivityMedalDetailBinding>() {

    private var btnType = ""
    private var currentMedalId = ""
    private var scoreBuyId = ""
    private var isInitFinish = false
    private var isOpenMedal = true
    private val vpAdapter =
        object : CacheViewPagerAdapter<MedalEntity, MineMedalItemBinding>(context, arrayListOf()) {
            override fun onItemDestroy(
                position: Int,
                item: MedalEntity,
                vb: MineMedalItemBinding
            ) {
            }

            override fun onItemBind(
                position: Int,
                item: MedalEntity,
                vb: MineMedalItemBinding
            ) {
                when (item.status) {
                    MedalStatus.UNFULFILLED.value, MedalStatus.RECEIVABLE.value -> {
                        vb.iv.load(item.imageBWUrl)
                    }
                    else -> {
                        vb.iv.load(item.imageUrl)
                    }
                }
            }

            override fun onCreateView(group: ViewGroup): MineMedalItemBinding {
                return MineMedalItemBinding.inflate(
                    LayoutInflater.from(context),
                    group,
                    false
                ).apply {
                    val imageSize = ScreenUtils.getScreenWidth(this@MedalDetailActivity) * 0.66f
                    iv.layoutParams.apply {
                        width = imageSize.toInt()
                        height = imageSize.toInt()
                    }
                }
            }
        }


    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        val imageSize = ScreenUtils.getScreenWidth(this@MedalDetailActivity) * 0.66f
        val currentUserId = LoginUtils.getCurrentUser()?.id ?: ""
        viewModel.apply {
            userId = uri.getQueryParameter(MineConstant.PARAM_KEY.ID) ?: currentUserId
            medalId = uri.getQueryParameter(MineConstant.PARAM_KEY.MEDAL_ID) ?: ""
            isMineMedalPage = viewModel.userId == currentUserId
        }
        viewBinding.apply {
            tvCheckMyMedal.hide(viewModel.isMineMedalPage)
            tvCheckMyMedal.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            clSuitPreview.hide(!viewModel.isMineMedalPage)

            lottieGetMedal.layoutParams.apply {
                width = imageSize.toInt()
                height = imageSize.toInt()
            }
            lottieOpenMedal.layoutParams.apply {
                width = imageSize.toInt()
                height = imageSize.toInt()
            }
        }
        initVp()
        viewModel.fetchUserMedal(viewModel.userId, viewModel.medalId)
        showLoadingView()
    }

    override fun initListeners() {
        viewBinding.apply {
            titleBar.setLeftOnClickListener {
                finish()
            }
            tvCheckMyMedal.setOnShakeClickListener {
                if (LoginUtils.isLoginAndRequestLogin(context)) {
                    UTHelper.commonEvent(UTConstant.Mine.MedalDetailP_click_Mine)
                    DcRouter(MineConstant.Uri.METAL_LIST)
                        .putUriParameter(
                            MineConstant.PARAM_KEY.ID,
                            LoginUtils.getCurrentUser()?.id ?: ""
                        )
                        .start()
                }
            }
            tvGetBtn.setOnShakeClickListener {
                clickMedalBtn(btnType)
            }
        }
    }

    private fun clickMedalBtn(type: String) {
        when (type) {
            MedalBtnStr.ALL_MEDAL.value -> {
                UTHelper.commonEvent(UTConstant.Mine.MedalDetailP_click_OthersAll)
                if (intent.getBooleanExtra(MineConstant.ExtraParam.FROM_LIST, false)) {
                    finish()
                    return
                }
                DcRouter(MineConstant.Uri.METAL_LIST)
                    .putUriParameter(MineConstant.PARAM_KEY.ID, viewModel.userId)
                    .start()
            }
            MedalBtnStr.BUY_MEDAL.value -> {
                vpAdapter.isNeedRefresh = true
                viewModel.fetchScoreInfo()
            }
            MedalBtnStr.GET_MEDAL.value -> {
                vpAdapter.isNeedRefresh = true
                viewModel.getNewMedal(currentMedalId)
            }
            MedalBtnStr.SUIT_MEDAL.value -> {
                viewModel.suitUpMedal(currentMedalId, isWear = true)
            }
            MedalBtnStr.UN_SUIT_MEDAL.value -> {
                viewModel.suitUpMedal(currentMedalId, isWear = false)
            }
            else -> {
                vpAdapter.isNeedRefresh = false
            }
        }
    }

    private fun initVp() {
        viewBinding.apply {
            val padding = (ScreenUtils.getScreenWidth(this@MedalDetailActivity) * 0.17f).toInt()
            vp.setPadding(padding, 0, padding, 0)
            vp.adapter = vpAdapter
            vp.setPageTransformer(false, ScalePageTransformer())
            vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    indicatorGroup.setProgress(positionOffset, position)
                }

                override fun onPageSelected(position: Int) {
                    updateControlPanel(vpAdapter.data[position], viewModel.isMineMedalPage)
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
    }

    private fun vpOpenMedalAnimation() {
        viewBinding.vp.isInvisible = false
        val vpAlpha = ObjectAnimator.ofFloat(viewBinding.vp, "alpha", 0f, 0f, 1f)
        val oas = AnimatorSet()
        oas.duration = 2000
        oas.play(vpAlpha)
        oas.start()
    }

    private fun vpGetMedalAnimation() {
        viewBinding.apply {
            val medalName = tvMedalName.text
            val medalTask = tvTask.text
            tvMedalName.text = "恭喜获得勋章"
            tvTask.text = medalName
            tvCondition.text = medalTask
        }
        viewBinding.vp.isInvisible = false

        val vpAlphaA = ObjectAnimator.ofFloat(viewBinding.tvMedalName, "alpha", 1f, 0f, 0f, 1f)
        val vpAlphaB = ObjectAnimator.ofFloat(viewBinding.tvTask, "alpha", 1f, 0f, 0f, 1f)
        val vpAlphaC = ObjectAnimator.ofFloat(viewBinding.tvCondition, "alpha", 1f, 0f, 0f, 1f)
        val vpAlphaD = ObjectAnimator.ofFloat(viewBinding.clSuitPreview, "alpha", 1f, 0f, 0f, 1f)
        val vpAlphaE = ObjectAnimator.ofFloat(viewBinding.vp, "alpha", 1f, 0f, 0f, 1f)
        val oas = AnimatorSet()
        oas.duration = 2500
        oas.play(vpAlphaA).with(vpAlphaB).with(vpAlphaC).with(vpAlphaD).with(vpAlphaE)
        oas.start()
    }

    override fun initBaseViewModelObserver() {
        viewModel.apply {
            userMedals.observe(this@MedalDetailActivity) {
                hideLoadingView()
                if (!it.isNullOrEmpty()) {
                    vpAdapter.setList(it)
                    viewBinding.indicatorGroup.hide(it.size == 1)
                    viewBinding.indicatorGroup.setCount(it.size)
                    var position = 0

                    for ((index, medal) in it.withIndex()) {
                        if (!isInitFinish) {
                            if (medal.id == medalId) {
                                position = index
                                isInitFinish = true
                            }
                        } else {
                            if (medal.id == currentMedalId) {
                                position = index
                            }
                        }
                    }
                    viewBinding.vp.currentItem = position
                    updateControlPanel(it[position], isMineMedalPage)
                    //打开勋章的动画
                    if (it[position].status != MedalStatus.UNFULFILLED.value
                        && it[position].status != MedalStatus.RECEIVABLE.value
                        && isOpenMedal
                        && !viewModel.isGetMedal
                    ) {
                        viewBinding.vp.isInvisible = true
                        isOpenMedal = false
                        viewModel.getBitmaps(firstUrl = it[position].imageUrl, secondUrl = "")
                    }
                    //领取勋章的动画
                    if (viewModel.isGetMedal) {
                        viewBinding.vp.isInvisible = true
                        viewModel.getBitmaps(
                            firstUrl = it[position].imageBWUrl,
                            secondUrl = it[position].imageUrl
                        )
                    }
                    if (position == 0) {
                        firstFakeDrag()
                    }
                }
            }
            userMsg.observe(this@MedalDetailActivity) {
                viewBinding.apply {
                    ivHead.loadAvatar(it.avatarUrl)
                    tvUserName.text = it.nickName
                }
            }
            isSuitMedalSuccess.observe(this@MedalDetailActivity) {
                viewBinding.tvGetBtn.text =
                    if (it) MedalBtnStr.UN_SUIT_MEDAL.value else MedalBtnStr.SUIT_MEDAL.value
                btnType = if (it) MedalBtnStr.UN_SUIT_MEDAL.value else MedalBtnStr.SUIT_MEDAL.value
            }
            isGetMedalSuccess.observe(this@MedalDetailActivity) {
                viewBinding.tvGetBtn.text =
                    if (it) MedalBtnStr.SUIT_MEDAL.value else MedalBtnStr.GET_MEDAL.value
                btnType = if (it) MedalBtnStr.SUIT_MEDAL.value else MedalBtnStr.GET_MEDAL.value
            }
            scoreNum.observe(this@MedalDetailActivity) {
                DcRouter(MineConstant.Uri.EXCHANGE_GOOD_DETAIL)
                    .putExtra(MineConstant.ExtraParam.FROM_WHERE, "FROM_MINE_MEDAL_PAGE")
                    .putExtra("id", viewModel.userId)
                    .putExtra("eventId", scoreBuyId)
                    .putExtra("scoreNumber", it)
                    .start()
            }
            bitmapLiveData.observe(this@MedalDetailActivity) {
                viewBinding.apply {
                    if (viewModel.isGetMedal) {//领取勋章动画
                        vpGetMedalAnimation()
                        viewModel.isGetMedal = false
                        lottieGetMedal.updateBitmap("image_1", it.firstBitmap)
                        lottieGetMedal.updateBitmap("image_2", it.secondBitmap)
                        lottieGetMedal.hide(false)
                        lottieGetMedal.playAnimation()
                        lottieGetMedal.addAnimatorListener(initAnimationCallback {
                            lottieGetMedal.hide(true)
                        })
                    } else {//打开已拥有勋章动画
                        lottieOpenMedal.updateBitmap("image_1", it.firstBitmap)
                        lottieOpenMedal.hide(false)
                        lottieOpenMedal.playAnimation()
                        vpOpenMedalAnimation()
                        lottieOpenMedal.addAnimatorListener(initAnimationCallback {
                            lottieOpenMedal.hide(true)
                        })
                    }
                }
            }
        }
        super.initBaseViewModelObserver()
    }

    private fun firstFakeDrag() {
        viewBinding.vp.post {
            viewBinding.vp.run {
                beginFakeDrag()
                fakeDragBy(1f)
                endFakeDrag()
            }
        }
    }

    private fun updateControlPanel(entity: MedalEntity, isMineMedalPage: Boolean) {
        viewBinding.apply {
            currentMedalId = entity.id ?: ""
            viewModel.currentMedalIconUrl = entity.iconUrl
            tvGetBtn.setTextColor(Color.parseColor("#FFFFFF"))
            tvMedalName.text = entity.name
            tvGetTime.hide(entity.createdAt == 0L)
            ivMedalPreview.load(entity.iconUrl)
            if (entity.createdAt != 0L) {
                tvGetTime.text =
                    "- 于${entity.createdAt?.let { TimeUtils.formatTimestampS5(it) }}获得 -"
            }
            tvTask.text = entity.criteriaStr //kuo hao???
            tvCondition.text = when (entity.madelStatus) {
                MedalStatus.UNFULFILLED -> {
                    entity.validityStr ?: ""
                }
                MedalStatus.RECEIVABLE, MedalStatus.COMPLETED -> {
                    if (entity.validity == 0L) {
                        "长期有效"
                    } else {
                        "有效期：".plus(entity.validity?.let {
                            TimeUtils.formatTimestampCustom(it, "dd天HH小时")
                        })
                    }
                }
                else -> {
                    ""
                }
            }
            tvGetBtn.isEnabled = true
            if (!isMineMedalPage) {//他人的勋章页面
                tvGetTime.hide(true)
                tvGetBtn.text = MedalBtnStr.ALL_MEDAL.value
                btnType = MedalBtnStr.ALL_MEDAL.value
            } else if (entity.creditProductId != null && entity.creditProductId != "") {//是否是积分勋章
                when (entity.status) {
                    MedalStatus.COMPLETED.value -> {
                        tvGetBtn.text = MedalBtnStr.SUIT_MEDAL.value
                        btnType = MedalBtnStr.SUIT_MEDAL.value
                    }
                    MedalStatus.WEARING.value -> {
                        tvGetBtn.text = MedalBtnStr.UN_SUIT_MEDAL.value
                        btnType = MedalBtnStr.UN_SUIT_MEDAL.value
                    }
                    else -> {
                        scoreBuyId = entity.creditProductId.toString()
                        tvGetBtn.text = entity.buttonStr ?: ""
                        btnType = MedalBtnStr.BUY_MEDAL.value
                    }
                }
            } else {//普通勋章
                tvGetBtn.isEnabled = entity.status != MedalStatus.UNFULFILLED.value
                when (entity.status) {
                    MedalStatus.UNFULFILLED.value -> {
                        tvGetBtn.text = MedalBtnStr.NOT_FINISH.value
                        btnType = MedalBtnStr.NOT_FINISH.value
                        tvGetBtn.setTextColor(Color.parseColor("#626466"))
                    }
                    MedalStatus.RECEIVABLE.value -> {
                        tvGetBtn.text = MedalBtnStr.GET_MEDAL.value
                        btnType = MedalBtnStr.GET_MEDAL.value
                    }
                    MedalStatus.COMPLETED.value -> {
                        tvGetBtn.text = MedalBtnStr.SUIT_MEDAL.value
                        btnType = MedalBtnStr.SUIT_MEDAL.value
                    }
                    MedalStatus.WEARING.value -> {
                        tvGetBtn.text = MedalBtnStr.UN_SUIT_MEDAL.value
                        btnType = MedalBtnStr.UN_SUIT_MEDAL.value
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun setSystemStatusBar() {
        StatusBarUtil.setDarkMode(this)
        StatusBarUtil.transparentStatus(this)
    }

    override fun moduleTag(): String {
        return ModuleConstant.MINE_MODULE
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMedalStateChange(event: EventMedalStateChange?) {
        viewModel.apply {
            isGetMedal = event?.isGetMedal ?: false
            fetchUserMedal(userId, medalId)
        }
    }

    private fun initAnimationCallback(animationEnd: () -> Unit): Animator.AnimatorListener {
        return object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                animationEnd.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        }
    }

    /**
     * 设置中间放大两边缩小
     */
    class ScalePageTransformer : ViewPager.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val img = page.findViewById<ImageView>(R.id.iv)
            img.pivotY = img.measuredHeight / 2f
            val transformPos: Float
            (page.parent as ViewPager).run {
                val clientWidth = measuredWidth - paddingLeft - paddingRight
                val offsetX = page.left - scrollX
                val deltaX = (measuredWidth - page.width) / 2f
                transformPos = (offsetX - deltaX) / clientWidth
            }
            img.pivotX = if (transformPos < 0) {
                img.measuredWidth.toFloat()
            } else {
                0f
            }
            if (transformPos < -1 || transformPos > 1f) {
                img.scaleX = MIN_SCALE
                img.scaleY = MIN_SCALE
                img.alpha = MIN_ALPHA
            } else if (transformPos <= 1f) {
                val scale = max(MIN_SCALE, 1 - abs(transformPos))
                val scaleReal = MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(transformPos))
                img.scaleX = scaleReal
                img.scaleY = scaleReal
                img.alpha = MIN_ALPHA + (scale - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
            }
        }

        companion object {
            const val MIN_SCALE = 0.736f
            const val MIN_ALPHA = 0.2f
        }
    }
}