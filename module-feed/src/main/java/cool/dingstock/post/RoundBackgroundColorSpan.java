package cool.dingstock.post;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import cool.dingstock.lib_base.util.SizeUtils;

public class RoundBackgroundColorSpan extends ReplacementSpan {
    private int bgColor;
    private int textColor;

    public RoundBackgroundColorSpan(int bgColor, int textColor) {
        super();
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return ((int) paint.measureText(text, start, end) + SizeUtils.dp2px(10));
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color1 = paint.getColor();
        paint.setColor(this.bgColor);
        canvas.drawRoundRect(new RectF(x, top + 1, x + ((int) paint.measureText(text, start, end) + SizeUtils.dp2px(6)), bottom - 1),
                SizeUtils.dp2px(2),
                SizeUtils.dp2px(2),
                paint);
        paint.setColor(this.textColor);
        canvas.drawText(text, start, end, x + SizeUtils.dp2px(3), y, paint);
        paint.setColor(color1);
    }
}