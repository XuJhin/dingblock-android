package cool.dingstock.appbase.imageload

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import cool.dingstock.appbase.R
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.helper.CutDrawableHelper
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.appbase.util.cellularConnected
import cool.dingstock.appbase.util.wifiConnected
import cool.dingstock.imagepicker.views.roundImageView.RoundImageView
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.util.StringUtils
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object GlideHelper {

    @SuppressLint("CheckResult")
    fun loadCircleImage(url: String?, target: ImageView, view: View) {
        val options = RequestOptions.circleCropTransform()
        options.placeholder(ContextCompat.getDrawable(target.context, R.drawable.img_load))
        options.error(ContextCompat.getDrawable(target.context, R.drawable.load_failed))
        Glide.with(view)
                .load(url)
                .apply(options)
                .into(target)
    }

    fun loadCircle(url: String?, target: ImageView, view: View, error: Int) {
        val options = RequestOptions
                .circleCropTransform()
                .fallback(error)
                .placeholder(ContextCompat.getDrawable(target.context, R.drawable.img_load))
                .error(ContextCompat.getDrawable(target.context, R.drawable.load_failed))
                .error(error)
        Glide.with(view)
                .load(url)
                .apply(options)
                .into(target)
    }


    fun loadRadiusImageWithCutDrawable(url: String?, target: ImageView, view: View, radius: Float, scaleWidth: Float, scaleHeight: Float) {
        val imgRadius = SizeUtils.dp2px(radius)
        val multi = MultiTransformation(
                CenterCrop(),
                RoundedCornersTransformation(imgRadius, 0)
        )
        val loading = ContextCompat.getDrawable(target.context, R.drawable.img_load)?.let {
            CutDrawableHelper.ZoomDrawableImage(it, false, scaleWidth, scaleHeight, null, null)
        }
        val loadFail = ContextCompat.getDrawable(target.context, R.drawable.load_failed)?.let {
            CutDrawableHelper.ZoomDrawableImage(it, false, scaleWidth, scaleHeight, null, null)
        }
        val options = RequestOptions
                .bitmapTransform(multi)
                .placeholder(loading)
                .error(loadFail)
        Glide.with(view)
                .load(url ?: "")
                .apply(options)
                .into(target)
    }


    fun loadRadiusImage(url: String?, target: ImageView, view: View, radius: Float) {
        val imgRadius = SizeUtils.dp2px(radius)
        val multi = MultiTransformation(
                CenterCrop(),
                RoundedCornersTransformation(imgRadius, 0)
        )
        val options = RequestOptions
                .bitmapTransform(multi)
                .placeholder(ContextCompat.getDrawable(target.context, R.drawable.img_load))
                .error(ContextCompat.getDrawable(target.context, R.drawable.load_failed))
        Glide.with(view)
                .load(url ?: "")
                .apply(options)
                .into(target)
    }

    fun loadRadiusImage(context: Context, url: String?, target: ImageView, radius: Float) {
        val imgRadius = SizeUtils.dp2px(radius)
        val multi = MultiTransformation(
                CenterCrop(),
                RoundedCornersTransformation(imgRadius, 0)
        )
        val options = RequestOptions
                .bitmapTransform(multi)
                .placeholder(ContextCompat.getDrawable(target.context, R.drawable.img_load))
                .error(ContextCompat.getDrawable(target.context, R.drawable.load_failed))
        Glide.with(context)
                .load(url ?: "")
                .apply(options)
                .into(target)
    }

    fun loadCircleVideoCover(url: String?, target: ImageView, view: View) {
        val radius = SizeUtils.dp2px(2f)
        val multi = MultiTransformation(
                CenterCrop(),
                RoundedCornersTransformation(radius, 0, RoundedCornersTransformation.CornerType.TOP)
        )

        val options = RequestOptions
                .bitmapTransform(multi)
                .placeholder(ContextCompat.getDrawable(target.context, R.drawable.img_load))
                .error(ContextCompat.getDrawable(target.context, R.drawable.load_failed))
        Glide.with(view)
                .load(url ?: "")
                .apply(options)
                .into(target)
    }

    fun loadRadiusImage(uri: Uri?, target: ImageView, context: Context, radius: Float) {
        val imgRadius = SizeUtils.dp2px(radius)
        val multi = MultiTransformation(
                CenterCrop(),
                RoundedCornersTransformation(imgRadius, 0)
        )
        val options = RequestOptions
                .bitmapTransform(multi)
                .placeholder(ContextCompat.getDrawable(target.context, R.drawable.img_load))
                .error(ContextCompat.getDrawable(target.context, R.drawable.load_failed))
        Glide.with(context)
                .load(uri ?: "")
                .apply(options)
                .into(target)
    }

    fun loadRadiusImage(file: File?, target: ImageView, context: Context, radius: Float) {
        val imgRadius = SizeUtils.dp2px(radius)
        val multi = MultiTransformation(
                CenterCrop(),
                RoundedCornersTransformation(imgRadius, 0)
        )
        val options = RequestOptions
                .bitmapTransform(multi)
                .placeholder(ContextCompat.getDrawable(target.context, R.drawable.img_load))
                .error(ContextCompat.getDrawable(target.context, R.drawable.load_failed))
        Glide.with(context)
                .load(file ?: "")
                .apply(options)
                .into(target)
    }

    fun loadRadiusImage(url: String?, target: ImageView, context: Context, radius: Float) {
        val imgRadius = SizeUtils.dp2px(radius)
        val multi = MultiTransformation(
                CenterCrop(),
                RoundedCornersTransformation(imgRadius, 0)
        )
        val options = RequestOptions
                .bitmapTransform(multi)
                .placeholder(ContextCompat.getDrawable(target.context, R.drawable.img_load))
                .error(ContextCompat.getDrawable(target.context, R.drawable.load_failed))
        Glide.with(context)
                .load(url ?: "")
                .apply(options)
                .into(target)
    }


    fun loadCircle(
            avatarUrl: String?,
            target: ImageView,
            activity: AppCompatActivity,
            @DrawableRes error: Int
    ) {
        val options = RequestOptions
                .circleCropTransform()
                .fallback(ContextCompat.getDrawable(target.context, error))
                .placeholder(ContextCompat.getDrawable(target.context, R.drawable.img_load))
                .error(ContextCompat.getDrawable(target.context, error))
        Glide.with(activity)
                .load(avatarUrl ?: "")
                .apply(options)
                .into(target)
    }

    fun loadCircle(
            file: File,
            target: ImageView,
            activity: AppCompatActivity,
            @DrawableRes error: Int
    ) {
        val options = RequestOptions
                .circleCropTransform()
                .fallback(ContextCompat.getDrawable(target.context, error))
                .placeholder(ContextCompat.getDrawable(target.context, R.drawable.img_load))
                .error(ContextCompat.getDrawable(target.context, error))
        Glide.with(activity)
                .load(file)
                .apply(options)
                .into(target)
    }

    fun loadOverlay(iconOverlay: Int, userAvatar: RoundImageView, itemView: View) {
        Glide.with(itemView)
                .load(iconOverlay)
                .into(userAvatar)
    }

    private fun getFileOnlyInCache(context: Context, url: String): Flowable<File> {
        return try {
            Flowable.create<File>({
                val file = Glide.with(context).downloadOnly().load(url)
                        .apply(RequestOptions().onlyRetrieveFromCache(true)).submit().get()
                it.onNext(file)
            }, BackpressureStrategy.BUFFER)
                    .compose(RxSchedulers.netio_main())
        } catch (e: Exception) {
            e.printStackTrace()
            Flowable.error(Exception())
        }
    }


    fun loadGifInGlideCacheWithOutWifi(
            context: Context,
            target: ImageView,
            gifTag: View,
            dynamicUrl: String?,
            defaultUrl: String?,
            radius: Float,
            checkWifi: Boolean
    ) {
        if (StringUtils.isEmpty(dynamicUrl)) {
            loadRadiusImage(defaultUrl, target, context, radius)
            gifTag.hide(true)
            return
        }
        if (!isGif(dynamicUrl)) {
            gifTag.hide(true)
            loadRadiusImage(dynamicUrl, target, context, radius)
            return
        }
        //先加载缩略图
        getFileOnlyInCache(context, dynamicUrl!!)
                .subscribe({
                    gifTag.hide(true)
                    loadRadiusImage(it, target, context, radius)
                }, {
                    if (checkWifi) {//需要判断Wifi 环境才加载
                        var loadDynamic = false
                        if (context.wifiConnected()) {
                            if (SettingHelper.getInstance().isWifiAutoGif) {
                                loadDynamic = true
                            }
                        } else if (context.cellularConnected()) {
                            if (SettingHelper.getInstance().isCellularAutoGif) {
                                loadDynamic = true
                            }
                        }
                        if (loadDynamic) {
                            loadRadiusImage(dynamicUrl, target, context, radius)
                        } else {
                            loadRadiusImage(defaultUrl, target, context, radius)
                            gifTag.hide(false)
                        }
                    } else {
                        loadRadiusImage(dynamicUrl, target, context, radius)
                    }
                })
    }

    fun isGif(url: String?): Boolean {
        return url?.endsWith("gif") ?: false
    }


    fun getGifFirstFrame(file: File): Flowable<File> {
        return Flowable.create<File>({
            try {
                val gif = BitmapFactory.decodeFile(file.absolutePath)
                val firstFrame =
                        Bitmap.createBitmap(gif?.width ?: 0, gif?.height
                                ?: 0, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(firstFrame)
                canvas.drawBitmap(gif, 0f, 0f, Paint())
                canvas.save()
                val newFileName = file.absolutePath.replace(".gif", "first_frame.png")
                val newFile = File(newFileName)
                if (newFile.exists() && newFile.isDirectory) {
                    newFile.delete()
                    newFile.createNewFile()
                }
                val bos = BufferedOutputStream(FileOutputStream(newFile))
                firstFrame.compress(Bitmap.CompressFormat.JPEG, 80, bos)
                bos.flush()
                bos.close()
                it.onNext(newFile)
            } catch (e: IOException) {
                e.printStackTrace()
                Flowable.error<File>(e)
            }
        }, BackpressureStrategy.BUFFER).compose(RxSchedulers.netio_main())

    }

    fun getImageFile(context: Context, url: String?): Flowable<File> {
        return Flowable.create<File?>({
            try {
                val file = Glide.with(context).downloadOnly().load(url).submit().get()
                it.onNext(file)
            } catch (e: Exception) {
                e.printStackTrace()
                Flowable.error<File?>(e)
            }
        }, BackpressureStrategy.BUFFER).compose(RxSchedulers.netio_main())
    }

    fun getGifFirstFrame(context: Context, url: String?): Flowable<File> {
        return Flowable.create<File?>({
            try {
                val drawable = Glide.with(context).asGif().load(url).submit().get()
                val newFile = File("${context.getExternalFilesDir("cache")}/first_frame.png")
                if (newFile.exists()) {
                    newFile.delete()
                    newFile.createNewFile()
                }
                BufferedOutputStream(FileOutputStream(newFile)).use { bos ->
                    drawable.firstFrame.compress(Bitmap.CompressFormat.PNG, 60, bos)
                    bos.flush()
                }
                it.onNext(newFile)
            } catch (e: Exception) {
                e.printStackTrace()
                Flowable.error<File?>(e)
            }
        }, BackpressureStrategy.BUFFER).compose(RxSchedulers.netio_main())
    }
}