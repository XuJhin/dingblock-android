package cool.dingstock.post.dialog.vote

import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.BaseBottomFullViewBindingFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.adapter.multiadapter.core.MultiTypeAdapter
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.SoftKeyBoardUtil
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.post.R
import cool.dingstock.post.databinding.DialogCircleVoteBinding

class CircleVoteDialog : BaseBottomFullViewBindingFragment<DialogCircleVoteBinding>() {
    val itemList: MutableList<VoteEntity> = arrayListOf()
    private lateinit var addItem: LinearLayout
    private lateinit var ivClose: ImageView
    private lateinit var rvVote: RecyclerView
    private lateinit var tvPublish: TextView
    var onPublishVoteListener: PublishVoteListener? = null
    private var voteAdapter: MultiTypeAdapter? = null

    override fun initDataEvent() {
        addItem = containerView.findViewById(R.id.layout_add_vote_item)
        ivClose = containerView.findViewById(R.id.iv_close)
        rvVote = containerView.findViewById(R.id.rv_vote_list)
        tvPublish = containerView.findViewById(R.id.tv_publish_vote)
        initViewAndEvent()
        initData()
    }

    fun setVoteList(list: MutableList<VoteEntity>) {
        if (list.isEmpty()) {
            return
        }
        if (list.size >= 4) {
            hideAddItem()
        } else {
            showAddItem()
        }
        itemList.clear()
        voteAdapter?.notifyDataSetChanged()
        itemList.addAll(list)
        voteAdapter?.notifyDataSetChanged()
    }

    private fun initData() {
        if (itemList.size > 0) {
            return
        }
        itemList.add(VoteEntity("", false))
        itemList.add(VoteEntity("", false))
        voteAdapter?.notifyDataSetChanged()
        if (itemList.size <= 4) {
            showAddItem()
        } else {
            hideAddItem()
        }
    }

    private fun initViewAndEvent() {
        voteAdapter = MultiTypeAdapter(itemList)
        voteAdapter?.register(VoteItemView().apply {
            clickDeleteVoteItem = object : ClickDeleteVoteItem {
                override fun onClickDeleteItem(position: Int) {
                    itemList.removeAt(position)
                    voteAdapter?.notifyDataSetChanged()
                    showAddItem()
                }
            }
        })
        rvVote.apply {
            adapter = voteAdapter
            layoutManager = LinearLayoutManager(context)
        }
        ivClose.setOnClickListener {
            clearDataAndDismiss()
        }
        addItem.setOnClickListener {
            itemList.add(VoteEntity("", true))
            voteAdapter?.notifyItemInserted(itemList.size - 1)
            if (itemList.size >= 4) {
                hideAddItem()
            }
            UTHelper.commonEvent(UTConstant.Circle.Editor_click_icon_vote, "添加选项")
        }
        tvPublish.setOnClickListener {
            val list = checkList()
            if (list.size < 2) {
                ToastUtil.getInstance()._short(context, "至少输入两项")
                return@setOnClickListener
            }

            if (onPublishVoteListener != null) {
                list.forEachIndexed { index, voteEntity ->
                    voteEntity.index = index
                    voteEntity.totalSize = itemList.size
                }
                onPublishVoteListener?.onPublishVoteList(list)
                hideInput()
                dismiss()
            }
            UTHelper.commonEvent(UTConstant.Circle.Editor_click_icon_vote, "完成")
        }
    }

    private fun hideInput() {
        SoftKeyBoardUtil.hideSoftKeyboard(requireContext(), dialog?.window?.decorView)
    }

    private fun clearDataAndDismiss() {
        itemList.clear()
        this.dismiss()
        UTHelper.commonEvent(UTConstant.Circle.Editor_click_icon_vote, "关闭")
    }

    /**
     * 检查输入的内容是否合法
     */
    private fun checkList(): ArrayList<VoteEntity> {
        val list = arrayListOf<VoteEntity>()
        list.addAll(itemList)
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.voteContent.isEmpty()) {
                iterator.remove()
            }
        }
        list.forEachIndexed { index, voteEntity ->
            voteEntity.showClear = index >= 2
        }
        return list
    }

    private fun hideAddItem() {
        addItem.visibility = View.INVISIBLE
    }

    private fun showAddItem() {
        addItem.visibility = View.VISIBLE
    }

    fun show(manager: FragmentManager) {
        super.show(manager, "CircleVoteDialog")
    }

}

interface PublishVoteListener {
    fun onPublishVoteList(list: MutableList<VoteEntity>)
}