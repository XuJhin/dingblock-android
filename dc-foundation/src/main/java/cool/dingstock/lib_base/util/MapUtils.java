package cool.dingstock.lib_base.util;

import java.util.Map;

public class MapUtils {

    private MapUtils() {
    }

    public static boolean isEmpty(Map map) {
        return null == map || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return null != map && !map.isEmpty();
    }

}
