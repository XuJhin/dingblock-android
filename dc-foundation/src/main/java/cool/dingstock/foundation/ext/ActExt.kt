package cool.dingstock.foundation.ext

import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.getColorById(resId: Int) {
    this.resources.getColor(resId)
}

fun AppCompatActivity.getDrawableById(resId: Int) {
    this.resources.getDrawable(resId)
}