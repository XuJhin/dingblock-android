package cool.dingstock.lib_base.util.image

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import cool.dingstock.lib_base.util.SizeUtils

object ImageUtils {
    @Synchronized
    fun getBitmap(context: Context): Bitmap? {
        var packageManager: PackageManager? = null
        var applicationInfo: ApplicationInfo? = null
        try {
            packageManager = context.applicationContext
                .packageManager
            applicationInfo = packageManager.getApplicationInfo(
                context.packageName, 0
            )
        } catch (e: PackageManager.NameNotFoundException) {

        }
        val d: Drawable? = applicationInfo?.run {
            return@run packageManager?.getApplicationIcon(applicationInfo!!) ?: return null
        }
        if (d == null) {
            return null
        }
        //xxx根据自己的情况获取drawable
        val APKicon: Bitmap
        APKicon = if (d is BitmapDrawable) {
            d.bitmap
        } else {
            val bitmap: Bitmap =
                Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_4444)
            val canvas = Canvas(bitmap)
            d.setBounds(0, 0, canvas.width, canvas.height)
            d.draw(canvas)
            bitmap
        }
        //        BitmapDrawable bd = (BitmapDrawable) d;
//        Bitmap bm = bd.getBitmap();
        return APKicon
    }


    fun setImgLayoutParams(
        iv: ImageView,
        width: Int?,
        height: Int?,
        defaultWidth: Float = 100f,
        defaultHeight: Float = 100f
    ) {
        //设置图片宽高
        val width: Int = width ?: 0
        val height: Int = height ?: 0
        val imageIvLayoutParams: ViewGroup.LayoutParams = iv.layoutParams
        if (width <= 0 || height <= 0) {
            imageIvLayoutParams.width = SizeUtils.dp2px(defaultWidth)
            imageIvLayoutParams.height = SizeUtils.dp2px(defaultHeight)
        } else if (width > height) {
            imageIvLayoutParams.width = SizeUtils.dp2px(defaultWidth)
            imageIvLayoutParams.height =
                (SizeUtils.dp2px(defaultHeight) * height.toFloat() / width).toInt()
        } else {
            imageIvLayoutParams.width =
                (SizeUtils.dp2px(defaultWidth) * width.toFloat() / height).toInt()
            imageIvLayoutParams.height = SizeUtils.dp2px(defaultHeight)
        }
        iv.layoutParams = imageIvLayoutParams
    }


}