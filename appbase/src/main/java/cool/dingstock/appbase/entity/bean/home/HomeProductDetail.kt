package cool.dingstock.appbase.entity.bean.home;

data class HomeProductDetail(
    var region: HomeRegion?,
    var raffles: List<HomeRaffle>?
) {

    fun setupFinishState() {
        if (region?.name.equals("已结束")) {
            raffles?.map {
                it.hasFinished = true
            }
        }
    }

}
