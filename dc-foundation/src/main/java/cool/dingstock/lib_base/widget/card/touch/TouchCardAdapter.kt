package cool.dingstock.lib_base.widget.card.touch

import androidx.recyclerview.widget.RecyclerView


/**
 * 类名：TouchCardAdapter
 * 包名：cool.dingstock.lib_base.widget.card.touch
 * 创建时间：2021/8/7 6:03 下午
 * 创建人： WhenYoung
 * 描述：
 **/
abstract class TouchCardAdapter<T, VH : RecyclerView.ViewHolder>(val data: ArrayList<T> = arrayListOf()) :
    RecyclerView.Adapter<VH>() {
}