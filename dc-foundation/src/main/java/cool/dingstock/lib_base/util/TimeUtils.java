package cool.dingstock.lib_base.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangshuwen on 2017/6/21.
 */
public class TimeUtils {

    /**
     * 把 秒 换算成 时、分、秒 int
     */
    public static Triple<Integer, Integer, Integer> processSeconds(int seconds) {
        Logger.d("seconds: " + seconds);
        return new Triple<>(
                seconds / (60 * 60),
                seconds / 60,
                seconds % 60
        );
    }


    public static String formatTimeSeconds(int seconds) {
        String timeString = (seconds / 60 < 10 ? ("0" + seconds / 60) : String.valueOf(seconds / 60))
                + ":"
                + (seconds % 60 < 10 ? ("0" + seconds % 60) : String.valueOf(seconds % 60));
        Logger.i("convert seconds: " + seconds + " to time: " + timeString);
        return timeString;
    }


    public static String formatTimestamp(String timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date myDate = null;
        try {
            myDate = format.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (null == myDate) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(myDate);
    }

    public static String formatTimestampCurrent() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    public static String formatTimestampS(long timestamp) {
        if (timestamp < Math.pow(10, 11)) {
            timestamp = timestamp * 1000;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestampS2(long timestamp) {
        if (timestamp < Math.pow(10, 11)) {
            timestamp = timestamp * 1000;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestampS3(long timestamp) {
        if (timestamp < Math.pow(10, 11)) {
            timestamp = timestamp * 1000;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestampS4(long timestamp) {
        if (timestamp < Math.pow(10, 11)) {
            timestamp = timestamp * 1000;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestampS5(long timestamp) {
        if (timestamp < Math.pow(10, 11)) {
            timestamp = timestamp * 1000;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestamp(long timestamp, String format) {
        if (timestamp < Math.pow(10, 11)) {
            timestamp = timestamp * 1000;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestampS2NoYear(long timestamp) {
        if (timestamp < Math.pow(10, 11)) {
            timestamp = timestamp * 1000;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestampS2NoYea2r(long timestamp) {
        if (timestamp < Math.pow(10, 11)) {
            timestamp = timestamp * 1000;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestamp(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestampM(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestampY(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestampMD(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String formatTimestampCustom(long timestamp, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(timestamp));
    }


    public static String getDynamicTime(long timestamp) {
        if (timestamp < Math.pow(10, 11)) {
            timestamp = timestamp * 1000;
        }
        long currentTimeMillis = System.currentTimeMillis();
        int reduceTime = (int) ((currentTimeMillis - timestamp) / 1000);
        if (reduceTime < 300) {
            return "刚刚";
        }
        int minutes = reduceTime / 60;
        if (minutes < 60) {
            return minutes + "分钟前";
        }
        int hours = reduceTime / 3600;
        if (hours < 24) {
            return hours + "小时前";
        }
        int days = reduceTime / 3600 / 24;
        if (days < 10) {
            return days + "天前";
        }
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        if (currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            return formatTimestampM(timestamp);
        } else {
            return formatTimestampY(timestamp);
        }
    }

    public static String getFashionTime(long timestamp) {
        if (timestamp < Math.pow(10, 11)) {
            timestamp = timestamp * 1000;
        }
        long currentTimeMillis = System.currentTimeMillis();
        int reduceTime = (int) ((currentTimeMillis - timestamp) / 1000);
        if (reduceTime < 300) {
            return "刚刚";
        }
        int minutes = reduceTime / 60;
        if (minutes < 60) {
            return minutes + "分钟前";
        }
        int hours = reduceTime / 3600;
        if (hours < 24) {
            return hours + "小时前";
        }
        int days = reduceTime / 3600 / 24;
        if (days < 10) {
            return days + "天前";
        }
        if (days > 10) {
            return "";
        }
        return formatTimestampY(timestamp);
    }

    public static Long getMouthFirstDay(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static String getRaffleTime(long timestamp) {
        if (timestamp < Math.pow(10, 11)) {
            timestamp = timestamp * 1000;
        }
        long currentTimeMillis = System.currentTimeMillis();
        int reduceTime = (int) ((timestamp - currentTimeMillis) / 1000);
        if (reduceTime < 60) {
            return reduceTime + "秒";
        }
        int minutes = reduceTime / 60;
        if (minutes < 60) {
            return minutes + "分钟";
        }
        int hours = reduceTime / 3600;
        if (hours < 24) {
            return hours + "小时";
        }
        int days = reduceTime / 3600 / 24;
        if (days < 365) {
            return days + "天";
        }
        int year = days / 365;
        return year + "年";
    }

    public static long getCurrentTime() {
        return new Date().getTime();
    }


    public static long format2Mill(long l) {
        if (l < 10000000000L) {
            return l * 1000;
        }
        return l;
    }

    public static String getWeekDay(long l) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(l);
        int i = instance.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
        }
        return "";
    }


}
