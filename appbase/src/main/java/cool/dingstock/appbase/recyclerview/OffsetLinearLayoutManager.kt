package cool.dingstock.appbase.recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * 类名：OffsetLinearLayoutManager
 * 包名：cool.dingstock.appbase.recyclerview
 * 创建时间：2021/11/2 11:04 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class OffsetLinearLayoutManager(
    context: Context?,
) : LinearLayoutManager(context) {

    private val heightMap = HashMap<Int, Int>()

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        val firstVisible = findFirstVisibleItemPosition()
        val lastVisible = findLastVisibleItemPosition()
        if (firstVisible >= 0 && lastVisible >= 0 && lastVisible >= firstVisible) {
            for (i in firstVisible until lastVisible) {
                getChildAt(i)?.let {
                    heightMap[i] = it.height
                }
            }
        }
    }

    fun mComputeVerticalScrollOffset(): Int {
        if (childCount == 0) return 0
        return try {
            val firstVisiblePosition = findFirstVisibleItemPosition()
            val viewByPosition = findViewByPosition(firstVisiblePosition)
            var offset = 0
            for (i in 0 until firstVisiblePosition) {
                offset += heightMap[i] ?: 0
            }
            offset -= viewByPosition?.top ?: 0
            offset
        } catch (e: Exception) {
            0
        }
    }
}