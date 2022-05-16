package cool.dingstock.appbase.service

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.anim.AppFloatDefaultAnimator
import com.lzf.easyfloat.anim.DefaultAnimator
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.utils.DisplayUtils
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.R
import cool.dingstock.appbase.customerview.TimeTextView
import cool.dingstock.appbase.customerview.suspendwindow.ScaleImage
import cool.dingstock.appbase.util.NotificationsUtils
import cool.dingstock.appbase.constant.CommonConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.customerview.ClockTimeType
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.SizeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@RouterUri(
        scheme = RouterConstant.SCHEME,
        host = RouterConstant.HOST,
        path = [CommonConstant.Path.TIME_CLOCK_WINDOW]
)
class TimeClockService : Service() {

    private val timeWindowTag = "ding_staock_time_window"

    private var selStyle: CommonConstant.ClockStyle = CommonConstant.ClockStyle.None

    private var offSetMs = 0L
    private var platTimeOffset = 0L
    private var clockType = ClockTimeType.Clock
    var targetTime: Long = 0L
    private var showMsec = false
    var titleTv: TextView? = null
    var timeTv: TimeTextView? = null
    var delayTimesTv: TextView? = null


    override fun onCreate() {
        super.onCreate()
        val notification = NotificationsUtils.createNotification(
                this,
                "ding_staock_nofication",
                "盯链",
                "悬浮时钟正在显示",
                R.mipmap.ic_launcher
        )
        startForeground(1, notification)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        offSetMs = intent?.getLongExtra("offset", 0L) ?: 0L
        clockType = intent?.getParcelableExtra("timeType") ?: ClockTimeType.Clock
        targetTime = intent?.getLongExtra("targetTime", 0L) ?: 0L
        platTimeOffset = intent?.getLongExtra("platTimeOffset", 0L) ?: 0L
        showMsec = intent?.getBooleanExtra("showMsec", true) ?: true

        val index = ConfigSPHelper.getInstance().getInt(CommonConstant.Sp.CLOCK_STYLE_KEY, 0)
        val style = CommonConstant.ClockStyle.values()[index]
        if (selStyle != style) {
            selStyle = style
            EasyFloat.dismissAppFloat(timeWindowTag)
            CoroutineScope(Dispatchers.IO).launch {
                delay(500)
                launch(Dispatchers.Main) {
                    showWindow()
                    updateView()
                }
            }
        } else {
            showWindow()
            updateView()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateView() {
        timeTv?.showTag = true
        timeTv?.setTimeColor(Color.BLACK)
        timeTv?.mBackgroundColor = Color.WHITE
        timeTv?.showMsec = showMsec
        timeTv?.offSett = offSetMs
        timeTv?.clockType = clockType
        timeTv?.targetTime = targetTime
        timeTv?.platTimeOffset = platTimeOffset
        timeTv?.offSett = offSetMs
        timeTv?.requestLayout()
        if (offSetMs < 0) {
            delayTimesTv?.text = "已设置提前${abs(offSetMs)}毫秒"
        } else {
            delayTimesTv?.text = "已设置延迟${abs(offSetMs)}毫秒"
        }
        if (offSetMs == 0L) {
            delayTimesTv?.visibility = View.GONE
        } else {
            delayTimesTv?.visibility = View.VISIBLE
        }
        when (selStyle) {
            CommonConstant.ClockStyle.Default -> {
                titleTv?.setTextColor(ContextCompat.getColor(this, R.color.color_text_black1))
                timeTv?.setTimeColor(Color.BLACK)
                timeTv?.setTagColor(Color.parseColor("#C5CDD9"))
                timeTv?.mBackgroundColor = Color.WHITE
                delayTimesTv?.setTextColor(ContextCompat.getColor(this, R.color.color_text_black5))
            }
            CommonConstant.ClockStyle.Style1 -> {
                titleTv?.setTextColor(Color.WHITE)
                timeTv?.setTimeColor(Color.WHITE)
                timeTv?.mBackgroundColor = Color.BLACK
                timeTv?.setTimeTextSize(16f)
                timeTv?.showTag = false
                timeTv?.setTagColor(Color.parseColor("#C5CDD9"))
                delayTimesTv?.setTextColor(ContextCompat.getColor(this, R.color.color_text_black5))
            }
            CommonConstant.ClockStyle.Style2 -> {
                titleTv?.setTextColor(Color.WHITE)
                timeTv?.setTimeColor(Color.WHITE)
                timeTv?.setTagColor(ContextCompat.getColor(this, R.color.color_text_black4))
                timeTv?.mBackgroundColor = Color.BLACK
                delayTimesTv?.setTextColor(ContextCompat.getColor(this, R.color.color_text_black5))
            }
            CommonConstant.ClockStyle.Style3 -> {
                titleTv?.setTextColor(Color.BLACK)
                timeTv?.setTimeColor(Color.BLACK)
                timeTv?.mBackgroundColor = Color.WHITE
                timeTv?.setTimeTextSize(16f)
                timeTv?.showTag = false
                timeTv?.setTagColor(Color.parseColor("#C5CDD9"))
                delayTimesTv?.setTextColor(ContextCompat.getColor(this, R.color.color_text_black5))
            }
            else -> {
                titleTv?.setTextColor(ContextCompat.getColor(this, R.color.color_text_black1))
                timeTv?.setTimeColor(Color.BLACK)
                timeTv?.setTagColor(Color.parseColor("#C5CDD9"))
                timeTv?.mBackgroundColor = Color.WHITE
                delayTimesTv?.setTextColor(ContextCompat.getColor(this, R.color.color_text_black5))
            }
        }
    }

    private fun showWindow() {
        val layoutId = when (selStyle) {
            CommonConstant.ClockStyle.Default -> {
                R.layout.suspend_windw_view_style_default_layout
            }
            CommonConstant.ClockStyle.Style1 -> {
                R.layout.suspend_windw_view_style_1_layout
            }
            CommonConstant.ClockStyle.Style2 -> {
                R.layout.suspend_windw_view_style_2_layout
            }
            CommonConstant.ClockStyle.Style3 -> {
                R.layout.suspend_windw_view_style_3_layout
            }
            else -> {
                R.layout.suspend_windw_view_style_default_layout
            }
        }
        EasyFloat.with(this)
                // 设置浮窗xml布局文件，并可设置详细信息
                .setLayout(layoutId) {
                    timeTv = it.findViewById(R.id.timeTv)
                    delayTimesTv = it.findViewById(R.id.delay_times_tv)
                    titleTv = it.findViewById(R.id.title_tv)
                    timeTv?.setTagTextSize(9f)
                    timeTv?.setTimeTextSize(28f)
                    titleTv?.textSize = 9f
                    delayTimesTv?.textSize = 9f
                    updateView()
                    val contentWindow = it.findViewById<FrameLayout>(R.id.content_window)
                    it.findViewById<ScaleImage>(R.id.scale_iv)?.onScaledListener =
                        object : ScaleImage.OnScaledListener {
                            val contentOut = it?.findViewById<View>(R.id.content_out)
                            val outParams = contentOut?.layoutParams as FrameLayout.LayoutParams
                            override fun onScaled(x: Float, y: Float, event: MotionEvent) {
                                //宽高比
                                val ratio = 90f / 198f

                                var width = max(outParams.width + x.toInt(), SizeUtils.dp2px(198f))
                                width = min(width, SizeUtils.getWidth() - SizeUtils.dp2px(40f))
                                val height = ratio * width

                                val changeRatio =
                                    (width - SizeUtils.dp2px(198f)) / (SizeUtils.getWidth() - SizeUtils.dp2px(
                                        40f
                                    ) - 198f)
                                val titleSize = changeRatio * (14f - 12) + 12
                                val timeSize = changeRatio * (40f - 28) + 28
                                val timeTagSize = changeRatio * (12f - 9) + 9
                                val delaySize = changeRatio * (12f - 9) + 9

                                titleTv?.textSize = titleSize
                                timeTv?.setTimeTextSize(timeSize)
                                timeTv?.setTagTextSize(timeTagSize)
                                delayTimesTv?.textSize = delaySize

                                outParams.width = width
                                outParams.height = height.toInt()
                                contentOut?.layoutParams = outParams
                                Log.e("onScaled", "${outParams.width},${outParams.height}")
                                timeTv?.canDraw = false
                                timeTv?.requestLayout()
                                timeTv?.requestDraw()
                            }

                            override fun onScaledComplete(x: Float, y: Float) {
                                val params = contentWindow.layoutParams as FrameLayout.LayoutParams
                                params.width = outParams.width
                                params.height = outParams.height
                                contentWindow.layoutParams = params
                                timeTv?.canDraw = true
                                timeTv?.requestDraw()
//                                    mask.visibility = View.GONE
                            }
                        }
                    it.findViewById<ImageView>(R.id.close_iv)?.setOnClickListener {
                        EasyFloat.dismissAppFloat(timeWindowTag)
                        stopService(Intent(this@TimeClockService, TimeClockService::class.java))
                    }
                }
            // 设置浮窗显示类型，默认只在当前Activity显示，可选一直显示、仅前台显示、仅后台显示
                .setShowPattern(ShowPattern.ALL_TIME)
                // 设置吸附方式，共15种模式，详情参考SidePattern
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                // 设置浮窗的标签，用于区分多个浮窗
                .setTag(timeWindowTag)
                // 设置浮窗是否可拖拽，默认可拖拽
                .setDragEnable(true)
                // 系统浮窗是否包含EditText，仅针对系统浮窗，默认不包含
                .hasEditText(false)
                // 设置浮窗固定坐标，ps：设置固定坐标，Gravity属性和offset属性将无效
                .setLocation(0, 100)
                // 设置浮窗的对齐方式和坐标偏移量
                .setGravity(Gravity.END or Gravity.CENTER_VERTICAL, 0, 200)
                // 设置宽高是否充满父布局，直接在xml设置match_parent属性无效
                .setMatchParent(widthMatch = false, heightMatch = false)
                // 设置Activity浮窗的出入动画，可自定义，实现相应接口即可（策略模式），无需动画直接设置为null
                .setAnimator(DefaultAnimator())
                // 设置系统浮窗的出入动画，使用同上
                .setAppFloatAnimator(AppFloatDefaultAnimator())
                // 设置系统浮窗的不需要显示的页面
//                .setFilter(MainActivity::class.java, SecondActivity::class.java)
                // 设置系统浮窗的有效显示高度（不包含虚拟导航栏的高度），基本用不到，除非有虚拟导航栏适配问题
                .setDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) }
            // 浮窗的一些状态回调，如：创建结果、显示、隐藏、销毁、touchEvent、拖拽过程、拖拽结束。
                // ps：通过Kotlin DSL实现的回调，可以按需复写方法，用到哪个写哪个
                // 创建浮窗（这是关键哦😂）
                .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        timeTv = null
        delayTimesTv = null
    }


}