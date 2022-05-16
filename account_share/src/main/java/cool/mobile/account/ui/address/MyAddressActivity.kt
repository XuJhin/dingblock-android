package cool.mobile.account.ui.address

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kongzue.dialogx.dialogs.WaitDialog.dismiss
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.shop.UserAddressEntity
import cool.dingstock.appbase.entity.event.box.EventConfirmAddress
import cool.dingstock.appbase.entity.event.shop.EventAddressDelete
import cool.dingstock.appbase.entity.event.shop.EventRefreshAddressListData
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.mobile.account.dagger.AccountApiHelper
import cool.mobile.account.databinding.ActivityMyAddressBinding
import cool.mobile.account.itemView.AddressItemBinder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [AccountConstant.Path.MY_ADDRESS]
)
class MyAddressActivity : VMBindingActivity<BaseViewModel, ActivityMyAddressBinding>() {

    private val itemBinder: AddressItemBinder = AddressItemBinder()
    private val mAdapter: DcBaseBinderAdapter = DcBaseBinderAdapter(arrayListOf())
    private var addressList: List<UserAddressEntity>? = arrayListOf()
    private var mEntity: UserAddressEntity? = null

    @Inject
    lateinit var accountApi: AccountApi

    init {
        EventBus.getDefault().register(this)
        AccountApiHelper.apiAccountComponent.inject(this)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        viewBinding.titleBar.title = "我的地址"
        viewBinding.titleBar.setLeftOnClickListener {
            selectedAddress()
            finish()
        }
        viewBinding.tvEmptyAction.setOnShakeClickListener {
            routerToAddAddress()
        }
        viewBinding.tvAddAddress.setOnShakeClickListener {
            routerToAddAddress()
        }
        mAdapter.addItemBinder(UserAddressEntity::class.java, itemBinder)
        viewBinding.rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
        viewBinding.addressListIndexRefresh.setOnRefreshListener {
            refreshPage(false)
        }
        refreshPage(true)
    }

    override fun initListeners() {
        itemBinder.mListener = object : AddressItemBinder.ActionListener {
            override fun onClickItem(entity: UserAddressEntity) {
                when (intent.getStringExtra("MyAddress")) {
                    "FromConFirm" -> {
                        val event = EventConfirmAddress()
                        event.id = entity.id
                        event.name = entity.name
                        event.phone = entity.mobileZone.plus(" ").plus(entity.mobile)
                        event.address = entity.province.plus(entity.city).plus(entity.district)
                            .plus(entity.address)
                        EventBus.getDefault().post(event)
                        finish()
                    }
                }
            }

            override fun onClickEditAddress(entity: UserAddressEntity) {
                DcRouter(AccountConstant.Uri.ADD_ADDRESS)
                    .putExtra(AccountConstant.ExtraParam.UserAddressEntity, entity)
                    .start()
            }

            override fun onClickDeleteAddress(id: String) {
                showDeleteAddressDialog(id)
            }

            override fun onClickSwitchDefaultMode(entity: UserAddressEntity) {
                switchDefaultMode(entity)
            }
        }
    }

    private fun refreshPage(isFirst: Boolean) {
        if (isFirst) {
            showLoadingView()
        }
        loadData()
    }

    private fun loadData() {
        accountApi.getAddressList()
            .subscribe({
                if (!it.err && it.res != null) {
                    addressList = it.res
                    mAdapter.setList(it.res)
                    finishRequest()
                    showEmptyView(mAdapter.data.isEmpty())
                } else {
                    finishRequest()
                    showToastShort(it.msg)
                }
            }, { err ->
                showToastShort(err.message)
            })
    }

    private fun finishRequest() {
        hideLoadingView()
        viewBinding.addressListIndexRefresh.finishRefresh()
    }

    private fun showEmptyView(isShow: Boolean) {
        if (isShow) {
            viewBinding.llEmptyView.visibility = View.VISIBLE
            viewBinding.tvEmptyAction.visibility = View.VISIBLE
            viewBinding.llButtonContainer.visibility = View.GONE
            viewBinding.emptyView.setText("暂无收货地址")
        } else {
            viewBinding.llEmptyView.visibility = View.GONE
            viewBinding.tvEmptyAction.visibility = View.GONE
            viewBinding.llButtonContainer.visibility = View.VISIBLE
        }
    }

    private fun selectedAddress() {
        this.finish()
    }

    private fun deleteAddress(id: String) {
        accountApi.deleteAddress(id)
            .subscribe({ res ->
                if (!res.err) {
                    showToastShort("删除成功")
                    EventBus.getDefault().post(EventAddressDelete(id))
                    loadData()
                } else {
                    showToastShort(res.msg)
                }
            }, { err ->
                showToastShort(err.message)
            })
    }

    fun switchDefaultMode(entity: UserAddressEntity) {
        accountApi.editAddress(entity)
            .subscribe({
                if (!it.err) {
                    addressList?.forEach { data ->
                        data.isDefault = entity.id == data.id
                    }
                    mAdapter.setList(addressList)
                } else {
                    showToastShort("修改失败")
                }
            }, {
                showToastShort("修改异常")
            })
    }

    fun showDeleteAddressDialog(id: String) {
        CommonDialog.Builder(this)
            .content("您确定要删除收货地址吗？")
            .confirmTxt("确认")
            .cancelTxt("取消")
            .onCancelClick {
                dismiss()
            }
            .onConfirmClick {
                deleteAddress(id)
                dismiss()
            }
            .builder()
            .show()
    }

    private fun routerToAddAddress() {
        DcRouter(AccountConstant.Uri.ADD_ADDRESS)
            .putExtra(AccountConstant.ExtraParam.ADDRESS_COUNT, mAdapter.data.size)
            .start()
    }

    override fun onBackPressed() {
        selectedAddress()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun afterAddOrEdit(eventRefreshData: EventRefreshAddressListData) {
        mEntity = eventRefreshData.entity
        loadData()
    }

    override fun moduleTag(): String {
        return AccountConstant.LABEL
    }
}