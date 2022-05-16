package cool.dingstock.lib_base.util;

import android.content.Context;
import android.text.TextUtils;

import cool.dingstock.lib_base.BaseLibrary;

public class ResUtil {

    public static String getStringByRes(String strRes) {
        if (TextUtils.isEmpty(strRes)) {
            return "";
        }
        Context context = BaseLibrary.getInstance().getContext();
        int values = context.getResources().getIdentifier(strRes, "string", context.getPackageName());
        return context.getString(values);
    }


    public static int getDrawableByRes(String strRes) {
        if (TextUtils.isEmpty(strRes)) {
            return -1;
        }
        Context context = BaseLibrary.getInstance().getContext();
        int values = context.getResources().getIdentifier(strRes, "drawable", context.getPackageName());
        return values;
    }


}