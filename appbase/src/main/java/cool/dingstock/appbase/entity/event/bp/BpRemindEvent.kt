package cool.dingstock.appbase.entity.event.bp

data class BpRemindEvent(val id: String, val count: Int, var isAdd: Boolean) {
}

data class BpTestTimeSuccessEvent(val testTime:Long,val msg: String, val advice: String) {
}

class BpRefreshTipEvent(){

}