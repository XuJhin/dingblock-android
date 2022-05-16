package cool.dingstock.appbase.entity.bean.setting;






data class DisturbBean (
    var startTime  : Int =0,
    var endTime  : Int =0,
    var switchStatus  : Boolean?=null){

    fun getDisplayValue() : String{
        if (switchStatus != true) {
            return "未开启"
        }
        if (startTime != -1 && endTime != -1) {
            return ("$startTime" ?: "")  + ":00" + "至" + (endTime ?: "") + ":00";
        }
        return ""
    }

}
