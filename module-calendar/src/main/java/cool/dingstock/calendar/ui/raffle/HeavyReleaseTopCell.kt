package cool.dingstock.calendar.ui.raffle

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.*
import cool.dingstock.appbase.entity.bean.home.CalenderProductEntity
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.calendar.R
import cool.dingstock.calendar.databinding.CalendarItemTopHeavyReleaseBinding


class HeavyReleaseTopCell(var itemClickAction: (CalenderProductEntity) -> Unit) :
    QuickViewBindingItemBinder<CalenderProductEntity, CalendarItemTopHeavyReleaseBinding>() {
    override fun convert(
        holder: BinderVBHolder<CalendarItemTopHeavyReleaseBinding>,
        data: CalenderProductEntity
    ) {
        holder.viewBinding.sivTopCover.load(data.imageUrl)
        holder.viewBinding.ivSelectedFlag.hide(!data.hasSelected)
        if (data.hasSelected) {
            holder.viewBinding.sivTopCover.apply {
                strokeColor = context.getColorStateList(R.color.calendar_selected_border)
                strokeWidth = 2.dp
            }
        } else {
            holder.viewBinding.sivTopCover.strokeWidth = 0.dp
        }
        holder.viewBinding.root.setOnClickListener {
            if (data.hasSelected) {
                return@setOnClickListener
            }
            itemClickAction.invoke(data)
        }
    }

    override fun onCreateViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): CalendarItemTopHeavyReleaseBinding {
        return CalendarItemTopHeavyReleaseBinding.inflate(layoutInflater, parent, false)
    }
}


fun ShapeableImageView.showIndivator() {
    val shapeAppearanceModel3 = ShapeAppearanceModel.builder().apply {
        setAllCorners(RoundedCornerTreatment())
        setAllCornerSizes(20f)
        setRightEdge(object : TriangleEdgeTreatment(20f, false) {
            // center 位置 ， interpolation 角的大小
            override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
                super.getEdgePath(length, 35f, interpolation, shapePath)
            }
        })
    }.build()
    val drawable3 = MaterialShapeDrawable(shapeAppearanceModel3).apply {
        setTint(ContextCompat.getColor(context, R.color.colorPrimary))
        paintStyle = Paint.Style.FILL
    }
    this.setBackgroundDrawable(drawable3)

}