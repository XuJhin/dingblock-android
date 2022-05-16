package cool.dingstock.appbase.entity.bean.mine

import cool.dingstock.appbase.entity.bean.circle.GoodsBean

data class LotteryNoteListBean(
    val list: List<LotteryNoteBean>,
    val nextKey: Long
)

data class LotteryNoteBean(
    val chapterStr: String?,
    var chapterDate: Long?,
    val createdAt: Long?,
    val id: String,
    val product: GoodsBean?,
    val shop: Shop?,
    var state: String?
)

data class Shop(
    val id: String,
    val imageUrl: String,
    val name: String
)

data class UpdateLotteryBean(
    val chapterDate: Long?,
    val id: String,
    val state: String?,
)