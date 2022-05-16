package cool.dingstock.post.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cool.dingstock.appbase.SpanUtils
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.entity.bean.circle.AwardEntity
import cool.dingstock.appbase.entity.bean.circle.LotteryEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.widget.recyclerview.decotation.LinearItemDecoration
import cool.dingstock.post.R
import cool.dingstock.post.databinding.PostLotteryAwardItemLayoutBinding
import cool.dingstock.post.databinding.PostLotteryDetailsLayoutBinding


/**
 * 类名：PostLotteryDetailsView
 * 包名：cool.dingstock.post.widget
 * 创建时间：2022/1/12 12:13 下午
 * 创建人： WhenYoung
 * 描述：
 **/
open class PostLotteryDetailsView(context: Context) : FrameLayout(context) {
    val vb = PostLotteryDetailsLayoutBinding.inflate(LayoutInflater.from(context), this, true)


    var entity: LotteryEntity? = null

    val adapter = DcBaseBinderAdapter(arrayListOf()).apply {
        addItemBinder(object :
            BaseViewBindingItemBinder<AwardEntity, PostLotteryAwardItemLayoutBinding>() {
            override fun provideViewBinding(
                parent: ViewGroup,
                viewType: Int
            ): PostLotteryAwardItemLayoutBinding {
                return PostLotteryAwardItemLayoutBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            }

            override fun onConvert(vb: PostLotteryAwardItemLayoutBinding, data: AwardEntity) {
                if (entity?.getStatus() == LotteryEntity.Status.end) {
                    vb.rootView.setBackgroundResource(R.drawable.post_lottery_award_end_bg)
                    vb.awardDescTv.hide(true)
                    vb.awardNameTv.text = data.name + " | " + data.contents
                    vb.awardCountTv.text = data.limit + "名"
                    vb.winningUserTv.text ="中奖用户：" + data.users
                    if (data.users.isNullOrEmpty()) {
                        vb.awardEndGroup.hide(true)
                    } else {
                        vb.awardEndGroup.hide(false)
                    }
                } else {
                    vb.rootView.setBackgroundResource(R.drawable.post_lottery_award_ing_bg)
                    vb.awardDescTv.hide(false)
                    vb.awardNameTv.text = data.name
                    vb.awardDescTv.text = data.contents
                    vb.awardCountTv.text = data.limit + "名"
                    vb.winningUserTv.text = ""
                    vb.awardEndGroup.hide(true)
                }
            }
        })
    }


    init {
        vb.lotteryRv.adapter = adapter
        vb.lotteryRv.layoutManager = LinearLayoutManager(context)
        vb.lotteryRv.addItemDecoration(
            LinearItemDecoration(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.line_space_2dp
                )!!, 0
            )
        )
    }


    fun setData(lotteryMap: LotteryEntity) {
        entity = lotteryMap
        entity?.let {
            vb.detailsLotteryStateTv.text = it.getStatus().title
            adapter.setList(it.awards)
            vb.detailsLotteryCountdownTv.hide(it.getStatus() != LotteryEntity.Status.drawing)
        }
        updateState()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateState(onTimeEnd: (() -> Unit)? = null) {
        val entity = this.entity ?: return
        val current = System.currentTimeMillis()
        var time = (entity.endAt ?: current) - current
        if (time <= 60 * 1000 || entity.getStatus() != LotteryEntity.Status.drawing) {
            //结束了 需要改变状态
            if (entity.getStatus() == LotteryEntity.Status.drawing) {
                vb.detailsLotteryCountdownTv.hide(true)
                entity.updateStatus(LotteryEntity.Status.waiting)
                vb.detailsLotteryStateTv.text = entity.getStatus().title
                adapter.notifyDataSetChanged()
            }
            onTimeEnd?.invoke()

        } else {
            vb.detailsLotteryCountdownTv.hide(false)
            val day = time / (1000 * 60 * 60 * 24)
            time %= (1000 * 60 * 60 * 24)

            val hour = time / (1000 * 60 * 60)
            time %= (1000 * 60 * 60)

            val minute = time / (1000 * 60)

            SpanUtils.with(vb.detailsLotteryCountdownTv)
                .append("距离开奖仅剩 ")
                .append(day.toString())
                .setForegroundColor(Color.parseColor("#FF3B47"))
                .append(" 天 ")
                .append(hour.toString())
                .setForegroundColor(Color.parseColor("#FF3B47"))
                .append(" 小时 ")
                .append(minute.toString())
                .setForegroundColor(Color.parseColor("#FF3B47"))
                .append(" 分 ")
                .create()
        }
    }
}