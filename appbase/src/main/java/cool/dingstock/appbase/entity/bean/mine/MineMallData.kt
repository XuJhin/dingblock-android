package cool.dingstock.appbase.entity.bean.mine

data class MineMallData(
        var checkInDays: Int?,
        var totalCredits: Int?,
        var checkedIn: Boolean?,
        var redeems: List<RedeemBean>?,
        var credits: List<String>?
)
