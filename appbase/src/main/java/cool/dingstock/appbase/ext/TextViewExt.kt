package cool.dingstock.appbase.ext

import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.lang.Exception

fun TextView.textColor(colorId: Int) {
	val textColor = ContextCompat.getColor(this.context, colorId)
	this.setTextColor(textColor)
}

fun TextView.textColor(colorStr:String){
	try {
	    setTextColor(Color.parseColor(colorStr))
	}catch (e:Exception){}
}