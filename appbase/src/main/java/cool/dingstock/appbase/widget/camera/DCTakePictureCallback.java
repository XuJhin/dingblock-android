package cool.dingstock.appbase.widget.camera;

import android.graphics.Bitmap;

public interface DCTakePictureCallback {

    void onTakeSuccess(Bitmap bitmap, boolean isVertical);
}
