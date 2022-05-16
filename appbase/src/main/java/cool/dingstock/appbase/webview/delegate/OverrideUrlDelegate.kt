package cool.dingstock.appbase.webview.delegate

import android.content.Context
import android.webkit.WebView
import java.lang.ref.WeakReference

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/25  19:05
 */
abstract class OverrideUrlDelegate {
    var context: WeakReference<Context>? = null

    fun isAttach(): Boolean = context?.get() != null

    constructor(context: Context){
        this.context = WeakReference(context)
    }

    abstract fun shouldOverrideUrlLoading(webView: WebView, url:String):Boolean
}