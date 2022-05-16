package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/7/2 11:46
 * @Version:         1.1.0
 * @Description:
 */
@Parcelize
data class LocationEntity(
		val name: String,
		val address: String,
		val latitude: Double,
		val longitude: Double,
		var selected: Boolean = false
) : Parcelable {
	override fun equals(other: Any?): Boolean {
		return if (other is LocationEntity) {
			(this.name == other.name
					&& this.address == other.address
					&& this.latitude.equals(other.latitude)
					&& this.longitude.equals(other.longitude))
		} else {
			false
		}
	}

	override fun hashCode(): Int {
		var result = name.hashCode()
		result = 31 * result + address.hashCode()
		result = 31 * result + latitude.hashCode()
		result = 31 * result + longitude.hashCode()
		result = 31 * result + selected.hashCode()
		return result
	}
}

class LocationEntityPermission