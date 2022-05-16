package cool.dingstock.appbase.helper.bp

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.home.bp.GoodDetailEntity
import cool.dingstock.appbase.exception.DcException
import cool.dingstock.appbase.helper.bp.bean.JDResolverResult
import cool.dingstock.appbase.helper.bp.bean.PlatBean
import cool.dingstock.appbase.helper.bp.bean.V2Bean
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableOnSubscribe
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/17  10:05
 */
class BpLocalHelper(val context: Context, livedata: MutableLiveData<Boolean>) {
    @Inject
    lateinit var commonApi: CommonApi
    private val PARSE_WITH_SERVICE = -300
    private var goodsId = ""
    private var webView: WebView
    private var isLoadFinish = false
    var minDuration = 600

    private var mHandler: Handler = object : Handler(BaseLibrary.getInstance().context.mainLooper) {

        override fun handleMessage(msg: Message) {
            livedata.postValue(true)
            super.handleMessage(msg)
        }
    }

    init {
        AppBaseApiHelper.appBaseComponent.inject(this)
        webView = WebView(context)
        webView.settings.javaScriptEnabled = true
        livedata.postValue(false)
        val currentTime = System.currentTimeMillis()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoadFinish = true
                val l = System.currentTimeMillis() - currentTime
                var duration: Long = 0

                if (l < minDuration) {
                    duration = minDuration - l
                }
                mHandler.sendEmptyMessageDelayed(0, duration)
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
        }
        commonApi.commonInfo("bpResolver")
            .subscribe({
                webView.loadUrl(it.res?.url ?: "https://oss.dingstock.net/bp/resolver.html")
                webView.clearCache(true)
            }, {
                webView.loadUrl("https://oss.dingstock.net/bp/resolver.html")
                webView.clearCache(true)
            })
//        webView.loadUrl("file:///android_asset/test.html")

    }

    suspend fun parseLink(url: String): BaseResult<GoodDetailEntity> = coroutineScope {
        try {
            goodsId = ""
            val platBean = getPlat(webView, url)
            return@coroutineScope when (platBean.res?.plat) {
                "tb" -> {
                    parseTb(platBean.res!!, url)
                }
                "jd" -> {
                    try {
                        parseJD(platBean.res!!, url)
                    } catch (e: Exception) {
                        goodsId = ""
                        suspendCoroutine { cont ->
                            parseLinkWithNet(url, e.message ?: "").subscribe({
                                if (!it.err && it.res != null) {
                                    cont.resume(it)
                                } else {
                                    cont.resume(BaseResult(true, null, it.code, it.msg))
                                }
                            }, {
                                cont.resume(BaseResult(true, null, 500, it.message ?: ""))
                            })
                        }
                    }
                }
                else -> {
                    suspendCoroutine<BaseResult<GoodDetailEntity>> { cont ->
                        launch(Dispatchers.IO) {
                            parseLinkWithNet(url, "")
                                .subscribe({
                                    if (!it.err && it.res != null) {
                                        cont.resume(it)
                                    } else {
                                        cont.resumeWithException(DcException(it.code, it.msg))
                                    }
                                }, {
                                    cont.resumeWithException(it)
                                })
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            return@coroutineScope BaseResult(true, null, 500, e.message ?: "")
        }
    }

    private suspend fun parseTb(platBean: PlatBean, url: String): BaseResult<GoodDetailEntity> =
        coroutineScope {
            return@coroutineScope suspendCoroutine { cont ->
                launch(Dispatchers.IO) {
                    rxParseTb(platBean, url)
                        .subscribe({
                            if (it.res != null) {
                                cont.resume(it as BaseResult<GoodDetailEntity>)
                            } else {
                                parseLinkWithNet(url, it.msg).subscribe({ result ->
                                    cont.resume(result)
                                }, { error ->
                                    cont.resumeWithException(error)
                                })
                            }
                        }, {
                            if (it is DcException) {
                                parseLinkWithNet(url, it.msg).subscribe({ result ->
                                    cont.resume(result)
                                }, { error ->
                                    cont.resumeWithException(error)
                                })
                            } else {
                                cont.resumeWithException(it)
                            }
                        })
                }
            }
        }

    //	start tb
    private suspend fun rxParseTb(
        platBean: PlatBean,
        url: String
    ): Flowable<BaseResult<out GoodDetailEntity?>> {
        return Flowable.just(BaseResult(false, platBean))
            .flatMap {
                if (goodsId.isNotEmpty() && ("tb" == it.res?.plat && it.res?.url != null)) {
                    return@flatMap Flowable.just(BaseResult(false, null, 0, ""))
                }
                if ("tb" == it.res?.plat && it.res?.url != null) {
                    return@flatMap getBody(it.res!!.url!!)
                }
                goodsId = ""
                return@flatMap Flowable.error<BaseResult<Response>>(
                    DcException(
                        PARSE_WITH_SERVICE,
                        "TB getPlat err"
                    )
                )
            }
            .flatMap {
                if (goodsId.isNotEmpty()) {
                    return@flatMap Flowable.just(BaseResult(false, goodsId, 0, "成功"))
                }
                val bodyString = it?.res?.body?.string()
                bodyString?.let {
                    return@flatMap getGoodsId(webView, url, bodyString)
                }
                //这里走网络淘口令 解析
                return@flatMap getGoodsIdWithTKL(url)
            }
            .flatMap {
                if (it.res == null || it.res.isNullOrEmpty()) {
                    return@flatMap getGoodsIdWithTKL(url)
                }
                return@flatMap Flowable.just(it)
            }
            .flatMap {
                if (it.res != null) { //掉V2接口
                    goodsId = it.res!!
                    return@flatMap getV2(webView, it.res!!)
                }
                return@flatMap Flowable.error<BaseResult<V2Bean>>(
                    DcException(
                        PARSE_WITH_SERVICE,
                        "TB getGoodsId err"
                    )
                )
            }
            .flatMap {
                if (it.res?.protocol == null || it?.res?.url == null) {
                    return@flatMap Flowable.error<BaseResult<String?>>(
                        DcException(
                            PARSE_WITH_SERVICE,
                            "TB getV2 err"
                        )
                    )
                }
                return@flatMap runV2(it.res)
            }
            .flatMap {
                it.res?.let { data ->
                    if (data.isNotEmpty()) {
                        return@flatMap resolver(webView, data, goodsId)
                    }
                }
                return@flatMap Flowable.error<BaseResult<GoodDetailEntity>>(
                    DcException(
                        PARSE_WITH_SERVICE,
                        "TB run V2 err"
                    )
                )
            }
            .flatMap {
                it.res?.let { data ->
                    //上报
                    uploadData(this.goodsId, data)
                    (it as? BaseResult<GoodDetailEntity?>)?.let {
                        return@flatMap Flowable.just<BaseResult<GoodDetailEntity?>>(it)
                    }
                }
                //熔断 抛出异常
                return@flatMap Flowable.error<BaseResult<GoodDetailEntity?>>(
                    DcException(
                        PARSE_WITH_SERVICE,
                        "TB resolver err"
                    )
                )
            }
    }

    private fun parseLinkWithNet(
        urlOrId: String,
        errMsg: String
    ): Flowable<BaseResult<GoodDetailEntity>> {
        var keyWord = urlOrId
        if (goodsId.isNotEmpty()) {
            keyWord = goodsId
        }
        return commonApi.resolveCommon(keyWord, errMsg)
    }

    /**
     * 获取平台信息
     * */
    private suspend fun getPlat(webView: WebView, parseStr: String): BaseResult<PlatBean> =
        suspendCoroutine<BaseResult<PlatBean>> { cont ->
            Log.e("getPlat", "开始")
            val jsFun = "getPlat(`${parseStr}`)"
            webView.clearCache(true)
            webView.evaluateJavascript(jsFun, ValueCallback {
                Log.e("getPlat1", it)
                val formatJsonStr = formatJsonStr(it)
                Log.e("getPlat2", formatJsonStr)
                val type = object : TypeToken<BaseResult<PlatBean>>() {}.type
                val baseResult: BaseResult<PlatBean>? =
                    JSONHelper.fromJson<BaseResult<PlatBean>>(formatJsonStr, type)
                if (baseResult == null || baseResult.err || baseResult.res == null) {
                    cont.resume(BaseResult(true, null, PARSE_WITH_SERVICE, "获取淘宝失败"))
                    return@ValueCallback
                }
                if (baseResult.res?.itemId != null && (baseResult.res?.itemId?.length
                        ?: 0) > 0
                ) { //有itemId
                    goodsId = baseResult.res?.itemId!!
                }
                cont.resume(baseResult)
            })
        }

    /**
     * 网络请求获取body
     *
     * */
    private fun getBody(url: String): Flowable<BaseResult<Response>> {
        return Flowable.create<BaseResult<Response>>(FlowableOnSubscribe {
            val okHttpClient = BpHttpClientManager.httpClient
            val request: Request = Request.Builder()
                .url(url) //要访问的链接
                .build()
            val call: Call = okHttpClient.newCall(request)
            val response = call.execute()
            it.onNext(BaseResult(false, response))
            it.onComplete()
        }, BackpressureStrategy.BUFFER)
    }

    /**
     * 获取重定向 ID
     * */
    private fun getGoodsId(
        webView: WebView,
        url: String,
        bodyStr: String
    ): Flowable<BaseResult<String?>> {
        return Flowable.create<BaseResult<String?>>(FlowableOnSubscribe { emitter ->
            Log.e("getGoodsId", "开始")
            val jsFun = "getGoodsId(`${url}`,`${bodyStr}`)"
            webView.clearCache(true)
            webView.evaluateJavascript(jsFun, ValueCallback {
                try {
                    Log.e("getGoodsId", it)
                    val formatJsonStr = formatJsonStr(it)
                    Log.e("getGoodsId", formatJsonStr)
                    val type = object : TypeToken<BaseResult<String>>() {}.type
                    val baseResult = JSONHelper.fromJson<BaseResult<String?>>(formatJsonStr, type)
                    if (baseResult == null || baseResult.err || baseResult.res == null) {
                        emitter.onError(DcException(PARSE_WITH_SERVICE, "获取goodsId失败"))
                        emitter.onComplete()
                        return@ValueCallback
                    }
                    emitter.onNext(baseResult)
                    emitter.onComplete()
                } catch (e: Exception) {
                    emitter.onNext(BaseResult(true, null, PARSE_WITH_SERVICE, "获取从定向id失败"))
                    emitter.onComplete()
                }
            })
        }, BackpressureStrategy.BUFFER).compose(RxSchedulers.main_io())
    }

    /**
     * 获取重定向 ID
     * */
    private fun getGoodsIdWithTKL(url: String): Flowable<BaseResult<String?>> {
        return commonApi.resolveTkl(url)
    }

    private fun getV2(webView: WebView, goodsId: String): Flowable<BaseResult<V2Bean>> {
        return Flowable.create<BaseResult<V2Bean>>(FlowableOnSubscribe { emitter ->
            Log.e("getV2", "开始")
            val jsFun = "getV2(`${goodsId}`)"
            webView.clearCache(true)
            webView.evaluateJavascript(jsFun, ValueCallback {
                Log.e("getV2", it)
                val formatJsonStr = formatJsonStr(it)
                Log.e("getV2", formatJsonStr)
                val type = object : TypeToken<BaseResult<V2Bean>>() {}.type
                val baseResult = JSONHelper.fromJson<BaseResult<V2Bean>>(formatJsonStr, type)
                if (baseResult == null || baseResult.err || baseResult.res == null) {
                    emitter.onError(DcException(PARSE_WITH_SERVICE, "获取V2失败"))
                    emitter.onComplete()
                    return@ValueCallback
                }
                emitter.onNext(baseResult)
                emitter.onComplete()
            })
        }, BackpressureStrategy.BUFFER).compose(RxSchedulers.main_io())
    }

    /**
     * 走位V2接口
     * */
    private fun runV2(v2Bean: V2Bean?): Flowable<BaseResult<String?>> {
        if (v2Bean == null) {
            return Flowable.error<BaseResult<String?>>(DcException(PARSE_WITH_SERVICE, "NOT_HAND"))
        }
        return Flowable.create<BaseResult<String?>>(FlowableOnSubscribe {
            val okHttpClient = BpHttpClientManager.httpClient
            val requestBuilder: Request.Builder = Request.Builder()
                .url(v2Bean.url)
                .get()
            //addHeader
            val headerMap = v2Bean.headers
            if (headerMap != null && headerMap.isNotEmpty()) {
                for (item in headerMap) {
                    requestBuilder.addHeader(item.key, item.value)
                }
            }
            val request = requestBuilder.build()
            val call: Call = okHttpClient.newCall(request)
            try {
                val response = call.execute()
                val string = response.body?.string()
                Log.e("v2Result:", string ?: "")
                if (string.isNullOrEmpty()) {
                    Flowable.error<BaseResult<String?>>(DcException(PARSE_WITH_SERVICE, "NOT_HAND"))
                }
                it.onNext(BaseResult(false, string, 0, ""))
                it.onComplete()
            } catch (e: Exception) {
                print(e.localizedMessage)
            }
        }, BackpressureStrategy.BUFFER)
    }

    private fun resolver(
        webView: WebView,
        data: String,
        goodsId: String
    ): Flowable<BaseResult<GoodDetailEntity?>> {
        return Flowable.create<BaseResult<GoodDetailEntity?>>(FlowableOnSubscribe { emitter ->
            Log.e("resolver", "开始")
            val jsFun = "resolver(${JSON.toJSONString(data)},`${goodsId}`)"
            Log.e("resolver,jsFun", jsFun)
            webView.clearCache(true)
            webView.evaluateJavascript(jsFun, ValueCallback {
                try {
                    Log.e("resolver", it)
                    val formatJsonStr = JSON.parse(it).toString()
                    Log.e("resolver", formatJsonStr)
                    val type = object : TypeToken<BaseResult<GoodDetailEntity?>>() {}.type
                    val baseResult =
                        JSONHelper.fromJson<BaseResult<GoodDetailEntity?>>(formatJsonStr, type)
                    if (baseResult == null || baseResult.err || baseResult.res == null) {
                        emitter.onError(DcException(PARSE_WITH_SERVICE, "解析失败"))
                        emitter.onComplete()
                    } else {
                        emitter.onNext(baseResult)
                        emitter.onComplete()
                    }
                } catch (e: Exception) {
                    emitter.onNext(BaseResult(true, null, PARSE_WITH_SERVICE, "解析失败"))
                    emitter.onComplete()
                }
            })
        }, BackpressureStrategy.BUFFER).compose(RxSchedulers.main_io())
    }

    /**
     * 上报
     *
     * */
    private fun uploadData(goodsId: String, data: GoodDetailEntity) {
        commonApi.reportResolve(goodsId, data)
            .subscribe({
            }, {
            })
    }

    private fun formatJsonStr(jsonOldStr: String): String {
        var jsonStr = jsonOldStr.replace("\\", "")
        jsonStr = jsonStr.substring(1, jsonStr.length - 1) //字符串 两边 一边多了一个 " 所以截取掉
        return jsonStr
    }

    //end tb

    //start jd

    private suspend fun parseJD(
        platBean: PlatBean,
        url: String
    ): BaseResult<GoodDetailEntity> = coroutineScope {
        var goodsUrl = platBean.url ?: ""
        if (goodsId.isEmpty()) {
            val goodsOriginalUrl = platBean.url ?: ""
            if (goodsOriginalUrl.isEmpty()) {
                throw DcException(500, "JD getPlat url and itemId is all Empty")
            }
            val locationRes = reqGet(goodsOriginalUrl)
            val locationResHtml = locationRes.body?.string() ?: ""
            if (locationResHtml.isEmpty()) {
                throw DcException(500, "JD request originUrl err")
            }
            val locationResult = getJDLocation(locationResHtml)
            goodsUrl = locationResult.res ?: ""
            if (goodsUrl.isEmpty()) {
                throw DcException(500, "JD getJDLocation err")
            }
        }
        if (goodsUrl.isEmpty()) {
            throw DcException(500, "JD getPlat url is all Empty")
        }
        val goodsUrlResult = reqGet(goodsUrl)
        if (goodsId.isEmpty()) {
            goodsUrl = goodsUrlResult.request.url.toString()
        }
        val goodsUrHtml = goodsUrlResult?.body?.string() ?: ""
        if (goodsUrHtml.isEmpty()) {
            throw DcException(500, "JD request locationUrl err")
        }
        val resolverResult = resolverJD(goodsUrl, goodsUrHtml)
        val goodDetailEntity = resolverResult.res?.goodsDetail
            ?: throw DcException(500, "JD resolverJD err")
        goodsId = goodDetailEntity.id ?: ""
        val priceReq = resolverResult.res?.priceReq
            ?: return@coroutineScope BaseResult(false, goodDetailEntity)
        val priceUrl = priceReq.url
            ?: throw DcException(500, "JD priceUrl is null")
        val priceHtml = reqGet(priceUrl, priceReq.headers).body?.string()
            ?: throw DcException(500, "JD request priceUrl err")
        val newGoodsDetailResult = getJDRes(goodDetailEntity, priceHtml)
        val newGoodsDetails = newGoodsDetailResult.res?.goodsDetail
            ?: throw DcException(500, "JD getJDRes err")
        //上报
        uploadData(goodsId, newGoodsDetails)
        return@coroutineScope BaseResult(false, newGoodsDetails, 0, "")
    }

    private suspend fun getJDLocation(parseStr: String): BaseResult<String> = coroutineScope {
        async(Dispatchers.Main) {
            suspendCoroutine<BaseResult<String>> { cont ->
                Log.e("getJDLocation", "开始")
                val jsFun = "getJDLocation(`${parseStr}`)"
                webView.clearCache(true)
                webView.evaluateJavascript(jsFun, ValueCallback {
                    Log.e("getJDLocation1", it)
                    val formatJsonStr = formatJsonStr(it)
                    Log.e("getJDLocation2", formatJsonStr)
                    val type = object : TypeToken<BaseResult<String>>() {}.type
                    val baseResult: BaseResult<String>? =
                        JSONHelper.fromJson<BaseResult<String>>(formatJsonStr, type)
                    if (baseResult == null || baseResult.err || baseResult.res == null) {
                        cont.resume(BaseResult(true, null, PARSE_WITH_SERVICE, "getJDLocation 失败"))
                        return@ValueCallback
                    }
                    cont.resume(baseResult)
                })
            }
        }.await()
    }

    private suspend fun resolverJD(
        originalUrl: String,
        parseStr: String
    ): BaseResult<JDResolverResult> =
        coroutineScope {
            async(Dispatchers.Main) {
                suspendCoroutine<BaseResult<JDResolverResult>> { cont ->
                    Log.e("resolverJD", "开始")
                    val newParseStr = format2JsStr(parseStr)
                    val jsFun = "resolverJD(`${originalUrl}`,`${newParseStr}`)"
                    webView.clearCache(true)
                    webView.evaluateJavascript(jsFun, ValueCallback {
                        Log.e("resolverJD1", it)
                        val formatJsonStr = formatJsonStr(it)
                        Log.e("resolverJD2", formatJsonStr)
                        val type = object : TypeToken<BaseResult<JDResolverResult>>() {}.type
                        val baseResult: BaseResult<JDResolverResult>? =
                            JSONHelper.fromJson<BaseResult<JDResolverResult>>(formatJsonStr, type)
                        if (baseResult == null || baseResult.err || baseResult.res == null) {
                            cont.resume(BaseResult(true, null, PARSE_WITH_SERVICE, "resolverJD 失败"))
                            return@ValueCallback
                        }
                        cont.resume(baseResult)
                    })
                }
            }.await()
        }

    private suspend fun getJDRes(
        goodsDetail: GoodDetailEntity,
        parseStr: String
    ): BaseResult<JDResolverResult> =
        coroutineScope {
            async(Dispatchers.Main) {
                suspendCoroutine<BaseResult<JDResolverResult>> { cont ->
                    Log.e("getJDRes", "开始")
                    val goodsDetailStr = Gson().toJson(goodsDetail)
                    val newParseStr = format2JsStr(parseStr)
                    val jsFun = "getJDRes(`${goodsDetailStr}`,`${newParseStr}`)"
                    webView.clearCache(true)
                    webView.evaluateJavascript(jsFun, ValueCallback {
                        Log.e("getJDRes1", it)
                        val formatJsonStr = formatJsonStr(it)
                        Log.e("getJDRes2", formatJsonStr)
                        val type = object : TypeToken<BaseResult<JDResolverResult>>() {}.type
                        val baseResult: BaseResult<JDResolverResult>? =
                            JSONHelper.fromJson<BaseResult<JDResolverResult>>(formatJsonStr, type)
                        if (baseResult == null || baseResult.err || baseResult.res == null) {
                            cont.resume(BaseResult(true, null, PARSE_WITH_SERVICE, "getJDRes 失败"))
                            return@ValueCallback
                        }
                        cont.resume(baseResult)
                    })
                }
            }.await()
        }

    //end jd


    suspend fun reqGet(url: String, headers: Map<String, String?>? = null): Response =
        coroutineScope {
            return@coroutineScope withContext(Dispatchers.IO) {
                val okHttpClient = BpHttpClientManager.httpClient
                val requestBuilder: Request.Builder = Request.Builder()
                    .url(url)
                    .get()
                headers?.keys?.forEach { key ->
                    headers.get(key)?.let { value ->
                        requestBuilder.addHeader(key, value)
                    }
                }
                val request = requestBuilder.build()
                val call: Call = okHttpClient.newCall(request)
                call.execute()
            }
        }

    private fun format2JsStr(text: String): String {
        var newText = text.replace("\\\"", "")
        val r: Pattern = Pattern.compile("<style([\\S\\s]+?)<\\/style>", Pattern.CASE_INSENSITIVE)
        val match = r.matcher(newText)
        var newPatternStr = newText
        while (match.find()) {
            val str = match.group()
            newPatternStr = newPatternStr.replace(str, "")
        }
        return newPatternStr
    }


}

