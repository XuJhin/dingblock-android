package cool.dingstock.appbase.util;

import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

public class ColorUtils {
    private static final int ENABLE_ATTR = android.R.attr.state_enabled;
    private static final int CHECKED_ATTR = android.R.attr.state_checked;
    private static final int PRESSED_ATTR = android.R.attr.state_pressed;

    public static ColorStateList generateThumbColorWithTintColor(final int tintColor) {
        int[][] states = new int[][]{
                {-ENABLE_ATTR, CHECKED_ATTR},
                {-ENABLE_ATTR},
                {PRESSED_ATTR, -CHECKED_ATTR},
                {PRESSED_ATTR, CHECKED_ATTR},
                {CHECKED_ATTR},
                {-CHECKED_ATTR}
        };

        int[] colors = new int[]{
                tintColor - 0xAA000000,
                0xFFBABABA,
                tintColor - 0x99000000,
                tintColor - 0x99000000,
                tintColor | 0xFF000000,
                0xFFEEEEEE
        };
        return new ColorStateList(states, colors);
    }

    public static ColorStateList generateBackColorWithTintColor(final int tintColor) {
        int[][] states = new int[][]{
                {-ENABLE_ATTR, CHECKED_ATTR},
                {-ENABLE_ATTR},
                {CHECKED_ATTR, PRESSED_ATTR},
                {-CHECKED_ATTR, PRESSED_ATTR},
                {CHECKED_ATTR},
                {-CHECKED_ATTR}
        };

        int[] colors = new int[]{
                tintColor - 0xE1000000,
                0x10000000,
                tintColor - 0xD0000000,
                0x20000000,
                tintColor - 0xD0000000,
                0x20000000
        };
        return new ColorStateList(states, colors);
    }

    public static boolean isLightColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5) {
            return true; // It's a light color
        } else {
            return false; // It's a dark color
        }
    }


    /*
     * 从color1 渐变到 color2
     */
    public static int getColorChanges(int cl1, int cl2,float progress) {
        int R, G, B;
        if(progress <0){
           progress = 0f;
        }
        if(progress>1){
            progress = 1f;
        }
        // 颜色的渐变，应该把分别获取对应的三基色，然后分别进行求差值；这样颜色渐变效果最佳
        R = (int) (Color.red(cl1) + (Color.red(cl2) - Color.red(cl1)) * progress);
        G = (int) (Color.green(cl1) + (Color.green(cl2) - Color.green(cl1)) * progress);
        B = (int) (Color.blue(cl1) + (Color.blue(cl2) - Color.blue(cl1)) * progress);

        return Color.rgb(R, G, B);
    }



}