package cool.dingstock.appbase.webview.bridge.x5

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import cool.dingstock.appbase.webview.bridge.IBridgeModule
import java.lang.ref.WeakReference


abstract class XWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(
    context, attrs
) {
    lateinit var jsBridge: X5JsBridge

    init {
        initBridge()
    }

    private fun initBridge() {
        jsBridge = X5JsBridge(this)
        createModules()?.let { modules ->
            modules.forEach {
                it.bridge = jsBridge
                it.context = WeakReference(context)
                jsBridge.registerModule(it)
            }
        }
        addJavascriptInterface(jsBridge, X5JsBridge.BridgeName)
        setWebContentsDebuggingEnabled(true)
        setupWebView()
    }

    abstract fun setupWebView()

    abstract fun createModules(): List<IBridgeModule>?

    fun release() {
        jsBridge.release()
    }

}