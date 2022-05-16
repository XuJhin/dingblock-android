package cool.dingstock.appbase.webview.module

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import cool.dingstock.appbase.webview.bridge.BridgeEvent
import cool.dingstock.appbase.webview.bridge.IBridgeModule
import cool.dingstock.appbase.webview.bridge.IJsBridge
import cool.dingstock.appbase.webview.bridge.XBridgeMethod
import cool.dingstock.appbase.webview.delegate.RouteModuleDelegate
import cool.dingstock.lib_base.util.Logger
import java.lang.ref.WeakReference

class RouteModule(
    private val delegate: RouteModuleDelegate,
    override var context: WeakReference<Context>?, override var bridge: IJsBridge?
) : IBridgeModule {

    override fun moduleName(): String = "route"

    @XBridgeMethod
    fun route(event: BridgeEvent) {
        Logger.e("route", "${event?.params?.get("url")}")
        if (null == event.params) {
            bridge?.let { event.toResponse().error("ERROR_PARAMS_EMPTY", "参数为空")?.send(it) }
            return
        }
        val urlStr = event.params!!["url"] as String?
        if (TextUtils.isEmpty(urlStr)) {
            bridge?.let { event.toResponse().error("ERROR_URL_EMPTY", "url为空")?.send(it) }
            return
        }
        val uri = Uri.parse(urlStr)
        if (null == uri) {
            bridge?.let { event.toResponse().error("ERROR_URL_EMPTY", "url解析异常")?.send(it) }
            return
        }
        delegate.route(urlStr!!)
        bridge?.let { event.toResponse().successDefault()?.send(it) }
    }
}