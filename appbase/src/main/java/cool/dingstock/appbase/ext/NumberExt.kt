package cool.dingstock.appbase.ext

import android.content.res.Resources
import android.util.TypedValue
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.SizeUtils
import java.lang.StringBuilder
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * 格式化价格为2位小数
 */
fun Double.formatPrice(): String {
    val df = DecimalFormat("0.00")
    df.roundingMode = RoundingMode.HALF_UP
    return df.format(this)
}

fun Float.formatPrice(): String {
    val df = DecimalFormat("0.00")
    df.roundingMode = RoundingMode.HALF_UP
    val str = df.format(this)
    if (str.indexOf(".") > 0) {
        val arr = str.toCharArray()
        for (index in arr.size - 1 downTo 0) {
            if (arr[index] != '0') {
                if (arr[index] == '.') {
                    return str.substring(0, index)
                }
                return str.substring(0, index + 1)
            }
        }

    }
    return str
}

fun Float.formatOnes(): String {
    val df = DecimalFormat("0.0")
    df.roundingMode = RoundingMode.HALF_UP
    return df.format(this)
}

fun Double.formatOnes(): String {
    val df = DecimalFormat("0.0")
    df.roundingMode = RoundingMode.HALF_UP
    return df.format(this)
}


fun Double.formatMillisecond(): String {
    val df = DecimalFormat("0.000")
    df.roundingMode = RoundingMode.HALF_UP
    return df.format(this)
}

val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

val Float.sp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )

val Float.azDp
    get() :Float {
        return SizeUtils.dp2px(this).toFloat()
    }

val Int.azDp
    get() :Float {
        return SizeUtils.dp2px(this.toFloat()).toFloat()
    }

val Double.azDp
    get() :Float {
        return SizeUtils.dp2px( this.toFloat()).toFloat()
    }

val Double.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

val Int.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
val Int.SP
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

fun String.formatAddMicoMeter(): String {
    var num = 0
    val str = this
    val ans = StringBuilder("")

    for ((index, chars) in str.reversed().withIndex()) {
        ans.append(chars)
        num += 1
        if (num == 3 && index != str.length - 1) {
            ans.append(",")
            num = 0
        }
    }
    return ans.toString().reversed()
}


fun Int.turnThousandPoint(): String {
    if (this < 1000) {
        return this.toString()
    }
    var ans = StringBuilder("")
    val zs = this / 1000
    val ys = this % 1000 / 100
    ans.append(zs.toString()).append(".").append(ys.toString()).append("K")
    return ans.toString()
}

fun Long.format2Number(): String {
    return if (this >= 10) {
        this.toString()
    } else {
        "0".plus(this.toString())
    }
}

fun Double.toFormatStr(format: String): String {
    return String.format(format, this)
}

