package cool.dingstock.appbase.widget.tablayout

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import cool.dingstock.appbase.imageload.GlideHelper
import cool.dingstock.lib_base.util.SizeUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/27  10:15
 */
class SimplePagerImgTitleView(
    context: Context,
    var bigWidth: Int,
    var bigHeight: Int,
    var mPadding: Float,
    var zoom: Float = 0.7f
) : AppCompatImageView(context), IMeasurablePagerTitleView {
    var imgUrl: String = ""
        set(value) {
            field = value
            GlideHelper.loadRadiusImage(context, value, this, 0f)
        }

    init {
        val layoutParams = ViewGroup.LayoutParams(
            (bigWidth * zoom).toInt() + SizeUtils.dp2px(mPadding.toFloat()),
            (bigHeight * zoom).toInt() + SizeUtils.dp2px(mPadding.toFloat())
        )
        setLayoutParams(layoutParams)
    }


    override fun getContentRight(): Int {
        return left + width - SizeUtils.dp2px(mPadding.toFloat())
    }

    override fun getContentLeft(): Int {
        return left + SizeUtils.dp2px(mPadding.toFloat())
    }

    override fun getContentBottom(): Int {
        return top + height - SizeUtils.dp2px(mPadding.toFloat())
    }

    override fun getContentTop(): Int {
        return top + SizeUtils.dp2px(mPadding.toFloat())
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        scaleX = zoom
        scaleY = zoom
    }

    override fun onSelected(index: Int, totalCount: Int) {
        scaleX = 1f
        scaleY = 1f
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        scaleX = (1 - (1 - zoom) * leavePercent).toFloat()
        scaleY = (1 - (1 - zoom) * leavePercent).toFloat()
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        scaleX = (zoom + (1 - zoom) * enterPercent).toFloat()
        scaleY = (zoom + (1 - zoom) * enterPercent).toFloat()
    }

}