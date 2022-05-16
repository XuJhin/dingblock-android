package cool.dingstock.appbase.entity.bean.shop

data class DelegationRecordUserEntity(
    val avatar: String,
    val name: String,
    val orderTime: String,
    val attrs: ArrayList<String>
) {
}