package cool.dingstock.post.comment

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailCommentsBean
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicSectionBean
import cool.dingstock.appbase.entity.bean.circle.CircleMentionedBean
import cool.dingstock.appbase.entity.event.circle.EventCommentCount
import cool.dingstock.appbase.entity.event.circle.EventCommentDel
import cool.dingstock.appbase.entity.event.circle.EventCommentFailed
import cool.dingstock.appbase.entity.event.circle.EventCommentSuccess
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.appbase.net.api.circle.CircleApi
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.appbase.util.LoginUtils.getCurrentUser
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.post.item.DynamicCommentItemBinder
import io.reactivex.rxjava3.core.Flowable
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


/**
 * 类名：CircleCommentBaseVM
 * 包名：cool.dingstock.post.comment
 * 创建时间：2021/12/24 3:42 下午
 * 创建人： WhenYoung
 * 描述：
 **/
abstract class CircleCommentBaseVM : BaseViewModel() {

    @Inject
    lateinit var commonApi: CommonApi

    @Inject
    lateinit var circleApi: CircleApi

    @Inject
    lateinit var calendarApi: CalendarApi

    @Inject
    lateinit var homeApi: HomeApi

    private val commentRunning = AtomicBoolean(false)

    val hideLoadMoreLiveData = MutableLiveData<Boolean>()
    val openLoadMoreLiveData = MutableLiveData<Boolean>()
    val mainBeanLiveData = MutableLiveData<CircleDynamicBean>()

    //需要单独实现
    val commentDataLiveData = MutableLiveData<ArrayList<*>>()
    val loadMoreData = MutableLiveData<ArrayList<*>>()

    var kusoEmojiId: String? = null

    /**
     * 评论所依赖的主ID（mainId or productId）
     */
    var mainId: String = ""

    /**
     * 二级评论里面的目标ID，如果有mentionedId 就使用mentionedId，如果没有就使用 targetId
     */
    var targetId: String = ""


    var mainBean: CircleDynamicBean? = null


    /**
     * 举报
     *
     * @param commentId 评论
     */
    open fun communityPostCommentReport(commentId: String?) {
        val flowable: Flowable<BaseResult<String>> = commentReport(commentId)
        if (flowable != null) {
            flowable.subscribe({ (err) ->
                if (!err) {
                    shortToast("举报成功")
                } else {
                }
            }) { err: Throwable? -> }
        }
    }


    /**
     * 删除评论
     *
     * @param commentId  评论Id
     * @param sectionPos pos
     */
    open fun communityPostCommentDelete(
        commentId: String?,
        onePos: Int,
        sectionPos: Int,
        mainSectionPos: Int
    ) {
        val flowable = commentDelete(commentId)
        if (flowable != null) {
            flowable.subscribe({ (err, _, _, msg) ->
                if (!err) {
                    EventBus.getDefault().post(
                        EventCommentDel(
                            getCommentStyle(),
                            mainId,
                            commentId!!,
                            onePos,
                            sectionPos,
                            mainSectionPos
                        )
                    )
                    //更新评论 数量
                    val dynamicBean: CircleDynamicBean = mainBean ?: return@subscribe
                    if (dynamicBean.commentCount > 0) {
                        dynamicBean.commentCount = dynamicBean.commentCount - 1
                    }
                    var id = dynamicBean.id
                    if (id == null) {
                        id = dynamicBean.objectId
                    }
                    EventBus.getDefault().post(EventCommentCount(false, id, dynamicBean))
                } else {
                    shortToast(msg)
                }
            }) { err: Throwable ->
                shortToast(err.message)
            }
        }
    }

    /**
     * 评论
     *
     * @param content     内容
     * @param mentionedId 被评论的ID
     */
    open fun communityPostCommentSubmit(
        content: String?,
        mainId: String?,
        mentionedId: String?,
        selImg: File?,
        selImgThumbnail: File?
    ) {
        if (commentRunning.get()) {
            Logger.w("communityPostCommentSubmit  but is running so do nothing")
            shortToast("正在发送")
            return
        }
        commentRunning.set(true)
        val currentUser = getCurrentUser()
        if (currentUser == null) {
            router(AccountConstant.Uri.INDEX)
            return
        }
        if (selImg != null && selImg.exists()) {
            val files = ArrayList<File>()
            files.add(selImg)
            if (selImgThumbnail != null && selImgThumbnail.exists()) {
                files.add(selImgThumbnail)
            }
            val disposable = commonApi.uploadImgList("comment", files)
                .flatMap { imgUrls ->
                    val img = imgUrls[0]
                    var staticImgUrl: String? = null
                    if (imgUrls.size > 1) {
                        staticImgUrl = imgUrls[1]
                    }
                    postCommentRx(mentionedId, content, img, staticImgUrl, kusoEmojiId)
                }
                .subscribe({ res: BaseResult<CircleDynamicDetailCommentsBean> ->
                    commentRunning.set(false)
                    if (!res.err && res.res != null) {
                        var mentioned = res.res!!.mentioned
                        if (mentioned == null) {
                            mentioned = CircleMentionedBean(null, mentionedId)
                            res.res!!.mentioned = mentioned
                        } else {
                            mentioned.mentionedId = mentionedId
                        }
                        res.res!!.mainId = mainId
                        EventBus.getDefault().post(
                            EventCommentSuccess(
                                getCommentStyle(),
                                res.res!!
                            )
                        )
                        //更新评论 数量
                        val dynamicBean: CircleDynamicBean = mainBean ?: return@subscribe
                        dynamicBean.commentCount = dynamicBean.commentCount + 1
                        var id = dynamicBean.id
                        if (id == null) {
                            id = dynamicBean.objectId
                        }
                        EventBus.getDefault().post(EventCommentCount(true, id, dynamicBean))
                        trackEmoji()
                    } else {
                        shortToast(res.msg)
                        EventBus.getDefault().post(EventCommentFailed())
                    }
                }) { error: Throwable ->
                    commentRunning.set(false)
                    shortToast(error.message)
                    EventBus.getDefault().post(EventCommentFailed())
                }
            addDisposable(disposable)
        } else {
            val subscribe = postCommentRx(mentionedId, content, null, null, kusoEmojiId)
                .subscribe({ res: BaseResult<CircleDynamicDetailCommentsBean> ->
                    commentRunning.set(false)
                    if (!res.err) {
                        var mentioned = res.res!!.mentioned
                        if (mentioned == null) {
                            mentioned = CircleMentionedBean(null, mentionedId)
                            res.res!!.mentioned = mentioned
                        } else {
                            mentioned!!.mentionedId = mentionedId
                        }
                        res.res!!.mainId = mainId
                        EventBus.getDefault()
                            .post(EventCommentSuccess(getCommentStyle(), res.res!!))
                        //更新评论 数量
                        val dynamicBean: CircleDynamicBean = mainBean ?: return@subscribe
                        dynamicBean.commentCount = dynamicBean.commentCount + 1
                        var id = dynamicBean.id
                        if (id == null) {
                            id = dynamicBean.objectId
                        }
                        EventBus.getDefault().post(EventCommentCount(true, id, dynamicBean))
                    } else {
                        shortToast(res.msg)
                        EventBus.getDefault().post(EventCommentFailed())
                    }
                }) { error: Throwable ->
                    commentRunning.set(false)
                    shortToast(error.message)
                    EventBus.getDefault().post(EventCommentFailed())
                }
            addDisposable(subscribe)
        }
    }

    private fun trackEmoji() {
        if (!kusoEmojiId.isNullOrEmpty()) {
            circleApi.track("community_emoji_use", null, kusoEmojiId)
                .subscribe()
        }
    }

    protected abstract fun postCommentRx(
        mentionedId: String?,
        content: String?,
        dynamicImgUrl: String?,
        staticImgUrl: String?,
        dcEmojiId: String?
    ): Flowable<BaseResult<CircleDynamicDetailCommentsBean>>


    protected abstract fun commentDelete(commentId: String?): Flowable<BaseResult<String>>

    protected abstract fun commentReport(commentId: String?): Flowable<BaseResult<String>>

    abstract fun loadMoreComments()

    /**
     * 子评论的 style
     *
     * @return 风格
     */
    @DynamicCommentItemBinder.Style
    abstract fun getCommentStyle(): String

}