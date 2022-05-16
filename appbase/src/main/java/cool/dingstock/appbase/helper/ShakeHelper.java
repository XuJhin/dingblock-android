package cool.dingstock.appbase.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

public class ShakeHelper implements SensorEventListener {
    private Context mContext;
    private final MyHandler mHandler;
    private static final int START_SHAKE = 0x1;
    private static final int END_SHAKE = 0x2;
    //传感器管理器
    private SensorManager mSensorManager;
    //检测的时间间隔
    private static final int UPDATE_INTERVAL = 130;
    //上一次检测的时间
    private long mLastUpdateTime;
    //上一次检测时，加速度在x、y、z方向上的分量，用于和当前加速度比较求差。
    private float mLastX, mLastY, mLastZ;
    //摇晃检测阈值，决定了对摇晃的敏感程度，越小越敏感。
    private static final int shakeThreshold = 3000;
    //记录摇动状态
    private boolean isShake = false;

    private boolean isStartListener = false;

    private boolean isOpen = true;

    public ShakeHelper(Context mContext) {
        this.mContext = mContext;
        mHandler = new MyHandler();
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - mLastUpdateTime;
        if (diffTime < UPDATE_INTERVAL) {
            return;
        }
        mLastUpdateTime = currentTime;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float deltaX = x - mLastX;
        float deltaY = y - mLastY;
        float deltaZ = z - mLastZ;
        mLastX = x;
        mLastY = y;
        mLastZ = z;
        float delta =
                (float) (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 10000);
        // 当加速度的差值大于指定的阈值，认为这是一个摇晃
        if (delta > shakeThreshold && !isShake) {
            isShake = true;
            try {
                if (isValid()) {
                    mHandler.obtainMessage(START_SHAKE).sendToTarget();
                }
                //开始 展示动画效果
                //Thread.sleep(100);
                //再来一次提示
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mHandler.obtainMessage(END_SHAKE).sendToTarget();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void start() {
        if (!isOpen){
            return;
        }
        if(isStartListener){
           return;
        }
        isStartListener = true;
        if (mSensorManager != null) {
            //传感器
            Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mSensor != null) {
                mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    public void stop() {
        if(!isStartListener){
            return;
        }
        isStartListener = false;
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            //清空消息队列
            mHandler.removeCallbacks(null);
            //置位可摇一摇
            isShake = false;
            //关闭动画
        }
    }

    private static final long intervalAtMillis = 1000L;
    private static long sLastTime;

    public static boolean isValid() {
        //防止暴力摇动
        long currentTime = System.currentTimeMillis();

        if (currentTime - sLastTime < intervalAtMillis) {
            return false;
        }
        sLastTime = currentTime;
        return true;
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_SHAKE:
                    //发出动画触发,执行动画
                    startListener.onStart();
                    break;
                case END_SHAKE:
                    isShake = false;
                    break;
            }
        }
    }

    public void open(){
       isOpen = true;
       start();
    }

    public void close(){
        isOpen = false;
        stop();
    }


    //接口回调，摇一摇开始通知外部
    public interface onStartListener {
        void onStart();
    }

    public onStartListener startListener;

    public void setStartListener(onStartListener startListener) {
        this.startListener = startListener;
    }

}
