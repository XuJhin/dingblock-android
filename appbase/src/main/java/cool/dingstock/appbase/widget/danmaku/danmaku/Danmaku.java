package cool.dingstock.appbase.widget.danmaku.danmaku;

import androidx.annotation.DrawableRes;

/**
 * Created by LittleFogCat.
 */

public class Danmaku {
    public static final String COLOR_WHITE = "#ffffffff";
    public static final String COLOR_RED = "#ffff0000";
    public static final String COLOR_GREEN = "#ff00ff00";
    public static final String COLOR_BLUE = "#ff0000ff";
    public static final String COLOR_YELLOW = "#ffffff00";
    public static final String COLOR_PURPLE = "#ffff00ff";

    public static final int DEFAULT_TEXT_SIZE = 24;

    public String text;// 文字
    public int size = DEFAULT_TEXT_SIZE;// 字号
    public Mode mode = Mode.scroll;// 模式：滚动、顶部、底部
    public String color = COLOR_WHITE;// 默认白色
    public int background = 0;
    //left top right bottom
    public int[] padding = {0, 0, 0, 0};

    public enum Mode {
        scroll, top, bottom
    }

    public Danmaku() {
    }

    public Danmaku(String text, int textSize, Mode mode, String color, @DrawableRes int background, int[] padding) {
        this.text = text;
        this.size = textSize;
        this.mode = mode;
        this.color = color;
        this.background = background;
        this.padding = padding;
    }

    @Override
    public String toString() {
        return "Danmaku{" +
                "text='" + text + '\'' +
                ", textSize=" + size +
                ", mode=" + mode +
                ", color='" + color + '\'' +
                '}';
    }
}
