package cool.dingstock.mine.enums

enum class LotteryNoteState(val key: String, val value: String?) {
    ALL("全部", null),
    GET("已中签", "get"),
    NOT_GET("未中签", "notGet")
}