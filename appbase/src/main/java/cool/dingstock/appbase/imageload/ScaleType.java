package cool.dingstock.appbase.imageload;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description: TODO
 * Author: Shper
 * Version: V0.1 2017/8/22
 */
@StringDef({ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER, ScaleType.FIT_END,
        ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE})
@Retention(RetentionPolicy.SOURCE)
public @interface ScaleType {
    String FIT_XY = "fitXY";
    String FIT_START = "fitStart";
    String FIT_CENTER = "fitCenter";
    String FIT_END = "fitEnd";
    String CENTER = "center";
    String CENTER_CROP = "centerCrop";
    String CENTER_INSIDE = "centerInside";
}