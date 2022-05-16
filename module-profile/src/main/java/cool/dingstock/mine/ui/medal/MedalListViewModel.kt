package cool.dingstock.mine.ui.medal

import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.mine.MedalListBean
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.net.retrofit.api.getHttpException
import cool.dingstock.mine.dagger.MineApiHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MedalListViewModel:BaseViewModel() {
    @Inject
    lateinit var mineApi: MineApi

    var userId: String = ""

    val timer = MutableStateFlow<Long?>(null)

    val medalList = MutableStateFlow<MedalListBean?>(null)

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }

    fun getMedalList() = viewModelScope.launch {
        mineApi.getMedalList(userId)
            .catch {
                postStateError(getHttpException(it).msg)
            }.collect {
                if (!it.err) {
                    medalList.value = it.res
                    postStateSuccess()
                } else {
                    postStateError(it.msg)
                }
            }
    }
}