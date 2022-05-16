package cool.dingstock.post.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.post.R
import cool.dingstock.appbase.entity.bean.common.LineEntity
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  11:14
 */
class LineItemBinder : DcBaseItemBinder<LineEntity, LineViewHolder>(){
    override fun onConvert(holder: LineViewHolder, data: LineEntity) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.line_layout, parent, false)
        return LineViewHolder(inflate)
    }
}

class LineViewHolder(view: View) : BaseViewHolder(view)
