package cool.dingstock.lib_base;

import android.app.Application;

import java.lang.ref.WeakReference;

import cool.dingstock.lib_base.util.AppUtils;
import cool.dingstock.lib_base.util.Logger;

public class BaseLibrary {

    private static volatile BaseLibrary mInstance;

    private WeakReference<Application> applicationWeak;

    public static BaseLibrary getInstance() {
        if (null == mInstance) {
            synchronized (BaseLibrary.class) {
                if (null == mInstance) {
                    mInstance = new BaseLibrary();
                }
            }
        }
        return mInstance;
    }


    public static void initialize(Application application) {
        Logger.d("Start to init the XBase lib.");
        getInstance().applicationWeak = new WeakReference<>(application);
    }

    /**
     * 释放资源
     */
    public static void release() {
        Logger.d("Start to release XBase resource.");
    }


    public Application getContext() {
        if (null == applicationWeak || null == applicationWeak.get()) {
            applicationWeak = new WeakReference<>(AppUtils.INSTANCE.getApplicationContext());
        }
        return applicationWeak.get();
    }

}
