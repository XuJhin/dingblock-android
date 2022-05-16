package cool.dingstock.appbase.widget.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cool.dingstock.lib_base.util.SizeUtils;


public class DCFocusView extends View {

    private int size;
    private int center_x;
    private int center_y;
    private int length;
    private Paint mPaint;

    public DCFocusView(Context context) {
        this(context, null);
    }

    public DCFocusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DCFocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            this.size = SizeUtils.dp2px(80);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(Color.parseColor("#FF6C6C"));
            mPaint.setStrokeWidth(4);
            mPaint.setStyle(Paint.Style.STROKE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            center_x = (int) (size / 2.0);
            center_y = (int) (size / 2.0);
            length = (int) (size / 2.0) - 2;
            setMeasuredDimension(size, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            canvas.drawRect(center_x - length, center_y - length, center_x + length, center_y + length, mPaint);
            canvas.drawLine(2, getHeight() / 2, size / 10, getHeight() / 2, mPaint);
            canvas.drawLine(getWidth() - 2, getHeight() / 2, getWidth() - size / 10, getHeight() / 2, mPaint);
            canvas.drawLine(getWidth() / 2, 2, getWidth() / 2, size / 10, mPaint);
            canvas.drawLine(getWidth() / 2, getHeight() - 2, getWidth() / 2, getHeight() - size / 10, mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
