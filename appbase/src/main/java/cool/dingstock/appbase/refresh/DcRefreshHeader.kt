package cool.dingstock.appbase.refresh

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import cool.dingstock.appbase.R
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.util.isDarkMode

open class DcRefreshHeader(context: Context) : FrameLayout(context), RefreshHeader {
	private val aniView: LottieAnimationView
	private val stateTv: TextView

	init {
		LayoutInflater.from(context).inflate(R.layout.dc_refresh_header_layout, this, true)
		aniView = findViewById(R.id.lottieAniView)
		if (context.isDarkMode()) {
			aniView.setAnimation("pull_white_refresh_ani.json")
		} else {
			aniView.setAnimation("pull_refresh_ani.json")
		}
		aniView.repeatCount = Int.MAX_VALUE
		aniView.progress = 0f
		aniView.alpha = 0f
		stateTv = findViewById(R.id.refreshStateTv)
	}

	override fun getSpinnerStyle(): SpinnerStyle {
		return SpinnerStyle.Translate
	}

	override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
		return 200 //延迟500毫秒之后再弹回
	}

	override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
	}

	override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
	}

	override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
	}

	override fun getView(): View {
		return this
	}

	override fun setPrimaryColors(vararg colors: Int) {
	}

	override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
	}

	override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
		when (newState) {
			//开始下拉
			RefreshState.PullDownToRefresh -> {
				aniView.cancelAnimation()
				//todo 这里动画需要小改。第一帧需要有图片
				aniView.progress = 0.0f
				aniView.hide(false)
				stateTv.hide(true)
			}
			//松手开始刷新
			RefreshState.ReleaseToRefresh -> {
			}
			//松手了
			RefreshState.RefreshReleased -> {
				aniView.playAnimation()
			}
			//刷新中
			RefreshState.Refreshing -> {
			}
			//刷新完成
			RefreshState.RefreshFinish -> {
				aniView.cancelAnimation()
				aniView.hide(true)
				stateTv.hide(false)
			}
			//未知 停止动画
			RefreshState.None -> {
				aniView.hide(true)
				stateTv.hide(true)
			}
		}
	}

	override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
		aniView.alpha = percent
		stateTv.alpha = percent
	}

	override fun isSupportHorizontalDrag(): Boolean {
		return false
	}
}