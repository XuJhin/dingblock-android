package cool.mobile.account.ui.address

import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.shop.UserAddressEntity
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.mobile.account.dagger.AccountApiHelper
import javax.inject.Inject

/**
 * @author wangjiang
 *  CreateAt Time 2021/7/29  11:23
 */
class MyAddAddressViewModel : BaseViewModel() {
    @Inject
    lateinit var accountApi: AccountApi

    @Inject
    lateinit var commonApi: CommonApi

    init {
        AccountApiHelper.apiAccountComponent.inject(this)
    }

    var isCanClickSaveButton = MutableLiveData<Boolean>()
    var isEmptyProvince = MutableLiveData<Boolean>()
    var isEmptyAddress = MutableLiveData<Boolean>()
    var isEmptyName = MutableLiveData<Boolean>()
    var isEmptyNumber = MutableLiveData<Boolean>()

    val chooseAddressData = MutableLiveData<UserAddressEntity>()
    val isAddOrEditFinish = MutableLiveData<UserAddressEntity>()

    fun initSaveButtonIsCanNotClickAble(boolean: Boolean) {
        isEmptyProvince.value = boolean
        isEmptyAddress.value = boolean
        isEmptyName.value = boolean
        isEmptyNumber.value = boolean
        upDateSaveButtonStatus()
    }

    fun addNewAddress(entity: UserAddressEntity) {
        accountApi.addNewAddress(entity)
                .subscribe({
                    if (!it.err) {
                        entity.id = it.res!!.id
                        shortToast("添加成功")
                        isAddOrEditFinish.postValue(entity)
                        chooseAddressData.postValue(entity)
                    } else {
                        shortToast("添加失败")
                    }
                }, {
                    shortToast("添加异常")
                })
    }
    
    fun editAddress(entity: UserAddressEntity) {
        accountApi.editAddress(entity)
                .subscribe({
                    if (!it.err) {
                        isAddOrEditFinish.postValue(entity)
                        shortToast("修改成功")
                    } else {
                        shortToast("修改失败")
                    }
                }, {
                    shortToast("修改异常")
                })
    }

    fun upDateSaveButtonStatus() {
        if (isEmptyProvince.value!!
                || isEmptyAddress.value!!
                || isEmptyName.value!!
                || isEmptyNumber.value!!
        ) {
            isCanClickSaveButton.postValue(false)
        } else {
            isCanClickSaveButton.postValue(true)
        }
    }
}