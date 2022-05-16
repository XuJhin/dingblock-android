package cool.dingstock.appbase.router

import android.net.Uri
import android.text.TextUtils

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/6  16:32
 */
interface RouterUtils {
    fun isScheme(uri: Uri?): Boolean {
        if (null == uri) {
            return false
        }
        val scheme = uri.scheme
        return if (TextUtils.isEmpty(scheme)) {
            false
        } else scheme != DcRouterUtils.PREFIX_HTTP && scheme != DcRouterUtils.PREFIX_HTTPS
    }
}