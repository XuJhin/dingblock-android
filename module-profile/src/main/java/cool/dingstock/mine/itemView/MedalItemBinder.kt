package cool.dingstock.mine.itemView

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.adapter.itembinder.ViewBindingVH
import cool.dingstock.appbase.entity.bean.mine.MedalBean
import cool.dingstock.appbase.entity.bean.mine.MedalStatus
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ext.visual
import cool.dingstock.mine.databinding.ItemMedalBinding

class MedalItemBinder(private val countDownEnd: () -> Unit): BaseViewBindingItemBinder<MedalBean, ItemMedalBinding>() {
    companion object {
        private const val ONE_DAY = 1000L * 60 * 60 * 24
        private const val ONE_HOUR = 1000L * 60 * 60
        private const val ONE_MINUTE = 1000L * 60
    }

    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemMedalBinding {
        return ItemMedalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemMedalBinding, data: MedalBean) {
        with(vb) {
            metalIv.load(if (data.madelStatus() == MedalStatus.UNFULFILLED) data.imageBWUrl else data.imageUrl)
            metalTv.text = data.name
            metalTv.setTextColor(if (data.madelStatus() != MedalStatus.UNFULFILLED)
                Color.parseColor("#D3D3D5") else Color.parseColor("#8A8B8D"))
            availableTv.isVisible = data.madelStatus() == MedalStatus.RECEIVABLE
            if (data.madelStatus() == MedalStatus.COMPLETED || data.madelStatus() == MedalStatus.WEARING) {
                data.validity?.let { validity ->
                    val timeDiff = validity - System.currentTimeMillis()
                    countDownGroup.visual(timeDiff > 0L)
                    if (timeDiff > 0L) {
                        countDownTv.text = formatTime(validity)
                    }
                }
            }
        }
    }

    override fun onConvert(holder: ViewBindingVH<ItemMedalBinding>, data: MedalBean, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onConvert(holder.vb, data)
        } else {
            with(holder.vb) {
                if (data.madelStatus() == MedalStatus.COMPLETED || data.madelStatus() == MedalStatus.WEARING) {
                    data.validity?.let { validity ->
                        val timeDiff = validity - System.currentTimeMillis()
                        countDownGroup.visual(timeDiff > 0L)
                        if (timeDiff > 0L) {
                            countDownTv.text = formatTime(validity)
                        } else {
                            countDownTv.text = ""
                            data.status = MedalStatus.UNFULFILLED.value
                            countDownEnd()
                        }
                    }
                }
            }
        }
    }

    private fun formatTime(time: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = time - currentTime
        val day = timeDifference / ONE_DAY
        val hour = (timeDifference % ONE_DAY) / ONE_HOUR
        val minute = (timeDifference % ONE_HOUR) / ONE_MINUTE
        return if (day > 0) {
            "${day}天${hour}小时"
        } else {
            if (hour == 0L && minute == 0L) {
                "即将过期"
            } else {
                "${hour}小时${minute}分"
            }
        }
    }
}