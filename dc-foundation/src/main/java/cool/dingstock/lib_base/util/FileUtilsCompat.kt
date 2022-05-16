package cool.dingstock.lib_base.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableOnSubscribe
import okio.buffer
import okio.sink
import okio.source
import top.zibin.luban.InputStreamProvider
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.*


/**
 * 类名：FileUtilsComp
 * 包名：cool.dingstock.lib_base.util
 * 创建时间：2021/6/25 11:52 上午
 * 创建人： WhenYoung
 * 描述：S
 **/
object FileUtilsCompat {

    const val IMG_MIME_TYPE = "image/jpg"

    /**
     * 保存图片
     *
     * */
    fun saveBitmap(context: Context, bitmap: Bitmap): String? {
        try {
            val values = ContentValues()
            val imageName = "dcImg_" + System.currentTimeMillis().toString() + ".jpg"
            values.put(MediaStore.Images.Media.DESCRIPTION, imageName)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
            values.put(MediaStore.Images.Media.MIME_TYPE, IMG_MIME_TYPE)
            values.put(MediaStore.Images.Media.TITLE, imageName)
            values.put(
                MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES
                        + File.separator + "DingStock"
            )
            values.put(MediaStore.MediaColumns.IS_PENDING, 1)
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val ops = resolver.openOutputStream(uri!!)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ops)
            ops?.flush()
            ops?.close()
            values.clear()
            values.put(MediaStore.MediaColumns.IS_PENDING, 0)
            values.putNull(MediaStore.MediaColumns.DATE_EXPIRES)
            resolver.update(uri, values, null, null)
            return "/storage/emulated/0/" + Environment.DIRECTORY_PICTURES + File.separator + "DingStock" + File.separator + imageName
        } catch (e: Exception) {
        }
        return null
    }

    fun saveBitmapInOwner(context: Context, bitmap: Bitmap): String? {
        try {
            val dir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + "/DingStock/" + System.currentTimeMillis() + ".jpg"
            val file = File(dir)
            if (!file.exists()) {
                val mkdirs = file.parentFile.mkdirs()
                val newFile = file.createNewFile()
            }
            val fileOutputStream = FileOutputStream(file)
            val bufferedOutputStream = BufferedOutputStream(fileOutputStream)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
            bufferedOutputStream.flush()
            bufferedOutputStream.close()
            return file.absolutePath
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 通过文件路径获取图片访问Uri
     * */
    fun getImgUri(context: Context, absFileName: String): Uri? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return Uri.fromFile(File(absFileName))
        }
        val filePath = absFileName.substring(0, absFileName.lastIndexOf("/") + 1)
            .replace("/storage/emulated/0/", "")
        val fileName = absFileName.substring(absFileName.lastIndexOf("/") + 1)
        val selection =
            "${MediaStore.Images.Media.RELATIVE_PATH} = ? AND ${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val sel = arrayOf(filePath, fileName)
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            selection,
            sel,
            null
        )
        while (cursor?.moveToNext() == true) {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        }
        return null
    }

    fun isGif(url: String?): Boolean {
        return url?.endsWith("gif") ?: false
    }

    fun lubanComposeFilePath(context: Context, fileName: String, uriPath: String?): Flowable<File> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var uri =
                if (!TextUtils.isEmpty(uriPath)) {
                    Uri.parse(uriPath)
                } else {
                    getImgUri(context, fileName)
                }
            return Flowable.create<File>(FlowableOnSubscribe {
                if (uri == null) {
                    it.onError(java.lang.Exception("文件未找到"))
                    it.onComplete()
                    return@FlowableOnSubscribe
                }
                val isp = object : InputStreamProvider {
                    override fun open(): InputStream {
                        val ips = context.contentResolver.openInputStream(uri)
                            ?: throw java.lang.Exception("文件未找到")
                        return ips
                    }

                    override fun getPath(): String {
                        return fileName
                    }
                }
                Luban.with(context)
                    .load(isp)
                    .ignoreBy(100)
                    .setTargetDir(FileUtils.getDCCompressImgDirCompat())
                    .filter { path -> !TextUtils.isEmpty(path) }
                    .setCompressListener(object : OnCompressListener {
                        override fun onStart() {
                        }

                        override fun onSuccess(file: File?) {
                            it.onNext(file)
                            it.onComplete()
                        }

                        override fun onError(e: Throwable?) {
                            it.onError(e)
                            it.onComplete()
                        }
                    })
                    .launch()
            }, BackpressureStrategy.BUFFER)
                .flatMap { oldFile ->
                    return@flatMap Flowable.create<File>(FlowableOnSubscribe {
                        //如果没有生成一张新的图片，那么就需要复制这张图片到内部储存空间
                        if (oldFile.absolutePath == fileName) {
                            val newComposeFile =
                                File(FileUtils.getDCCompressImgDirCompat() + "/composeImg_" + System.currentTimeMillis() + ".jpg")
                            if (uri == null) {
                                it.onError(Exception("文件未找到"))
                                it.onComplete()
                            }
                            context.contentResolver.openInputStream(uri!!)
                                ?.let { copyToFile(it, newComposeFile) }
                            it.onNext(newComposeFile)
                            it.onComplete()
                        } else {
                            it.onNext(oldFile)
                            it.onComplete()
                        }
                    }, BackpressureStrategy.BUFFER)
                }
                .compose(RxSchedulers.io_main())
        } else {
            return Flowable.create<File>(FlowableOnSubscribe<File> {
                Luban.with(context)
                    .load(fileName)
                    .ignoreBy(100)
                    .setTargetDir(FileUtils.getDCCompressImgDirCompat())
                    .filter { path -> !TextUtils.isEmpty(path) }
                    .setCompressListener(object : OnCompressListener {
                        override fun onStart() {
                        }

                        override fun onSuccess(file: File?) {
                            it.onNext(file)
                            it.onComplete()
                        }

                        override fun onError(e: Throwable?) {
                            it.onError(e)
                            it.onComplete()
                        }
                    })
                    .launch()
            }, BackpressureStrategy.BUFFER)

        }
    }

    fun lubanCopyFile(context: Context, fileName: String, uriPath: String?): Flowable<File> {
        val uri = if (!TextUtils.isEmpty(uriPath)) {
            Uri.parse(uriPath)
        } else {
            getImgUri(context, fileName)
        }
        return Flowable.create(FlowableOnSubscribe {
            if (uri == null) {
                it.onError(java.lang.Exception("文件未找到"))
                it.onComplete()
                return@FlowableOnSubscribe
            }
            val newComposeFile = copyUriToExternalFilesDir(context, uri)
            if (newComposeFile != null) {
                it.onNext(newComposeFile)
            } else {
                it.onError(Throwable("no file"))
            }
            it.onComplete()
        }, BackpressureStrategy.BUFFER)
    }

    /**
     * 同步执行
     * */
    fun luanComposeFilePathSync(context: Context, fileName: String, uriPath: String?): File? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var uri =
                if (!TextUtils.isEmpty(uriPath)) {
                    Uri.parse(uriPath)
                } else {
                    getImgUri(context, fileName)
                }
            if (uri == null) {
                return null
            }
            if (isGif(fileName)) {
                return copyUriToExternalFilesDir(context, uri)
            }
            val isp = object : InputStreamProvider {
                override fun open(): InputStream {
                    val ips = context.contentResolver.openInputStream(uri)
                        ?: throw java.lang.Exception("文件未找到")
                    return ips
                }

                override fun getPath(): String {
                    return fileName
                }
            }
            val composeFile = Luban
                .with(context)
                .load(isp)
                .ignoreBy(100)
                .setTargetDir(FileUtils.getDCCompressImgDirCompat())
                .filter { path -> !TextUtils.isEmpty(path) }
                .get()
                .get(0)
            if (composeFile.absolutePath == fileName) {
                val newComposeFile =
                    File(FileUtils.getDCCompressImgDirCompat() + "/composeImg_" + System.currentTimeMillis() + ".jpg")
                context.contentResolver.openInputStream(uri)?.let { copyToFile(it, newComposeFile) }
                return newComposeFile
            } else {
                return composeFile
            }

        } else {
            return Luban
                .with(context)
                .load(fileName)
                .ignoreBy(100)
                .setTargetDir(FileUtils.getDCCompressImgDirCompat())
                .filter { path -> !TextUtils.isEmpty(path) }
                .get()
                .get(0)
        }
    }

    //复制文件到应用程序的关联目录并返回绝对路径
    private fun copyUriToExternalFilesDir(context: Context, uri: Uri): File? {
        var fileName = "${System.currentTimeMillis()}.jpg"
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        context.contentResolver.query(uri, projection, null, null, null).use {
            it?.let {
                if (it.moveToFirst()) {
                    fileName =
                        it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                }
            }
        }
        val inputStream = context.contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val file = File("${FileUtils.getDCCompressImgDirCompat()}/$fileName")
            inputStream.use {
                file.sink().buffer().write(it.source().buffer().readByteArray()).close()
            }
            println(file.absolutePath)
            return file
        }
        return null
    }

    private fun copyToFile(inputStream: InputStream, destFile: File): Boolean {
        return try {
            if (destFile.exists()) {
                destFile.delete()
            }
            val out = FileOutputStream(destFile)
            try {
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } >= 0) {
                    out.write(buffer, 0, bytesRead)
                }
            } finally {
                out.flush()
                try {
                    out.fd.sync()
                } catch (e: java.lang.Exception) {
                } finally {
                    out.close()
                    inputStream.close()
                }
            }
            true
        } catch (e: java.lang.Exception) {
            false
        }
    }

    fun base64ToBitmap(img64: String): Bitmap {
        val bytes = Base64.decode(img64.trim(), Base64.NO_WRAP)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }


}