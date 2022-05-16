package cool.dingstock.home.ui.home

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.constant.HomeBusinessConstant
import cool.dingstock.appbase.entity.bean.home.HomeCategoryBean
import cool.dingstock.appbase.entity.bean.home.HomeData
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.appbase.storage.NetDataCacheHelper
import cool.dingstock.home.dagger.HomeApiHelper
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.lib_base.util.Logger
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/8  15:28
 */
class HomeIndexFragmentVm : BaseViewModel() {
    companion object {
        const val cacheKey = "homeApiCacheKey"
    }

    val tabDataListLiveData = MutableLiveData<ArrayList<HomeCategoryBean>>()
    val viewPagerDataLiveData = MutableLiveData<HomeData>()


    @Inject
    lateinit var homeApi: HomeApi

    private val DEFAULT_CATEGORY = "首页"
    private val DEFALUT_ID = HomeBusinessConstant.CategoryType.HOME

    init {
        HomeApiHelper.apiHomeComponent.inject(this)
    }

    fun loadData() {
        getHomeData()
    }

    private fun getHomeData() {
        val subscribe = homeApi.home().subscribe({ res ->
            val data = res.res
            if (!res.err && data != null) {
                NetDataCacheHelper.saveData(cacheKey, data)
                postStateSuccess()
                val categories: List<HomeCategoryBean?>? = data.categories
                val categoryList = addDefaultCategory(categories)
                tabDataListLiveData.postValue(categoryList)
                data.categories = categoryList
                viewPagerDataLiveData.postValue(data)
            } else {
                postStateError(res.msg)
            }

        }, { error ->
            postStateError(error.message ?: "网络加载失败")
        })
        addDisposable(subscribe)
    }

    fun addDefaultCategory(originList: List<HomeCategoryBean?>?): ArrayList<HomeCategoryBean> {
        val categoryList: ArrayList<HomeCategoryBean> = ArrayList()
        val homeCategoryBean = HomeCategoryBean()
        homeCategoryBean.name = DEFAULT_CATEGORY
        homeCategoryBean.objectId = DEFALUT_ID
        homeCategoryBean.type = DEFALUT_ID
        categoryList.add(homeCategoryBean)
        if (CollectionUtils.isNotEmpty(originList)) {
            for (categoryBean in originList!!) {
                if (TextUtils.isEmpty(categoryBean!!.type)) {
                    Logger.w("addDefaultCategory  type is null ignore $categoryBean")
                    continue
                }
                categoryList.add(categoryBean)
            }
        }
        return categoryList
    }


}