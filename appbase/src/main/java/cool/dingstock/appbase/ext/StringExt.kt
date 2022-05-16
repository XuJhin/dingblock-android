package cool.dingstock.appbase.ext


/**
 * 类名：StringExt
 * 包名：cool.dingstock.appbase.ext
 * 创建时间：2021/9/2 11:35 上午
 * 创建人： WhenYoung
 * 描述：
 **/
fun String?.toSymbolPrice(symbol: String): String {
    if (this == null || this == "") {
        return ""
    }
    if (this.contentEquals(symbol)) {
        return this
    }
    return "${symbol}${this}"
}

fun String.filterWhitespace(): String {
    return filter { !it.isWhitespace() }
}
