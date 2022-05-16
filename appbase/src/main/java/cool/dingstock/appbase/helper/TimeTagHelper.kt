package cool.dingstock.appbase.helper

import cool.dingstock.lib_base.stroage.ConfigSPHelper
import java.util.*

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/12  12:16
 */
object TimeTagHelper {
    val smsDrawHint = "smsDrawHint"
    val flashDrawHint = "flashDrawHint"
    val vipBubHint = "vipBubHint"
    val DealDetailsExpressTips = "dealDetailsExpressTips"
    val ImExpressTips = "imExpressTips"
    val DynamicDetailsExpressTips = "dynamicDetailsExpressTips"

    enum class TimeTag {
        /**
         * APP生命周期只显示一次
         * */
        APP_LIFE,
        ONCE_DAY,
        ONCE_24H
    }


    fun checkTimeTag(action: String, timeTag: TimeTag): Boolean {
        val lastTime = ConfigSPHelper.getInstance().getLong(action, 0L)
        val currentTimeMillis = System.currentTimeMillis()
        when (timeTag) {
            TimeTag.ONCE_DAY -> {
                val current = Calendar.getInstance()
                current.timeInMillis = currentTimeMillis

                val last = Calendar.getInstance()
                last.timeInMillis = lastTime
                //判断不是同一天就返回true
                if (current.get(Calendar.DAY_OF_YEAR) != last.get(Calendar.DAY_OF_YEAR) || current.get(Calendar.YEAR) != last.get(Calendar.YEAR)) {
                    return true
                }
                return false
            }
            TimeTag.ONCE_24H -> {
                if (currentTimeMillis - lastTime > 1000 * 60 * 60 * 24) {
                    return true
                }
            }
            TimeTag.APP_LIFE -> {
                if (lastTime == 0L) {
                    return true
                }
            }
        }
        return false
    }


//    fun checkTimeTagByHashMap(key: String, id: String, timeTag: TimeTag): Boolean {
//        val lastTime = ConfigSPHelper.getInstance().getHashMap(key, id)
//        val currentTimeMillis = System.currentTimeMillis()
//        when (timeTag) {
//            TimeTag.ONCE_DAY -> {
//                val current = Calendar.getInstance()
//                current.timeInMillis = currentTimeMillis
//
//                val last = Calendar.getInstance()
//                last.timeInMillis = lastTime
//                //判断不是同一天就返回true
//                if (current.get(Calendar.DAY_OF_YEAR) != last.get(Calendar.DAY_OF_YEAR) || current.get(Calendar.YEAR) != last.get(Calendar.YEAR)) {
//                    return true
//                }
//                return false
//            }
//            TimeTag.ONCE_24H -> {
//                if (currentTimeMillis - lastTime > 1000 * 60 * 60 * 24) {
//                    return true
//                }
//            }
//            TimeTag.APP_LIFE -> {
//                if (lastTime == 0L) {
//                    return true
//                }
//            }
//        }
//        return false
//    }

    fun updateTimeTag(action: String, time: Long) {
        ConfigSPHelper.getInstance().save(action, time)
    }

//    fun updateTimeTagByHashMap(action: String, key: String, time: Long) {
//        ConfigSPHelper.getInstance().saveHashMap(action, key, time)
//    }


}