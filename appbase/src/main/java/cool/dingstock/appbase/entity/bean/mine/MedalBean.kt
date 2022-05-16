package cool.dingstock.appbase.entity.bean.mine

data class MedalListBean(
    val avatarUrl: String?,
    val sections: List<MedalSection>?,
    val nickName: String?,
    val isVip: Boolean?
)

data class MedalSection(
    val sectionTitle: String?,
    val achievements: List<MedalBean>?
)

data class MedalBean(
    val criteria: String?,
    val creditProductId: String?,
    val iconUrl: String?,
    val id: String?,
    val imageUrl: String?,
    val imageBWUrl: String?,
    val lastAchievementId: String?,
    val name: String?,
    val nextAchievementId: String?,
    val rootAchievementId: String?,
    var status: String?,
    val validity: Long?,
    val validityStr: String?,
) {
    fun madelStatus() = when (status) {
        MedalStatus.UNFULFILLED.value -> MedalStatus.UNFULFILLED
        MedalStatus.RECEIVABLE.value -> MedalStatus.RECEIVABLE
        MedalStatus.COMPLETED.value -> MedalStatus.COMPLETED
        MedalStatus.WEARING.value -> MedalStatus.WEARING
        else -> MedalStatus.UNFULFILLED
    }
}

enum class MedalStatus(val value: String) {
    UNFULFILLED("unfulfilled"),
    RECEIVABLE("receivable"),
    COMPLETED("completed"),
    WEARING("wearing"),
}