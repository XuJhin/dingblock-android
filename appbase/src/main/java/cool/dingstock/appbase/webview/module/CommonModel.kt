package cool.dingstock.appbase.webview.module

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import cool.dingstock.appbase.constant.PushConstant
import cool.dingstock.appbase.constant.ServerConstant
import cool.dingstock.appbase.entity.event.account.EventActivated
import cool.dingstock.appbase.net.retrofit.NetHelper
import cool.dingstock.appbase.share.ShareParams
import cool.dingstock.appbase.share.SharePlatform
import cool.dingstock.appbase.share.ShareServiceHelper
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.webview.bridge.*
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.AppUtils.getVersionCode
import cool.dingstock.lib_base.util.AppUtils.getVersionName
import cool.dingstock.lib_base.util.FileUtils
import cool.dingstock.lib_base.util.FileUtilsCompat
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.ToastUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

class CommonModel(
    override var context: WeakReference<Context>?,
    override var bridge: IJsBridge?
) : IBridgeModule {
    val scope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    override fun moduleName(): String {
        return "common"
    }

    init {
        EventBus.getDefault().register(this)
    }

    override fun release() {
        super.release()
        scope.cancel()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @XBridgeMethod
    fun mobClick(event: BridgeEvent) {
        try {
            val name = event.params?.get("name") as String
            val params = event.params?.get("params") as? Map<String, String>
            UTHelper.commonEvent(name, params);
        } catch (e: Exception) {
        }
    }

    @XBridgeMethod
    fun getenv(event: BridgeEvent) {
        val share: SharedPreferences? =
            context?.get()?.getSharedPreferences("net_env", Context.MODE_PRIVATE)
        val serverUrl = share?.getString("env", "https://api.dingstock.net")
        val result =
            if (serverUrl == ServerConstant.SERVER_PRODUCT_NEW) "Release" else if (serverUrl == ServerConstant.SERVER_PRERELEASE_NEW) "PreRelease" else if (serverUrl == ServerConstant.SERVER_DEBUG_NEW) "Debug" else "Debug"
        val toResponse = event.toResponse()
        bridge?.let { toResponse.success(result)?.send(it) }
        Logger.d("result", Gson().toJson(toResponse))
    }

    @XBridgeMethod
    fun getVersion(event: BridgeEvent) {
        val versionName = getVersionName(BaseLibrary.getInstance().context)
        val versionCode = getVersionCode(BaseLibrary.getInstance().context)
        val result = mapOf(
//            "version" to BuildConfig.VERSION_NAME,
//            "versionCode" to BuildConfig.VERSION_CODE,
            "app" to "Android${versionName}(${versionCode})"
        )
        val toResponse = event.toResponse()
        bridge?.let { toResponse.success(result)?.send(it) }
        Logger.d("result", Gson().toJson(toResponse))
    }


    @XBridgeMethod
    fun httpRequest(event: BridgeEvent) {
        //param参数
        val url = event.params?.get("url") as String
        val method = event.params?.get("method") as String
        val body = event.params?.get("body") as String
        val headerStr = event.params?.get("headers") as MutableMap<*, *>
        val requestBody = body.toRequestBody("application/json;charset=utf-8".toMediaType())
        //构造请求
        val okHttpClient = NetHelper().getHttpClient()
        val requestBuilder = Request.Builder().url(url)
        if (headerStr.isNotEmpty()) {
            headerStr.forEach {
                requestBuilder.addHeader(it.key.toString(), it.value.toString())
            }
        }
        val request = if (method == "get" || method == "get".toUpperCase(Locale.ROOT)) {
            requestBuilder
                .get()
                .build()
        } else {
            requestBuilder.method(method, requestBody).build()
        }
        val newCall = okHttpClient?.newCall(request)
        newCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                bridge?.let { event.toResponse().error("-1", e.message!!)?.send(it) }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val jsonObject = JSONObject()
                jsonObject.put("result", result)
                if (result != null) {
                    bridge?.let { event.toResponse().success(jsonObject)?.send(it) }
                }
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onActiveSuccessEvent(event: EventActivated) {
        Logger.d("onActiveSuccessEvent  ---- ")
        bridge?.let {
            BridgeEvent.eventBuild().apply {
                module = moduleName()
                eventName = PushConstant.Event.UPDATE_USER_INFO
            }.send(it)
        }
    }


    @XBridgeMethod
    fun saveImg(event: BridgeEvent) {
        scope.launch(Dispatchers.Main) {
            context?.get()?.let {
                if (!PermissionX.isGranted(it, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    (it as? Fragment)?.let {
                        PermissionX.init(it)
                            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request(RequestCallback { allGranted, grantedList, deniedList ->
                                save(event)
                            })
                    }
                    (it as? FragmentActivity)?.let {
                        PermissionX.init(it)
                            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request(RequestCallback { allGranted, grantedList, deniedList ->
                                save(event)
                            })
                    }
                    Logger.d("result", "saveImg2Paht ---- 权限申请 ")
                    return@launch
                }
                save(event)
            }
        }
    }

    private fun save(event: BridgeEvent) {
        scope.launch {
            try {
                var img64 = event.params?.get("img64") as String
                img64 = img64.split(",")[1]
                val bitMap = FileUtilsCompat.base64ToBitmap(img64)
                val path = FileUtils.saveBitmapToPath(bitMap)
                val toResponse = event.toResponse()
                bridge?.let { toResponse.success(path)?.send(it) }
                ToastUtil.getInstance()._short(context?.get(), "图片保存成功")
                Logger.d("result", "saveImg2Path ---- ${path} ")
            } catch (e: Exception) {

            }
        }
    }

    @XBridgeMethod
    fun shareImage(event: BridgeEvent) {
        context?.get()?.let { context ->
            scope.launch {
                try {
                    val type = ShareType.Image
                    val params = ShareParams()
                    type.params = params
                    params.title = event.params?.get("title") as? String ?: ""
                    params.content = event.params?.get("content") as? String ?: ""
                    val plat = event.params?.get("plat") as? String
                    var img64 = event.params?.get("img64") as String?
                    if (!TextUtils.isEmpty(img64)) {
                        img64 = img64!!.split(",")[1]
                        val bitmap = FileUtilsCompat.base64ToBitmap(img64)
                        params.imageBitmap = bitmap
                    }
                    launch(Dispatchers.Main) {
                        when (plat) {
                            "wechat" -> {
                                ShareServiceHelper.share(context, type, SharePlatform.WeChat)
                            }
                            "wechatMoment" -> {
                                ShareServiceHelper.share(context, type, SharePlatform.WeChatMoments)
                            }
                        }
                    }
                    Logger.d("result", "share ----------")
                } catch (e: Exception) {

                }
            }
        }
    }


    @XBridgeMethod
    fun saveData(event: BridgeEvent) {
        try {
            val key = event.params?.get("key") as? String
            val value = (event.params?.get("value") as? String)?.toString()
            if (key != null && value != null) {
                ConfigSPHelper.getInstance().save(key, value)
                bridge?.let { event.toResponse().successDefault()?.send(it) }
            }
        } catch (e: Exception) {
        }
    }

    @XBridgeMethod
    fun getData(event: BridgeEvent) {
        try {
            val key = event.params?.get("key") as? String
            if (key != null) {
                bridge?.let {
                    event.toResponse().success(ConfigSPHelper.getInstance().getString(key))
                        ?.send(it)
                }
            }
        } catch (e: Exception) {
        }
    }

    @XBridgeMethod
    fun getTheme(event: BridgeEvent) {
        try {
            context?.get()?.let { ctx ->
                val mode =
                    if (ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
                        "dark" else "light"
                val jsonObject = JSONObject()
                jsonObject["theme"] = mode
                bridge?.let { event.toResponse().success(jsonObject)?.send(it) }
            }
        } catch (e: Exception) {
        }
    }

}
