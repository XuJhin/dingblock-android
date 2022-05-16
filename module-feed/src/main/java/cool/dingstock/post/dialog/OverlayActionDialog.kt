package cool.dingstock.post.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.BottomSpaceDialog
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.adapter.multiadapter.core.ItemViewBinder
import cool.dingstock.appbase.adapter.multiadapter.core.MultiTypeAdapter
import cool.dingstock.appbase.adapter.multiadapter.core.OnItemClickListener
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.widget.dialog.common.DcTitleDialog
import cool.dingstock.post.R
import cool.dingstock.post.databinding.DialogPostActionBinding
import cool.dingstock.post.item.PostItemShowWhere

const val ACTION_ENTITY = "ACTION_ENTITY"
const val ACTION_POSITION = "ACTION_POSITION"

class OverlayActionDialog : BottomSpaceDialog<DialogPostActionBinding>() {
    private var showWhere: PostItemShowWhere = PostItemShowWhere.Default
    private var action: PostActionListener? = null
    private lateinit var actionAdapter: MultiTypeAdapter
    private val itemList: MutableList<Action> = arrayListOf()
    private var postItemViewModel: PostItemViewModel? = null
    private var parcelable: CircleDynamicBean? = null
    var index: Int? = 0


    override fun initEventView() {
        initAdapterAndEvent()
        asyncData()
    }

    private fun asyncData() {
        postItemViewModel?.actionList?.observe(viewLifecycleOwner) {
            itemList.clear()
            itemList.addAll(it)
            actionAdapter.notifyDataSetChanged()
        }
        postItemViewModel?.overlayResult?.observe(
            viewLifecycleOwner
        ) {
            when (it) {
                is PostOverlayType.PostReportResult -> {
                    action?.postReportResult(it.isSuccess, it.msg, it.position)
                }
                is PostOverlayType.UserBlockResult -> {
                    action?.userBlockResult(it.isSuccess, it.msg, it.position)
                }
                is PostOverlayType.PostBlockResult -> {
                    action?.postBlockResult(it.isSuccess, it.msg, it.position)
                }
                is PostOverlayType.PostDeleteResult -> {
                    action?.deletePostResult(it.isSuccess, it.msg, it.position)
                }
            }
        }
    }

    private fun initAdapterAndEvent() {
        actionAdapter = MultiTypeAdapter(itemList)
        actionAdapter.register(ActionItemView())
        viewBinding.rvPostAction.apply {
            adapter = actionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        actionAdapter.setOnItemClickListener(object : OnItemClickListener<Any> {
            override fun onItemClick(
                view: View?,
                holder: RecyclerView.ViewHolder?,
                entity: Any,
                position: Int
            ) {
                if (LoginUtils.getCurrentUser() == null) {
                    this@OverlayActionDialog.dismissAllowingStateLoss()
                    DcUriRequest(requireActivity(), AccountConstant.Uri.INDEX)
                        .start()
                    return
                }
                if (entity is Action) {
                    when (entity) {
                        Action.NotInterested -> {
                            UTHelper.commonEvent(
                                UTConstant.Circle.Dynamic_click_More,
                                "name",
                                "不感兴趣"
                            )
                            postItemViewModel?.lostInterested()
                        }
                        Action.CancelShielding -> {
                            UTHelper.commonEvent(
                                UTConstant.Circle.Dynamic_click_More,
                                "name",
                                "屏蔽该用户"
                            )
                            DcTitleDialog.Builder(requireContext())
                                .title("确定屏蔽用户吗？")
                                .content("屏蔽后对方无法私信、回复、点赞、关注你，你也无法与对方互动。")
                                .cancelTxt("取消")
                                .confirmTxt("确定")
                                .onConfirmClick {
                                    postItemViewModel?.shield()
                                }
                                .builder()
                                .show()
                        }
                        Action.Report -> {
                            UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_More, "name", "举报")
                            postItemViewModel?.report()
                        }
                        Action.Delete -> {
                            UTHelper.commonEvent(UTConstant.Circle.Dynamic_click_Delete)
                            DcTitleDialog.Builder(requireContext())
                                .title("提示")
                                .content("动态删除后无法恢复,确定删除该条动态吗？")
                                .cancelTxt("取消")
                                .confirmTxt("确定")
                                .onConfirmClick {
                                    postItemViewModel?.deletePost()
                                }
                                .builder()
                                .show()
                        }
                        Action.ShieldingUsers -> {
                            postItemViewModel?.cancelShield()
                        }
                        else -> {
                        }
                    }
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            parcelable = arguments?.getParcelable(ACTION_ENTITY)
            index = arguments?.getInt(ACTION_POSITION, 0)
        }
        postItemViewModel = ViewModelProvider(this)[PostItemViewModel::class.java]
        postItemViewModel?.updateShowWhere(showWhere)
        postItemViewModel?.initPostEntity(parcelable, index)
    }

    fun showDialog(manager: FragmentManager, tag: String) {
        try {
            manager.beginTransaction().remove(this).commitAllowingStateLoss()
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setActionListener(listener: PostActionListener) {
        this.action = listener
    }

    fun updateShowWhere(data: PostItemShowWhere) {
        this.showWhere = data
    }

    companion object {
        fun instance(entity: CircleDynamicBean, position: Int): OverlayActionDialog {
            return OverlayActionDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ACTION_ENTITY, entity)
                    putInt(ACTION_POSITION, position)
                }
            }
        }
    }
}

interface PostActionListener {
    /**
     * 举报用户
     */
    fun postReportResult(isSuccess: Boolean, msg: String?, index: Int?)

    /**
     * 屏蔽用户
     */
    fun userBlockResult(isSuccess: Boolean, msg: String?, index: Int?)

    /**
     * 屏蔽动态
     */
    fun postBlockResult(isSuccess: Boolean, msg: String?, index: Int?)

    /**
     * 删除动态
     */
    fun deletePostResult(isSuccess: Boolean, msg: String?, index: Int?)
}


/**
 * itemView
 */
class ActionItemView : ItemViewBinder<Action, ActionItemView.Holder>() {
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAction: TextView = itemView.findViewById(R.id.tv_title)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.item_post_action, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, item: Action) {
        holder.tvAction.text = item.cnName
        if (item.cnName == "删除") {
            holder.tvAction.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.color_red
                )
            )
        } else {
            holder.tvAction.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.color_text_black1
                )
            )
        }
    }
}

/**
 * 操作的结果数据封装
 */
sealed class PostOverlayType {
    data class PostBlockResult(val isSuccess: Boolean, val msg: String?, val position: Int?) :
        PostOverlayType()

    data class UserBlockResult(val isSuccess: Boolean, val msg: String?, val position: Int?) :
        PostOverlayType()

    data class PostReportResult(val isSuccess: Boolean, val msg: String?, val position: Int?) :
        PostOverlayType()

    data class PostDeleteResult(val isSuccess: Boolean, val msg: String?, val position: Int?) :
        PostOverlayType()
}




