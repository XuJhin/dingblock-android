package cool.dingstock.appbase.customerview.shade.guide

import android.content.Context
import android.graphics.*
import android.view.View

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/24  12:05
 */
class ShadeTransparencyView(context: Context) : View(context) {
    private var shadeColor = "#88000000"
    private var bgColor = "#00ffffff"
    private var holePaint: Paint = Paint()


    private val holeList = arrayListOf<GuideHoleIndicator>()

    init {
        holePaint.alpha = 0
        holePaint.isAntiAlias = true
        holePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        setBackgroundColor(Color.parseColor(bgColor))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas1 = Canvas(bitmap)
        //绘制阴影遮罩
        canvas1.drawColor(Color.parseColor(shadeColor))
        for (hole in holeList) {
            var holeRectF = RectF(hole.targetStart, hole.targetTop, hole.targetEnd, hole.targetBottom)
            //绘制透明穿孔
            canvas1.drawRoundRect(holeRectF, hole.targetRadius, hole.targetRadius, holePaint)
        }
        //将镂空层画到背景上
        canvas?.drawBitmap(bitmap, 0f, 0f, null)
    }


    fun setHoleList(holeList: ArrayList<GuideHoleIndicator>) {
        this.holeList.clear()
        this.holeList.addAll(holeList)
        invalidate()
    }


}