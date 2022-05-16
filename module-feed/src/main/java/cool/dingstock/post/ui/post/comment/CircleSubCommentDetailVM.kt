package cool.dingstock.post.ui.post.comment

import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.CircleCommentDetailBean
import cool.dingstock.appbase.entity.bean.circle.CircleDetailLoadBean
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailCommentsBean
import cool.dingstock.appbase.entity.bean.common.LineEntity
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.post.comment.CircleCommentBaseVM
import cool.dingstock.post.dagger.PostApiHelper
import cool.dingstock.post.item.DynamicCommentItemBinder
import io.reactivex.rxjava3.core.Flowable
import java.util.*


/**
 * 类名：CircleSubCommentDetailVM
 * 包名：cool.dingstock.post.ui.post.comment
 * 创建时间：2021/12/25 11:37 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class CircleSubCommentDetailVM : CircleCommentBaseVM() {

    private var nextKey: Long? = null
    var model: String = ""
    var mainCommentBean: CircleDynamicDetailCommentsBean? = null

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }


    override fun postCommentRx(
        mentionedId: String?,
        content: String?,
        dynamicImgUrl: String?,
        staticImgUrl: String?,
        dcEmojiId: String?
    ): Flowable<BaseResult<CircleDynamicDetailCommentsBean>> {
        //这里是二级评论 判断如果没有 mentionedId那就需要使用 targetId
        var mentionedIdVar = mentionedId
        if (StringUtils.isEmpty(mentionedIdVar)) {
            mentionedIdVar = targetId
        }
        return when(model) {
            CircleConstant.Constant.Model_Product, CircleConstant.Constant.Model_Shoes -> homeApi.commentProduct(
                mainId, mentionedIdVar,null, content, dynamicImgUrl, staticImgUrl, dcEmojiId)
            /*CircleConstant.Constant.Model_Shoes -> calendarApi.commentShoes(
                mainId, mentionedIdVar, content, dynamicImgUrl, staticImgUrl)*/
            else -> circleApi.communityComment(
                mainId, mentionedIdVar, content, dynamicImgUrl, staticImgUrl, dcEmojiId)
        }
    }

    override fun commentDelete(commentId: String?): Flowable<BaseResult<String>> {
        return when(model) {
            CircleConstant.Constant.Model_Product, CircleConstant.Constant.Model_Shoes -> calendarApi.productCommentDelete(commentId!!)
//            CircleConstant.Constant.Model_Shoes -> calendarApi.shoesCommentDelete(commentId!!)
            else -> circleApi.communityCommentDelete(commentId!!)
        }
    }

    override fun commentReport(commentId: String?): Flowable<BaseResult<String>> {
        return when(model) {
            CircleConstant.Constant.Model_Product, CircleConstant.Constant.Model_Shoes -> calendarApi.productPostCommentReport(commentId)
//            CircleConstant.Constant.Model_Shoes -> calendarApi.productPostCommentReport(commentId)
            else -> circleApi.communityPostCommentReport(commentId!!)
        }
    }


    override fun loadMoreComments() {
        loadMoresecondaryComments()
    }

    override fun getCommentStyle(): String = DynamicCommentItemBinder.Style.DETAIL_SUB

    fun secondaryComments() {
        val flowable = when(model) {
            CircleConstant.Constant.Model_Product, CircleConstant.Constant.Model_Shoes -> calendarApi.productCommentDetail(mainCommentBean?.objectId ?: "")
//            CircleConstant.Constant.Model_Shoes -> calendarApi.shoesCommentDetail(mainCommentBean?.objectId ?: "")
            else -> circleApi.communityCommentDetail(mainCommentBean?.objectId ?: "")
        }
        flowable.subscribe({ res: BaseResult<CircleCommentDetailBean> ->
            if (!res.err && res.res != null) {
                val data = res.res
                if (data?.sections == null || data.sections!!.isEmpty()) {
                    openLoadMoreLiveData.postValue(false)
                }
                nextKey = data!!.nextKey
                postStateSuccess()
                val list: ArrayList<Any> = ArrayList<Any>()
                //这里需要在添加一个数据
                mainCommentBean?.let {
                    list.add(it)
                }
                list.add(LineEntity(""))
                data.sections?.let {
                    list.addAll(it)
                }
                commentDataLiveData.postValue(list)
            } else {
                postStateError(res.msg)
            }
        }) { err: Throwable? ->
            postStateError(err?.message ?: "")
        }
    }

    fun loadMoresecondaryComments() {
        val flowable: Flowable<BaseResult<CircleDetailLoadBean>> =
            if (CircleConstant.Constant.Model_Product == model) {
                calendarApi.productCommentDetailCommentPage(
                    mainCommentBean?.objectId ?: "",
                    nextKey
                )
            } else {
                circleApi.communityCommentDetailCommentPage(
                    mainCommentBean?.objectId ?: "",
                    nextKey
                )
            }
        flowable.subscribe({ res: BaseResult<CircleDetailLoadBean> ->
            val data = res.res
            if (!res.err && res.res != null) {
                if (CollectionUtils.isEmpty(data!!.comments)) {
                    openLoadMoreLiveData.postValue(false)
                } else {
                    loadMoreData.postValue(data.comments!!)
                }
                nextKey = data.nextKey
            } else {
                openLoadMoreLiveData.postValue(false)
            }
        }) {
            openLoadMoreLiveData.postValue(false)
        }
    }


}