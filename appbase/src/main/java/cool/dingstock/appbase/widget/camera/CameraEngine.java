package cool.dingstock.appbase.widget.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cool.dingstock.lib_base.util.Logger;
import cool.dingstock.lib_base.util.SizeUtils;

import static android.graphics.Bitmap.createBitmap;

public class CameraEngine implements Camera.PreviewCallback {
    /**
     * 后置摄像头 id
     */
    private int faceBackCameraId;
    /**
     * 前置摄像头 id
     */
    private int faceFrontCameraId;
    /**
     * 摄像头参数
     */
    private Camera.Parameters mParams;
    /**
     * 摄像头实体
     */
    private Camera mCamera;
    /**
     * 当前缩放比
     */
    private int nowScaleRate;
    /**
     * 当前摄像头ID
     */
    private int mCameraId;
    /**
     * 是否在预览模式
     */
    private boolean isPreviewing;

    private int mCameraAngle = 90;
    private int screenAngle;
    private int pictureAngle;

    private int handlerTime;

    CameraEngine(Context context) {
        init(context);
    }

    private void init(Context context) {
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; ++i) {
            final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            //后置摄像头
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                faceBackCameraId = i;
            }
            //前置摄像头
            else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                faceFrontCameraId = i;
            }
        }
        //默认后置
        mCameraId = faceBackCameraId;
        registerSensorManager(context);
    }

    void startPreview(Context context,
                      boolean faceBack,
                      float scale,
                      SurfaceTexture surface) {
        try {
            release(context, true);
            mCameraId = faceBack ? faceBackCameraId : faceFrontCameraId;
//            mCameraAngle = CameraParamUtil.getInstance().getCameraDisplayOrientation(context, mCameraId);
//
//            mCamera = Camera.open(mCameraId);
//            mParams = mCamera.getParameters();
////            Camera.Size previewSize = CameraParamUtil.getInstance().getPreviewSize(mParams
//                    .getSupportedPreviewSizes(), 1000, scale, 0.2f);
////            Camera.Size pictureSize = CameraParamUtil.getInstance().getPictureSize(mParams
//                    .getSupportedPictureSizes(), 1000, scale, 0.2f);
//            mParams.setPreviewSize(previewSize.width, previewSize.height);
//            mParams.setPictureSize(pictureSize.width, pictureSize.height);
////            if (CameraParamUtil.getInstance().isSupportedFocusMode(
////                    mParams.getSupportedFocusModes(),
////                    Camera.Parameters.FOCUS_MODE_AUTO)) {
////                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
////            }
////            if (CameraParamUtil.getInstance().isSupportedPictureFormats(mParams.getSupportedPictureFormats(),
////                    ImageFormat.JPEG)) {
////                mParams.setPictureFormat(ImageFormat.JPEG);
////                mParams.setJpegQuality(100);
////            }
            mCamera.setParameters(mParams);
            mParams = mCamera.getParameters();
            mCamera.setPreviewTexture(surface);
            //浏览角度
            mCamera.setDisplayOrientation(mCameraAngle);
            //每一帧回调
            mCamera.setPreviewCallback(this);
            //启动浏览
            mCamera.startPreview();
            isPreviewing = true;
            Logger.d("=== Start Preview ===");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void autoFocus() {
        try {
            if (null == mCamera) {
                return;
            }
            final Camera.Parameters params = mCamera.getParameters();
            //取消连续聚焦
            mCamera.cancelAutoFocus();
            final String currentFocusMode = params.getFocusMode();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(params);
            mCamera.autoFocus((success, camera) -> {
                if (success) {
                    Logger.d("autoFocus success");
                    Camera.Parameters params1 = camera.getParameters();
                    params1.setFocusMode(currentFocusMode);
                    camera.setParameters(params1);
                } else {
                    Logger.w("autoFocus failed ");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void handleFocus(final float x, final float y, final DCCameraFocusCallback callback) {
        try {
            if (null == mCamera) {
                return;
            }
            final Camera.Parameters params = mCamera.getParameters();
            Rect focusRect = calculateTapArea(x, y);
            //取消连续聚焦
            mCamera.cancelAutoFocus();
            if (params.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<>();
                focusAreas.add(new Camera.Area(focusRect, 800));
                params.setFocusAreas(focusAreas);
            } else {
                callback.onFocusFailed();
                return;
            }
            final String currentFocusMode = params.getFocusMode();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(params);
            mCamera.autoFocus((success, camera) -> {
                if (success || handlerTime > 10) {
                    Camera.Parameters params1 = camera.getParameters();
                    params1.setFocusMode(currentFocusMode);
                    camera.setParameters(params1);
                    handlerTime = 0;
                    callback.onFocusSuccess();
                    Logger.d("handleFocus success");
                } else {
                    Logger.d("handleFocus failed ");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            callback.onFocusFailed();
        }
    }


    void zoom(float diff) {
        if (null == mCamera || null == mParams) {
            return;
        }
        int scaleRate = (int) (diff / 10);
        if (scaleRate < mParams.getMaxZoom()) {
            nowScaleRate += scaleRate;
            if (nowScaleRate < 0) {
                nowScaleRate = 0;
            } else if (nowScaleRate > mParams.getMaxZoom()) {
                nowScaleRate = mParams.getMaxZoom();
            }
            mParams.setZoom(nowScaleRate);
            mCamera.setParameters(mParams);
        }
        Logger.d("nowScaleRate=" + nowScaleRate + " max=" + mParams.getMaxZoom());
    }

    void takePicture(final DCTakePictureCallback callback) {
        try {
            if (mCamera == null) {
                return;
            }
            switch (mCameraAngle) {
                case 90:
                    pictureAngle = Math.abs(screenAngle + mCameraAngle) % 360;
                    break;
                case 270:
                    pictureAngle = Math.abs(mCameraAngle - screenAngle);
                    break;
            }

            mCamera.takePicture(null, null, (data, camera) -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                if (mCameraId == faceBackCameraId) {
                    matrix.setRotate(pictureAngle);
                } else if (mCameraId == faceFrontCameraId) {
                    matrix.setRotate(360 - pictureAngle);
                    matrix.postScale(-1, 1);
                }
                bitmap = createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (callback != null) {
                    if (pictureAngle == 90 || pictureAngle == 270) {
                        callback.onTakeSuccess(bitmap, true);
                    } else {
                        callback.onTakeSuccess(bitmap, false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void doStopPreview() {
        if (null != mCamera) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                //这句要在stopPreview后执行，不然会卡顿或者花屏
                mCamera.setPreviewDisplay(null);
                isPreviewing = false;
                Logger.d("=== Stop Preview ===");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void release(Context context, boolean inner) {
        if (null != mCamera) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                //这句要在stopPreview后执行，不然会卡顿或者花屏
                mCamera.setPreviewDisplay(null);
                isPreviewing = false;
                mCamera.release();
                mCamera = null;
                if (!inner) {
                    unregisterSensorManager(context);
                }
                Logger.d("=== Destroy Camera ===");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Logger.w("=== Camera  Null===");
        }
    }

    void release(Context context) {
        release(context, false);
    }


    private SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
                return;
            }
            float[] values = event.values;
//            screenAngle = UdeskUtils.getSensorAngle(values[0], values[1]);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    private void registerSensorManager(Context context) {
        if (null == context) {
            return;
        }
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (null == sensorManager) {
            return;
        }
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensorManager(Context context) {
        if (null == context) {
            return;
        }
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (null == sensorManager) {
            return;
        }
        sensorManager.unregisterListener(sensorEventListener);
    }


    private Rect calculateTapArea(float x, float y) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize).intValue();
        int centerX = (int) (x / SizeUtils.getWidth() * 2000 - 1000);
        int centerY = (int) (y / SizeUtils.getHeight() * 2000 - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF
                .bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

}
