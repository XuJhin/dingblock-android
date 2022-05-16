package cool.dingstock.appbase.helper

import android.text.TextUtils
import com.google.gson.Gson
import cool.dingstock.appbase.entity.bean.calendar.SmsPersonEntity
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import java.lang.Exception


/**
 * 类名：SmsPersonHelper
 * 包名：cool.dingstock.calendar.hepler
 * 创建时间：2021/7/2 12:25 下午
 * 创建人： WhenYoung
 * 描述：
 **/
object SmsPersonHelper {
    private val PERSON_IDS = "PersonIds"


    fun getPersonArray(): ArrayList<SmsPersonEntity> {
        try {
            val personIdsStr = ConfigSPHelper.getInstance().getString(PERSON_IDS)
            if (TextUtils.isEmpty(personIdsStr)) {
                return arrayListOf()
            }
            val personIds = JSONHelper.fromJsonList(personIdsStr, String::class.java)
            val list = arrayListOf<SmsPersonEntity>()
            personIds.forEach { id ->
                val str = ConfigSPHelper.getInstance().getString(id)
                if (!TextUtils.isEmpty(str)) {
                    try {
                        val entity = JSONHelper.fromJson(str, SmsPersonEntity::class.java)
                        list.add(entity)
                    } catch (e: Exception) {
                    }
                }
            }
            return list
        } catch (e: Exception) {
        }
        return arrayListOf()
    }

    fun deletePerson(id: String) {
        try {
            val personIdsStr = ConfigSPHelper.getInstance().getString(PERSON_IDS)
            if (TextUtils.isEmpty(personIdsStr)) {
            }
            val personIds = JSONHelper.fromJsonList(personIdsStr, String::class.java)
            personIds.remove(id)
            ConfigSPHelper.getInstance().save(PERSON_IDS, Gson().toJson(personIds))
        } catch (e: Exception) {
        }
        ConfigSPHelper.getInstance().save(id, "")
    }


    fun savePerson(smsEntity: SmsPersonEntity) {
        try {
            var personIdsStr = ConfigSPHelper.getInstance().getString(PERSON_IDS)
            if (TextUtils.isEmpty(personIdsStr)) {
               personIdsStr = "[]"
            }
            val personIds = JSONHelper.fromJsonList(personIdsStr, String::class.java)
            if (!personIds.contains(smsEntity.id)) {
                personIds.add(smsEntity.id)
                ConfigSPHelper.getInstance().save(PERSON_IDS, Gson().toJson(personIds))
            }
        } catch (e: Exception) {
        }
        ConfigSPHelper.getInstance().save(smsEntity.id, Gson().toJson(smsEntity))
    }

}