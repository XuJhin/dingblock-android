package cool.dingstock.lib_base.util;

import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;

public class Base64Utils {

    public static String encode(@NonNull String source) {
        try {
            return new String(Base64.encode(source.getBytes(), Base64.NO_WRAP), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return source;
    }

    public static String decode(@NonNull String source) {
        try {
            return new String(Base64.decode(source, Base64.NO_WRAP), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return source;
    }
}
