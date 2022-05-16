package cool.dingstock.appbase.helper

import android.util.Log
import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.util.Logger
import javax.inject.Inject


/**
 * 类名：HomeSuggestHelper
 * 包名：cool.dingstock.appbase.helper
 * 创建时间：2022/2/23 11:28 上午
 * 创建人： WhenYoung
 * 描述：
 **/
object HomeSuggestHelper {
    val SP_HOME_TAG = "uploadHomeFeeds"
    val SP_SEARCH_TAG = "uploadSearchFeeds"
    private val net by lazy {
        Net()
    }
    private var haveUploadHome: Boolean? = null
    private var haveUploadSearch: Boolean? = null

    fun uploadHomeFeeds() {
        Log.e("test", "uploadHomeFeeds")
        if (haveUploadHome == null) {
            haveUploadHome =
                !TimeTagHelper.checkTimeTag(getHomeTimeTag(), TimeTagHelper.TimeTag.ONCE_DAY)
        }
        if (haveUploadHome == true) {
            return
        }

        haveUploadHome = true
        TimeTagHelper.updateTimeTag(getHomeTimeTag(), System.currentTimeMillis())
        Log.e("test", "uploadHomeFeeds 开始上传")

        net.api.trackPostFeeds("home")
            .subscribe({
                Logger.d("uploadHomeFeeds", it.toString())
            }, {
                Logger.d("uploadHomeFeeds", it.message)
            })
    }

    fun uploadSearchFeeds() {
        Log.e("test", "uploadSearchFeeds")
        if (haveUploadSearch == null) {
            haveUploadSearch =
                !TimeTagHelper.checkTimeTag(getSearchTimeTag(), TimeTagHelper.TimeTag.ONCE_DAY)
        }
        if (haveUploadSearch == true) {
            return
        }
        haveUploadSearch = true
        TimeTagHelper.updateTimeTag(getSearchTimeTag(), System.currentTimeMillis())
        Log.e("test", "uploadSearchFeeds 开始上传")
        net.api.trackPostFeeds("search")
            .subscribe({
                Logger.d("uploadSearchFeeds", it.toString())
            }, {
                Logger.d("uploadSearchFeeds", it.message)
            })

    }

    fun getHomeTimeTag(): String {
        return SP_HOME_TAG + LoginUtils.getCurrentUser()?.id ?: ""
    }

    fun getSearchTimeTag(): String {
        return SP_SEARCH_TAG + LoginUtils.getCurrentUser()?.id ?: ""
    }

    fun reset() {
        haveUploadHome = null
        haveUploadSearch = null
    }


    class Net {
        @Inject
        lateinit var api: HomeApi

        init {
            AppBaseApiHelper.appBaseComponent.inject(this)
        }

    }
}