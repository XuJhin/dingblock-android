package cool.dingstock.appbase.entity.event.update

data class EventScoreChange(val change: Boolean)

class EventRefreshMineCollectionAndMinePage(val pageType: String? = "")

class EventRefreshLotteryNote

class EventMedalStateChange(val userId: String? = "",val isGetMedal:Boolean? = false)

data class EventMedalWear(val userId: String? = "", val iconUrl: String? = null)