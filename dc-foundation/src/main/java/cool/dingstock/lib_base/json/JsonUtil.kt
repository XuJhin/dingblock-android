package cool.dingstock.lib_base.json//package cool.dingstock.lib_base.json
//
//import com.squareup.moshi.Moshi
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//
//object JsonUtil {
//
//    val moshi: Moshi = Moshi.Builder()
//        .addLast(KotlinJsonAdapterFactory())
//        .build()
//
//
//
//    inline fun <reified T> fromJson(json: String): T? {
//        return moshi.adapter(T::class.java)
//            .nullSafe()
//            .fromJson(json)
//
//    }
//
//    inline fun <reified T> toJson(entity: T): String {
//        return moshi.adapter(T::class.java)
//            .toJson(entity)
//    }
//}