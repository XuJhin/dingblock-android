package cool.dingstock.appbase.entity.event.update

data class EventUpdateInfo (
    var androidVersion  : String?,
    var androidForceUpdate  : Boolean=false,
    var link  : String?,
    var androidUpdateTip  : String?
)
