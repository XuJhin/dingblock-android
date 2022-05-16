package cool.dingstock.appbase.webview.bridge

import android.content.Context
import java.lang.ref.WeakReference

interface IBridgeModule {
    var bridge: IJsBridge?
    var context: WeakReference<Context>?
    fun release() {
        context = null
    }

    fun isAttach(): Boolean {
        return context?.get() != null
    }

    fun moduleName(): String
}