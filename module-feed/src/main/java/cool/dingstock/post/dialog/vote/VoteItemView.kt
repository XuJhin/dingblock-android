package cool.dingstock.post.dialog.vote

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.post.R
import cool.dingstock.appbase.adapter.multiadapter.core.ItemViewBinder

class VoteItemView : ItemViewBinder<VoteEntity, VoteItemView.VoteItemHolder>() {
    lateinit var clickDeleteVoteItem: ClickDeleteVoteItem

    var etFocusPos = 0

    class VoteItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDelete: ImageView = itemView.findViewById(R.id.iv_clear_vote_item)
        val edtVoteContent: EditText = itemView.findViewById(R.id.edit_vote)
        lateinit var textChangListener: TextChangListener
        var textWatcher: TextWatcher? = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                //每次修改文字后，保存在数据集合中
                textChangListener.onTextChange(s.toString())
            }
        }
    }


    override fun onViewAttachedToWindow(holder: VoteItemHolder) {
        super.onViewAttachedToWindow(holder)
        holder.edtVoteContent.addTextChangedListener(holder.textWatcher)
        //如果当前显示的item是焦点记录位置，那么获取焦点，并把光标位置置于文字最后，需要显示输入法的话可自行添加操作
        if (getPosition(holder) == 1) {
            return
        }
        holder.edtVoteContent.requestFocus()
        holder.edtVoteContent.setSelection(holder.edtVoteContent.text.length)
    }

    override fun onViewDetachedFromWindow(holder: VoteItemHolder) {
        super.onViewDetachedFromWindow(holder)
        //删除文字变化监听器
        holder.edtVoteContent.removeTextChangedListener(holder.textWatcher)
        //清除焦点
        holder.edtVoteContent.clearFocus()
        holder.edtVoteContent.clearComposingText()

    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VoteItemHolder {
        return VoteItemHolder(inflater.inflate(R.layout.item_vote_content, parent, false))
    }

    override fun onBindViewHolder(holder: VoteItemHolder, item: VoteEntity) {
        holder.edtVoteContent.setText(item.voteContent)
        holder.edtVoteContent.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.color_text_black1))
        holder.ivDelete.setOnClickListener {
            if (::clickDeleteVoteItem.isInitialized) {
                clickDeleteVoteItem.onClickDeleteItem(getPosition(holder))
            }
        }
        holder.edtVoteContent.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                //记录焦点位置
                etFocusPos = getPosition(holder)
            }
        }
        holder.edtVoteContent.setSelection(item.voteContent.length)
        if (item.showClear) {
            holder.ivDelete.visibility = View.VISIBLE
        } else {
            holder.ivDelete.visibility = View.GONE
        }
        holder.textChangListener = object : TextChangListener {
            override fun onTextChange(string: String) {
                item.voteContent = string
            }
        }
    }

}


interface ClickDeleteVoteItem {
    fun onClickDeleteItem(position: Int)
}

interface TextChangListener {
    fun onTextChange(string: String)
}