package cool.dingstock.appbase.webview.bridge

import cool.dingstock.lib_base.json.JSONHelper

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/24  11:30
 */
class ActionBrideEvent {
    interface ActionType{
        companion object{
            const val BACK_CLICK_ACTION = "backClickAction"
            const val RIGHT_CLICK_ACTION = "rightClickAction"
            const val ON_RESUME = "onResume"
            const val ON_PAUSE = "onPause"
            const val VIP_RECEIVED = "vipReceived"
            const val USER_PAY_SUCCESS = "userPaySuccess"
        }
    }

    var module:String = ""
    var action:String = ""
    var params:Any? = null
    var result:Any = "empty"

    companion object {
        fun actionEventBuild(action:String,module: IBridgeModule): ActionBrideEvent {
            val event = ActionBrideEvent()
            event.module = module.moduleName()
            event.action = action
            return event
        }
    }

    fun toJson():String{
        return JSONHelper.toJson(this)
    }

}