package cool.dingstock.uicommon.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/22 16:13
 * @Version:         1.1.0
 * @Description:
 */
class SquareRelativeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec)
	}
}