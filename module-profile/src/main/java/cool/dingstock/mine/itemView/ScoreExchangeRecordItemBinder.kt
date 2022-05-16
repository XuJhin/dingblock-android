package cool.dingstock.mine.itemView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.score.ScoreRecordEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.mine.R

class ScoreExchangeRecordItemBinder() : DcBaseItemBinder<ScoreRecordEntity, ScoreExchangeRecordVH>() {

    interface ActionListener {
        fun onCopyClick(entity: ScoreRecordEntity)
        fun onInputMessage(id: String, eventId: String)
        fun onCheckMail(entity: ScoreRecordEntity)
        fun onClickConfirmAddress(id: String, eventId: String)
    }

    var mListener: ActionListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreExchangeRecordVH {
        return ScoreExchangeRecordVH(LayoutInflater.from(context).inflate(R.layout.score_exchange_record_item_layout, parent, false))
    }

    override fun onConvert(holder: ScoreExchangeRecordVH, data: ScoreRecordEntity) {
        holder.apply {
            name.text = data.eventName
            time.text = "兑换时间：" + TimeUtils.formatTimestamp(data.createdAt, "yyyy-MM-dd HH:mm")
            action.text = data.buttonStr
            action.hide(data.buttonStr == "")

            action.apply {
                when (data.buttonStr) {
                    "复制券码" -> {
                        setOnClickListener {
                            mListener?.onCopyClick(data)
                        }
                    }
                    "填写收货地址" -> {
                        setOnClickListener {
                            mListener?.onInputMessage(data.id, data.eventId)
                        }
                    }
                    "去领取" -> {
                        setOnClickListener {
                            mListener?.onClickConfirmAddress(data.id, data.eventId)
                        }
                    }
//                    "查看快递单号", "待处理", "配货中", "查看物流" -> {
//                        setOnClickListener {
//                            mListener?.onCheckMail(data)
//                        }
//                    }
                    else -> {
                        setOnClickListener {
                            mListener?.onCheckMail(data)
                        }
                    }
                }
            }
        }
    }

}

class ScoreExchangeRecordVH(view: View) : BaseViewHolder(view) {

    val name: TextView = view.findViewById(R.id.title)
    val time: TextView = view.findViewById(R.id.time)
    val action: TextView = view.findViewById(R.id.action)

}