package cool.dingstock.post.ui.post.detail

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.*
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.post.comment.CircleCommentBaseVM
import cool.dingstock.post.dagger.PostApiHelper
import cool.dingstock.post.item.DynamicCommentItemBinder
import io.reactivex.rxjava3.core.Flowable

class CircleDynamicDetailViewModel : CircleCommentBaseVM() {


    init {
        PostApiHelper.apiPostComponent.inject(this)
    }

    var page = 0
    var nextKey: Long? = null
    var isAutoComment = false
    var isFromShoes = false

    val showAntiFraudLiveData = MutableLiveData<Boolean>()
    val postDataLiveData = MutableLiveData<CircleDynamicBean?>()
    val headIvLiveData = MutableLiveData<String>()
    val headPendant = MutableLiveData<String>()
    val headVerifiedLiveData = MutableLiveData<Boolean>()
    val showCommentEditLiveData = MutableLiveData<Boolean>()
    val scroll2CommentLiveData = MutableLiveData<Boolean>()

    val followFailed = MutableLiveData<String>()

    fun wantTrading(postId: String?) {
        circleApi.wantTrading(postId ?: "").subscribe({
            Logger.d(it.toString())
        }, {
            Logger.d(it.message)
        })
    }


    override fun postCommentRx(
        mentionedId: String?,
        content: String?,
        dynamicImgUrl: String?,
        staticImgUrl: String?,
        dcEmojiId: String?
    ): Flowable<BaseResult<CircleDynamicDetailCommentsBean>> {
        //这里是一级评论
        return circleApi.communityComment(
            mainId,
            mentionedId,
            content,
            dynamicImgUrl,
            staticImgUrl,
            dcEmojiId
        )
    }

    override fun commentDelete(commentId: String?): Flowable<BaseResult<String>> {
        return circleApi.communityCommentDelete(commentId!!)
    }

    override fun commentReport(commentId: String?): Flowable<BaseResult<String>> {
        return circleApi.communityPostCommentReport(commentId!!)
    }

    override fun loadMoreComments() {
        if (nextKey == null || nextKey == 0L) {
            hideLoadMoreLiveData.postValue(true)
            return
        }
        val subscribe = circleApi.communityPostComments(
            mainId,
            nextKey!!
        )
            .subscribe({ res: BaseResult<CircleDetailLoadBean> ->
                val data = res.res
                if (!res.err && res.res != null) {
                    if (CollectionUtils.isEmpty(data!!.comments)) {
                        openLoadMoreLiveData.postValue(false)
                    } else {
                        hideLoadMoreLiveData.postValue(true)
                        loadMoreData.postValue(data.comments!!)
                    }
                    nextKey = data.nextKey
                } else {
                    page--
                    hideLoadMoreLiveData.postValue(true)
                }
            }, {
                page--
                hideLoadMoreLiveData.postValue(true)
            })
        addDisposable(subscribe)
    }

    override fun getCommentStyle(): String = DynamicCommentItemBinder.Style.NORMAL

    fun communityPostDetail() {
        openLoadMoreLiveData.postValue(true)
        nextKey = 0L
        circleApi.communityPostDetail(mainId)
            .subscribe({ res: BaseResult<CircleDynamicDetailBean> ->
                if (!res.err && res.res != null) {
                    postStateSuccess()
                    if(isFromShoes){
                        res.res?.post?.isShowInShoes = true
                    }
                    mainBean = res.res?.post
                    mainBeanLiveData.postValue(res.res?.post)
                    onLoadPostSuccess(res.res!!)
                } else {
                    postStateError(res.msg)
                }
            }, { err: Throwable ->
                showAntiFraudLiveData.postValue(false)
                postStateError(err.message)
            })
    }


    private fun onLoadPostSuccess(data: CircleDynamicDetailBean) {
        if (data.post != null && data.post!!.user != null && data.post!!.user!!.avatarUrl != null) {
            headIvLiveData.postValue(data.post!!.user!!.avatarUrl ?: "")
            headPendant.postValue(data.post!!.user!!.avatarPendantUrl ?: "")
            val isDeal = data.post!!.isDeal
            if (isDeal) {
                showAntiFraudLiveData.postValue(true)
            } else {
                showAntiFraudLiveData.postValue(false)
            }
            headVerifiedLiveData.postValue(data.post?.user?.isVerified == true)
        } else {
            headVerifiedLiveData.postValue(false)
            showAntiFraudLiveData.postValue(false)
        }
        val sections: ArrayList<CircleDynamicSectionBean> = data.sections ?: arrayListOf()
        if (CollectionUtils.isEmpty(sections)
            || CollectionUtils.isEmpty(sections[0].comments)
        ) {
            openLoadMoreLiveData.postValue(false)
            //评论数量=0 的时候 直接弹出软件盘
            if (isAutoComment) {
                showCommentEditLiveData.postValue(true)
            }
        } else {
            if (sections.isNotEmpty()) {
                for (sectionBean in sections) {
                    if (sectionBean.nextKey != 0L) {
                        nextKey = sectionBean.nextKey
                    }
                }
            }
            openLoadMoreLiveData.postValue(true)
            if (isAutoComment) {
                scroll2CommentLiveData.postValue(true)
            }
        }
        commentDataLiveData.postValue(arrayListOf<Any>().apply { addAll(sections) })
        if (data.post != null) {
            data.post!!.hideOverLay = true
            if (data.post!!.user != null) {
                data.post!!.user!!.followed = data.isMutual
            }
            data.post!!.showFollow = !data.isMutual
            data.post!!.showWhere = ShowWhere.DETAIL
        }
        postDataLiveData.postValue(data.post)
    }


}
