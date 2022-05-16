package cool.dingstock.appbase.entity.bean.shop

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author wangjiang
 *  CreateAt Time 2021/5/31  18:20
 */
@Parcelize
data class AddressEntity(
		var id: String,  /* 地址ID */
		val userId: String? = "",
		val name: String,/* 姓名，必填 */
		val mobile: String,/* 手机号，必填 */
		val mobileZone: String,/* 手机国家代码，必填 */
		val province: String,/* 省，必填 */
		val city: String,/* 市，必填 */
		val district: String,/* 区，必填 */
		val address: String,/* 详细地址，必填 */
		val idNo: String? = "",/* 身份证号码，必填 */
		val idCard: IdCard? = null,/* 身份证照片，必填 */
		var isDefault: Boolean/* 是否默认地址 */
) : Parcelable

@Parcelize
data class UserAddressEntity(
		var id: String?,  /* 地址ID */
		val name: String,/* 姓名，必填 */
		val mobile: String,/* 手机号，必填 */
		val mobileZone: String,/* 手机国家代码，必填 */
		val province: String,/* 省，必填 */
		val city: String,/* 市，必填 */
		val district: String,/* 区，必填 */
		val address: String,/* 详细地址，必填 */
		var isDefault: Boolean = false/* 是否默认地址 */
) : Parcelable

@Parcelize
data class IdCard(
		val front: String,
		val back: String) : Parcelable

data class AddressList(
		val list: List<AddressEntity> = arrayListOf()
)

data class IdCradBackMessage(
		val endDate: String,
		val issue: String,
		val startDate: String
)

data class AddressId(
		val id: String
)

data class IdCardFrontMessage(
		val IDNumber: String,
		val name: String,
		val nationality: String,
		val gender: String,
		val birthDate: String,
		val address: String
)