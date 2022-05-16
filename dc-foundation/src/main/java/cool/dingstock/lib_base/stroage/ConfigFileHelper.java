package cool.dingstock.lib_base.stroage;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import cool.dingstock.lib_base.BaseLibrary;
import cool.dingstock.lib_base.util.Logger;

public class ConfigFileHelper {

    private static final String CHARSET_NAME = "UTF-8";

    ConfigFileHelper() {
    }

    public static String getConfigJson(@NonNull String fileName) {
        String jsonStr = getSavedJson(fileName);
        jsonStr = !TextUtils.isEmpty(jsonStr) ? jsonStr : getDefaultJson(fileName);
        Logger.d(jsonStr);
        return jsonStr;
    }

    public static void saveConfigJson(@NonNull String fileName, @NonNull String jsonStr) {
        FileOutputStream outputStream = null;

        try {
            outputStream = BaseLibrary.getInstance().getContext().openFileOutput(fileName, 0);
            outputStream.write(jsonStr.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException var12) {
            var12.printStackTrace();
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }
            }

        }

    }

    public static String getSavedJson(@NonNull String fileName) {
        try {
            return readInputStream(BaseLibrary.getInstance().getContext().openFileInput(fileName));
        } catch (FileNotFoundException var2) {
            return "";
        }
    }

    public static String getDefaultJson(@NonNull String fileName) {
        try {
            return readInputStream(BaseLibrary.getInstance().getContext().getAssets().open(fileName));
        } catch (IOException var2) {
            return "";
        }
    }

    private static String readInputStream(InputStream inputStream) {
        String jsonStr = "";

        String var3;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteOut.write(buffer, 0, len);
            }

            jsonStr = byteOut.toString("UTF-8");
            byteOut.flush();
            return jsonStr;
        } catch (FileNotFoundException var15) {
            var15.printStackTrace();
            var3 = jsonStr;
        } catch (IOException var16) {
            var16.printStackTrace();
            var3 = jsonStr;
            return var3;
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }

        }

        return var3;
    }
}
