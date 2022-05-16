package cool.dingstock.monitor.ui.regoin.comment

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailCommentsBean
import cool.dingstock.appbase.entity.bean.circle.ProductCommentEntity
import cool.dingstock.appbase.entity.bean.home.HomeProduct
import cool.dingstock.appbase.entity.bean.home.HomeProductSketch
import cool.dingstock.appbase.net.api.calendar.CalendarHelper.productCommentPage
import cool.dingstock.appbase.net.api.calendar.CalendarHelper.productComments
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.monitor.dagger.MonitorApiHelper
import cool.dingstock.post.comment.CircleCommentBaseVM
import cool.dingstock.post.dagger.PostApiHelper
import cool.dingstock.post.item.DynamicCommentItemBinder
import io.reactivex.rxjava3.core.Flowable


/**
 * 类名：HomeRaffleCommentVM
 * 包名：cool.dingstock.monitor.ui.regoin.comment
 * 创建时间：2021/12/25 4:56 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class HomeRaffleCommentVM : CircleCommentBaseVM() {

    private var nextKey: Long = 0L
    var productBean: HomeProductSketch? = null
    var commentType: String = ""

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    override fun postCommentRx(
        mentionedId: String?,
        content: String?,
        dynamicImgUrl: String?,
        staticImgUrl: String?,
        dcEmojiId: String?
    ): Flowable<BaseResult<CircleDynamicDetailCommentsBean>> {
        return homeApi.commentProduct(
            mainId,
            mentionedId,
            commentType,
            content,
            dynamicImgUrl,
            staticImgUrl,
            dcEmojiId
        )
    }

    override fun commentDelete(commentId: String?): Flowable<BaseResult<String>> {
        return calendarApi.productCommentDelete(commentId!!)
    }

    override fun commentReport(commentId: String?): Flowable<BaseResult<String>> {
        return calendarApi.productPostCommentReport(commentId)
    }


    fun productComments() {
        val subscribe = productComments(mainId)
            .subscribe({ res: BaseResult<ProductCommentEntity> ->
                if (!res.err && res.res != null) {
                    val data = res.res
                    nextKey = data?.nextKey ?: -1
                    postStateSuccess()
                    openLoadMoreLiveData.postValue(true)
                    commentDataLiveData.postValue(data?.sections ?: arrayListOf<Any>())
                } else {
                    postStateError(res.msg)
                }
            }, { err: Throwable ->
                postStateError(err.message)
            })
        addDisposable(subscribe)
    }

    override fun loadMoreComments() {
        productCommentPage(mainId, nextKey, null)
            .subscribe({ res ->
                if (!res.err && res.res != null) {
                    val data = res.res
                    nextKey = data?.nextKey ?: -1
                    if (CollectionUtils.isEmpty(data?.data)) {
                        openLoadMoreLiveData.postValue(false)
                        return@subscribe
                    }
                    loadMoreData.postValue(data?.data ?: arrayListOf<Any>())
                } else {
                    postStateError(res.msg)
                }
            }, { err: Throwable? -> postStateError(err?.message ?: "") })
    }

    override fun getCommentStyle(): String = DynamicCommentItemBinder.Style.HOME_RAFFLE


}