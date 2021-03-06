package cool.dingstock.appbase.widget.date_time_picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Date;

import cool.dingstock.appbase.R;
import cool.dingstock.appbase.widget.date_time_picker.number_picker.NumberPicker;

public class DateTimePicker extends FrameLayout {

    public static int YEAR = 0;
    public static int MONTH = 1;
    public static int DAY = 2;
    public static int HOUR = 3;
    public static int MIN = 4;

    private NumberPicker mYearSpinner;
    private NumberPicker mMonthSpinner;
    private NumberPicker mDaySpinner;
    private NumberPicker mHourSpinner;
    private NumberPicker mMinuteSpinner;
    private Calendar mDate;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private int minMonth = 1, minDay = 1, minHour = 0, minMinute = 0;
    private int maxMonth, maxDay, maxHour, maxMinute;
    private long millisecond;
    private OnDateTimeChangedListener mOnDateTimeChangedListener;

    private int[] displayType = {YEAR, MONTH, DAY, HOUR, MIN};

    private boolean showLabel = true;
    private int themeColor;
    private int textSize;


    public DateTimePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs);

        init(context);

    }

    public DateTimePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.DateTimePicker);
        showLabel = attributesArray.getBoolean(R.styleable.DateTimePicker_showLabel, true);
        themeColor = attributesArray.getColor(R.styleable.DateTimePicker_themeColor, ContextCompat.getColor(context, R.color.colorAccent));
        textSize = (int) px2dip(attributesArray.getDimensionPixelSize(R.styleable.DateTimePicker_textSize, (int) dip2px(15f)));
        attributesArray.recycle();
        init(context);

    }

    public DateTimePicker(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        mDate = Calendar.getInstance();
        mYear = mDate.get(Calendar.YEAR);
        mMonth = mDate.get(Calendar.MONTH) + 1;
        mHour = mDate.get(Calendar.HOUR_OF_DAY);
        mMinute = mDate.get(Calendar.MINUTE);
        millisecond = mDate.getTimeInMillis();

        inflate(context, R.layout.view_date_picker, this);


        mYearSpinner = this.findViewById(R.id.np_datetime_year);
        mYearSpinner.setMaxValue(2100);
        mYearSpinner.setMinValue(1900);
        mYearSpinner.setLabel("???");
        mYearSpinner.setValue(mYear);
        mYearSpinner.setFocusable(true);
        mYearSpinner.setFocusableInTouchMode(true);
        mYearSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);//??????NumberPicker????????????
        mYearSpinner.setOnValueChangedListener(mOnYearChangedListener);//??????NumberPicker???????????????????????????

        mMonthSpinner = this.findViewById(R.id.np_datetime_month);
        mMonthSpinner.setMaxValue(12);
        mMonthSpinner.setMinValue(1);
        mMonthSpinner.setLabel("???");
        mMonthSpinner.setValue(mMonth);
        mMonthSpinner.setFocusable(true);
        mMonthSpinner.setFocusableInTouchMode(true);
        mMonthSpinner.setFormatter(formatter);//??????????????????????????????????????????0
        mMonthSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mMonthSpinner.setOnValueChangedListener(mOnMonthChangedListener);

        mDaySpinner = this.findViewById(R.id.np_datetime_day);
        leapMonth();//?????????????????????????????????2???????????????
        mDay = mDate.get(Calendar.DAY_OF_MONTH);
        mDaySpinner.setFormatter(formatter);
        mDaySpinner.setLabel("???");
        mDaySpinner.setFocusable(true);
        mDaySpinner.setFocusableInTouchMode(true);
        mDaySpinner.setValue(mDay);
        mDaySpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mDaySpinner.setOnValueChangedListener(mOnDayChangedListener);

        mHourSpinner = this.findViewById(R.id.np_datetime_hour);
        mHourSpinner.setMaxValue(23);
        mHourSpinner.setMinValue(0);
        mYearSpinner.setFocusable(true);
        mHourSpinner.setFocusableInTouchMode(true);
        mHourSpinner.setLabel("???");
        mHourSpinner.setValue(mHour);
        mHourSpinner.setFormatter(formatter);
        mHourSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mHourSpinner.setOnValueChangedListener(mOnHourChangedListener);

        mMinuteSpinner = this.findViewById(R.id.np_datetime_minute);
        mMinuteSpinner.setMaxValue(59);
        mMinuteSpinner.setMinValue(0);
        mMinuteSpinner.setFocusable(true);
        mMinuteSpinner.setLabel("???");
        mMinuteSpinner.setFocusableInTouchMode(true);
        mMinuteSpinner.setValue(mMinute);
        mMinuteSpinner.setFormatter(formatter);
        mMinuteSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mMinuteSpinner.setOnValueChangedListener(mOnMinuteChangedListener);


        refreshUI();

    }

    private void refreshUI() {
        if (showLabel) {
            mYearSpinner.setLabel("???");
            mMonthSpinner.setLabel("???");
            mDaySpinner.setLabel("???");
            mHourSpinner.setLabel("???");
            mMinuteSpinner.setLabel("???");
        } else {
            mYearSpinner.setLabel("");
            mMonthSpinner.setLabel("");
            mDaySpinner.setLabel("");
            mHourSpinner.setLabel("");
            mMinuteSpinner.setLabel("");
        }

        mYearSpinner.setTextColor(themeColor);
        mYearSpinner.setTextSize(textSize);

        mMonthSpinner.setTextColor(themeColor);
        mMonthSpinner.setTextSize(textSize);

        mDaySpinner.setTextColor(themeColor);
        mDaySpinner.setTextSize(textSize);

        mHourSpinner.setTextColor(themeColor);
        mHourSpinner.setTextSize(textSize);

        mMinuteSpinner.setTextColor(themeColor);
        mMinuteSpinner.setTextSize(textSize);
    }

    private NumberPicker.OnValueChangeListener mOnYearChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal, EditText editText) {
            mYear = mYearSpinner.getValue();
            leapMonth();
            limitMaxMin();
            onDateTimeChanged();
        }

    };

    private NumberPicker.OnValueChangeListener mOnMonthChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal, EditText editText) {
            mMonth = mMonthSpinner.getValue();
            leapMonth();
            limitMaxMin();
            onDateTimeChanged();
        }
    };

    private NumberPicker.OnValueChangeListener mOnDayChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal, EditText editText) {
            mDay = mDaySpinner.getValue();
            limitMaxMin();
            onDateTimeChanged();
        }
    };

    private NumberPicker.OnValueChangeListener mOnHourChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal, EditText editText) {
            mHour = mHourSpinner.getValue();
            limitMaxMin();
            onDateTimeChanged();
        }
    };

    private NumberPicker.OnValueChangeListener mOnMinuteChangedListener = new NumberPicker.OnValueChangeListener() {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal, EditText editText) {
            mMinute = mMinuteSpinner.getValue();
//            limitMaxMin();
            onDateTimeChanged();
        }
    };

    //??????????????????<10?????????????????????0
    private static NumberPicker.Formatter formatter = value -> {
        String Str = String.valueOf(value);
        if (value < 10) {
            Str = "0" + Str;
        }
        return Str;
    };

    /*
     *???????????? ??????????????????View ??????????????????
     */
    public interface OnDateTimeChangedListener {
        void onDateTimeChanged(DateTimePicker view, long millisecond, int year, int month, int day, int hour, int minute);
    }


    /**
     * ??????????????????
     */
    private void onDateTimeChanged() {
        millisecond = StringUtils.conversionTime(mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":" + mMinute + ":00", "yyyy-MM-dd HH:mm:ss");
        if (mOnDateTimeChangedListener != null) {
            mOnDateTimeChangedListener.onDateTimeChanged(this, millisecond, mYear, mMonth, mDay, mHour, mMinute);
        }
    }

    /**
     * ????????????
     */
    private void leapMonth() {
        if (mMonth == 2) {
            if (isLeapYear(mYear)) {
                if (mDaySpinner.getMaxValue() != 29) {
                    mDaySpinner.setDisplayedValues(null);
                    mDaySpinner.setMinValue(1);
                    mDaySpinner.setMaxValue(29);
                }
            } else {
                if (mDaySpinner.getMaxValue() != 28) {
                    mDaySpinner.setDisplayedValues(null);
                    mDaySpinner.setMinValue(1);
                    mDaySpinner.setMaxValue(28);
                }
            }
        } else {
            switch (mMonth) {
                case 4:
                case 6:
                case 9:
                case 11:
                    if (mDaySpinner.getMaxValue() != 30) {
                        mDaySpinner.setDisplayedValues(null);
                        mDaySpinner.setMinValue(1);
                        mDaySpinner.setMaxValue(30);
                    }
                    break;
                default:
                    if (mDaySpinner.getMaxValue() != 31) {
                        mDaySpinner.setDisplayedValues(null);
                        mDaySpinner.setMinValue(1);
                        mDaySpinner.setMaxValue(31);
                    }
            }
        }


        if (mYear == mYearSpinner.getMinValue() && mMonth == mMonthSpinner.getMinValue()) {
            mDaySpinner.setMinValue(minDay);
        }

        mDay = mDaySpinner.getValue();

    }

    /**
     * ?????????2???????????????
     *
     * @return
     */
    private int leapMonth2days(int year) {
        if (mMonth == 2) {
            if (isLeapYear(year)) {
                return 29;
            } else {
                return 28;
            }
        } else {
            switch (mMonth) {
                case 4:
                case 6:
                case 9:
                case 11:
                    return 30;
                default:
                    return 31;
            }
        }
    }

    private void limitMaxMin() {
        //?????????????????????
        if (mYear == mYearSpinner.getMinValue())
            mMonthSpinner.setMinValue(minMonth);
        else
            mMonthSpinner.setMinValue(1);
        //?????????????????????
        if (mYear == mYearSpinner.getMaxValue())
            mMonthSpinner.setMaxValue(maxMonth);
        else
            mMonthSpinner.setMaxValue(12);
        mMonth = mMonthSpinner.getValue();
        /******************************************************/
        //??????????????????
        if (mYear == mYearSpinner.getMinValue() && mMonth == mMonthSpinner.getMinValue())
            mDaySpinner.setMinValue(minDay);
        else
            mDaySpinner.setMinValue(1);
        //??????????????????
        if (mYear == mYearSpinner.getMaxValue() && mMonth == mMonthSpinner.getMaxValue())
            mDaySpinner.setMaxValue(maxDay);
        else
            mDaySpinner.setMaxValue(leapMonth2days(mYear));
        mDay = mDaySpinner.getValue();
        /******************************************************/
        //?????????????????????
        if (mYear == mYearSpinner.getMinValue() && mMonth == mMonthSpinner.getMinValue() && mDay == mDaySpinner.getMinValue())
            mHourSpinner.setMinValue(minHour);
        else
            mHourSpinner.setMinValue(0);

        //?????????????????????
        if (mYear == mYearSpinner.getMaxValue() && mMonth == mMonthSpinner.getMaxValue() && mDay == mDaySpinner.getMaxValue())
            mHourSpinner.setMaxValue(maxHour);
        else
            mHourSpinner.setMaxValue(23);
        mHour = mHourSpinner.getValue();
        /******************************************************/
        //?????????????????????
        if (mYear == mYearSpinner.getMinValue() && mMonth == mMonthSpinner.getMinValue()
                && mDay == mDaySpinner.getMinValue() && mHour == mHourSpinner.getMinValue())
            mMinuteSpinner.setMinValue(minMinute);
        else
            mMinuteSpinner.setMinValue(0);
        //?????????????????????
        if (mYear == mYearSpinner.getMaxValue() && mMonth == mMonthSpinner.getMaxValue()
                && mDay == mDaySpinner.getMaxValue() && mHour == mHourSpinner.getMaxValue())
            mMinuteSpinner.setMaxValue(maxMinute);
        else
            mMinuteSpinner.setMaxValue(59);
        mMinute = mMinuteSpinner.getValue();
    }

    /**
     * ????????????????????????dp ??????px(??????)
     */
    private float dip2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }


    /**
     * ????????????????????????px(??????) ??????dp
     */
    private float px2dip(float pxValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (pxValue / scale + 0.5f);
    }

    /**
     * ???????????????
     *
     * @param year ????????????
     * @return true or false
     */
    private boolean isLeapYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(year, 2, 1);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.get(Calendar.DAY_OF_MONTH) == 28;
    }

    /*
     *?????????????????????
     */
    public void setOnDateTimeChangedListener(OnDateTimeChangedListener callback) {
        mOnDateTimeChangedListener = callback;
        onDateTimeChanged();
    }

    /**
     * ??????????????????
     *
     * @param types
     */
    public void setDisplayType(int[] types) {
        if (types == null || types.length <= 0)
            return;
        this.displayType = types;
        mYearSpinner.setVisibility(GONE);
        mMonthSpinner.setVisibility(GONE);
        mDaySpinner.setVisibility(GONE);
        mHourSpinner.setVisibility(GONE);
        mMinuteSpinner.setVisibility(GONE);
        for (int i = 0; i < types.length; i++) {
            if (displayType[i] == YEAR)
                mYearSpinner.setVisibility(VISIBLE);
            if (displayType[i] == MONTH)
                mMonthSpinner.setVisibility(VISIBLE);
            if (displayType[i] == DAY)
                mDaySpinner.setVisibility(VISIBLE);
            if (displayType[i] == HOUR)
                mHourSpinner.setVisibility(VISIBLE);
            if (displayType[i] == MIN)
                mMinuteSpinner.setVisibility(VISIBLE);
        }
    }

    /**
     * ?????????????????????
     *
     * @param time
     */
    public void setDefaultMillisecond(long time) {
        if (time != 0)
            mDate.setTime(new Date(time));
        mYear = mDate.get(Calendar.YEAR);
        mMonth = mDate.get(Calendar.MONTH) + 1;
        mDay = mDate.get(Calendar.DAY_OF_MONTH);
        mHour = mDate.get(Calendar.HOUR_OF_DAY);
        mMinute = mDate.get(Calendar.MINUTE);
        millisecond = mDate.getTimeInMillis();

        mYearSpinner.setValue(mYear);
        mMonthSpinner.setValue(mMonth);
        mDaySpinner.setValue(mDay);
        mHourSpinner.setValue(mHour);
        mMinuteSpinner.setValue(mMinute);

        limitMaxMin();
        onDateTimeChanged();
    }

    /**
     * ????????????????????????
     *
     * @param time
     */
    public void setMinMillisecond(long time) {
        if (time == 0)
            return;
        Calendar mDate = Calendar.getInstance();
        mDate.setTime(new Date(time));
        int mYear = mDate.get(Calendar.YEAR);
        minMonth = mDate.get(Calendar.MONTH) + 1;
        minDay = mDate.get(Calendar.DAY_OF_MONTH);
        minHour = mDate.get(Calendar.HOUR_OF_DAY);
        minMinute = mDate.get(Calendar.MINUTE);

        mYearSpinner.setMinValue(mYear);
        mMonthSpinner.setMinValue(mMonth);
        mDaySpinner.setMinValue(minDay);
        mHourSpinner.setMinValue(minDay);
        mMinuteSpinner.setMinValue(minDay);

        limitMaxMin();

        if (this.mDate.getTimeInMillis() < mDate.getTimeInMillis())
            setDefaultMillisecond(time);

    }

    /**
     * ????????????????????????
     *
     * @param time
     */
    public void setMaxMillisecond(long time) {
        if (time == 0)
            return;
        Calendar mDate = Calendar.getInstance();
        mDate.setTime(new Date(time));
        int mYear = mDate.get(Calendar.YEAR);
        maxMonth = mDate.get(Calendar.MONTH) + 1;
        maxDay = mDate.get(Calendar.DAY_OF_MONTH);
        maxHour = mDate.get(Calendar.HOUR_OF_DAY);
        maxMinute = mDate.get(Calendar.MINUTE);

        mYearSpinner.setMaxValue(mYear);
        mMonthSpinner.setMaxValue(maxMonth);
        mDaySpinner.setMaxValue(maxDay);
        mHourSpinner.setMaxValue(maxHour);
        mMinuteSpinner.setMaxValue(maxMinute);

        limitMaxMin();

        if (this.mDate.getTimeInMillis() > mDate.getTimeInMillis())
            setDefaultMillisecond(time);

    }

    /**
     * ????????????label
     *
     * @param b
     */
    public void showLabel(boolean b) {
        this.showLabel = b;
        refreshUI();
    }

    /**
     * ????????????
     *
     * @param color
     */
    public void setThemeColor(@ColorInt int color) {
        if (color == 0)
            return;
        this.themeColor = color;
        refreshUI();
    }

    /**
     * ????????????
     *
     * @param sp
     */
    public void setTextSize(@Dimension int sp) {
        this.textSize = sp;
        refreshUI();
    }


}
