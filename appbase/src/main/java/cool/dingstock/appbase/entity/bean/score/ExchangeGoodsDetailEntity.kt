package cool.dingstock.appbase.entity.bean.score
import cool.dingstock.appbase.entity.bean.circle.MedalPreViewUserEntity

data class ExchangeGoodsDetailEntity(
    val id: String? = "",
    val createAt: Long? = 0,
    val name: String? = "",
    val cost: Long? = 0,
    val imageUrl: String? = "",
    val explain: String? = "",
    val introduce: String? = "",
    val buttonStr: String? = "",
    val validityStr: String? = "",
    val soldOut: Boolean? = false,
    val desc: String? = "",
    val user: MedalPreViewUserEntity? = null,
    val achievement: MedalIconEntity? = null
)

data class MedalIconEntity(
    val iconUrl: String? = ""
)

enum class SourceType(val value: String) {
    FROM_SCORE_INDEX("FROM_SCORE_INDEX"),
    FROM_EXCHANGE_PAGE("FROM_EXCHANGE_PAGE"),
    FROM_MINE_MEDAL_PAGE("FROM_MINE_MEDAL_PAGE")
}