package cool.dingstock.appbase.webview.bridge

import cool.dingstock.lib_base.json.JSONHelper

enum class EBridgeEventType(val tag:String){
    Request("request"),
    Response("response"),
    Event("event"),
}

class BridgeEvent {

    interface EventType {
        companion object {
            val REQUEST: String
                get() = "request"
            val RESPONSE: String
                get() = "response"
            val EVENT: String
                get() = "event"
        }
    }

    var type: String = ""
    var module: String = ""
    var method: String = ""
    var requestId: String = ""
    var params: HashMap<String, Any?>? = null
    var error: Any? = null
    var result: Any? = null
    var eventName: String? = null

    companion object {
        fun eventBuild(): BridgeEvent {
            val event = BridgeEvent()
            event.type = EventType.EVENT
            event.requestId = System.currentTimeMillis().toString()
            return event
        }
    }


    private fun toEventString(): String {
        return JSONHelper.toJson(this)
    }


    fun toResponse(): BridgeEvent {
        val response = BridgeEvent()
        response.type = EventType.RESPONSE
        response.module = this.module
        response.method = this.method
        response.requestId = this.requestId
        return response
    }


    fun success(result: Any): BridgeEvent? {
        if (this.type != EventType.RESPONSE) {
            return null
        }
        this.result = result
        return this
    }

    fun successDefault(): BridgeEvent? {
        if (this.type != EventType.RESPONSE) {
            return null
        }
        this.result = Any()
        return this
    }

    fun error(code: String, message: String): BridgeEvent? {
        if (this.type != EventType.RESPONSE) {
            return null
        }
        eventName = "error"
        val err = hashMapOf<String, String>()
        with(err) {
            put("code", code)
            put("msg", message)
        }
        this.error = err
        return this
    }

    fun send(bridge: IJsBridge) {
        bridge.receiveMessage(toEventString())
    }
}