package cool.dingstock.foundation.ext

import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.DrawableCompat

fun AppCompatImageView.tint(tintColor: Int) {
    val drawable = this.drawable
    DrawableCompat.setTint(drawable, tintColor)
    setImageDrawable(drawable)
}

fun AppCompatImageView.tintColorRes(corRes: Int) {
    val drawable = this.drawable
    val color = this.context.getColor(corRes)
    DrawableCompat.setTint(drawable, color)
    setImageDrawable(drawable)
}