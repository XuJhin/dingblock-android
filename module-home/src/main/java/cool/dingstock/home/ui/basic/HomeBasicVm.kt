package cool.dingstock.home.ui.basic

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.HomeData
import cool.dingstock.appbase.entity.bean.score.ScoreIndexUserInfoEntity
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.home.HomeHelper
import cool.dingstock.appbase.storage.NetDataCacheHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.home.ui.home.HomeIndexFragmentVm

class HomeBasicVm : BaseViewModel() {

    val basicApi = BasicRepository()

    val rvDataLiveData = MutableLiveData<HomeData>()
    val refreshLiveData = MutableLiveData<Boolean>()
    val signLiveData = MutableLiveData<ScoreIndexUserInfoEntity>()

    fun loadData() {
        //查询新接口 获取 后台业务判断 信息
        val homeData = HomeHelper.getInstance().cacheHomeData
        if (null != homeData) {
            if (null != homeData.posts) {
                for (post in homeData.posts!!.posts) {
                    post.recommendItem = true
                }
            }
            rvDataLiveData.postValue(homeData)
            return
        }
        getHomeData(false)
    }

    fun getHomeData(isRefresh: Boolean) {
        val subscribe = basicApi.home()
            .subscribe({ res ->
                refreshLiveData.postValue(true)
                val data = res.res
                if (!res.err && data != null) {
                    NetDataCacheHelper.saveData(HomeIndexFragmentVm.cacheKey, data)
                    if (null != data.posts) {
                        for (post in data.posts!!.posts) {
                            post.recommendItem = true
                        }
                    }
                    rvDataLiveData.postValue(data!!)
                } else {
                    if (!isRefresh) {
                        postStateError(res.msg)
                    }
                }
            }, { err ->
                refreshLiveData.postValue(true)
                if (!isRefresh) {
                    postStateError(err.message ?: "网络链接失败")
                }
            })
        addDisposable(subscribe)

    }


    fun sign() {
        basicApi.scoreSign()
            .subscribe({
                if (!it.err) {
                    UTHelper.commonEvent(UTConstant.Score.HomeP_ShakeToSignIn)
                    signLiveData.postValue(it.res!!)
                }
            }, {

            })
    }
}