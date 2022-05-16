package cool.dingstock.appbase.entity.bean.circle


/**
 * 类名：HomeTopicEntity
 * 包名：cool.dingstock.appbase.entity.bean.circle
 * 创建时间：2021/9/28 3:57 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class HomeTopicEntity(val id: String, val name: String, var label:String? = null,var isDeal:Boolean? = false) {
    var isSelect = false
}


data class HomeTopicResultEntity(val list:ArrayList<HomeTopicEntity>?,val dealShoes:ArrayList<HomeHotTradingEntity>?)

data class HomeHotTradingEntity(val id:String,val imageUrl:String,val name:String)