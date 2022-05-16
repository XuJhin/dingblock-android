package cool.dingstock.tide.adapter.viewholder

import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.widget.stickyheaders.SectioningAdapter
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.tide.databinding.HomeTideHeaderLayoutBinding
import java.util.*


/**
 * 类名：HomeTideHeaderViewHolder
 * 包名：cool.dingstock.tide.adapter.viewholder
 * 创建时间：2021/7/20 4:52 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class HomeTideHeaderViewHolder(val viewBinding: HomeTideHeaderLayoutBinding) :
    SectioningAdapter.HeaderViewHolder(viewBinding.root) {

    fun bindData(time: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val week = TimeUtils.getWeekDay(time)
        viewBinding.dayTv.text = "${day}"
        viewBinding.monthTv.text = "${month}"
        viewBinding.weekTv.text = "(${week})"
    }
}