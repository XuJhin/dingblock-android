package cool.dingstock.lib_base.util;

import android.text.TextUtils;

import java.util.regex.Pattern;

public class UrlUtils {

    public static String getFileName(String url) {
        if (TextUtils.isEmpty(url)) {
            Logger.e("The url is empty.");
            return "";
        }

        int lastIndex = url.lastIndexOf("/") + 1;
        if (lastIndex > url.length()) {
            return "";
        }

        String fileName = url.substring(lastIndex, url.length());
        if (fileName.contains("?")) {
            int firstIndex = url.indexOf("?");
            fileName = fileName.substring(0, firstIndex);
        }

        return fileName;
    }

    public static boolean isHttpUrl(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            Logger.e("url text is empty");
            return false;
        }

        String regex = "(http|https):\\/\\/([\\w.]+\\/?)\\S*";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text).matches();
    }


}
