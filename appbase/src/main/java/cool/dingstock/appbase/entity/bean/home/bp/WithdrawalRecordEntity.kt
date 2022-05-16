package cool.dingstock.appbase.entity.bean.home.bp

/**
 * "id":"",
"status":"", // processing:处理中 processed:已处理 finish:已到账
"time":1604660851280, // 申请提现时间
"amount":1 // 提现金额
 * */
data class WithdrawalRecordEntity(val id:String,
                                  var status:WithdrawalStatus,
                                  val time:Long,
                                  val amount:Float
){
    enum class WithdrawalStatus(val str:String){
        processing("审核中"),processed("已通过"),finish("已到账")
    }
}

data class WithdrawAllRecordList(
        val list: List<WithdrawalRecordEntity>,
        val nextKey: Long?
)