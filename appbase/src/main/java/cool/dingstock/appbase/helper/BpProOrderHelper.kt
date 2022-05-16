package cool.dingstock.appbase.helper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cool.dingstock.lib_base.stroage.ConfigSPHelper

object BpProOrderHelper {

    private const val bpOrderHelper = "bpOrderHelper"
    private const val bpOrderLimitCount = 10

    fun saveOrderGood(userId: String, goodId: String) {
        var isHaveThisUser = false
        var msgList: ArrayList<ProOrderEntity> = arrayListOf()
        val data = ConfigSPHelper.getInstance().getString(bpOrderHelper)
        if (data.isNullOrEmpty()) {
            val list = arrayListOf<String>()
            list.add(goodId)
            msgList.add(ProOrderEntity(userId, list))
        } else {
            val turnsType = object : TypeToken<List<ProOrderEntity>>() {}.type
            val gson = Gson()
            msgList = gson.fromJson(data, turnsType)
            msgList.forEach {
                if (it.userId == userId) {
                    if (it.goodIdList.size >= bpOrderLimitCount) {
                        it.goodIdList.removeFirst()
                    }
                    it.goodIdList.add(goodId)
                    isHaveThisUser = true
                    return@forEach
                }
            }
            if (!isHaveThisUser) {
                val list = arrayListOf<String>()
                list.add(goodId)
                msgList.add(
                    ProOrderEntity(
                        userId, list
                    )
                )
            }
        }
        ConfigSPHelper.getInstance().save(bpOrderHelper, Gson().toJson(msgList))
    }


    fun checkIsOrder(userId: String, goodId: String): Boolean {
        val msgList: List<ProOrderEntity>
        val data = ConfigSPHelper.getInstance().getString(bpOrderHelper)
        if (data.isNullOrEmpty()) {
            return false
        }
        val turnsType = object : TypeToken<List<ProOrderEntity>>() {}.type
        val gson = Gson()
        msgList = gson.fromJson(data, turnsType)
        msgList.forEach {
            if (it.userId == userId) {
                it.goodIdList.forEach { goodID ->
                    if (goodId == goodID) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun clearOrderData() {
        val msgList: ArrayList<ProOrderEntity>
        val data = ConfigSPHelper.getInstance().getString(bpOrderHelper)
        if (data.isNullOrEmpty()) {
            return
        }
        val turnsType = object : TypeToken<List<ProOrderEntity>>() {}.type
        val gson = Gson()
        msgList = gson.fromJson(data, turnsType)
        msgList.clear()
        ConfigSPHelper.getInstance().save(bpOrderHelper, Gson().toJson(msgList))
    }

    data class ProOrderEntity(
        val userId: String,
        var goodIdList: ArrayList<String>
    )
}