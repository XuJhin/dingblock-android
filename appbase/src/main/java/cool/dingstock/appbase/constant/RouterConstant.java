package cool.dingstock.appbase.constant;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

/**
 * @author WhenYoung
 * CreateAt Time 2021/1/5  15:14
 */
public interface RouterConstant {
    String SCHEME = "https";
    String HOST = "app.dingstock.net";
    String DC_SCHEME = "dingstock";

    static String getUri(String path) {
        return SCHEME + "://" + HOST + path;
    }

    @Contract(pure = true)
    @NonNull
    static String getSchemeHost() {
        return SCHEME + "://" + HOST;
    }

}
