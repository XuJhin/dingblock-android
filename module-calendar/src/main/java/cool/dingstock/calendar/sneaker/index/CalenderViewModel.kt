package cool.dingstock.calendar.sneaker.index

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.common.DateBean
import cool.dingstock.appbase.entity.bean.home.CalenderDataBean
import cool.dingstock.appbase.entity.bean.home.HomeBrandBean
import cool.dingstock.appbase.entity.bean.home.HomeProductGroup
import cool.dingstock.appbase.entity.bean.home.HomeTypeBean
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.calendar.dagger.CalendarApiHelper
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class CalenderViewModel : BaseViewModel() {
	@Inject
	lateinit var homeApi: HomeApi

	init {
		CalendarApiHelper.apiHomeComponent.inject(this)
	}

	val sneakerCalenderLiveData = MutableLiveData<CalenderDataBean>()
	val errorLiveData = MutableLiveData<String>()
	val monthListData = MutableLiveData<MutableList<DateBean>>()
	val requestFinish = MutableLiveData<Boolean>()
	private var monthList: MutableList<DateBean> = arrayListOf<DateBean>()
	var currentMonthIndex = 0
	private val filterList: ArrayList<String> = ArrayList()
	private val typeList: ArrayList<String> = ArrayList()

	private fun sneakerCalendar() {
		val currentMonthIndex: Int = currentMonthIndex
		val dateBean = monthList[currentMonthIndex]
		val subscribe = homeApi.loadSneakerCalendar(dateBean.timeStamp, null, null)
				.subscribe({ res ->
					if (!res.err && res.res != null) {
						val data = res.res
						if (data?.sections != null && data.sections!!.size > 0) {
							updateBrandSelectedStatus(data.sections)
						}
						res?.res?.isRefresh = true
						sneakerCalenderLiveData.postValue(data!!)
						requestFinish.postValue(true)
					} else {
						errorLiveData.postValue(res.msg)
						requestFinish.postValue(true)
					}
				}, { err ->
					errorLiveData.postValue(err.message)
					requestFinish.postValue(true)
				})
		addDisposable(subscribe)
	}

	/**
	 * 更换选中状态
	 */
	private fun updateBrandSelectedStatus(data: MutableList<HomeProductGroup>?) {
		if (filterList.isNullOrEmpty()) {
			return
		}
		data?.forEach {
			it.brands?.let {
				it.forEach { brand ->
					brand.isSelected = filterList.contains(brand.id)
				}
			}
		}
	}

	private fun sneakerMonthCalendar(isRefresh: Boolean) {
		if (isRefresh) {
			filterList.clear()
			typeList.clear()
		}
		val dateBean = monthList[currentMonthIndex]
		homeApi.loadSneakerCalendar(dateBean.timeStamp, filterList, typeList)
				.subscribe({ res ->
					if (!res.err && res.res != null) {
						val data = res.res
						data?.isRefresh = isRefresh
						updateBrandSelectedStatus(data?.sections)
						sneakerCalenderLiveData.postValue(data!!)
						requestFinish.postValue(true)
					} else {
						errorLiveData.postValue(res.msg)
						requestFinish.postValue(true)
					}
				}, {
					errorLiveData.postValue(it.message)
					requestFinish.postValue(true)
				})
	}

	/**
	 * refresh data
	 */
	fun refresh() {
		filterList.clear()
		typeList.clear()
		sneakerCalendar()
	}

	fun filterRequest() {
		sneakerMonthCalendar(false)
	}

	/**
	 * 先获取月份列表
	 */
	fun onDataLoad() {
		getMonthList()
	}

	/**
	 * 获取月份列表
	 * 当月份数据加载完毕时
	 */
	private fun getMonthList() {
		val calendar = Calendar.getInstance()
		val currentMonth = calendar[Calendar.MONTH] + 1
		val year = calendar[Calendar.YEAR]
		monthList = ArrayList(12)
		for (i in -6..-1) {
			var temp = currentMonth + i
			var tempYear = year
			//去年的
			if (temp <= 0) {
				tempYear -= 1
				temp += 12
			}
			calendar[tempYear, temp - 1, 1, 0, 0] = 0
			monthList.add(
                DateBean(
                    temp,
                    calendar.timeInMillis
                )
            )
		}
		var current: DateBean? = null
		for (i in 0..6) {
			var tempYear = year
			var temp = currentMonth + i
			if (temp > 12) {
				tempYear += 1
				temp -= 12
			}
			calendar[tempYear, temp - 1, 1, 0, 0] = 0
			val dateBean =
                DateBean(
                    temp,
                    calendar.timeInMillis
                )
			if (temp == currentMonth) {
				current = dateBean
			}
			monthList.add(dateBean)
		}
		currentMonthIndex = monthList.indexOf(current)
		//设置tab数据
		monthListData.postValue(monthList)
	}

	fun updateSelectedMonth(monthIndex: Int) {
		this.currentMonthIndex = monthIndex
		sneakerMonthCalendar(true)
	}

	/**
	 *
	 */
	fun updateFilterList(filterBrandList: MutableList<HomeBrandBean>, filterTypeList: MutableList<HomeTypeBean>) {
		filterList.clear()
		typeList.clear()
		for (homeBrandBean in filterBrandList) {
			val objectId = homeBrandBean.id
			if (TextUtils.isEmpty(objectId)) {
				continue
			}
			if (homeBrandBean.isSelected) {
				if (!filterList.contains(objectId)) {
					filterList.add(objectId ?: "")
				}
			} else {
				filterList.remove(objectId)
			}
		}
		for (homeTypeBean in filterTypeList) {
			if (!typeList.contains(homeTypeBean.name)) {
				typeList.add(homeTypeBean.name)
			}
		}
		if (filterBrandList.size == 0) {
			filterList.clear()
		}
		if (typeList.size == 0) {
			typeList.clear()
		}
		filterRequest()
	}
}