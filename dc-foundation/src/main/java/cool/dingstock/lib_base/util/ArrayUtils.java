package cool.dingstock.lib_base.util;

public class ArrayUtils {

    private ArrayUtils() {
    }

    public static boolean isEmpty(Object[] array) {
        return null == array || array.length < 1;
    }

    public static boolean isNotEmpty(Object[] array) {
        return null != array && array.length > 0;
    }

}
