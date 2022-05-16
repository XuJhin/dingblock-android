package cool.dingstock.appbase.webview.bridge.x5


import android.webkit.ValueCallback
import android.webkit.WebView
import cool.dingstock.appbase.webview.bridge.AbsJsBridge
import cool.dingstock.appbase.webview.bridge.ActionBrideEvent
import cool.dingstock.appbase.webview.bridge.OnNativeActionCallback
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.util.Logger
import java.lang.ref.WeakReference

class X5JsBridge(webView: WebView) : AbsJsBridge() {

    companion object {
        val BridgeName
            get() = "DCBridge"
    }
    private var mWebView: WeakReference<WebView> = WeakReference(webView)
    override fun receiveMessage(message: String) {
        Logger.d("js->Native  $message")
        val script = "window.$BridgeName.receiveMessage($message);"
        mHandler.post {
            mWebView.get()?.evaluateJavascript(script, null)
        }
    }

    override fun nativeAction(actionEvent: String, callback: OnNativeActionCallback) {
        Logger.d("action: Native->Js  $actionEvent")
        val script = "window.$BridgeName.nativeAction($actionEvent);"
        mHandler.post {
            mWebView.get()?.evaluateJavascript(script, ValueCallback {
                if (!it.isEmpty()) {
                    try {

                        var jsonStr = it.replace("\\", "")
                        jsonStr = jsonStr.substring(1, jsonStr.length - 1)//字符串 两边 一边多了一个 " 所以截取掉
                        val fromJson = JSONHelper.fromJson<ActionBrideEvent>(
                            jsonStr,
                            ActionBrideEvent::class.java
                        )
                        callback.onCallback(fromJson)
                        return@ValueCallback
                    } catch (e: Exception) {
                    }
                }
                val actionBrideEvent = ActionBrideEvent()
                callback.onCallback(actionBrideEvent)
            })
        }
    }
}