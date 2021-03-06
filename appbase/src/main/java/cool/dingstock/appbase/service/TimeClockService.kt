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
                "??????",
                "????????????????????????",
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
            delayTimesTv?.text = "???????????????${abs(offSetMs)}??????"
        } else {
            delayTimesTv?.text = "???????????????${abs(offSetMs)}??????"
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
                // ????????????xml???????????????????????????????????????
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
                                //?????????
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
            // ?????????????????????????????????????????????Activity???????????????????????????????????????????????????????????????
                .setShowPattern(ShowPattern.ALL_TIME)
                // ????????????????????????15????????????????????????SidePattern
                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                // ????????????????????????????????????????????????
                .setTag(timeWindowTag)
                // ?????????????????????????????????????????????
                .setDragEnable(true)
                // ????????????????????????EditText??????????????????????????????????????????
                .hasEditText(false)
                // ???????????????????????????ps????????????????????????Gravity?????????offset???????????????
                .setLocation(0, 100)
                // ?????????????????????????????????????????????
                .setGravity(Gravity.END or Gravity.CENTER_VERTICAL, 0, 200)
                // ?????????????????????????????????????????????xml??????match_parent????????????
                .setMatchParent(widthMatch = false, heightMatch = false)
                // ??????Activity???????????????????????????????????????????????????????????????????????????????????????????????????????????????null
                .setAnimator(DefaultAnimator())
                // ????????????????????????????????????????????????
                .setAppFloatAnimator(AppFloatDefaultAnimator())
                // ?????????????????????????????????????????????
//                .setFilter(MainActivity::class.java, SecondActivity::class.java)
                // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                .setDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) }
            // ??????????????????????????????????????????????????????????????????????????????touchEvent?????????????????????????????????
                // ps?????????Kotlin DSL??????????????????????????????????????????????????????????????????
                // ?????????????????????????????????????
                .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        timeTv = null
        delayTimesTv = null
    }


}