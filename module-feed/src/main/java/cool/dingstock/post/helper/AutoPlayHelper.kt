package cool.dingstock.post.helper

import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shuyu.gsyvideoplayer.GSYVideoManager
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.appbase.util.cellularConnected
import cool.dingstock.appbase.util.wifiConnected
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.post.R
import cool.dingstock.post.view.DcVideoPlayer

fun getViewVisiblePercent(view: View?): Float {
    if (view == null) return 0f
    val rect = Rect()
    if (!view.getLocalVisibleRect(rect)) {
        return 0f
    }
    val visibleHeight = rect.bottom - rect.top
    return visibleHeight / view.height.toFloat()
}

var preVideoPosition: Long = 0L
var preVideoUrl: String = ""
var preDynamicId: String? = null

fun onScrollReleaseAllVideos(recyclerView: RecyclerView, tag: String, percent: Float) {
    (recyclerView.layoutManager as? LinearLayoutManager)?.apply {
        val firstVisibleItem = findFirstVisibleItemPosition()
        val lastVisibleItem = findLastVisibleItemPosition()
        val position = GSYVideoManager.instance().playPosition
        if (GSYVideoManager.instance().playPosition >= 0) {
            if (GSYVideoManager.instance().playTag == tag &&
                (position <= firstVisibleItem || position >= lastVisibleItem - 1)) {
                if(GSYVideoManager.isFullState(recyclerView.context as Activity)) {
                    return
                }
                val headerCount = (recyclerView.adapter as? DcBaseBinderAdapter)?.headerLayoutCount ?: 0
                val currentView = recyclerView.getChildAt(position + headerCount - firstVisibleItem)
                if (getViewVisiblePercent(currentView) < percent) {
                    GSYVideoManager.releaseAllVideos()
                    preVideoPosition = 0L
                    preVideoUrl = ""
                }
            }
        }
    }
}

fun onScrollPlayVideo(recyclerView: RecyclerView, playerId: Int, tag: String) {
    if (recyclerView.context.wifiConnected()) {
        if (SettingHelper.getInstance().isWifiAutoPlay) {
            autoPlay(recyclerView, playerId, tag)
        }
    } else if (recyclerView.context.cellularConnected()) {
        if (SettingHelper.getInstance().isCellularAutoPlay) {
            autoPlay(recyclerView, playerId, tag)
        }
    }
}

private fun autoPlay(
    recyclerView: RecyclerView,
    playerId: Int,
    tag: String
) {
    (recyclerView.layoutManager as? LinearLayoutManager)?.apply {
        val firstVisibleItem = findFirstVisibleItemPosition()
        val lastVisibleItem = findLastVisibleItemPosition()
        for (position in 0..(lastVisibleItem - firstVisibleItem)) {
            val child = recyclerView.getChildAt(position)
            val view = child?.findViewById<DcVideoPlayer>(playerId)
            if ((view?.parent as? View)?.isVisible == true) {
                if (getViewVisiblePercent(view) >= 0.6f) {
                    if (GSYVideoManager.instance().playPosition != position + firstVisibleItem ||
                            GSYVideoManager.instance().playTag != tag) {
                        if (view.dynamicId?.endsWith(CircleConstant.Extra.AUTO_PLAY) == true) {
                            val isCurrentMediaListener = GSYVideoManager.instance().listener() != null &&
                                GSYVideoManager.instance().listener() == view
                            if (!isCurrentMediaListener) {
                                if (!recyclerView.context.wifiConnected() && recyclerView.context.cellularConnected() &&
                                    ConfigSPHelper.getInstance().getBoolean("videoCellularShowToast", true)) {
                                    ToastUtil.getInstance()._short(BaseLibrary.getInstance().context, "已开启移动流量自动播放，可在个人中心-设置中关闭")
                                    ConfigSPHelper.getInstance().save("videoCellularShowToast", false)
                                }
                                view.startButton?.performClick()
                            }
                        }
                    }
                    break
                }
            }
        }
    }
}

fun RecyclerView.setVideoAutoPlay(lifecycle: Lifecycle?) {
    val tag = adapter?.hashCode()?.toString() ?: ""
    addOnScrollListener(object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            lifecycle?.let {
                if (it.currentState == Lifecycle.State.RESUMED && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onScrollPlayVideo(recyclerView, R.id.detail_player, tag)
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            lifecycle?.let {
                if (it.currentState == Lifecycle.State.RESUMED) {
                    if (dy == 0) {
                        onScrollPlayVideo(recyclerView, R.id.detail_player, tag)
                    } else {
                        onScrollReleaseAllVideos(recyclerView, tag, 0.4f)
                    }
                }
            }
        }
    })
}