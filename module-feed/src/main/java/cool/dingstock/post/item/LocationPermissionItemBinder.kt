package cool.dingstock.post.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.itembinder.DcBaseItemBinder
import cool.dingstock.appbase.entity.bean.circle.LocationEntityPermission
import cool.dingstock.post.R

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/7/1 14:43
 * @Version:         1.1.0
 * @Description:
 */
class LocationPermissionItemBinder : DcBaseItemBinder<LocationEntityPermission, LocationPermissionViewHolder>() {
	override fun onConvert(holder: LocationPermissionViewHolder, data: LocationEntityPermission) {
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationPermissionViewHolder {
		val view = LayoutInflater.from(parent.context)
				.inflate(R.layout.post_item_location_permissin, parent, false)
		return LocationPermissionViewHolder(view)
	}
}

class LocationPermissionViewHolder(itemView: View) : BaseViewHolder(itemView)

