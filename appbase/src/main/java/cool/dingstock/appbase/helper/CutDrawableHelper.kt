package cool.dingstock.appbase.helper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

/**
 * 对Drawable资源进行缩放
 */
object CutDrawableHelper {
    var old_width:Int? = null
    var old_height:Int? = null
    /**
     * 对要求的drawable资源进行缩放或者放大，use_scale为true时使用缩放比例
     */
    fun ZoomDrawableImage(drawable:Drawable, user_scale:Boolean, new_width:Float?, new_height:Float?, scale_width:Float?, scale_height:Float?):Drawable{
        old_width = drawable.intrinsicWidth
        old_height = drawable.intrinsicHeight
        val old_bitMap:Bitmap = DrawableToBitMap(drawable)
        val matrix:Matrix = Matrix()
        if(user_scale){
            matrix.postScale(scale_width!!, scale_height!!)
        }else
        {
            val scale_width_create:Float = new_width!!/old_width!!
            val scale_height_create:Float = new_height!!/old_height!!
            matrix.postScale(scale_width_create, scale_height_create)
        }
        val newBitMap:Bitmap = Bitmap.createBitmap(old_bitMap, 0, 0, old_width!!, old_height!!, matrix, true)
        return BitmapDrawable(newBitMap)
    }
    /**
     * 将Drawable转换为BitMap
     */
    private fun DrawableToBitMap(drawable: Drawable):Bitmap{
        val width:Int = drawable.intrinsicWidth  //获取宽度、高度
        val height:Int = drawable.intrinsicHeight
        val config:Bitmap.Config =  if(drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565//获取颜色格式
        val bitmap:Bitmap = Bitmap.createBitmap(width, height, config) //创建BitMap流
        val canvas: Canvas = Canvas(bitmap) //绘制
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }
}
