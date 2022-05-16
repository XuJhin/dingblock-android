package cool.dingstock.post.comment

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import cool.dingstock.appbase.adapter.StickyDecoration
import cool.dingstock.appbase.adapter.itembinder.OnItemClickListener
import cool.dingstock.appbase.adapter.itembinder.OnItemLongClickListener
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.UIConstant
import cool.dingstock.appbase.customerview.EmptyLoadMore
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.appbase.entity.bean.common.LineEntity
import cool.dingstock.appbase.entity.bean.config.EmojiConfig
import cool.dingstock.appbase.entity.event.circle.EventCommentDel
import cool.dingstock.appbase.entity.event.circle.EventCommentFailed
import cool.dingstock.appbase.entity.event.circle.EventCommentSuccess
import cool.dingstock.appbase.imageload.GlideHelper
import cool.dingstock.appbase.imageload.GlideHelper.getGifFirstFrame
import cool.dingstock.appbase.imageload.GlideHelper.isGif
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.net.api.calendar.CalendarHelper.productPostCommentReport
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.util.ClipboardHelper.copyInfo
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.appbase.widget.KeyBoardBottomSheetDialog
import cool.dingstock.appbase.widget.TitleBar
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.appbase.widget.dialog.Action
import cool.dingstock.appbase.widget.dialog.DcBottomMenu
import cool.dingstock.appbase.widget.dialog.OnMenuClickListener
import cool.dingstock.imagepicker.ImagePicker
import cool.dingstock.imagepicker.bean.ImageItem
import cool.dingstock.imagepicker.bean.MimeType
import cool.dingstock.imagepicker.custom.CustomImgPickerPresenter
import cool.dingstock.imagepicker.views.AvatarView
import cool.dingstock.imagepre.ImagePreview
import cool.dingstock.imagepre.bean.ImageInfo
import cool.dingstock.lib_base.rv.DingLoadMore
import cool.dingstock.lib_base.util.*
import cool.dingstock.lib_base.util.FileUtilsCompat.lubanComposeFilePath
import cool.dingstock.lib_base.util.FileUtilsCompat.lubanCopyFile
import cool.dingstock.lib_base.widget.tabs.TabLayoutMediator
import cool.dingstock.post.R
import cool.dingstock.post.adapter.DynamicBinderAdapter
import cool.dingstock.post.adapter.EmojiPagerAdapter
import cool.dingstock.post.adapter.holder.DynamicCommentViewHolder
import cool.dingstock.post.databinding.CircleItemHeadCommentBinding
import cool.dingstock.post.databinding.CommentInputLayoutBinding
import cool.dingstock.post.item.DynamicCommentItemBinder
import cool.dingstock.post.item.DynamicCommentItemBinder.Style.Companion.DETAIL_MAIN
import cool.dingstock.post.item.DynamicCommentItemBinder.Style.Companion.DETAIL_SUB
import cool.dingstock.post.item.DynamicCommentItemBinder.Style.Companion.HOME_RAFFLE
import cool.dingstock.post.item.DynamicCommentItemBinder.Style.Companion.NORMAL
import cool.dingstock.post.item.DynamicCommentsHeadBinder
import cool.dingstock.post.item.LineItemBinder
import cool.dingstock.post.item.PostItemShowWhere
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


/**
 * 类名：CircleCommentBaseActivity1
 * 包名：cool.dingstock.post.comment
 * 创建时间：2021/12/24 3:42 下午
 * 创建人： WhenYoung
 * 描述：
 **/
abstract class CircleCommentBaseActivity<VM : CircleCommentBaseVM, VB : ViewBinding> :
    VMBindingActivity<VM, VB>() {

    protected var dialogFull = true
    protected var showCommentOtherBtn = true

    private val commentBinding by lazy {
        CommentInputLayoutBinding.inflate(layoutInflater)
    }

    protected val commentDialog by lazy {
        initCommentBottomSheet(dialogFull)
    }

    protected lateinit var rvAdapter: DynamicBinderAdapter
    protected lateinit var commentsHeadBinder: DynamicCommentsHeadBinder
    protected lateinit var commentItemBinder: DynamicCommentItemBinder
    private lateinit var lineItemBinder: LineItemBinder

    private val headers = SparseArray<String>()
    private var position: Int? = null

    /**
     * 被评论的id
     */
    private var mentionedId: String? = null


    /**
     * 如果是二级评论就是 被依赖的评论的position
     */
    protected var onePos = 0

    /**
     * 最后一个HEAD的字符串
     */
    protected var lastHeadStr: String? = null

    /**
     * 最后一个sectionKey
     */
    protected var lastSectionKey = 0

    /**
     * 最后一个分组Header是否被添加
     */
    protected var lastSectionIsAdd = false

    /**
     * 被评论的sectionKEY
     */
    private var mentionedSectionKey = -1

    /**
     * 评论的图片
     */
    private var seledImgFile: File? = null

    /**
     * 评论的图片 GIF的第一帧
     */
    private var seledImgFirstFrameFile: File? = null

    /**
     * 编辑内容
     */
    private var editContent: String? = null

    private var commentIsEmpty = true

    private var isOpenEmoji = false

    protected var showHeader = true

    override fun moduleTag(): String = ModuleConstant.CIRCLE

    override fun finish() {
        //删除选中的图片
        deleteFile()
        super.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //删除选中的图片
        deleteFile()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        viewModel.mainId = uri.getQueryParameter(CircleConstant.UriParams.ID) ?: ""
        val onePosStr = uri.getQueryParameter(CircleConstant.UriParams.ONE_POS)
        onePos = try {
            onePosStr!!.toInt()
        } catch (e: Exception) {
            -1
        }
        if (TextUtils.isEmpty(viewModel.mainId)) {
            Logger.w("onCreate but mainId = null ")
        }
        initRV()
        rvAdapter.updateShowWhere(PostItemShowWhere.Detail)
        initObserver()
    }

    private fun initObserver() {
        viewModel.apply {
            hideLoadMoreLiveData.observe(this@CircleCommentBaseActivity) {
                rvAdapter.loadMoreModule.loadMoreComplete()
            }
            openLoadMoreLiveData.observe(this@CircleCommentBaseActivity) {
                if (it) {
                    openLoadMore()
                } else {
                    closeLoadMore()
                }
            }
            mainBeanLiveData.observe(this@CircleCommentBaseActivity) {
                setMainBean(it)
            }
            commentDataLiveData.observe(this@CircleCommentBaseActivity) {
                setCommentsData(it)
            }
            loadMoreData.observe(this@CircleCommentBaseActivity) {
                loadMore(it)
            }
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initListeners() {
        commentItemBinder.onItemLongCLickListener = object : OnItemLongClickListener {
            override fun onItemClick(
                adapter: BaseBinderAdapter,
                holder: BaseViewHolder,
                position: Int
            ): Boolean {
                val item = adapter.getItem(position)
                if (holder is DynamicCommentViewHolder) {
                    if (item is CircleDynamicDetailCommentsBean) {
                        var mainSectionPos = -1
                        if (viewModel.getCommentStyle() == DETAIL_SUB) mainSectionPos = position - 3
                        showMenu(commentItemBinder.mStyle, onePos, position, mainSectionPos, item)
                    }
                }
                return true
            }
        }
        commentItemBinder.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseBinderAdapter,
                holder: BaseViewHolder,
                position: Int
            ) {
                val item = adapter.getItem(position)
                if (holder is DynamicCommentViewHolder) {
                    if (item is CircleDynamicDetailCommentsBean) {
                        replyUser(holder.contentTxt, item.sectionKey, position, item)
                    }
                }
            }
        }
        getEditLayer()?.setOnClickListener {
            commentDialog.show()
            closeEmoji()
        }
        getImgSel()?.setOnClickListener { //打开图片选择器
            commentDialog.show()
            selectPic()
            closeEmoji()
        }
        getEmojiSel()?.setOnClickListener {
            isOpenEmoji = true
            commentDialog.show()
            commentBinding.selectEmojiIv.setImageResource(R.drawable.ic_open_keyboard)
            commentBinding.emojiGroup.isVisible = true
        }
        getRootView().viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = getRootView().rootView.height - getRootView().height
            Log.i("commentBinding", "root Y: ${commentBinding.root.y}")
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                if (heightDiff > SizeUtils.dp2px(200f)) { // if more than 200 dp, it's probably a keyboard...
                    //打开
                    onKeyboardShow()
                } else {
                    //关闭cc
                    onKeyboardHide()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (commentDialog.isShowing && !commentBinding.emojiGroup.isVisible) {
            dialogShowKeyBoard()
        }
    }

    override fun onDestroy() {
        //删除选中的图片
        deleteFile()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun initCommentBottomSheet(fullScreen: Boolean): KeyBoardBottomSheetDialog {
        val dialog = if (fullScreen)
            KeyBoardBottomSheetDialog(this, R.style.BottomSheetDialog) else
            KeyBoardBottomSheetDialog(this, R.style.BottomSheetDialogTransparent)

        with(commentBinding) {
            otherBtnGroup.isVisible = showCommentOtherBtn
            selectPicIv.setOnClickListener { //打开图片选择器
                selectPic()
            }
            selectEmojiIv.setOnShakeClickListener {
                if (emojiGroup.isVisible) {
                    closeEmoji()
                    dialogShowKeyBoard()
                } else {
                    openEmoji()
                }
            }
            imgIv.setOnClickListener(View.OnClickListener {
                val imageInfo = ImageInfo()
                if (seledImgFile == null || !seledImgFile!!.exists()) {
                    return@OnClickListener
                }
                imageInfo.originUrl = seledImgFile!!.absolutePath
                imageInfo.thumbnailUrl = seledImgFile!!.absolutePath
                val list = ArrayList<ImageInfo>()
                list.add(imageInfo)
                routeToImagePre(list)
            })
            imgCancel.setOnClickListener { //清除选中的图片
                deleteFile()
                seledImgFile = null
                seledImgFirstFrameFile = null
                imgFra.visibility = View.GONE
                imgIv.setImageResource(R.drawable.common_default_shape)
            }
            sendBtn.setOnClickListener {
                if (null == getAccount()) {
                    return@setOnClickListener
                }
                if (TextUtils.isEmpty(editContent) && (seledImgFile == null || !seledImgFile!!.exists())) {
                    return@setOnClickListener
                }
                dialog.hideKeyBoard()
                showLoadingDialog("发送中…")
                viewModel.communityPostCommentSubmit(
                    editContent,
                    viewModel.mainId,
                    mentionedId,
                    seledImgFile,
                    seledImgFirstFrameFile
                )
            }
            commentEt.doOnTextChanged { text, _, _, _ ->
                editContent = text.toString().trim { it <= ' ' }
            }
            commentEt.setOnClickListener {
                closeEmoji()
            }
            commentEt.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    closeEmoji()
                }
            }

            val list = MobileHelper.getInstance()?.emojiConfigs ?: arrayListOf(
                EmojiConfig("", "系统内置", "emoji", null)
            )
            val adapter = EmojiPagerAdapter(this@CircleCommentBaseActivity, list,
                selectEmoji = {
                    val text = "${commentEt.text}$it"
                    commentEt.setText(text)
                    commentEt.setSelection(text.length)
                },
                selectKusoEmoji = {
                    viewModel.kusoEmojiId = it.id
                    GlideHelper.getImageFile(this@CircleCommentBaseActivity, it.imageUrl).subscribe ({ res ->
                        setImgFile(res)
                    }, { e ->
                        e.printStackTrace()
                    })
                    getGifFirstFrame(this@CircleCommentBaseActivity, it.imageUrl).subscribe { res ->
                        seledImgFirstFrameFile = res
                    }
                }
            )
            emojiVp.adapter = adapter
            TabLayoutMediator(emojiTabLayout, emojiVp) { tab, position ->
                list[position].let {
                    if (it.type == "emoji") {
                        tab.setIcon(R.drawable.ic_select_emoji)
                    } else {
                        Glide.with(this@CircleCommentBaseActivity).load(it.icon).into(object: CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                tab.icon = resource
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                    }
                }
            }.attach()
        }
        dialog.apply {
            window?.let {
                StatusBarUtil.transparentStatus(it)
                StatusBarUtil.setNavigationBarColor(it, getCompatColor(R.color.white))
            }
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_SETTLING) {
                        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
            setContentView(commentBinding.root)
            setOnShowListener {
                if (!isOpenEmoji) {
                    dialogShowKeyBoard()
                } else {
                    dialogHideKeyBoard()
                    isOpenEmoji = false
                }
            }
            setOnDismissListener {
                if (StringUtils.isEmpty(editContent) && (seledImgFile == null || !seledImgFile!!.exists())) {
                    disableMentionedTxt()
                }
            }
        }
        return dialog
    }

    private fun dialogHideKeyBoard() {
        commentBinding.root.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }
        commentDialog.hideKeyBoard()
    }

    private fun dialogShowKeyBoard() {
        commentBinding.commentEt.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }
        commentDialog.showKeyBoard()
    }

    private fun openEmoji() {
        commentBinding.selectEmojiIv.setImageResource(R.drawable.ic_open_keyboard)
        commentDialog.hideKeyBoard()
        commentBinding.root.postDelayed({
            commentBinding.emojiGroup.isVisible = true
        }, 300)
    }

    private fun closeEmoji() {
        commentBinding.selectEmojiIv.setImageResource(R.drawable.ic_select_emoji)
        commentBinding.emojiGroup.isVisible = false
    }

    open fun initRV() {
        rvAdapter = DynamicBinderAdapter(ArrayList())
        commentsHeadBinder = DynamicCommentsHeadBinder(false)
        commentItemBinder = DynamicCommentItemBinder()
        lineItemBinder = LineItemBinder()
        rvAdapter.registerDynamicReload(lifecycle)
        rvAdapter.addItemBinder(
            CircleDynamicDetailCommentsBean::class.java,
            commentItemBinder
        )
        rvAdapter.addItemBinder(
            DynamicCommentHeaderBean::class.java,
            commentsHeadBinder
        )
        rvAdapter.addItemBinder(LineEntity::class.java, lineItemBinder)
        rvAdapter.setEmptyView(CommonEmptyView(this, null, noBg = true, fullParent = false))
        getRv().layoutManager = LinearLayoutManager(this)
        getRv().adapter = rvAdapter
        if (needLoadMore()) {
            rvAdapter.loadMoreModule.isEnableLoadMore = true
            rvAdapter.loadMoreModule.loadMoreComplete()
        }
        setHeaderAlpha(0f)
        getRv().addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Logger.e("onScrolled,", "" + getRv().computeVerticalScrollOffset())
                val titleTotalOffSet = SizeUtils.dp2px(70f).toFloat()
                val headStartOffSet = SizeUtils.dp2px(30f).toFloat()
                val headEndOffSet = SizeUtils.dp2px(100f).toFloat()
                val headETotalOffSet = headEndOffSet - headStartOffSet
                if (getHeadView() != null && getTitleBar() != null) {
                    var titleOffSet = titleTotalOffSet - getRv().computeVerticalScrollOffset()
                    if (titleOffSet < 0) {
                        titleOffSet = 0f
                    }
                    getTitleBar()!!.setTitleAlpha(titleOffSet / titleTotalOffSet)
                    var headOffSet =
                        titleTotalOffSet - getRv().computeVerticalScrollOffset() + headStartOffSet
                    if (headOffSet < 0) {
                        headOffSet = 0f
                    }
                    if (headOffSet > headETotalOffSet) {
                        headOffSet = headETotalOffSet
                    }
                    setHeaderAlpha((headETotalOffSet - headOffSet) / headETotalOffSet)
                }
            }
        })
    }

    private fun setHeaderAlpha(alpha: Float) {
        (getHeadGroup() as? ViewGroup)?.let {
            it.children.forEach { child ->
                if (child is AvatarView) {
                    child.setDrawableAlpha(alpha)
                } else {
                    child.alpha = alpha
                }
            }
        }
    }

    open fun getHeadView(): AvatarView? {
        return null
    }

    open fun getHeadGroup(): View? {
        return null
    }

    /**
     * 是否有分页
     *
     * @return 分页flag
     */
    protected abstract fun needLoadMore(): Boolean

    protected abstract fun getRv(): RecyclerView

    protected open fun getTitleBar(): TitleBar? {
        return null
    }

    open fun onKeyboardShow() {

    }

    open fun onKeyboardHide() {
//        if (commentDialog.isShowing) commentDialog.dismiss()
    }


    protected open fun calculateStatusColor(color: Int, alpha: Int): Int {
        if (alpha == 0) {
            return color
        }
        val a = 1 - alpha / 255f
        var red = color shr 16 and 0xff
        var green = color shr 8 and 0xff
        var blue = color and 0xff
        red = (red * a + 0.5).toInt()
        green = (green * a + 0.5).toInt()
        blue = (blue * a + 0.5).toInt()
        return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
    }

    open fun getHeadVerified(): View? {
        return null
    }

    protected open fun getRootView(): View {
        return window.decorView
    }

    open fun getMainBean(): CircleDynamicBean? {
        return viewModel.mainBean
    }

    open fun setMainBean(mainBean: CircleDynamicBean?) {
        commentItemBinder.mainBean = mainBean
        viewModel.mainBean = mainBean
    }

    open fun getMainId(): String {
        return viewModel.mainId
    }

    protected abstract fun getEditLayer(): View?

    protected abstract fun getImgSel(): View?

    protected abstract fun getEmojiSel(): View?

    open fun getTargetId(): String {
        return viewModel.targetId
    }


    protected open fun selectPic() {
        closeEmoji()
        ImagePicker.withMulti(CustomImgPickerPresenter()) //指定presenter
            //设置选择的最大数
            .setMaxCount(1) //设置列数
            .setColumnCount(4) //设置要加载的文件类型，可指定单一类型
            .mimeTypes(MimeType.ofImage()) //设置需要过滤掉加载的文件类型
            .showCamera(true) //显示拍照
            .pick(
                this
            ) { items -> handleImage(items) }
    }

    private fun handleImage(items: ArrayList<ImageItem>) {
        if (items.isEmpty()) {
            return
        }
        val filePath = items[0].getPath()
        if (TextUtils.isEmpty(filePath)) {
            return
        }
        if (isGif(filePath)) {
            lubanCopyFile(this, items[0].getPath(), items[0].uriPath)
                .subscribe({ res: File ->
                    //选择图片默认弹起软件盘
                    dialogShowKeyBoard()
                    hideLoadingDialog()
                    composeGif(res)
                }) { hideLoadingDialog() }
            return
        }
        showLoadingDialog()
        lubanComposeFilePath(this, items[0].getPath(), items[0].uriPath)
            .subscribe({ res: File ->
                //选择图片默认弹起软件盘
                dialogShowKeyBoard()
                hideLoadingDialog()
                setImgFile(res)
            }) { hideLoadingDialog() }
    }

    private fun setImgFile(res: File) {
        commentBinding.imgFra.visibility = View.VISIBLE
        deleteFile()
        seledImgFile = res
        Glide.with(this).load(res.absolutePath)
            .into(commentBinding.imgIv)
    }

    private fun composeGif(res: File) {
        seledImgFile = res
        if (FileUtils.getFileSize(seledImgFile) > 1024 * 1024 * 5) {
            ToastUtil.getInstance().makeTextAndShow(this, "选择文件过大", Toast.LENGTH_SHORT)
            seledImgFile = null
            return
        }
        commentBinding.imgFra.visibility = View.VISIBLE
        Glide.with(this).load(res.absolutePath)
            .into(commentBinding.imgIv)
        getGifFirstFrame(seledImgFile!!)
            .subscribe({ res: File? ->
                seledImgFirstFrameFile = res
            }, { })
    }

    /**
     * 初次加载 添加列表
     *
     * @param sectionList 评论列表
     */
    open fun setCommentsData(sectionList: ArrayList<*>) {
        position = when(commentItemBinder.mStyle) {
            NORMAL, DETAIL_MAIN -> 2
            DETAIL_SUB -> 3
            HOME_RAFFLE -> 1
            else -> null
        }

        val arrayList = arrayListOf<Any>()
        for (index in sectionList.indices) {
            val o = sectionList[index]!!
            if (o is CircleDynamicSectionBean) {
                val header = o.header
                var sectionKey = sectionList.indexOf(o)
                if (haveHeadSection()) {
                    sectionKey += 1
                }
                val commentList = o.comments
                val commentListNew: MutableList<CircleDynamicDetailCommentsBean> = ArrayList()
                if (commentList == null) {
                    continue
                }
                for (bean in commentList) { //全部设置sectionKey
                    if (bean != null) {
                        bean.sectionKey = sectionKey
                        commentListNew.add(bean)
                        position?.let {
                            headers.put(it, header)
                            position = it + 1
                        }
                    }
                }
                if (index == sectionList.size - 1) {
                    lastSectionKey = sectionKey
                    lastHeadStr = header
                }
                if (TextUtils.isEmpty(header) || CollectionUtils.isEmpty(commentList)) {
                    continue
                }
                val headerBean = DynamicCommentHeaderBean(header!!, sectionKey)
                //添加分组的头
                if (index == sectionList.size - 1) {
                    lastSectionIsAdd = true
                }
                arrayList.add(headerBean)
                //添加这个分组的数据
                arrayList.addAll(commentListNew)

            } else {
                arrayList.add(o)
            }
        }
        commentIsEmpty = arrayList.isEmpty()
        if (arrayList.isEmpty() && showEmptyViewWithComment()) {
            rvAdapter.loadMoreModule.loadMoreView = EmptyLoadMore()
            val headerBean = DynamicCommentHeaderBean(lastHeadStr?:"", lastSectionKey)
            lastSectionIsAdd = true
            arrayList.add(headerBean)
            position?.let {
                headers.put(it, lastHeadStr)
            }
            val commonEmptyView = CommonEmptyView(this).apply { alpha = 0.4f }
            rvAdapter.addFooterView(commonEmptyView)
        }
        addHeader()
        rvAdapter.setList(arrayList)
    }

    private fun addHeader() {
        if (showHeader && getRv().itemDecorationCount <= 0) {
            getRv().addItemDecoration(StickyDecoration(
                ContextCompat.getColor(this, cool.dingstock.appbase.R.color.white),
                ContextCompat.getColor(this, cool.dingstock.appbase.R.color.color_text_black1)
            ) {
                return@StickyDecoration headers[it]
            })
        }
    }

    protected open fun haveHeadSection(): Boolean {
        return true
    }

    open fun loadMore(sectionList: ArrayList<*>?) {
        if (sectionList == null) {
            return
        }
        for (bean in sectionList) {
            if (bean is CircleDynamicDetailCommentsBean) {
                bean.sectionKey = lastSectionKey
                position?.let {
                    headers.put(it, lastHeadStr)
                    position = it + 1
                }
            }
        }
        rvAdapter.addData(sectionList)
        rvAdapter.loadMoreModule.loadMoreComplete()
    }

    private fun replyUser(
        view: View,
        sectionKey: Int,
        sectionPos: Int,
        commentsBean: CircleDynamicDetailCommentsBean
    ) {
        var isMe = false
        var commentUserNickName: String? = null
        val commentUser = commentsBean.user
        val user = AccountHelper.getInstance().user
        if (null != commentUser) {
            if (null != user) {
                isMe = user.id == commentUser.objectId
            }
            commentUserNickName = commentUser.nickName
        }
        replyUser(sectionKey, commentsBean, commentUserNickName)
    }

    /**
     * 展示悬浮菜单
     *
     * @param sectionKey   节
     * @param sectionPos   pos
     * @param commentsBean 评论实体
     */
    private fun showMenu(
        style: String,
        sectionKey: Int,
        sectionPos: Int,
        mainSectionPos: Int,
        commentsBean: CircleDynamicDetailCommentsBean
    ) {
        var isMe = false
        var isMineDynamic = false
        var commentUserNickName: String? = null
        val commentUser = commentsBean.user
        val user = AccountHelper.getInstance().user
        if (null != commentUser) {
            if (null != user) {
                isMe = TextUtils.equals(user.id, commentUser.objectId)
                viewModel.mainBean?.user?.let {
                    isMineDynamic = TextUtils.equals(it.objectId, user.id)
                }
            }
            commentUserNickName = commentUser.nickName
        }
        val popupMenuView = DcBottomMenu.Builder()
        val optionMenuList = ArrayList<Action>()
        if (!isMe) {
            optionMenuList.add(Action("复制", UIConstant.MenuId.COPY))
            optionMenuList.add(Action("举报", UIConstant.MenuId.REPORT))
        }
        if (isMe || isMineDynamic) {
            optionMenuList.add(Action("删除", UIConstant.MenuId.DEL))
        }
        val finalCommentUserNickName = commentUserNickName
        popupMenuView.show(supportFragmentManager, optionMenuList, object : OnMenuClickListener {
            override fun onItemClick(index: Int, action: Action, bottomMenu: DcBottomMenu) {
                when (action.id) {
                    UIConstant.MenuId.DEL -> CommonDialog.Builder(
                        context
                    )
                        .content("确定要删除该评论？删除后无法恢复")
                        .confirmTxt("删除")
                        .cancelTxt("取消")
                        .onConfirmClick {
                            viewModel.communityPostCommentDelete(
                                commentsBean.objectId,
                                sectionKey,
                                sectionPos,
                                mainSectionPos
                            )
                            bottomMenu.dismissAllowingStateLoss()
                        }
                        .builder()
                        .show()
                    UIConstant.MenuId.COPY -> {
                        copyInfo(commentsBean.content)
                        showToastShort("已复制")
                        bottomMenu.dismissAllowingStateLoss()
                    }
                    UIConstant.MenuId.REPORT -> {
                        bottomMenu.dismissAllowingStateLoss()
                        if (HOME_RAFFLE.equals(style, ignoreCase = true)) {
                            productPostCommentReport(commentsBean.objectId)
                                .subscribe(
                                    { res ->
                                        if (!res.err) {
                                            showSuccessDialog("举报成功")
                                        }
                                    }, { })
                        } else {
                            viewModel.communityPostCommentReport(commentsBean.objectId)
                        }
                    }
                    UIConstant.MenuId.REPLY -> {
                        bottomMenu.dismissAllowingStateLoss()
                        replyUser(sectionKey, commentsBean, finalCommentUserNickName)
                    }
                    else -> {}
                }
            }
        })
    }

    /**
     * 回复用户
     *
     * @param sectionKey
     * @param commentsBean
     * @param finalCommentUserNickName
     */
    private fun replyUser(
        sectionKey: Int,
        commentsBean: CircleDynamicDetailCommentsBean,
        finalCommentUserNickName: String?
    ) {
        if (TextUtils.isEmpty(finalCommentUserNickName)) {
            return
        }
        commentDialog.show()
        closeEmoji()
        mentionedId = commentsBean.objectId
        mentionedSectionKey = sectionKey
        commentBinding.commentEt.hint = "回复 $finalCommentUserNickName"
    }

    private val tempMentioned: CircleMentionedBean? = null

    /**
     * 评论成功
     *
     * @param data 评论成功后服务端返回的评论
     */
    open fun onCommentSuccess(data: CircleDynamicDetailCommentsBean?) {
        hideLoadingDialog()
        /*val list: List<Any> = rvAdapter.getDataList()
        if (list.isNotEmpty()) {
            val o0 = list[0]
            if (o0 is CircleDynamicBean) {
                if (o0.video != null || o0.webpageLink != null) {
                    UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_VideoDynamic_Interactive, "name", "发送评论");
                }
            }
        }*/
        if (null == data) {
            resetEdit()
            return
        }
        val dataList: List<Any> = rvAdapter.getDataList()
        if (!lastSectionIsAdd) {
            lastSectionIsAdd = true
            rvAdapter.getDataList().add(DynamicCommentHeaderBean(lastHeadStr!!, lastSectionKey))
            position?.let {
                headers.put(it, lastHeadStr)
            }
            addHeader()
        }
        rvAdapter.loadMoreModule.loadMoreView = DingLoadMore()
        rvAdapter.removeAllFooterView()

        //如果是在二级评论页面 直接在最上面插入一条评论
        if (viewModel.getCommentStyle() == DETAIL_SUB) {
            for (i in dataList.indices) {
                val o = dataList[i]
                if (o is DynamicCommentHeaderBean) {
                    val (header) = o
                    if (header == lastHeadStr) { //找到最后一个Header
                        data.sectionKey = lastSectionKey
                        rvAdapter.addData(i + 1, data)
                        break
                    }
                }
            }
        } else {
            val newItem = data.copy(
                data.favorCount, data.favored, data.content,
                data.user, data.mentioned, data.subComments,
                data.subCommentsCount, data.objectId, data.createdAt,
                data.hotComment, data.staticImg, data.dynamicImg,
                data.sectionKey, data.mainId
            )
            data.mentioned?.mentionedId?.isEmpty() != false
            if (StringUtils.isEmpty(data.mentioned?.mentionedId)) {
                if (viewModel.mainId == data.mainId) {
                    //这就是评论动态 直接在最上面插入一条评论
                    for (i in dataList.indices) {
                        val o = dataList[i]
                        if (o is DynamicCommentHeaderBean) {
                            val (header) = o
                            if (header == lastHeadStr) { //找到最后一个Header
                                data.sectionKey = lastSectionKey
                                rvAdapter.addData(i + 1, newItem)
                                position?.let { 
                                    headers.put(it, lastHeadStr)
                                    position = it + 1
                                }
                                break
                            }
                        }
                    }
                } else {
                    //这就是评论某一个评论 需要找到这个评论 并添加一条子评论，并且 置空 Mentoned
                    for (i in dataList.indices) {
                        val o = dataList[i]
                        if (o is CircleDynamicDetailCommentsBean) {
                            if (o.objectId == data.mainId) { //找到被评论的 评论
                                if (o.subComments == null) {
                                    o.subComments = ArrayList()
                                }
                                // TODO: 2020/10/10 这里让Mentoned为空。表示只是在回复评论，而不是回复评论的评论
                                newItem.mentioned = null
                                o.subComments?.add(0, newItem)
                                o.subCommentsCount = o.subCommentsCount + 1
                                rvAdapter.notifyDataItemChanged(i)
                                //todo 这里这个 评论 如果在热门评论里面，那么只会更新热门评论。而不会更新这次品论，有一个小BUG
                                break
                            }
                        }
                    }
                }
            } else {
                if (viewModel.mainId == data.mainId) {
                    //如果是评论某一个评论 需要找到这个评论 并添加一条子评论，并且 置空 Mentoned
                    for (i in dataList.indices) {
                        val o = dataList[i]
                        if (o is CircleDynamicDetailCommentsBean) {
                            if (o.objectId == data.mentioned!!.mentionedId) { //找到被评论的 评论
                                if (o.subComments == null) {
                                    o.subComments = ArrayList()
                                }
                                // TODO: 2020/10/10 这里让Mentoned为空。表示只是在回复评论，而不是回复评论的评论
                                newItem.mentioned = null
                                o.subCommentsCount = o.subCommentsCount + 1
                                o.subComments?.add(0, newItem)
                                rvAdapter.notifyDataItemChanged(i)
                                //todo 这里这个 评论 如果在热门评论里面，那么只会更新热门评论。而不会更新这次品论，有一个小BUG
                                break
                            }
                        }
                    }
                } else {
                    //这就是评论 二级评论的评论了 需要找到评论并添加一个子评论 并不置空 Mentoned
                    for (i in dataList.indices) {
                        val o = dataList[i]
                        if (o is CircleDynamicDetailCommentsBean) {
                            if (o.objectId == data.mainId) { //找到被评论的 评论
                                if (o.subComments == null) {
                                    o.subComments = ArrayList()
                                }
                                o.subComments?.add(0, newItem)
                                o.subCommentsCount = o.subCommentsCount + 1
                                rvAdapter.notifyDataItemChanged(i)
                                //todo 这里这个 评论 如果在热门评论里面，那么只会更新热门评论。而不会更新这次品论，有一个小BUG
                                break
                            }
                        }
                    }
                }
            }
        }
        resetEdit()
    }

    /**
     * 重置输入框
     */
    private fun resetEdit() {
        commentDialog.dismiss()
        deleteFile()
        disableMentionedTxt()
        commentBinding.commentEt.setText("")
        editContent = null
        commentBinding.imgFra.visibility = View.GONE
        commentBinding.imgIv.setImageResource(R.drawable.common_default_shape)
        seledImgFile = null
        seledImgFirstFrameFile = null
    }

    private fun deleteFile() {
        Log.e("deleFIle", "deleFile")
    }

    /**
     * 重置@的text
     */
    private fun disableMentionedTxt() {
        commentBinding.commentEt.hint = "说点什么吧~"
        mentionedId = null
        mentionedSectionKey = -1
    }

    /**
     * 删除成功
     */
    open fun onCommentDelSuccess(
        mainId: String,
        commentId: String?,
        onePos: Int,
        secondPos: Int,
        mainSectionPos: Int
    ) {
        if (TextUtils.isEmpty(commentId)) {
            return
        }
        try {
            if (mainId == getMainId()) {
                //在本页面删除，只需要判断onePos
                if (rvAdapter.getDataList()[secondPos] is CircleDynamicDetailCommentsBean) {
                    val objectId = (rvAdapter.getDataList()[secondPos] as? CircleDynamicDetailCommentsBean)?.objectId
                    if (TextUtils.equals(objectId, commentId)) {
                        rvAdapter.removeAt(secondPos)
                    }
                    if (rvAdapter.getDataList().size == 1 && rvAdapter.getDataList()[0] is CircleItemHeadCommentBinding) {
                        rvAdapter.removeAt(0)
                    }
                }
            } else {
                if (rvAdapter.getDataList()[onePos] is CircleDynamicDetailCommentsBean) {
                    val oneBean =
                        rvAdapter.getDataList()[onePos] as CircleDynamicDetailCommentsBean
                    if (TextUtils.equals(oneBean.objectId, mainId)) {
                        if (oneBean.subComments != null && mainSectionPos >= 0 && mainSectionPos < oneBean.subComments!!.size) {
                            if (TextUtils.equals(
                                    oneBean.subComments!![mainSectionPos].objectId,
                                    commentId
                                )
                            ) {
                                oneBean.subComments?.removeAt(mainSectionPos)
                            }
                        }
                        if (oneBean.subCommentsCount > 1) {
                            oneBean.subCommentsCount = oneBean.subCommentsCount - 1
                        }
                        rvAdapter.notifyDataItemChanged(onePos)
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 删除失败
     */
    open fun onCommentDelFailed(errorMsg: String?) {
        showToastShort(errorMsg)
    }

    open fun openLoadMore() {
        rvAdapter.loadMoreModule.isEnableLoadMore = true
    }

    open fun closeLoadMore() {
        rvAdapter.loadMoreModule.loadMoreComplete()
        rvAdapter.loadMoreModule.loadMoreEnd(false)
    }

    private fun routeToImagePre(list: ArrayList<ImageInfo>) {
        if (list.isEmpty()) {
            return
        }
        ImagePreview.getInstance()
            .setContext(this)
            .setIndex(0)
            .setEnableDragClose(true)
            .setFolderName("DingChao")
            .setShowCloseButton(true)
            .setLoadStrategy(ImagePreview.LoadStrategy.NetworkAuto)
            .setErrorPlaceHolder(R.drawable.img_load)
            .setShowDownButton(false)
            .setImageInfoList(list)
            .start()
    }

    open fun setHeadIv(avatar: String?) {
        if (getHeadView() != null) {
            getHeadView()!!.setAvatarUrl(avatar)
        }
    }

    open fun setHeadIvPendant(pendant: String?) {
        if (getHeadView() != null) {
            getHeadView()!!.setPendantUrl(pendant)
        }
    }

    open fun setHeadVerified(isVerified: Boolean) {
        if (getHeadVerified() != null) {
            getHeadVerified()!!.visibility = if (isVerified) View.VISIBLE else View.GONE
        }
    }

    open fun showEmptyViewWithComment(): Boolean {
        return false
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun eventCommentDelete(event: EventCommentDel) {
        onCommentDelSuccess(
            event.mainId,
            event.commentId,
            event.onePosition,
            event.secondPosition,
            event.mainSecondPosition
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun eventCommentSuccess(event: EventCommentSuccess) {
        onCommentSuccess(event.bean)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventCommentFailed(event: EventCommentFailed) {
        hideLoadingDialog()
    }
}