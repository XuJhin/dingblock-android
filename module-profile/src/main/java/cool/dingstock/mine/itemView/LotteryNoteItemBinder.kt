package cool.dingstock.mine.itemView

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.load.resource.bitmap.CenterInside
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.mine.LotteryNoteBean
import cool.dingstock.appbase.ext.load
import cool.dingstock.foundation.ext.tintColorRes
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.ItemLotteryNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class LotteryNoteItemBinder(
    private val onFailed: (Int, LotteryNoteBean) -> Unit,
    private val onSucceed: (Int, LotteryNoteBean) -> Unit,
    private val onJumpToDeal: (LotteryNoteBean) -> Unit,
    private val onMore: (Int, LotteryNoteBean) -> Unit,
) : BaseViewBindingItemBinder<LotteryNoteBean, ItemLotteryNoteBinding>() {
    var simpleDateFormat = SimpleDateFormat("yyyy MM.dd", Locale.getDefault())

    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemLotteryNoteBinding {
        return ItemLotteryNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemLotteryNoteBinding, data: LotteryNoteBean) {
        with(vb) {
            val position = if (holder != null) getDataPosition(holder!!) else 0
            icon.load(url = data.product?.imageUrl, radius = 4f, scaleType = CenterInside())
            title.text = data.product?.name
            shopIcon.load(url = data.shop?.imageUrl, radius = 4f, scaleType = CenterInside())
            shopName.text = data.shop?.name
            haveNotLottery.isVisible = data.state.isNullOrEmpty()
            hadLottery.isVisible = !data.state.isNullOrEmpty()
            jumpToDeal.text = if (data.state == "get") "发布到交易区" else "到交易区求购"
            lotteryStateIcon.setImageResource(if (data.state == "get") R.drawable.mine_svg_lottery_success else R.drawable.mine_svg_lottery_failed)
            lotteryStateIcon.tintColorRes(if (data.state == "get") R.color.lottery_note_choose else R.color.lottery_note_un_choose)
            stateYear.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (data.state == "get") R.color.lottery_note_choose else R.color.lottery_note_un_choose
                )
            )
            stateDate.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (data.state == "get") R.color.lottery_note_choose else R.color.lottery_note_un_choose
                )
            )

            data.chapterDate?.let { date ->
                if (date != 0L) {
                    simpleDateFormat.format(Date(date)).let {
                        val dates = it.split(" ")
                        if (dates.size > 1) {
                            stateYear.text = dates[0]
                            stateDate.text = dates[1]
                        }
                    }
                }
            }
            if (data.chapterDate == null) {
                stateYear.text = ""
                stateDate.text = ""
            }

            failed.setOnClickListener {
                onFailed(position, data)
            }
            succeed.setOnClickListener {
                onSucceed(position, data)
            }
            jumpToDeal.setOnClickListener {
                onJumpToDeal(data)
            }
            more.setOnClickListener {
                onMore(position, data)
            }
        }
    }
}