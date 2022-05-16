package cool.dingstock.lib_base.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

import cool.dingstock.lib_base.BaseLibrary;
import cool.dingstock.lib_base.util.image.SingleMediaScanner;

public class FileUtils {

    /**
     * 获取压缩存放的dir
     *
     * @return dir
     */
    public static String getDCCompressImgDirCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return BaseLibrary.getInstance().getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        } else {
            String path = Environment.getExternalStorageDirectory() + "/DingStock/image/";
            File file = new File(path);
            if (file.mkdirs()) {
                return path;
            }
            return path;
        }
    }


    public static String getDCScreenRecorderDirCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return BaseLibrary.getInstance().getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath();
        } else {
            String path = Environment.getExternalStorageDirectory() + "/DingStock/ScreenRecorder/";
            File file = new File(path);
            if (file.mkdirs()) {
                return path;
            }
            return path;
        }
    }

    public static String getDCCacheDirCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return BaseLibrary.getInstance().getContext().getExternalCacheDir().getAbsolutePath() + "/dcCache";
        } else {
            String path = Environment.getExternalStorageDirectory() + "/DingStock/dcCache";
            File file = new File(path);
            if (file.mkdirs()) {
                return path;
            }
            return path;
        }
    }

    /**
     * 拷贝图片到DCIM目录
     */
    public static boolean copyPhotoToDCIM(Context context, String imagePath) {
        try {
            File oldFile = new File(imagePath);
            String newName = oldFile.getName();
            if (!newName.contains(".png")) {
                newName = newName + ".png";
            }
            final File folder = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            final File newFile = new File(folder, newName);
            if (FileUtils.copyFile(oldFile, newFile)) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(newFile);
                intent.setData(uri);
                context.sendBroadcast(intent);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取String文件
     */
    public static String readFile(File file) {
        RandomAccessFile accessFile = null;
        byte[] bytes = new byte[0];
        try {
            accessFile = new RandomAccessFile(file, "r");
            bytes = new byte[(int) accessFile.length()];
            accessFile.readFully(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != accessFile) {
                    accessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String(bytes);
    }

    /**
     * 将图片存到本地
     */
    public static Uri saveBitmap(Bitmap bitmap) {
        Context context = BaseLibrary.getInstance().getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String path = FileUtilsCompat.INSTANCE.saveBitmap(context, bitmap);
            return FileUtilsCompat.INSTANCE.getImgUri(context, path);
        } else {
            try {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/DingStock/";
                String imageName = System.currentTimeMillis() + ".jpg";

                File file = new File(path + imageName);
                if (!file.exists()) {
                    boolean mkdirs = file.getParentFile().mkdirs();
                    boolean newFile = file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                //插入图片到系统图库
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), "", "");
                Uri uri = FileProvider.getUriForFile(context, "cool.dingstock.mobile.FileProvider", file);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(uri);
                //通知图库更新
                context.sendBroadcast(intent);
                new SingleMediaScanner(context, path.concat(imageName),
                        new SingleMediaScanner.ScanListener() {
                            @Override
                            public void onScanFinish() {

                            }
                        });
                return uri;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }
    }


    public static String saveBitmapToPath(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return FileUtilsCompat.INSTANCE.saveBitmap(BaseLibrary.getInstance().getContext(), bitmap);
        } else {
            try {
                String dir = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/DingStock/" + System.currentTimeMillis() + ".jpg";
                File file = new File(dir);
                if (!file.exists()) {
                    boolean mkdirs = file.getParentFile().mkdirs();
                    boolean newFile = file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                return file.getAbsolutePath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String saveBitmapToPathAndRefreshPhoto(Context context, Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String filePath = FileUtilsCompat.INSTANCE.saveBitmap(context, bitmap);
            Logger.e("saveBitmap", filePath);
            return filePath;
        } else {
            try {
                String dir = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/DingStock/" + System.currentTimeMillis() + ".jpg";
                File file = new File(dir);
                if (!file.exists()) {
                    boolean mkdirs = file.getParentFile().mkdirs();
                    boolean newFile = file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                Uri uri1 = Uri.fromFile(file);
                Intent intent1 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent1.setData(uri1);
                context.sendBroadcast(intent1);
                return file.getAbsolutePath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] readFileByte(File file) {
        RandomAccessFile accessFile = null;
        byte[] bytes = new byte[0];
        try {
            accessFile = new RandomAccessFile(file, "r");
            bytes = new byte[(int) accessFile.length()];
            accessFile.readFully(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != accessFile) {
                    accessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    public static void writeFile(String source, File installation) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(installation);
            out.write(source.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean fileIsExists(String path) {
        if (path == null || path.trim().length() <= 0) {
            return false;
        }
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public static String getPathFromDrawableRes(Context context, int id) {
        Resources resources = context.getResources();
        String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(id) + "/"
                + resources.getResourceTypeName(id) + "/"
                + resources.getResourceEntryName(id);
        return path;
    }


    public static String readInputStream(InputStream inputStream) {
        Logger.d("Start to read inputStream.");

        String jsonStr = "";

        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteOut.write(buffer, 0, len);
            }

            jsonStr = byteOut.toString("UTF-8");
            byteOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return jsonStr;
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonStr;
    }


    /**
     * 删除目录
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteDir(File dir) {
        if (dir == null) return false;
        // 目录不存在返回true
        if (!dir.exists()) return true;
        // 不是目录返回false
        if (!dir.isDirectory()) return false;
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!deleteFile(file)) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return dir.delete();
    }


    /**
     * 删除文件
     *
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }


    public static String getFileMD5(File file) {
        StringBuffer stringbuffer = null;
        try {
            char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            FileInputStream in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            messagedigest.update(byteBuffer);
            byte[] bytes = messagedigest.digest();
            int n = bytes.length;
            stringbuffer = new StringBuffer(2 * n);
            for (int l = 0; l < n; l++) {
                byte bt = bytes[l];
                char c0 = hexDigits[(bt & 0xf0) >> 4];
                char c1 = hexDigits[bt & 0xf];
                stringbuffer.append(c0);
                stringbuffer.append(c1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringbuffer.toString();

    }

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.flush();
                try {
                    out.getFD().sync();
                } catch (Exception e) {
                } finally {
                    out.close();
                    inputStream.close();
                }

            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取指定文件大小
     */
    public static long getFileSize(File file) {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 保存共享媒体资源，必须使用先在MediaStore创建表示视频保存信息的Uri，然后通过Uri写入视频数据的方式。
     * 在"分区存储"模型中，这是官方推荐的，因为在Android 10禁止通过File的方式访问媒体资源，Android 11又允许了
     * 从Android 10开始默认是分区存储模型
     * <p>
     * <p>
     * 说明：
     * 此方法中MediaStore默认的保存目录是/storage/emulated/0/video
     * 而Environment.DIRECTORY_MOVIES的目录是/storage/emulated/0/Movies
     *
     * @param context
     * @return
     */
    static Uri getSaveToGalleryVideoUri(Context context, String videoName, String mineType, String subDir) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DISPLAY_NAME, videoName);
        values.put(MediaStore.Video.Media.MIME_TYPE, mineType);
        values.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + subDir);
        }

        Uri uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
//        printMediaInfo(context, uri);
        return uri;
    }


    public static String save2OwnerFile(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return FileUtilsCompat.INSTANCE.saveBitmapInOwner(BaseLibrary.getInstance().getContext(), bitmap);
        } else {
            return saveBitmapToPath(bitmap);
        }
    }
}
