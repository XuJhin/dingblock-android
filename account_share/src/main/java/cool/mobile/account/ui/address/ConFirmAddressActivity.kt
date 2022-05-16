package cool.mobile.account.ui.address

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.dialog.LogisticsInfoDialog
import cool.dingstock.appbase.entity.bean.shop.UserAddressEntity
import cool.dingstock.appbase.entity.event.box.EventConfirmAddress
import cool.dingstock.appbase.entity.event.shop.EventRefreshBoxData
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.util.ToastUtil
import cool.mobile.account.R
import cool.mobile.account.dagger.AccountApiHelper
import cool.mobile.account.databinding.ActivityConFirmAddressBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [AccountConstant.Path.CONFIRM_ADDRESS])
class ConFirmAddressActivity : VMBindingActivity<BaseViewModel, ActivityConFirmAddressBinding>() {

    @Inject
    lateinit var accountApi: AccountApi

    init {
        EventBus.getDefault().register(this)
        AccountApiHelper.apiAccountComponent.inject(this)
    }

    private var goodId = ""
    private var count = 1
    private var allBagCount = 0
    private var addressId = ""

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        goodId = intent.getStringExtra(AccountConstant.ExtraParam.ID).toString()
        allBagCount = intent.getIntExtra(AccountConstant.ExtraParam.COUNT, 0)

        viewBinding.apply {
            tvCount.text = count.toString()
            ivGood.load(intent.getStringExtra(AccountConstant.ExtraParam.ICON))
            tvGoodName.text = intent.getStringExtra(AccountConstant.ExtraParam.TITLE)
            tvMsg.text = intent.getStringExtra(AccountConstant.ExtraParam.MSG)
            titleBar.title = "确认领取"
        }
        fetchDataAndUI()
    }

    private fun fetchDataAndUI() {
        accountApi.getAddressList()
                .subscribe({
                    if (!it.err && it.res != null) {
                        val isNoAddress = it.res.isNullOrEmpty()
                        viewBinding.apply {
                            layoutHaveAddress.hide(isNoAddress)
                            layoutNoAddress.hide(!isNoAddress)
                            upDateButtonState(!isNoAddress)
                            if (!isNoAddress) {
                                val addressList: List<UserAddressEntity>? = it.res
                                addressList?.forEach { it ->
                                    if (it.isDefault) {
                                        addressId = it.id.toString()
                                        tvName.text = it.name
                                        tvNumber.text = it.mobileZone.plus(" ").plus(it.mobile)
                                        tvAddress.text = it.province.plus(it.city).plus(it.district).plus(it.address)
                                    }
                                }
                            }
                        }
                    } else {
                        showToastShort(it.msg)
                    }
                }, { err ->
                    showToastShort(err.message)
                })
    }

    override fun initListeners() {
        viewBinding.apply {
            titleBar.setLeftOnClickListener {
                EventBus.getDefault().post(EventRefreshBoxData())
                finish()
            }
            layoutNoAddress.setOnShakeClickListener {
                DcRouter(AccountConstant.Uri.ADD_ADDRESS)
                        .putExtra(AccountConstant.ExtraParam.ADD_ADDRESS, AccountConstant.ExtraParam.FROM_CONFIRM)
                        .start()
            }
            layoutHaveAddress.setOnShakeClickListener {
                DcRouter(AccountConstant.Uri.MY_ADDRESS)
                        .putExtra(AccountConstant.ExtraParam.MY_ADDRESS, AccountConstant.ExtraParam.FROM_CONFIRM)
                        .start()
            }
            tvGetGood.setOnShakeClickListener {
                if (layoutHaveAddress.visibility == View.VISIBLE) {
                    val dialog = LogisticsInfoDialog(context)
                    dialog.onClickListener = object : LogisticsInfoDialog.OnClickListener {
                        override fun onClickConfirm() {
                            accountApi.createReceiveOrder(goodId, count, addressId)
                                    .subscribe({
                                        ToastUtil.getInstance().makeTextAndShow(context, "领取成功，审核通过后会尽快发货到您的收货地址", Toast.LENGTH_SHORT)
                                        EventBus.getDefault().post(EventRefreshBoxData())
                                        finish()
                                    }, {
                                        ToastUtil.getInstance().makeTextAndShow(context, "领取失败", Toast.LENGTH_SHORT)
                                    })
                        }
                    }
                    dialog.setData(tvName.text.toString(),
                            tvNumber.text.toString(),
                            tvAddress.text.toString()).show()
                }
            }

            tvSub.setOnClickListener {
                if (count > 1) {
                    count -= 1
                    tvCount.text = count.toString()
                }
            }

            tvAdd.setOnClickListener {
                if (count < allBagCount) {
                    count += 1
                    tvCount.text = count.toString()
                }
            }
        }
    }

    private fun upDateButtonState(isClick: Boolean) {
        viewBinding.apply {
            when (isClick) {
                true -> {
                    tvGetGood.background =  AppCompatResources.getDrawable(context, R.drawable.common_btn_r8_black)
                    tvGetGood.isClickable = true
                }
                false -> {
                    tvGetGood.background =  AppCompatResources.getDrawable(context, R.drawable.common_btn_r8_gray)
                    tvGetGood.isClickable = false
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun afterConfirmAddress(data: EventConfirmAddress) {
        viewBinding.apply {
            addressId = data.id.toString()
            tvName.text = data.name
            tvNumber.text = data.phone
            tvAddress.text = data.address
            layoutHaveAddress.hide(false)
            layoutNoAddress.hide(true)
            upDateButtonState(true)
        }
    }

    override fun onBackPressed() {
        EventBus.getDefault().post(EventRefreshBoxData())
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun moduleTag(): String {
        return AccountConstant.LABEL
    }
}
