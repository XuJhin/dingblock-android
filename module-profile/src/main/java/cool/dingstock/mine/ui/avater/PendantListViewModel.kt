package cool.dingstock.mine.ui.avater

import cool.dingstock.appbase.entity.bean.mine.PendantDetail
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PendantListViewModel: BaseViewModel() {
    private val _pendantList = MutableStateFlow(arrayListOf<PendantDetail>())
    val pendantList: StateFlow<ArrayList<PendantDetail>> get() = _pendantList

    fun postPendantList(pendantList: ArrayList<PendantDetail>) {
        _pendantList.value = pendantList
    }
}