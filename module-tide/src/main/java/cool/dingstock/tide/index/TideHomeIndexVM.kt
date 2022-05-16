package cool.dingstock.tide.index

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.constant.CalendarConstant
import cool.dingstock.appbase.entity.bean.common.DateBean
import cool.dingstock.appbase.entity.bean.tide.*
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.tide.TideApi
import cool.dingstock.tide.dagger.TideApiHelper
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * 类名：TideHomdeIndexVM
 * 包名：cool.dingstock.tide.index
 * 创建时间：2021/7/20 3:30 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class TideHomeIndexVM : BaseViewModel() {

    @Inject
    lateinit var tideApi: TideApi

    init {
        TideApiHelper.apiShopComponent.inject(this)
    }

    private var monthList: MutableList<DateBean> = arrayListOf()
    val monthListData = MutableLiveData<MutableList<DateBean>>()
    var currentMonthIndex = 0
    val filterResultListLiveData = MutableLiveData<ArrayList<TideCompanyFilterEntity>>()
    val dealFilterResultListLiveData = MutableLiveData<ArrayList<TideDealInfoFilterEntity>>()
    val tideItemListLiveData = MutableLiveData<ArrayList<TideSectionsEntity>>()
    val tabLiveData = MutableLiveData<ArrayList<CategoriesEntity>>()
    val bannerData = MutableLiveData<ArrayList<BannerEntity>>()
    val goodItemListLiveData = MutableLiveData<ArrayList<GoodItem>>()
    val refreshLayoutLiveData = MutableLiveData<Boolean>()
    val subChangeLiveData = MutableLiveData<Boolean>()
    private var relMonthIndex = 0
    var currentTabIndex = MutableLiveData<Int>()

    val filterList = arrayListOf<TideCompanyFilterEntity>()
    val dealFilterList = arrayListOf<TideDealInfoFilterEntity>()

    /**
     * 获取月份列表
     * 当月份数据加载完毕时
     */
    fun getMonthList() {
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
        relMonthIndex = currentMonthIndex
        //设置tab数据
        monthListData.postValue(monthList)
    }


    fun loadData(isFirstLoad: Boolean, needRefreshTab: Boolean = false) {
        if (isFirstLoad) {
            postStateLoading()
        }
        val time: Long = if (currentMonthIndex == relMonthIndex) {
            System.currentTimeMillis()
        } else {
            monthList[currentMonthIndex].timeStamp
        }
        var type: String? = null
        if (tabLiveData.value?.size ?: 0 >= 2 && currentTabIndex.value ?: 0 < tabLiveData.value?.size ?: 0) {
            type = tabLiveData.value?.get(currentTabIndex.value ?: 0)?.id
        }
        tideApi.homeTideList(date = time, type = type, company = arrayListOf(), dealType = arrayListOf())
            .subscribe({
                if (!it.err && it.res != null) {
                    if (isFirstLoad) {
                        postStateSuccess()
                    }
                    val filterList = arrayListOf<TideCompanyFilterEntity>()
                    val allEntity = TideCompanyFilterEntity("全部", true)
                    allEntity.isSelected = true
                    filterList.add(allEntity)
                    it.res?.companies?.let { companies ->
                        filterList.addAll(companies)
                    }
                    val dealFilterList = arrayListOf<TideDealInfoFilterEntity>()
                    val dealAllEntity = TideDealInfoFilterEntity("全部", true)
                    dealAllEntity.isSelected = true
                    dealFilterList.add(dealAllEntity)
                    it.res?.dealType?.forEach { dealDesc ->
                        dealFilterList.add(TideDealInfoFilterEntity(dealDesc, false))
                    }
                    if(needRefreshTab){
                        tabLiveData.postValue(it.res?.categories?: arrayListOf())
                    }
                    filterResultListLiveData.postValue(filterList)
                    dealFilterResultListLiveData.postValue(dealFilterList)
                    tideItemListLiveData.postValue(it.res?.sections ?: arrayListOf())
                    if (type != CalendarConstant.CommentType.TIDE) {
                        if (!it.res?.banner.isNullOrEmpty()) {
                            goodItemListLiveData.postValue(arrayListOf())
                        } else {
                            goodItemListLiveData.postValue(it.res?.featured ?: arrayListOf())
                        }
                        bannerData.postValue(it.res?.banner ?: arrayListOf())
                    } else {
                        goodItemListLiveData.postValue(it.res?.featured ?: arrayListOf())
                        bannerData.postValue(arrayListOf())
                    }
                } else {
                    if (isFirstLoad) {
                        postStateError(it.msg)
                    }
                }
            }, {
                if (isFirstLoad) {
                    postStateError(it.message)
                }
            })
    }


    fun reLoadData(isOnlySubscribe: Boolean) {
        val companies = arrayListOf<String>()
        filterList.forEach {
            if (it.isSelected && !it.isAll) {
                companies.add(it.company)
            }
        }
        val dealDesc = arrayListOf<String>()
        dealFilterList.forEach {
            if (it.isSelected && !it.isAll) {
                dealDesc.add(it.dealDesc)
            }
        }
        val time: Long = if (currentMonthIndex == relMonthIndex) {
            System.currentTimeMillis()
        } else {
            monthList[currentMonthIndex].timeStamp
        }
        var type: String? = null
        if (tabLiveData.value?.size ?: 0 >= 2 && currentTabIndex.value ?: 0 < tabLiveData.value?.size ?: 0) {
            type = tabLiveData.value?.get(currentTabIndex.value ?: 0)?.id
        }
        tideApi.homeTideList(date = time, type = type, company = companies, dealType = dealDesc, isSubscribe = isOnlySubscribe)
            .subscribe({
                refreshLayoutLiveData.postValue(true)
                if (!it.err && it.res != null) {
                    tideItemListLiveData.postValue(it.res?.sections ?: arrayListOf())
                    if (type != CalendarConstant.CommentType.TIDE) {
                        if (!it.res?.banner.isNullOrEmpty()) {
                            goodItemListLiveData.postValue(arrayListOf())
                        } else {
                            goodItemListLiveData.postValue(it.res?.featured ?: arrayListOf())
                        }
                        bannerData.postValue(it.res?.banner ?: arrayListOf())
                    } else {
                        goodItemListLiveData.postValue(it.res?.featured ?: arrayListOf())
                        bannerData.postValue(arrayListOf())
                    }
                } else {
                    tideItemListLiveData.postValue(arrayListOf())
                    goodItemListLiveData.postValue(arrayListOf())
                    bannerData.postValue(arrayListOf())
                    shortToast(it.msg)
                }
            }, {
                refreshLayoutLiveData.postValue(true)
                tideItemListLiveData.postValue(arrayListOf())
                goodItemListLiveData.postValue(arrayListOf())
                bannerData.postValue(arrayListOf())
                shortToast(it.message)
            })
    }


    fun subscribeChange(entity: TideItemEntity) {
        tideApi.subscribeChange(entity.id, !entity.isSubscribe)
            .subscribe({
                if (!it.err) {
                    entity.isSubscribe = !entity.isSubscribe
                    shortToast(if (entity.isSubscribe) "订阅成功" else "取消订阅")
                    subChangeLiveData.postValue(true)
                } else {
                    shortToast(it.msg)
                }
            }, {
                shortToast(it.message)
            })
    }

}