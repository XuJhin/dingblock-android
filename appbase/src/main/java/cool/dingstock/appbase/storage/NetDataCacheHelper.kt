package cool.dingstock.appbase.storage

import com.google.gson.Gson
import java.lang.Exception

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/8  11:29
 */
object NetDataCacheHelper {

    val versionKey = "NetDataCacheHelperVersion"

    fun <T> saveData(cacheKey: String, data: T): Boolean {
        try {
            val dataJson = Gson().toJson(data)
            NetDataSpHelper.save(cacheKey, dataJson)
            return true
        } catch (e: Exception) {
        }
        return false
    }


    fun <T> getData(cacheKey: String,clazz: Class<T>): T? {
        try {
            val string = NetDataSpHelper.getString(cacheKey )
            string?.let {
                val data = Gson().fromJson(string, clazz)
                return data
            }
        } catch (e: Exception) {

        }
        return null
    }


}