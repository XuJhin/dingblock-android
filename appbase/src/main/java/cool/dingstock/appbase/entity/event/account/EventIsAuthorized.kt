package cool.dingstock.appbase.entity.event.account;


data class EventIsAuthorized(
    var login: Boolean,
    val isWhere2Login: String = ""
)
