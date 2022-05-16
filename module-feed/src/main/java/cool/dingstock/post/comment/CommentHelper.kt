package cool.dingstock.post.comment

import android.text.TextUtils
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailCommentsBean
import cool.dingstock.appbase.entity.event.circle.EventCommentDel
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.post.databinding.CircleItemHeadCommentBinding
import java.lang.Exception
import java.util.*


/**
 * 类名：CommentHeper
 * 包名：cool.dingstock.post.comment
 * 创建时间：2022/1/10 11:48 上午
 * 创建人： WhenYoung
 * 描述：
 **/
object CommentHelper {
    fun onDynamicDetailsCommentSuccess(
        mainId: String,
        rvAdapter: DcBaseBinderAdapter,
        style: String,
        data: CircleDynamicDetailCommentsBean
    ) {
        val dataList = rvAdapter.getDataList()
        val newItem = data.copy(
            data.favorCount, data.favored, data.content,
            data.user, data.mentioned, data.subComments,
            data.subCommentsCount, data.objectId, data.createdAt,
            data.hotComment, data.staticImg, data.dynamicImg,
            data.sectionKey, data.mainId
        )
        data.mentioned?.mentionedId?.isEmpty() != false
        if (StringUtils.isEmpty(data.mentioned?.mentionedId)) {
            if (mainId == data.mainId) {
                //这就是评论动态主体暂时不处理
//                rvAdapter.addData(0, newItem)
            } else {
                //这就是评论某一个评论 需要找到这个评论 并添加一条子评论，并且 置空 Mentoned
                for (i in dataList.indices) {
                    val o = dataList[i]
                    if (o is CircleDynamicDetailCommentsBean) {
                        val headerBean = o
                        if (headerBean.objectId == data.mainId) { //找到被评论的 评论
                            if (headerBean.subComments == null) {
                                headerBean.subComments = ArrayList()
                            }
                            // TODO: 2020/10/10 这里让Mentoned为空。表示只是在回复评论，而不是回复评论的评论
                            newItem.mentioned = null
                            headerBean.subComments?.add(0, newItem)
                            headerBean.subCommentsCount = headerBean.subCommentsCount + 1
                            rvAdapter.notifyDataItemChanged(i)
                            //todo 这里这个 评论 如果在热门评论里面，那么只会更新热门评论。而不会更新这次品论，有一个小BUG
                            break
                        }
                    }
                }
            }
        } else {
            if (mainId == data.mainId) {
                //如果是评论某一个评论 需要找到这个评论 并添加一条子评论，并且 置空 Mentoned
                for (i in dataList.indices) {
                    val o = dataList[i]
                    if (o is CircleDynamicDetailCommentsBean) {
                        val headerBean = o
                        if (headerBean.objectId == data.mentioned!!.mentionedId) { //找到被评论的 评论
                            if (headerBean.subComments == null) {
                                headerBean.subComments = ArrayList()
                            }
                            // TODO: 2020/10/10 这里让Mentoned为空。表示只是在回复评论，而不是回复评论的评论
                            newItem.mentioned = null
                            headerBean.subCommentsCount = headerBean.subCommentsCount + 1
                            headerBean.subComments?.add(0, newItem)
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
                        val headerBean = o
                        if (headerBean.objectId == data.mainId) { //找到被评论的 评论
                            if (headerBean.subComments == null) {
                                headerBean.subComments = ArrayList()
                            }
                            headerBean.subComments?.add(0, newItem)
                            headerBean.subCommentsCount = headerBean.subCommentsCount + 1
                            rvAdapter.notifyDataItemChanged(i)
                            //todo 这里这个 评论 如果在热门评论里面，那么只会更新热门评论。而不会更新这次品论，有一个小BUG
                            break
                        }
                    }
                }
            }
        }
    }

    fun onDynamicDetailsCommentDel(
        event: EventCommentDel,
        mainId: String,
        rvAdapter: DcBaseBinderAdapter,
    ) {
        try {
            rvAdapter.getDataList().forEachIndexed { index, any ->
                (any as? CircleDynamicDetailCommentsBean)?.let { commentBean ->
                    if (commentBean.objectId == event.commentId) {
                        rvAdapter.removeAt(index)
                    } else {
                        commentBean.subComments?.forEachIndexed { secIndex, circleDynamicDetailCommentsBean ->
                            if (circleDynamicDetailCommentsBean.objectId == event.commentId) {
                                commentBean.subComments?.removeAt(secIndex)
                                if (circleDynamicDetailCommentsBean.subCommentsCount > 1) {
                                    circleDynamicDetailCommentsBean.subCommentsCount =
                                        circleDynamicDetailCommentsBean.subCommentsCount - 1
                                }
                                rvAdapter.notifyDataItemChanged(event.onePosition)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }

    }
}

