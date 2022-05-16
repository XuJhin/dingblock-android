package cool.dingstock.appbase.util;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import cool.dingstock.lib_base.BaseLibrary;
import cool.dingstock.lib_base.util.Logger;

/**
 * 生成二维码
 */

public class QRUtils {

    private static final String OR_NAME = "alien_wifi_qr_";

    /**
     * @param text 二维码包含的信息
     * @param size bitmap大小单位 px
     *
     * @return
     */
    public static Bitmap createQRCode(String text, int size, int margin) {
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            // 指定编码格式
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            /// 指定纠错等级
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            //设置白边
            hints.put(EncodeHintType.MARGIN, margin);

            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }

                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static synchronized File getFile(@NonNull Bitmap bitmap) {
        String fileName = OR_NAME + System.currentTimeMillis() + ".png";
        Logger.d("Start save bitmap to get file, fileName: " + fileName);

        FileOutputStream outputStream = null;
        try {
            outputStream = BaseLibrary.getInstance().getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }

        if (null != outputStream) {
            return BaseLibrary.getInstance().getContext().getFileStreamPath(fileName);
        }

        return null;
    }

    public static synchronized void deleteQRFile(@NonNull String fileName) {
        Logger.d("Start save bitmap to delete file, fileName: " + fileName);
        BaseLibrary.getInstance().getContext().deleteFile(fileName);
    }

}
