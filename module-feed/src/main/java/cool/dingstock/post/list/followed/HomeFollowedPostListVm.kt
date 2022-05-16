package cool.dingstock.post.list.followed

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.home.RecommendKolUserEntity
import cool.dingstock.appbase.entity.event.circle.EventFollowerChange
import cool.dingstock.appbase.entity.event.relation.EventFollowChange
import cool.dingstock.appbase.ext.doRequest
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.appbase.net.api.mine.MineHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.post.dagger.PostApiHelper
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


/**
 * 类名：HomeFollowedPostListVm
 * 包名：cool.dingstock.post.list.followed
 * 创建时间：2021/9/27 5:01 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class HomeFollowedPostListVm : BaseViewModel() {

    @Inject
    lateinit var homeApi: HomeApi

    init {
        PostApiHelper.apiPostComponent.inject(this)
    }
    val recommendKolUserLiveData = MutableLiveData<ArrayList<RecommendKolUserEntity>>()


    fun loadRecommendKolUser() {
        postStateLoading()
        viewModelScope.doRequest({ homeApi.recommendKolUser() }, {
            if (!it.err&&it.res!=null) {
                postStateSuccess()
                recommendKolUserLiveData.postValue(it.res!!)
            } else {
                 postStateError(it.msg)
            }
        }, {
            postStateError(it.message)
        })
    }

    fun switchFollowState(follow:Boolean,id:String){
        MineHelper.getInstance().switchFollowState(follow,id, object :
            ParseCallback<Int> {
            override fun onSucceed(data: Int?) {
                shortToast(if(follow) "关注成功" else "取关成功" )
                EventBus.getDefault().post(EventFollowerChange(id, follow,data?:0,EventFollowerChange.Source.HomePageFollow))
            }

            override fun onFailed(errorCode: String?, errorMsg: String?) {
                shortToast(errorMsg?:"")
                shortToast(errorMsg)
            }
        })
    }

}