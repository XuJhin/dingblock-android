package cool.dingstock.appbase.customerview.betterlinktv

import android.content.Context
import android.graphics.Color
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.LeadingMarginSpan
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.widget.expandabletext.OverLinkMovementMethod
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern


class BetterLinkTv(context: Context, attr: AttributeSet) : AppCompatTextView(context, attr) {
    var enableClickLink = true

    companion object {
        val pattern =
            "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,7})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,7})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)"
    }

    fun setBetterLink() {
        setBetterLink(0, arrayListOf())
    }

    fun setBetterLink(retract: Int){
        setBetterLink(retract,null)
    }

    fun setBetterLink(retract: Int, portals_: ArrayList<String>? = null) {
        val portals = arrayListOf<String>().apply {
            portals_?.let { list->
                addAll(list)
            }
        }
        autoLinkMask = 0
        highlightColor = Color.TRANSPARENT
        if (isEnabled) {
            movementMethod = OverLinkMovementMethod.getInstance()
        }
        isEnabled = true
        val text = text
        val urls: ArrayList<UrlLocation> = matchLink(text)
        var styleNew = SpannableStringBuilder()
        if (urls.isNotEmpty()) {
            var lastUrlSpan: UrlLocation? = null
            //所有拼接、截取 都是 前取后不取
            for (urlIndex in urls.indices) {
                val url = urls[urlIndex]
                //判断是否替换为传送门
                var replaceStr =
                    if (portals.size > 0) {
                        val portal = portals.removeAt(0)
                        if (portal.isEmpty()) {
                            betterReplaceUrl(url.url)
                        } else {
                            portal
                        }
                    } else {
                        betterReplaceUrl(url.url)
                    }
                //旧Style 上一个Url的结束 当做开始的下标
                var lastIndex = 0
                lastUrlSpan?.let { lastIndex = it.end }
                lastUrlSpan = url
                var currentIndex = url.start
                //这里获取旧Style 上一次Url 到这一个Url之间的 内容
                val lastUrl2CurrentUrlStr = text.substring(lastIndex, currentIndex)
                //将内容拼接到新Style里面去
                styleNew.append(lastUrl2CurrentUrlStr)


                val clickUrlSpan = object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        if (enableClickLink) {
                            try {
                                DcUriRequest(context, url.url)
                                    .start()
                            } catch (e: Exception) {
                            }
                        } else {

                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                        ds.color = Color.parseColor("#4D9FF6")
                    }
                }
                //当前 新style开始的Index
                var newAppendIndex = styleNew.length
                //拼接替换之后的Url内容
                styleNew.append(replaceStr)
                //设置新Url的颜色以及点击事件
                styleNew.setSpan(
                    clickUrlSpan,
                    newAppendIndex,
                    newAppendIndex + replaceStr.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (urlIndex == urls.size - 1) {
                    //最后一个Url的时候。需要把最后的全部 添加进去
                    val lastUrlEndIndex = url.end
                    //将内容拼接到新Style里面去
                    styleNew.append(text.substring(lastUrlEndIndex))
                }
            }
        } else {
            styleNew.append(text)
        }
        val spannableString = getSpannableString(retract, styleNew)
        setText(spannableString)
    }

    fun setBetterLinkSpan(arr: List<SpannableLocation>) {
        try {
            if (text is Spannable) {
                val span: Spannable = text as Spannable
                for (spl in arr) {
                    span.setSpan(
                        spl.spannable,
                        spl.startIndex,
                        spl.endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                text = span
            } else {
                val span: Spannable = SpannableString(text)
                for (spl in arr) {
                    span.setSpan(
                        spl.spannable,
                        spl.startIndex,
                        spl.endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                text = span
            }
        } catch (e: java.lang.Exception) {
        }
    }

    fun removeHttp(url: String): String {
        var s = url.replace("http://", "")
        s = s.replace("https://", "")
        return s
    }


    fun betterReplaceUrl(url: String): String {
        try {
            val host = URL(url).host
            if (host.isNullOrEmpty()) {
                return url
            }
            return host
        } catch (e: Exception) {

        }
        return url
    }


    /**
     * 首行缩进的SpannableString
     *
     * @param length      首行缩进宽度 px
     * @param description 描述信息
     */
    fun getSpannableString(length: Int, description: CharSequence): SpannableString? {
        val spannableString = SpannableString(description)
        val leadingMarginSpan: LeadingMarginSpan = LeadingMarginSpan.Standard(length, 0) //仅首行缩进
        spannableString.setSpan(
            leadingMarginSpan,
            0,
            description.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }


    private fun matchLink(text: CharSequence?): java.util.ArrayList<UrlLocation> {
        val arr = arrayListOf<UrlLocation>()
        // 创建 Pattern 对象
        val r: Pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
        // 现在创建 matcher 对象
        val m: Matcher = r.matcher(text)
        while (m.find()) {
            //保存到sb中，"\r\n"表示找到一个放一行，就是换行
            val url = m.group()
            val start = m.start()
            val end = m.end()
            arr.add(UrlLocation(url, start, end))
            Log.e("matchLink", "url:$url , start:$start ,end:$end ")
        }
        return arr
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return super.onTouchEvent(event)
    }

    fun setEnableClick(enable: Boolean) {
        this.isEnabled = enable
        if (!enable) {
            movementMethod = null
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)

    }

}

class SpannableLocation(
    var spannable: Any,
    var startIndex: Int,
    var endIndex: Int
)

data class UrlLocation(var url: String, var start: Int, var end: Int)