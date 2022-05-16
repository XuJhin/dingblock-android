package cool.dingstock.appbase.widget.date_time_picker.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import cool.dingstock.appbase.R
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.widget.date_time_picker.DateTimePicker
import cool.dingstock.appbase.widget.date_time_picker.StringUtils
import cool.dingstock.lib_base.util.ToastUtil
import java.util.*

/**
 *
 * @ProjectName:    DatePicker
 * @Package:        com.loper7.date_time_picker.dialog
 * @ClassName:      DateDateDateTimePickerDialog
 * @CreateDate:     2020/3/3 0003 11:38
 * @Description:    java类作用描述
 * @Author:         LOPER7
 * @Email:          loper7@163.com
 */
class CardDatePickerDialog(context: Context) : BottomSheetDialog(context), View.OnClickListener {
    companion object {
        const val CARD = 0//卡片
        const val CUBE = 1//方形
        const val STACK = 2//顶部圆角

        private var builder: Builder? = null
        fun builder(context: Context): Builder {
            builder = Builder(context)
            return builder!!
        }
    }

    private var builder: Builder? = null

    private var listener: OnChooseListener? = null

    private var tv_cancel: TextView? = null
    private var tv_submit: TextView? = null
    private var tv_title: TextView? = null
    private var tv_sub_title: TextView? = null
    private var tv_choose_date: TextView? = null
    private var btn_today: TextView? = null
    private var datePicker: DateTimePicker? = null
    private var tv_go_back: TextView? = null
    private var linear_now: LinearLayout? = null
    private var linear_bg: LinearLayout? = null
    private var mBehavior: BottomSheetBehavior<FrameLayout>? = null

    private var millisecond: Long = 0


    init {
        if (builder == null) {
            builder = Builder(context)
        }
    }

    constructor(context: Context, builder: Builder) : this(context) {
        this.builder = builder
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_time_picker)
        super.onCreate(savedInstanceState)
        val bottomSheet = delegate.findViewById<FrameLayout>(R.id.design_bottom_sheet)
        bottomSheet!!.setBackgroundColor(Color.TRANSPARENT)
        tv_cancel = findViewById(R.id.dialog_cancel)
        tv_submit = findViewById(R.id.dialog_submit)
        datePicker = findViewById(R.id.dateTimePicker)
        tv_title = findViewById(R.id.tv_title)
        tv_sub_title = findViewById(R.id.sub_title)
        btn_today = findViewById(R.id.btn_today)
        tv_choose_date = findViewById(R.id.tv_choose_date)
        tv_go_back = findViewById(R.id.tv_go_back)
        linear_now = findViewById(R.id.linear_now)
        linear_bg = findViewById(R.id.linear_bg)
        mBehavior = BottomSheetBehavior.from(bottomSheet)
        //背景模式
        if (builder!!.model != 0) {
            var parmas = FrameLayout.LayoutParams(linear_bg!!.layoutParams)
            when (builder!!.model) {
                CARD -> {
                    parmas.setMargins(dip2px(12f), dip2px(12f), dip2px(12f), dip2px(12f))
                    linear_bg!!.layoutParams = parmas
                    linear_bg!!.setBackgroundResource(R.drawable.shape_bg_round_white_5)
                }
                CUBE -> {
                    parmas.setMargins(0, 0, 0, 0)
                    linear_bg!!.layoutParams = parmas
                    linear_bg!!.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                }
                STACK -> {
                    parmas.setMargins(0, 0, 0, 0)
                    linear_bg!!.layoutParams = parmas
                    linear_bg!!.setBackgroundResource(R.drawable.shape_bg_top_round_white_15)
                }
                else -> {
                    parmas.setMargins(0, 0, 0, 0)
                    linear_bg!!.layoutParams = parmas
                    linear_bg!!.setBackgroundResource(builder!!.model)
                }
            }
        }
        //标题
        if (!TextUtils.isEmpty(builder!!.titleValue)) {
            tv_title!!.text = builder!!.titleValue
            tv_title!!.setPadding(10.dp.toInt())
        }

        tv_sub_title!!.hide(TextUtils.isEmpty(builder!!.subTitleValue))
        tv_sub_title!!.text = builder!!.subTitleValue

        tv_choose_date!!.hide(!builder!!.needChoseTv)
        

        //显示标签
        datePicker!!.showLabel(builder!!.dateLabel)
        //显示模式
        if (builder!!.displayTypes == null) {
            builder!!.displayTypes = intArrayOf(
                DateTimePicker.YEAR,
                DateTimePicker.MONTH,
                DateTimePicker.DAY,
                DateTimePicker.HOUR,
                DateTimePicker.MIN
            )
        }
        datePicker!!.setDisplayType(builder!!.displayTypes)
        //回到当前时间展示
        if (builder!!.displayTypes != null) {
            var year_month_day_hour = 0
            for (i in builder!!.displayTypes!!) {
                if (i == DateTimePicker.YEAR && year_month_day_hour <= 0) {
                    year_month_day_hour = 0
                    tv_go_back!!.text = "回到今年"
                    btn_today!!.text = "今"
                }
                if (i == DateTimePicker.MONTH && year_month_day_hour <= 1) {
                    year_month_day_hour = 1
                    tv_go_back!!.text = "回到本月"
                    btn_today!!.text = "本"
                }
                if (i == DateTimePicker.DAY && year_month_day_hour <= 2) {
                    year_month_day_hour = 2
                    tv_go_back!!.text = "回到今日"
                    btn_today!!.text = "今"
                }
                if ((i == DateTimePicker.HOUR || i == DateTimePicker.MIN) && year_month_day_hour <= 3) {
                    year_month_day_hour = 3
                    tv_go_back!!.text = "回到此刻"
                    btn_today!!.text = "此"
                }
            }

        }
        linear_now!!.visibility = if (builder!!.backNow) View.VISIBLE else View.GONE
        tv_choose_date!!.visibility = if (builder!!.focusDateInfo) View.VISIBLE else View.GONE

        //设置最小时间
        datePicker!!.setMinMillisecond(builder!!.minTime)
        //设置最大时间
        datePicker!!.setMaxMillisecond(builder!!.maxTime)

        datePicker!!.setDefaultMillisecond(builder!!.defaultMillisecond)

        datePicker!!.setTextSize(15)
        if (builder!!.themeColor != 0) {
            datePicker!!.setThemeColor(builder!!.themeColor)
            tv_submit!!.setTextColor(builder!!.themeColor)
            val gd = GradientDrawable()
            gd.setColor(builder!!.themeColor)
            gd.cornerRadius = dip2px(60f).toFloat()
            btn_today!!.background = gd
        }
        tv_cancel!!.setOnClickListener(this)
        tv_submit!!.setOnClickListener(this)
        btn_today!!.setOnClickListener(this)
        datePicker!!.setOnDateTimeChangedListener { _, millisecond, _, _, _, _, minute ->
            this.millisecond = millisecond
            tv_choose_date!!.text =
                (StringUtils.conversionTime(millisecond, "yyyy年MM月dd日 ") + StringUtils.getWeek(
                    millisecond
                ))
        }
    }

    override fun onStart() {
        super.onStart()
        mBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onClick(v: View) {
        if (listener == null)
            return
        when (v.id) {
            R.id.btn_today -> {
                listener?.onChoose(Calendar.getInstance().timeInMillis)
            }
            R.id.dialog_submit -> {
//                if (millisecond < System.currentTimeMillis()) {
//                    showTimeErrorToast()
//                    return
//                }
                listener?.onChoose(millisecond)
            }
        }
        this.dismiss()
    }

    private fun showTimeErrorToast() {
        ToastUtil.getInstance()._short(this.context, "设置跳转时间需要大于当前时间")
    }

    class Builder(var context: Context) {
        var backNow: Boolean = true
        var focusDateInfo: Boolean = true
        var dateLabel: Boolean = true
        var titleValue: String? = null
        var subTitleValue: String? = null
        var needChoseTv: Boolean = true
        var defaultMillisecond: Long = 0
        var minTime: Long = 0
        var maxTime: Long = 0
        var displayTypes: IntArray? = null
        var model: Int =
            CARD
        var themeColor: Int = 0

        /**
         * 设置标题
         */
        fun setTitle(value: String): Builder {
            this.titleValue = value
            return this
        }

        fun setSubTitle(value: String): Builder {
            this.subTitleValue = value
            return this
        }

        fun setNeedChoseTv(value: Boolean): Builder {
            this.needChoseTv = value
            return this
        }

        /**
         * 设置显示值
         */
        fun setDisplayType(vararg types: Int): Builder {
            this.displayTypes = types
            return this
        }

        /**
         * 设置显示值
         */
        fun setDisplayType(types: MutableList<Int>): Builder {
            this.displayTypes = types.toIntArray()
            return this
        }

        /**
         * 设置默认时间
         */
        fun setDefaultTime(millisecond: Long): Builder {
            this.defaultMillisecond = millisecond
            return this
        }

        /**
         * 设置范围最小值
         */
        fun setMinTime(millisecond: Long): Builder {
            this.minTime = millisecond
            return this
        }

        /**
         * 设置范围最大值
         */
        fun setMaxTime(millisecond: Long): Builder {
            this.maxTime = millisecond
            return this
        }


        /**
         * 是否显示回到当前
         */
        fun showBackNow(b: Boolean): Builder {
            this.backNow = b
            return this
        }

        /**
         * 是否显示选中日期信息
         */
        fun showFocusDateInfo(b: Boolean): Builder {
            this.focusDateInfo = b
            return this
        }

        /**
         * 是否显示单位标签
         */
        fun showDateLabel(b: Boolean): Builder {
            this.dateLabel = b
            return this
        }

        /**
         * 显示模式
         */
        fun setBackGroundModel(model: Int): Builder {
            this.model = model
            return this
        }

        /**
         * 设置主题颜色
         */
        fun setThemeColor(@ColorInt themeColor: Int): Builder {
            this.themeColor = themeColor
            return this
        }

        fun build(): CardDatePickerDialog {
            var dialog = CardDatePickerDialog(
                context,
                this
            )
            return dialog
        }
    }

    /**
     * 绑定监听
     */
    fun setOnChooseListener(listener: OnChooseListener): CardDatePickerDialog {
        this.listener = listener
        return this
    }

    interface OnChooseListener {
        fun onChoose(millisecond: Long)
    }


    /**
     * 根据手机的分辨率dp 转成px(像素)
     */
    private fun dip2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率px(像素) 转成dp
     */
    private fun px2dip(pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

}