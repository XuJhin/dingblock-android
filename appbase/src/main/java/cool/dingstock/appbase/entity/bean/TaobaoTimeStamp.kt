package cool.dingstock.appbase.entity.bean

data class TaobaoTimeStamp(
        val api: String?,
        val v: String?,
        val ret: MutableList<String?> = arrayListOf(),
        val data: Time
)

data class Time(
        val t: String?
)