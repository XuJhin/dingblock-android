package cool.dingstock.appbase.entity.bean.circle


/**
 * 类名：TradingTopicFilterEntity
 * 包名：cool.dingstock.appbase.entity.bean.circle
 * 创建时间：2021/12/3 10:24 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class TradingTopicFilterEntity() {

    private val map: HashMap<String, TradingTopicFilterField> = HashMap()

    fun getGoodsType(): TradingTopicFilterField {
        if (map["goodsType"] == null) {
            map["goodsType"] = TradingTopicFilterField("goodsType", arrayListOf())
        }
        return map["goodsType"]!!
    }

    fun getGoodsQuality(): TradingTopicFilterField {
        if (map["goodsQuality"] == null) {
            map["goodsQuality"] = TradingTopicFilterField("goodsQuality", arrayListOf())
        }
        return map["goodsQuality"]!!
    }

    fun getSizeList(): TradingTopicFilterField {
        if (map["goodsSizeList"] == null) {
            map["goodsSizeList"] = TradingTopicFilterField("goodsSizeList", arrayListOf())
        }
        return map["goodsSizeList"]!!
    }

    fun getSenderAddress(): TradingTopicFilterField {
        if (map["senderAddress"] == null) {
            map["senderAddress"] = TradingTopicFilterField("senderAddress", arrayListOf())
        }
        return map["senderAddress"]!!
    }

    fun getFilterFields(): ArrayList<TradingTopicFilterField> {
        val list = arrayListOf<TradingTopicFilterField>()
        map.values.forEach {
            list.add(it)
        }
        return list
    }

    fun reset(){
        getGoodsType().list.clear()
        getGoodsQuality().list.clear()
        getSizeList().list.clear()
        getSenderAddress().list.clear()
        map["productId"]?.list?.clear()
    }


    fun putProductId(value: String) {
        putValue("productId", value, true)
    }

    fun putGoodsType(value: String) {
        putValue("goodsType", value, true)
    }

    fun putGoodsQuality(value: String) {
        putValue("goodsQuality", value, true)
    }

    fun putSizeList(value: String) {
        putValue("goodsSizeList", value, false)
    }

    fun putGoodsAddress(value: String) {
        putValue("senderAddress", value, true)
    }

    fun removeGoodsSize(value: String) {
        getSizeList().list.remove(value)
    }


    private fun putValue(key: String, value: String, singleSel: Boolean) {
        var list = map[key]?.list
        if (list == null) {
            map[key] = TradingTopicFilterField(key, arrayListOf())
            list = map[key]?.list
        }
        if (singleSel) {
            list?.clear()
            list?.add(value)
        } else {
            if (list?.contains(value) != true) {
                list?.add(value)
            }
        }
    }

    fun hasFilter():Boolean{
        map.keys.forEach {
            if(map[it]?.list?.size?:0>0){
                return true
            }
        }

        return false
    }

}

class TradingTopicFilterField(val id: String, val list: ArrayList<String>)


