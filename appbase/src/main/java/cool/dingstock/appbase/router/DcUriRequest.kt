package cool.dingstock.appbase.router

import android.content.Context
import android.os.Parcelable
import com.sankuai.waimai.router.common.DefaultUriRequest
import com.sankuai.waimai.router.common.UriParamInterceptor
import cool.dingstock.appbase.R
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.ut.UTHelper
import java.util.*

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/6  14:49
 */
class DcUriRequest(context: Context, uri: String) : DefaultUriRequest(context, uri) {
    companion object{
        const val BOTTOM_ANI = "bottomAni"
        const val CENTER_ANI = "centerAni"
    }
    var isCusAni = false

    fun putUriParameter(key: String, value: String?): DcUriRequest {
        value?.let {
            uri = uri.buildUpon().appendQueryParameter(key, it).build()
        }
        return this
    }

    override fun appendParams(params: HashMap<String?, String?>?): DcUriRequest {
        putField<HashMap<String?, String?>>(UriParamInterceptor.FIELD_URI_APPEND_PARAMS, params)
        params?.let { map ->
            val buildUpon = uri.buildUpon()
            for (key in map.keys) {
                map[key]?.let {
                    buildUpon.appendQueryParameter(key, it)
                }
            }
            uri = buildUpon.build()

        }
        return this
    }

    override fun putExtra(name: String, value: Int): DcUriRequest {
        super.putExtra(name, value)
        return this
    }



    fun dialogCenterAni():DcUriRequest{
        isCusAni = true
        overridePendingTransition(R.anim.dialog_window_scale_in, 0)
        return this
    }

    fun dialogBottomAni(): DcUriRequest {
        isCusAni = true
        overridePendingTransition(R.anim.dialog_in_bottom, 0)
        return this
    }

    /**
     * 不会修改 当前Uri
     * */
    fun appendParamsOnly(params: HashMap<String?, String?>?): DcUriRequest {
        putField<HashMap<String?, String?>>(UriParamInterceptor.FIELD_URI_APPEND_PARAMS, params)
        return this
    }

    override fun overridePendingTransition(enterAni: Int, extAni: Int): DcUriRequest {
        isCusAni = true
		super.overridePendingTransition(enterAni, extAni)
		return this
	}

	override fun start() {
		if (!isCusAni) {
			overridePendingTransition(R.anim.on_activity_open_enter, R.anim.on_activity_open_exit)
		}
		super.start()
        UTHelper.commonEvent(UTConstant.ROUTE.ROUTE,"url",uri.toString())
	}

	fun startNoTransition() {
		super.start()
        UTHelper.commonEvent(UTConstant.ROUTE.ROUTE,"url",uri.toString())
	}

	fun start(needAni: Boolean) {
		if (needAni && !isCusAni) {
			overridePendingTransition(R.anim.on_activity_open_enter, R.anim.on_activity_open_exit)
		}
		super.start()
        UTHelper.commonEvent(UTConstant.ROUTE.ROUTE,"url",uri.toString())
	}

}