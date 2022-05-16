package cool.dingstock.appbase.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.LinearLayout;

public class PhotoUtils {

    public Bitmap createLongPhoto(LinearLayout linearLayout) {
        try {
            Bitmap bitmapAll = Bitmap.createBitmap(linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(bitmapAll);
            canvas.drawColor(Color.parseColor("#333B5A"));
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setDither(true);
            linearLayout.draw(canvas);
            return bitmapAll;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
