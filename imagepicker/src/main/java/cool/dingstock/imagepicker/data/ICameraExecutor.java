package cool.dingstock.imagepicker.data;

import androidx.annotation.Nullable;

import cool.dingstock.imagepicker.bean.ImageItem;

public interface ICameraExecutor {

    void takePhoto();

    void takeVideo();

    void onTakePhotoResult(@Nullable ImageItem imageItem);
}
