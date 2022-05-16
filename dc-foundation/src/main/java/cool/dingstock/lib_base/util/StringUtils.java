package cool.dingstock.lib_base.util;

import android.text.TextUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SQ on 2017/7/15.
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        return null == str || str.length() < 1 || "null".equals(str.toLowerCase());
    }

    /**
     * 判断字符是否有emoji表情
     */
    public static boolean isEmojiCharacter(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }

        char[] chars = content.toCharArray();

        for (char c : chars) {
            if (isEmojiCharacter(c)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断字符是否是emoji
     */
    public static boolean isEmojiCharacter(char c) {
        return !((c == 0x0) ||
                (c == 0x9) ||
                (c == 0xA) ||
                (c == 0xD) ||
                ((c >= 0x20) && (c <= 0xD7FF)) ||
                ((c >= 0xE000) && (c <= 0xFFFD)) ||
                ((c >= 0x10000) && (c <= 0x10FFFF)));
    }

    /**
     * 判断字符串只包含数字、英文、字母
     */
    public static boolean containSymbol(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }

        String regex = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(content);
        return !match.matches();
    }

    /**
     * 判断字符串是否只包含中文、英文 数字、空格
     */
    public static boolean containZhEnNumSpace(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }

        String regex = "^[0-9a-zA-Z\\s\u4E00-\u9FA5]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(content);
        return match.matches();
    }

    /**
     * 判断字符串是否只包含中文、英文 数字
     */
    public static boolean containZhEnNum(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }

        String regex = "^[0-9a-zA-Z\u4E00-\u9FA5]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(content);
        return match.matches();
    }

    /**
     * 只有 中文字符
     */
    public static boolean isChineseString(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }

        String regex = "^[\u4E00-\u9FA5]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(content);
        return match.matches();
    }

    public static String removeChineseChar(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        String regex = "[\u4E00-\u9FA5]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll("");
    }

    /**
     * 清除语句中所有的标点符号
     */
    public static String clearAllPunctuation(String text) {
        String str = text.replaceAll("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]", "");
        return str.trim();
    }

    public static String splitStr(List<String> strList, String split) {
        if (CollectionUtils.isEmpty(strList)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String str : strList) {
            if (strList.indexOf(str) > 0) {
                sb = sb.append(split);
            }
            sb.append(str);
        }
        return sb.toString();
    }

    public static String filterStr(String text, String removeStr) {
        String str = text.replaceAll("\\s*", "").replaceAll("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]", "").replace(removeStr, "");
        return str.trim();
    }

    public static String formatInt(Integer i) {
        if (i == null) {
            return "0";
        }
        return String.valueOf(i);
    }

    public static String formatIntAsThousand(int i) {
        if (i <= 1000) {
            return i + "";
        } else {
            return String.format("%.1f", i / 1000f) + "k";
        }
    }

    public static boolean isWebLink(String str) {
        String pnt = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,7})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,7})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";
        return str.matches(pnt);
    }

    public static boolean concatWebLink(String str) {
        String pnt = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,7})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,7})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";
        Pattern compile = Pattern.compile(pnt, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(str);
        return matcher.find();
    }

    public static boolean compareVersion(String v1, String v2, boolean enableEquals) {
        try {
            String[] arr1 = v1.split("\\.");
            String[] arr2 = v2.split("\\.");
            int maxLength = arr1.length;
            if (arr2.length > maxLength) {
                maxLength = arr2.length;
            }
            String[] matchArr1 = new String[maxLength];
            String[] matchArr2 = new String[maxLength];
            for (int i = 0; i < maxLength; i++) {
                String str1 = "";
                if (i < arr1.length) {
                    str1 = arr1[i];
                } else {
                    str1 = "0";
                }
                String str2 = "";
                if (i < arr2.length) {
                    str2 = arr2[i];
                } else {
                    str2 = "0";
                }
                if (str2.length() > str1.length()) {
                    for (int j = 0; j < str2.length() - str1.length(); j++) {
                        str1 = "0" + str1;
                    }
                } else if (str2.length() < str1.length()) {
                    for (int j = 0; j < str1.length() - str2.length(); j++) {
                        str2 = "0" + str2;
                    }
                }
                matchArr1[i] = str1;
                matchArr2[i] = str2;
            }
            String intV1 = "";
            for (String s : matchArr1) {
                intV1 += s;
            }
            String intV2 = "";
            for (String s : matchArr2) {
                intV2 += s;
            }
            if (enableEquals) {
                return Long.parseLong(intV1) >= Long.parseLong(intV2);
            } else {
                return Long.parseLong(intV1) > Long.parseLong(intV2);
            }
        } catch (Exception e) {
        }
        return false;
    }

}
