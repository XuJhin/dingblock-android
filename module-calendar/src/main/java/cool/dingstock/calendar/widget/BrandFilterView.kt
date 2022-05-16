package cool.dingstock.calendar.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.HomeBrandBean
import cool.dingstock.appbase.entity.bean.home.HomeTypeBean
import cool.dingstock.appbase.entity.event.home.EventBrandChooseChange
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.calendar.R
import cool.dingstock.calendar.pop.BrandChooserPop
import org.greenrobot.eventbus.EventBus

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/13  18:27
 */
class BrandFilterView(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
	private var filterIv: View? = null
	private var filterTv: View? = null

	//记录所有Brands
	private var allBrands: ArrayList<HomeBrandBean> = arrayListOf()

	//记录筛选的brand
	private val filterResultList: MutableList<HomeBrandBean> = arrayListOf()
	private val typeResultList: MutableList<HomeTypeBean> = arrayListOf()

	private val defaultTypesList = listOf(
		HomeTypeBean("国内发售"),
		HomeTypeBean("国外发售"),
		HomeTypeBean("国内线上"),
	)

	private val allTypes = arrayListOf<HomeTypeBean>()

	init {
		LayoutInflater.from(context).inflate(R.layout.brand_filter_layout, this, true)
		filterIv = findViewById(R.id.filter_iv)
		filterTv = findViewById(R.id.filter_tv)
	}

	fun clearFilter() {
		filterResultList.clear()
		typeResultList.clear()
		updateFilterSelected()
	}

	fun setBrandLayerData(brands: List<HomeBrandBean>, types: List<HomeTypeBean>, isRefresh: Boolean) {
		if (isRefresh) {
			allBrands.clear()
			//将品牌全部添加到列表
			allBrands.addAll(brands)
			allTypes.clear()
			//将类型全部添加到列表
			if (types.size < defaultTypesList.size) {
				allTypes.addAll(defaultTypesList)
			} else {
				allTypes.addAll(types)
			}
		}
		for (brand in allBrands) {
			//初始化时所有都应选中
			if (filterResultList.isEmpty()) {
				brand.isSelected = true
			} else {
				brand.isSelected = filterResultList.contains(brand)
			}
		}
		for (type in allTypes) {
			//初始化时所有都应选中
			if (typeResultList.isEmpty()) {
				type.selected = true
			} else {
				type.selected = typeResultList.contains(type)
			}
		}
		updateFilterSelected()
		setOnClickListener {
			UTHelper.commonEvent(UTConstant.Calendar.CalendarP_click_Filter)
			BrandChooserPop.newBuilder(context)
				.brands(allBrands)
				.types(allTypes)
				.dismissListener { filterList, typeList ->
					if (allBrands.isEmpty()) {
						return@dismissListener
					}
					if (filterList.isNullOrEmpty()) {
						return@dismissListener
					}
					filterResultList.clear()
					typeResultList.clear()
					typeResultList.addAll(typeList)
					filterResultList.addAll(filterList)
					updateFilterSelected()
					EventBus.getDefault().post(EventBrandChooseChange(filterResultList, typeResultList))
				}.build().show()
		}
	}

	private fun updateFilterSelected() {
		val state = if (filterResultList.size == 0 && typeResultList.size == 0) {
			false
		} else {
			filterResultList.size != allBrands.size || typeResultList.size != allTypes.size
		}
		filterIv?.isSelected = state
		filterTv?.isSelected = state
	}
}

