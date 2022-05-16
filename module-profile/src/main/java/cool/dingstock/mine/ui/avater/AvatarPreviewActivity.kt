package cool.dingstock.mine.ui.avater

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.event.account.EventUserLogin
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.textColor
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.imagepicker.ImagePicker
import cool.dingstock.imagepicker.bean.ImageItem
import cool.dingstock.imagepicker.bean.MimeType
import cool.dingstock.imagepicker.bean.selectconfig.CropConfig
import cool.dingstock.imagepicker.custom.CustomImgPickerPresenter
import cool.dingstock.imagepre.ImagePreview
import cool.dingstock.imagepre.bean.ImageInfo
import cool.dingstock.imagepre.glide.FileTarget
import cool.dingstock.imagepre.glide.ImageLoader
import cool.dingstock.imagepre.glide.progress.OnProgressListener
import cool.dingstock.imagepre.glide.progress.ProgressManager
import cool.dingstock.imagepre.tool.common.HandlerUtils.HandlerHolder
import cool.dingstock.imagepre.tool.image.DownloadPictureUtil
import cool.dingstock.imagepre.view.photoview.PhotoView
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.AvatarLayoutPreviewBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*

class AvatarPreviewActivity : VMBindingActivity<AvatarPreviewViewModel, AvatarLayoutPreviewBinding>(), Handler.Callback {
    private var imageInfoList: List<ImageInfo>? = null
    private var currentItem = 0 // 当前显示的图片索引

    private var pendantUrl: String? = null
    private var isMine = false
    private var isShowDownButton = false
    private var isShowCloseButton = false
    private var isShowIndicator = false
    private var imagePreviewAdapter: ImagePreviewAdapter? = null
    private var progressParentLayout: View? = null
    private var isUserCustomProgressView = false

    // 下载按钮显示状态
    private var downloadButtonStatus = false

    // 关闭按钮显示状态
    private var closeButtonStatus = false

    //背景色
    private var bgColor = "000000"
    private var currentItemOriginPathUrl = "" // 当前显示的原图链接
    private var handlerHolder: HandlerHolder? = null
    private var lastProgress = 0

    override fun setSystemStatusBar() {
        super.setSystemStatusBar()
        StatusBarUtil.setDarkMode(this)
        StatusBarUtil.transparentStatus(this)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        handlerHolder = HandlerHolder(this)
        imageInfoList = ImagePreview.getInstance().imageInfoList
        bgColor = ImagePreview.getInstance().bgColor
        if (null == imageInfoList || imageInfoList!!.isEmpty()) {
            onBackPressed()
            return
        }
        currentItem = ImagePreview.getInstance().index
        isShowDownButton = ImagePreview.getInstance().isShowDownButton
        isShowCloseButton = ImagePreview.getInstance().isShowCloseButton
        isShowIndicator = ImagePreview.getInstance().isShowIndicator
        pendantUrl = ImagePreview.getInstance().pendantUrl
        currentItemOriginPathUrl = imageInfoList!![currentItem].originUrl

        setBottomView()

        viewBinding.fmCenterProgressContainer.visibility = View.GONE
        val progressLayoutId = ImagePreview.getInstance().progressLayoutId
        // != -1 即用户自定义了view
        if (progressLayoutId != -1) {
            // add用户自定义的view到frameLayout中，回调进度和view
            progressParentLayout = View.inflate(this, ImagePreview.getInstance().progressLayoutId, null)
            isUserCustomProgressView = if (progressParentLayout != null) {
                viewBinding.fmCenterProgressContainer.removeAllViews()
                viewBinding.fmCenterProgressContainer.addView(progressParentLayout)
                true
            } else {
                // 使用默认的textView进行百分比的显示
                false
            }
        } else {
            // 使用默认的textView进行百分比的显示
            isUserCustomProgressView = false
        }

        //设置背景色
        try {
            viewBinding.rootView.setBackgroundColor(Color.parseColor("#$bgColor"))
        } catch (e: Exception) { }

        downloadButtonStatus = isShowDownButton
        viewBinding.imgDownload.isVisible = isShowDownButton

        closeButtonStatus = isShowCloseButton
        viewBinding.imgCloseButton.isVisible = isShowCloseButton

        imagePreviewAdapter = ImagePreviewAdapter(this, imageInfoList!!)
        viewBinding.viewPager.adapter = imagePreviewAdapter
        viewBinding.viewPager.currentItem = currentItem

        viewModel.uploadImageFailed.observe(this) { s ->
            showToastShort(s)
            hideLoadingDialog()
        }
        viewModel.userAvatarLiveData.observe(this) { url ->
            hideLoadingDialog()
            showSuccessDialog("更新成功")
            viewBinding.avatarIv.setAvatarUrl(url)
            imageInfoList!![0].apply {
                thumbnailUrl = url
                originUrl = url
            }
            imagePreviewAdapter?.notifyDataSetChanged()
        }
    }

    private fun setBottomView() {
        LoginUtils.getCurrentUser()?.let { user ->
            if (user.id == ImagePreview.getInstance().userId) {
                isMine = true
            }
        }
        ImagePreview.getInstance().avatarPendantName?.let {
            val pendantName = "Ta正在使用：${it} 挂件"
            viewBinding.pendantNameTv.text = pendantName
        }
        viewBinding.changeAvatarPendantBtn.text = if (isMine) "更换头像挂件" else "去设置我的头像挂件"
        viewBinding.avatarIv.setAvatarUrl(imageInfoList!![0].originUrl)
        pendantUrl?.let {
            if (it.isNotEmpty()) viewBinding.avatarIv.setPendantUrl(it)
        }
        viewBinding.changeAvatarPendantBtn.apply {
            textSize = if (isMine) 14f else 12f
            textColor(if (isMine) "#ffffff" else "#b3ffffff")
            setBackgroundResource(if (isMine) R.drawable.shape_30white_8dp else R.drawable.shape_30white_6dp)
            layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
                height = if (isMine) 50.dp.toInt() else 32.dp.toInt()
                marginStart = if (isMine) 20.dp.toInt() else 30.dp.toInt()
                marginEnd = if (isMine) 20.dp.toInt() else 15.dp.toInt()
            }
        }
        setBottomVisible()
    }

    override fun initListeners() {
        //设置挂件
        viewBinding.changeAvatarPendantBtn.setOnClickListener {
            if (!LoginUtils.isLoginAndRequestLogin(this)) {
                return@setOnClickListener
            }
            UTHelper.commonEvent(if (isMine) UTConstant.Mine.Avatarbox_click_mine else
                UTConstant.Mine.Avatarbox_click_others)
            DcRouter(MineConstant.Uri.MODIFY_PENDANT).start()
        }
        //设置头像
        viewBinding.changeAvatarBtn.setOnClickListener {
            if (!LoginUtils.isLoginAndRequestLogin(this)) {
                return@setOnClickListener
            }
            selectPic()
        }

        // 关闭页面按钮
        viewBinding.imgCloseButton.setOnClickListener {
            onBackPressed()
        }
        // 下载图片按钮
        viewBinding.imgDownload.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@AvatarPreviewActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // 拒绝权限
                    ToastUtil.getInstance()._short(this, "您拒绝了存储权限，下载失败！")
                } else {
                    //申请权限
                    ActivityCompat.requestPermissions(this@AvatarPreviewActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                }
            } else {
                // 下载当前图片
                downloadCurrentImg()
            }
        }
        viewBinding.viewPager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (ImagePreview.getInstance().bigImagePageChangeListener != null) {
                    ImagePreview.getInstance().bigImagePageChangeListener.onPageSelected(position)
                }
                currentItem = position
                currentItemOriginPathUrl = imageInfoList!![position].originUrl
                // 如果是自定义百分比进度view，每次切换都先隐藏，并重置百分比
                if (isUserCustomProgressView) {
                    viewBinding.fmCenterProgressContainer.visibility = View.GONE
                    lastProgress = 0
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (ImagePreview.getInstance().bigImagePageChangeListener != null) {
                    ImagePreview.getInstance()
                        .bigImagePageChangeListener
                        .onPageScrolled(position, positionOffset, positionOffsetPixels)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (ImagePreview.getInstance().bigImagePageChangeListener != null) {
                    ImagePreview.getInstance().bigImagePageChangeListener.onPageScrollStateChanged(
                        state
                    )
                }
            }
        })
    }

    private fun selectPic() {
        ImagePicker.withMulti(CustomImgPickerPresenter()) //指定presenter
            //设置选择的最大数
            .setMaxCount(1) //设置列数
            .setColumnCount(4) //设置要加载的文件类型，可指定单一类型
            .mimeTypes(MimeType.ofImage()) //设置需要过滤掉加载的文件类型
            .filterMimeTypes(MimeType.GIF)
            .showCamera(true) //显示拍照
            .cropSaveInDCIM(false) //设置剪裁比例
            .setCropRatio(1, 1) //设置剪裁框间距，单位px
            .cropRectMinMargin(120) //设置剪裁模式，留白或充满  CropConfig.STYLE_GAP 或 CropConfig.STYLE_FILL
            .cropStyle(CropConfig.STYLE_FILL) //设置留白模式下生成的图片背景色，支持透明背景
            .cropGapBackgroundColor(Color.TRANSPARENT)
            .crop(this) { items -> //图片剪裁回调，主线程
                handleImage(items)
            }
    }

    private fun handleImage(items: ArrayList<ImageItem>) {
        if (items.isEmpty()) {
            return
        }
        val filePath = items[0].cropUrl
        showLoadingDialog()
        val cropUri = FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", File(filePath)).toString()
        viewModel.compressPhoto(filePath, cropUri)
    }

    private fun setBottomVisible() {
        viewBinding.changeAvatarBtn.isVisible = isMine
        if (!isMine) {
            viewBinding.changeAvatarPendantBtn.isVisible = !pendantUrl.isNullOrEmpty()
            viewBinding.pendantNameTv.isVisible = !pendantUrl.isNullOrEmpty()
            viewBinding.avatarIv.isVisible = !pendantUrl.isNullOrEmpty()
        } else {
            viewBinding.changeAvatarPendantBtn.isVisible = true
            viewBinding.pendantNameTv.isVisible = false
            viewBinding.avatarIv.isVisible = false
        }
    }

    /**
     * 下载当前图片到SD卡
     */
    private fun downloadCurrentImg() {
        DownloadPictureUtil.downloadPicture(
            applicationContext,
            currentItemOriginPathUrl,
            imagePreviewAdapter!!.getGifView(currentItemOriginPathUrl),
            imagePreviewAdapter!!.getImgView(currentItemOriginPathUrl)
        )
    }

    override fun onBackPressed() {
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun moduleTag(): String {
        return ModuleConstant.MINE_MODULE
    }

    private fun convertPercentToBlackAlphaColor(percent: Float): Int {
        var percentVar = percent
        percentVar = 1f.coerceAtMost(0f.coerceAtLeast(percentVar))
        val intAlpha = (percentVar * 255).toInt()
        val stringAlpha = Integer.toHexString(intAlpha).lowercase(Locale.getDefault())
        val color = "#" + (if (stringAlpha.length < 2) "0" else "") + stringAlpha + bgColor
        return Color.parseColor(color)
    }

    fun setAlpha(alpha: Float) {
        val colorId = convertPercentToBlackAlphaColor(alpha)
        viewBinding.rootView.setBackgroundColor(colorId)
        if (alpha >= 1) {
            if (downloadButtonStatus) {
                viewBinding.imgDownload.visibility = View.VISIBLE
            }
            if (closeButtonStatus) {
                viewBinding.imgCloseButton.visibility = View.VISIBLE
            }
            setBottomVisible()
        } else {
            viewBinding.changeAvatarPendantBtn.isVisible = false
            viewBinding.pendantNameTv.isVisible = false
            viewBinding.avatarIv.isVisible = false
            viewBinding.changeAvatarBtn.isVisible = false
            viewBinding.imgDownload.visibility = View.GONE
            viewBinding.imgCloseButton.visibility = View.GONE
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == 0) { // 点击查看原图按钮，开始加载原图
            val path = imageInfoList!![currentItem].originUrl
            if (path.endsWith(".gif")) {
                val gifView = imagePreviewAdapter!!.getGifView(path)
                downGif(path, gifView)
                return true
            }
            if (checkCache(path)) {
                val message = handlerHolder!!.obtainMessage()
                val bundle = Bundle()
                bundle.putString("url", path)
                message.what = 1
                message.obj = bundle
                handlerHolder!!.sendMessage(message)
                return true
            }
            loadOriginImage(path)
        } else if (msg.what == 1) { // 加载完成
            val bundle = msg.obj as Bundle
            val url = bundle.getString("url")
            if (currentItem == getRealIndexWithPath(url)) {
                if (isUserCustomProgressView) {
                    viewBinding.fmCenterProgressContainer.visibility = View.GONE
                    if (ImagePreview.getInstance().onOriginProgressListener != null) {
                        progressParentLayout!!.visibility = View.GONE
                        ImagePreview.getInstance().onOriginProgressListener.finish(
                            progressParentLayout
                        )
                    }
                    imagePreviewAdapter!!.loadOrigin(imageInfoList!![currentItem])
                } else {
                    imagePreviewAdapter!!.loadOrigin(imageInfoList!![currentItem])
                }
            }
        } else if (msg.what == 2) { // 加载中
            val bundle = msg.obj as Bundle
            val url = bundle.getString("url")
            val progress = bundle.getInt("progress")
            if (currentItem == getRealIndexWithPath(url)) {
                if (isUserCustomProgressView) {
                    viewBinding.fmCenterProgressContainer.visibility = View.VISIBLE
                    if (ImagePreview.getInstance().onOriginProgressListener != null) {
                        progressParentLayout!!.visibility = View.VISIBLE
                        ImagePreview.getInstance()
                            .onOriginProgressListener
                            .progress(progressParentLayout, progress)
                    }
                }
            }
        }
        return true
    }

    private fun downGif(path: String, gifView: PhotoView) {
        Glide.with(this).downloadOnly().load(path).into(object : FileTarget() {
            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                super.onResourceReady(resource, transition)
                Glide.with(this@AvatarPreviewActivity).load(resource).into(gifView)
                gifView.visibility = View.VISIBLE
                val imgView = imagePreviewAdapter!!.getImgView(path)
                imgView.visibility = View.GONE
            }
        })
    }

    private fun getRealIndexWithPath(path: String?): Int {
        for (i in imageInfoList!!.indices) {
            if (path.equals(imageInfoList!![i].originUrl, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    private fun checkCache(url: String): Boolean {
        val cacheFile = ImageLoader.getGlideCacheFile(this, url)
        return cacheFile != null && cacheFile.exists()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (i in permissions.indices) {
                if (grantResults[i] == PermissionChecker.PERMISSION_GRANTED) {
                    downloadCurrentImg()
                } else {
                    ToastUtil.getInstance()._short(this, "您拒绝了存储权限，下载失败！")
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            EventBus.getDefault().unregister(this)
            ImagePreview.getInstance().reset()
            if (imagePreviewAdapter != null) {
                imagePreviewAdapter!!.closePage()
            }
        }
    }

    private fun loadOriginImage(path: String) {
        Glide.with(this).downloadOnly().load(path).into(object : FileTarget() {
            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                super.onResourceReady(resource, transition)
            }
        })
        ProgressManager.addListener(
            path,
            OnProgressListener { url, isComplete, percentage, _, _ ->
                if (isComplete) { // 加载完成
                    val message = handlerHolder!!.obtainMessage()
                    val bundle = Bundle()
                    bundle.putString("url", url)
                    message.what = 1
                    message.obj = bundle
                    handlerHolder!!.sendMessage(message)
                } else { // 加载中，为减少回调次数，此处做判断，如果和上次的百分比一致就跳过
                    if (percentage == lastProgress) {
                        return@OnProgressListener
                    }
                    lastProgress = percentage
                    val message = handlerHolder!!.obtainMessage()
                    val bundle = Bundle()
                    bundle.putString("url", url)
                    bundle.putInt("progress", percentage)
                    message.what = 2
                    message.obj = bundle
                    handlerHolder!!.sendMessage(message)
                }
            })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogin(event: EventUserLogin) {
        setBottomView()
    }
}