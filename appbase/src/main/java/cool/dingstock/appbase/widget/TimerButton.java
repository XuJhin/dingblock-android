package cool.dingstock.appbase.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 自定义倒计时按钮
 * <p/>
 */
public class TimerButton extends AppCompatTextView {

    public static final long DEFAULT_LENGTH = 60 * 1000;
    /**
     * 倒计时时长，默认倒计时时间60秒；
     */
    private long length = DEFAULT_LENGTH;
    /**
     * 开始执行计时的类，可以在每秒实行间隔任务
     */
    private ScheduledExecutorService mExecuteTaskService;
    /**
     * 每秒时间到了之后所执行的任务
     */
    private Runnable timerTask;
    /**
     * 在点击按钮之前按钮所显示的文字，默认是获取验证码
     */
    private String beforeText = "获取验证码";
    private String rBeforeText = "重新获取";

    private boolean isRObtain = false;

    /**
     * 在开始倒计时之后那个秒数数字之后所要显示的字，默认是秒
     */

    public TimerButton(Context context) {
        this(context, null);
    }

    public TimerButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化操作
     */
    private void initView() {
        if (!TextUtils.isEmpty(getText())) {
            beforeText = getText().toString().trim();
        }
        this.setText(beforeText);
    }

    /**
     * 初始化时间
     */
    private void initTask() {
        stop();
        mExecuteTaskService = Executors.newSingleThreadScheduledExecutor();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
    }

    private void stop() {
        if (null != mExecuteTaskService) {
            mExecuteTaskService.shutdownNow();
            mExecuteTaskService = null;
        }
        setText(beforeText);
    }


    /**
     * 设置倒计时时长
     *
     * @param length 默认毫秒
     */
    public void setLength(long length) {
        this.length = length;
    }


    public long getLength() {
        return length;
    }

    /**
     * 设置未点击时显示的文字
     *
     * @param beforeText
     */
    public void setBeforeText(String beforeText) {
        this.beforeText = beforeText;
    }

    public String getrBeforeText() {
        return rBeforeText;
    }

    public void setrBeforeText(String rBeforeText) {
        this.rBeforeText = rBeforeText;
    }

    public boolean isRObtain() {
        return isRObtain;
    }

    public void setRObtain(boolean RObtain) {
        isRObtain = RObtain;
    }

    /**
     * 开始倒计时
     */
    public void start() {
        initTask();
        this.setText(String.valueOf(length / 1000));
        this.setEnabled(false);
        mExecuteTaskService.scheduleAtFixedRate(timerTask, 0, 1000, TimeUnit.MILLISECONDS);
    }


    /**
     * 更新显示的文本
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setText(length / 1000 + "s");
            length -= 1000;
            if (length <= 0) {
                TimerButton.this.setEnabled(true);
                if (isRObtain) {
                    TimerButton.this.setText(rBeforeText);
                } else {
                    TimerButton.this.setText(beforeText);
                }
                clearTimer();
                length = 60 * 1000;
            }
        }
    };

    /**
     * 清除倒计时
     */
    private void clearTimer() {
        if (timerTask != null) {
            timerTask = null;
        }
        if (mExecuteTaskService != null) {
            mExecuteTaskService.shutdownNow();
            mExecuteTaskService = null;
        }
    }

    /**
     * 记得一定要在activity或者fragment消亡的时候清除倒计时，
     * 因为如果倒计时没有完的话子线程还在跑，
     * 这样的话就会引起内存溢出
     */
    @Override
    protected void onDetachedFromWindow() {
        clearTimer();
        if (handler != null) {
            handler.removeMessages(1);
            handler = null;
        }
        super.onDetachedFromWindow();
    }

}