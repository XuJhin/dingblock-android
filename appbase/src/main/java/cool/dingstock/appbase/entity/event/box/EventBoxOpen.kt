package cool.dingstock.appbase.entity.event.box


/**
 * 类名：EventBoxOpen
 * 包名：cool.dingstock.appbase.entity.event.box
 * 创建时间：2021/8/4 11:06 上午
 * 创建人： WhenYoung
 * 描述：
 **/
class EventBoxOpen {
}

class EventBoxBuy {
}

class EventFormalChallenges {
    val id: String? = null
}

class EventBoxOpenAgain {
    var type: String? = ""
}

class EventUseCardOpenBox {
    var type: String? = ""
    var preRecordId: String = ""
    var isExChangeCardFirst: Boolean = false
}

class EventBoxAllOpenFromBoxDetail {
}


