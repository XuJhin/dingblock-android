package cool.dingstock.appbase.entity.bean.mine

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PendantHomeBean(
    val pendants: List<Pendant>?,
    val user: User?
)

data class Pendant(
    val id: String,
    val name: String?,
    val pendantList: List<PendantDetail>?
)

data class User(
    val id: String?,
    var avatarPendantId: String?,
    var avatarPendantUrl: String?,
    val avatarUrl: String?,
    val isVip: Boolean?
)

@Parcelize
data class PendantDetail(
    val id: String,
    val cornerIcon: String?,
    val needVip: Boolean?,
    val imageUrl: String?,
    val name: String?,
    var selected: Boolean = false
) : Parcelable

data class AccountSettingEntity(
    val zone: String?,
    var mobile: String?,
    var isWXBinding: Boolean?
)

@Parcelize
data class MobileEntity(
    val zone: String?,
    var mobile: String?,
    val code: String?
): Parcelable