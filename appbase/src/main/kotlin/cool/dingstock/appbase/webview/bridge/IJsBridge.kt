package cool.dingstock.appbase.webview.bridge

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.util.Logger
import java.lang.reflect.Method


interface IJsBridge {
    fun release() {}
    fun registerModule(module: IBridgeModule) {
        findMethod(module)
    }

    fun findMethod(module: IBridgeModule)
    fun receiveMessage(message: String)
    fun nativeAction(actionEvent: String, callback: OnNativeActionCallback)
}

abstract class AbsJsBridge : IJsBridge {

    companion object {
        const val TAG = "JsBridge"
    }

    private var mModuleFunMap = HashMap<String, MutableList<Method>>()
    private var mModuleMap = HashMap<String, IBridgeModule>()
    protected val mHandler = Handler(Looper.getMainLooper())

    override fun release() {
        if (mModuleMap.isEmpty()) {
            return
        }
        for (mutableEntry in mModuleMap) {
            mutableEntry.value.release()
        }
    }

    override fun findMethod(module: IBridgeModule) {
        val methods = module.javaClass.methods
        if (methods.isEmpty()) {
            return
        }
        val bridgeMethodList = mutableListOf<Method>()
        for (method in module.javaClass.methods) {
            if (!method.isAnnotationPresent(XBridgeMethod::class.java)) {
                continue
            }
            bridgeMethodList.add(method)
        }
        mModuleFunMap[module.moduleName()] = bridgeMethodList
        mModuleMap[module.moduleName()] = module
    }

    override fun registerModule(module: IBridgeModule) {
        findMethod(module)
    }


    /**
     * JS调用本地方法
     */
    @JavascriptInterface
    fun postMessage(message: String) {
        Logger.d("JS->Native $message")
        val bridgeEvent: BridgeEvent = JSONHelper.fromJson(message, BridgeEvent::class.java)
            ?: return

        val moduleName = bridgeEvent.module
        if (moduleName.isEmpty()) {
            Logger.d(TAG, "module name is empty")
            return
        }
        //获取module name
        if (mModuleMap[moduleName] == null) {
            bridgeEvent.toResponse()
                .error("-1", "moduleName params miss")
                ?.send(this)
            return
        }
        //获取module 中的fun list
        val methodList = mModuleFunMap[moduleName]
        if (null == methodList) {
            bridgeEvent.toResponse()
                .error("-1", "methodList empty")
                ?.send(this)
            return
        }
        val methodFilterList = methodList.filter {
            it.name == bridgeEvent.method
        }
        if (methodFilterList.isEmpty()) {
            bridgeEvent.toResponse().error("-1", "method not found")
        }
        methodFilterList[0].invoke(mModuleMap[moduleName], bridgeEvent)
    }
}

