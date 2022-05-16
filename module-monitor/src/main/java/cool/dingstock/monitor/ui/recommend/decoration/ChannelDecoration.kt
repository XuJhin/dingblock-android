package cool.dingstock.monitor.ui.recommend.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.ext.dp

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/12/24 17:22
 * @Version:         1.1.0
 * @Description:
 */
class ChannelDecoration : RecyclerView.ItemDecoration() {
	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		val currentViewPosition = parent.getChildAdapterPosition(view)
		val totalCount = parent.adapter?.itemCount
		if (totalCount == 0 || totalCount == null) {
			return
		}
		if (currentViewPosition == totalCount - 1) {
			outRect.right = 60.dp.toInt()
		}
	}
}