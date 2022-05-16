package cool.dingstock.appbase.customerview

import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import cool.dingstock.appbase.R
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.SizeUtils
import kotlinx.parcelize.Parcelize
import kotlin.math.max

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/30  15:22
 */
class TimeTextView : SurfaceView, SurfaceHolder.Callback, Runnable {
    //
    var targetTime: Long = 0
    var interval = 20L
    var platTimeOffset = 0L
    var offSett = 0L
    var clockType = ClockTimeType.Clock

    private lateinit var mSurfaceHolder: SurfaceHolder

    //绘图的Canvas
    private lateinit var mCanvas: Canvas

    //画笔
    private lateinit var paintTime: Paint

    //画笔
    private lateinit var paintTag: Paint

    //子线程标志位
    private var mIsDrawing = false

    //
    var canDraw = true

    var timeBaseLine: Float = 0f
    var timeStartX: Float = 0f
    var timeStartY: Float = 0f

    var tagBaseline: Float = 0f
    var hourCenter: Float = 0f
    var minuteCenter: Float = 0f
    var secondCenter: Float = 0f
    var mescCenter: Float = 0f
    var showMsec = true
    var showTag = true
    var mBackgroundColor = Color.WHITE

    var currentHourLength = 0
    var mWidth = 0;

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributes,
        defStyleAttr
    ) {

    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {

    }

    constructor(context: Context) : super(context) {

    }

    init {
        initView();
    }

    /**
     * 初始化View
     */
    private fun initView() {
        mSurfaceHolder = holder
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT)
        //注册回调方法
        mSurfaceHolder.addCallback(this)
        //设置一些参数方便后面绘图
        isFocusable = true
        keepScreenOn = true
        isFocusableInTouchMode = true
        paintTime = Paint()
        paintTime.color =  resources.getColor(R.color.color_text_black1)//Color.BLACK
        paintTime.isAntiAlias = true
        paintTime.textSize = SizeUtils.dp2px(40f).toFloat()
        paintTime.isFakeBoldText = true

        paintTag = Paint()
        paintTag.color = Color.parseColor("#C5CDD9");
        paintTag.isAntiAlias = true
        paintTag.textSize = SizeUtils.dp2px(10f).toFloat()
        paintTag.textAlign = Paint.Align.CENTER
        mBackgroundColor = resources.getColor(R.color.white)
    }


    fun setTimeColor(color: Int) {
        paintTime.color = color
    }

    fun setTimeTextSize(size: Float) {
        paintTime.textSize = SizeUtils.sp2px(size).toFloat()
    }

    fun setTagColor(color: Int) {
        paintTag.color = color
    }

    fun setTagTextSize(size: Float) {
        paintTag.textSize = SizeUtils.sp2px(size).toFloat()
    }


    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }


    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mIsDrawing = false;
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mIsDrawing = true;
        //开启子线程
        Thread(this).start()
    }

    override fun run() {
        while (mIsDrawing) {
            if (canDraw) {
                drawSomething()
            }
            SystemClock.sleep(interval)
        }
        Logger.d("TimeTextView", "destory")
    }

    //绘图逻辑
    private fun drawSomething() {
        try {
            //获得canvas对象
            mCanvas = mSurfaceHolder.lockCanvas()
            mCanvas.drawColor(mBackgroundColor)
            //绘制背景
            val currentTimeMillis =
                when (clockType) {
                    ClockTimeType.Clock -> {
                        currentTime() - offSett
                    }
                    ClockTimeType.Countdown -> {
                        val time = targetTime - currentTime() + offSett
                        max(time, 0)
                    }
                }
            if (clockType == ClockTimeType.Countdown) {
                reMeasure(currentTimeMillis, true)
            }

            val timeText =
                if (showMsec) getTimeByTimestamp(currentTimeMillis) else getTimeByTimestamp2(
                    currentTimeMillis
                )

            mCanvas.drawText(timeText, 0, timeText.length, timeStartX, timeBaseLine, paintTime)

            if (showTag) {
                mCanvas.drawText("时", timeStartX + hourCenter, tagBaseline, paintTag)
                mCanvas.drawText("分", timeStartX + minuteCenter, tagBaseline, paintTag)
                mCanvas.drawText("秒", timeStartX + secondCenter, tagBaseline, paintTag)
                if (showMsec) {
                    mCanvas.drawText("毫秒", timeStartX + mescCenter, tagBaseline, paintTag)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (mCanvas != null) {
                    //释放canvas对象并提交画布
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas)
                }
            } catch (e: java.lang.Exception) {
            }
        }
    }

    private fun currentTime() = System.currentTimeMillis() + platTimeOffset

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = mMeasureWidth(widthMeasureSpec).toInt()
        setMeasuredDimension(
            mWidth,
            mMeasureHeight(heightMeasureSpec).toInt()
        )
        //计算位置
        timeBaseLine = getBaseline(paintTime)
        //计算高度
        timeStartY = paintTime.fontMetrics.descent - paintTime.fontMetrics.ascent
        when (clockType) {
            ClockTimeType.Countdown -> {
                reMeasure(targetTime - currentTime() + offSett)
            }
            ClockTimeType.Clock -> {
                reMeasure(0)
            }
        }
    }

    private fun reMeasure(time: Long, isReMeasure: Boolean = false) {
        var time = time
        if (time < 0) {
            time = 0
        }
        var hourStr = getHourStr(time)
        if (isReMeasure) {
            if (currentHourLength == hourStr.length) {
                return
            }
            currentHourLength = hourStr.length
        }
        if (showMsec) {
            timeStartX =
                (mWidth - paintTime.measureText("${hourStr}:00:00.000")) / 2
        } else {
            timeStartX =
                (mWidth - paintTime.measureText("${hourStr}:00:00.0")) / 2
        }
        tagBaseline = getBaseline(paintTag) + timeStartY
        //下面开始画时分秒毫秒
        hourCenter = paintTime.measureText(hourStr) / 2
        minuteCenter = paintTime.measureText("${hourStr}:") + paintTime.measureText("00") / 2
        secondCenter = paintTime.measureText("${hourStr}:00:") + paintTime.measureText("00") / 2
        mescCenter = paintTime.measureText("${hourStr}:00:00.") + paintTime.measureText("000") / 2

    }

    private fun mMeasureHeight(measureSpec: Int): Float {
        Log.e("measure", "measure")
        var result = 0f
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize.toFloat()
        } else if (specMode == MeasureSpec.AT_MOST) {
            val fontMetrics = paintTime.fontMetrics
            val fontMetricsTag = paintTag.fontMetrics
            if (showTag) {
                return fontMetrics.bottom - fontMetrics.top + fontMetricsTag.bottom - fontMetricsTag.top
            } else {
                return fontMetrics.bottom - fontMetrics.top
            }
        } else if (specMode == MeasureSpec.UNSPECIFIED) {
            val fontMetrics = paintTime.fontMetrics
            val fontMetricsTag = paintTag.fontMetrics
            if (showTag) {
                return fontMetrics.bottom - fontMetrics.top + fontMetricsTag.bottom - fontMetricsTag.top
            } else {
                return fontMetrics.bottom - fontMetrics.top
            }
        }
        return result
    }

    private fun mMeasureWidth(measureSpec: Int): Float {
        Log.e("measure", "measure")
        var result = 0f
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize.toFloat()
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                val fontMetrics = paintTime.fontMetrics
                val fontMetricsTag = paintTag.fontMetrics
                if (showMsec) {
                    when (clockType) {
                        ClockTimeType.Clock -> {
                            return paintTime.measureText("00:00:00.000")
                        }
                        ClockTimeType.Countdown -> {
                            return paintTime.measureText(getTimeByTimestamp(currentTime() - System.currentTimeMillis()))
                        }
                    }

                } else {
                    when (clockType) {
                        ClockTimeType.Clock -> {
                            return paintTime.measureText("00:00:00.0")
                        }
                        ClockTimeType.Countdown -> {
                            return paintTime.measureText(getTimeByTimestamp2(currentTime() - System.currentTimeMillis()))
                        }
                    }
                }
            }
        }
        return result
    }

    private fun getHourStr(time: Long): String {
        val hour = time / (1000 * 60 * 60)
        return get2String(hour)
    }


    fun requestDraw() {
        drawSomething()
    }

    /**
     * 计算绘制文字时的基线到中轴线的距离
     *
     * @param p
     * @param centerY
     * @return 基线和centerY的距离
     */
    fun getBaseline(p: Paint): Float {
        val fontMetrics: Paint.FontMetrics = p.fontMetrics
        val height = p.fontMetrics.descent - p.fontMetrics.ascent
        return height / 2 + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
    }

    fun getTimeByTimestamp(time: Long): String {
        var time = time
        if (clockType == ClockTimeType.Clock) {
            time += 8 * (1000 * 60 * 60).toLong() // 时间戳0 是 8点开始 所以给一个偏移量
            time %= (1000 * 60 * 60 * 24)
        }
        val hour = time / (1000 * 60 * 60)
        time %= (1000 * 60 * 60).toLong()
        val minute = time / (1000 * 60)
        time %= (1000 * 60).toLong()
        val second = time / 1000
        val millis = time % 1000
        return get2String(hour) + ":" + get2String(minute) + ":" + get2String(second) + "." + get3String(
            millis
        )
    }

    fun getTimeByTimestamp2(time: Long): String {
        var time = time
        if (clockType == ClockTimeType.Clock) {
            time += 8 * (1000 * 60 * 60).toLong() // 时间戳0 是 8点开始 所以给一个偏移量
            time %= (1000 * 60 * 60 * 24)
        }
        val hour = time / (1000 * 60 * 60)
        time %= (1000 * 60 * 60).toLong()
        val minute = time / (1000 * 60)
        time %= (1000 * 60).toLong()
        val second = time / 1000
        val millis = time % 1000
        return get2String(hour) + ":" + get2String(minute) + ":" + get2String(second) + "." + get1String(
            millis
        )
    }

    fun get1String(l: Long): String {
        return (l / 100).toString()
    }

    fun get2String(l: Long): String {
        return if (l < 10) {
            "0$l"
        } else {
            "" + l
        }
    }

    fun get3String(l: Long): String {
        return if (l > 10 && l < 100) {
            "0$l"
        } else if (l < 10) {
            "00$l"
        } else {
            "" + l
        }
    }

}

@Parcelize
enum class ClockTimeType : Parcelable {
    Clock, Countdown
}