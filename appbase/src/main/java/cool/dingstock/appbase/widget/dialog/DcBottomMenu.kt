package cool.dingstock.appbase.widget.dialog

import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.BottomSpaceDialog
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.R
import cool.dingstock.appbase.adapter.multiadapter.core.ItemViewBinder
import cool.dingstock.appbase.adapter.multiadapter.core.MultiTypeAdapter
import cool.dingstock.appbase.adapter.multiadapter.core.OnItemClickListener
import cool.dingstock.appbase.databinding.DcBottomMenuLayoutBinding
import cool.dingstock.appbase.ext.hide

val cancelId = -0x001315

class DcBottomMenu : BottomSpaceDialog<DcBottomMenuLayoutBinding> {
    private lateinit var actionAdapter: MultiTypeAdapter
    private val itemList: MutableList<Action> = arrayListOf()
    var onMenuClickListener: OnMenuClickListener? = null
    var title: String? = null

    private constructor() : super()

    override fun initEventView() {
        initAdapter()
        viewBinding.titleTv.hide(title == null)
        viewBinding.titleTv.text = title ?: ""
    }

    private fun initAdapter() {
        actionAdapter = MultiTypeAdapter(itemList)
        actionAdapter.register(ActionItemView())
        viewBinding.rvMenuAction.apply {
            adapter = actionAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        actionAdapter.setOnItemClickListener(object : OnItemClickListener<Any> {
            override fun onItemClick(
                view: View?,
                holder: RecyclerView.ViewHolder?,
                entity: Any,
                position: Int
            ) {
                (entity as? Action)?.let { action ->
                    if (id == cancelId) {
                        dismissAllowingStateLoss()
                    } else {
                        onMenuClickListener?.onItemClick(position, action, this@DcBottomMenu)
                    }
                }
            }
        })
    }


    fun showDialog(manager: FragmentManager, tag: String) {
        try {
            manager.beginTransaction().remove(this).commitAllowingStateLoss()
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {

        fun builder(): Builder {
            return Builder()
        }

    }

    class Builder {
        private var title: String? = null
        private var showCancel: Boolean? = null

        fun title(title: String?): Builder {
            this.title = title
            return this
        }

        fun showCancel(showCancel: Boolean?): Builder {
            this.showCancel = showCancel
            return this
        }

        fun show(
            fragmentManager: FragmentManager,
            menuList: ArrayList<Action>,
            onMenuClickListener: OnMenuClickListener
        ) {
            val bottomMenu = DcBottomMenu()
            bottomMenu.itemList.clear()
            bottomMenu.itemList.addAll(menuList)
            if (showCancel == true) {
                bottomMenu.itemList.add(Action("取消", cancelId))
            }
            bottomMenu.title = title
            bottomMenu.onMenuClickListener = onMenuClickListener
            bottomMenu.showDialog(fragmentManager, "DcBottomMenu")
        }
    }

}

interface OnMenuClickListener {
    fun onItemClick(index: Int, action: Action, bottomMenu: DcBottomMenu)
}


data class Action(
    val name: String,
    val id: Int
)


/**
 * itemView
 */
class ActionItemView : ItemViewBinder<Action, ActionItemView.Holder>() {
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAction = itemView.findViewById<TextView>(R.id.tv_title)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): Holder {
        return Holder(inflater.inflate(R.layout.dc_bottom_menu_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, item: Action) {
        holder.tvAction.text = item.name
        if (item.name == "删除") {
            holder.tvAction.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.delete_color))
        } else {
            holder.tvAction.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.color_text_black1))
        }
    }
}