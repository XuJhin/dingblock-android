package cool.mobile.account.ui.address

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import com.lljjcoder.Interface.OnCityItemClickListener
import com.lljjcoder.bean.DistrictBean
import com.lljjcoder.bean.ProvinceBean
import com.lljjcoder.citywheel.CityConfig
import com.lljjcoder.style.citypickerview.CityPickerView
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.shop.UserAddressEntity
import cool.dingstock.appbase.entity.event.box.EventConfirmAddress
import cool.dingstock.appbase.entity.event.shop.EventRefreshAddressListData
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.mobile.account.R
import cool.mobile.account.databinding.ActivityMyAddAddressBinding
import org.greenrobot.eventbus.EventBus

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [AccountConstant.Path.ADD_ADDRESS]
)
class MyAddAddressActivity :
    VMBindingActivity<MyAddAddressViewModel, ActivityMyAddAddressBinding>() {

    private var isDefaultAddress = false
    private var mEntity: UserAddressEntity? = null
    private var mPicker: CityPickerView? = null
    private var mAddressID = ""

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        mPicker = CityPickerView()
        mPicker!!.init(this)
        initViewContent()
        viewBinding.titleBar.title = "收货人信息"
        viewBinding.titleBar.setLeftOnClickListener {
            finish()
        }
    }

    private fun initViewContent() {
        val entity: UserAddressEntity? =
            intent.getParcelableExtra(AccountConstant.ExtraParam.UserAddressEntity)
        mAddressID = entity?.id ?: ""
        viewBinding.apply {
            if (!entity?.name.isNullOrEmpty()) {
                edName.setText(entity?.name)
                edNumber.setText(entity?.mobile)
                edProvince.setText(entity?.province.plus(" " + entity?.city + " " + entity?.district))
                edAddress.setText(entity?.address)
                isDefaultAddress = entity?.isDefault ?: false
                viewModel.initSaveButtonIsCanNotClickAble(false)
            } else {
                isDefaultAddress =
                    intent.getIntExtra(AccountConstant.ExtraParam.ADDRESS_COUNT, -1) == 0
                viewModel.initSaveButtonIsCanNotClickAble(true)
            }
        }
    }

    override fun initListeners() {
        viewBinding.apply {
            tvSave.setOnShakeClickListener {
                clickSaveButton()
            }
            edProvince.inputType = InputType.TYPE_NULL
            edProvince.setOnShakeClickListener {
                chooseArea(edProvince)
            }

            edProvince.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    chooseArea(edProvince)
                }
            }
            edAddress.setOnFocusChangeListener { _, hasFocus ->
                ivAddress.hide(!hasFocus)
            }
            edName.setOnFocusChangeListener { _, hasFocus ->
                ivName.hide(!hasFocus)
            }
            edNumber.setOnFocusChangeListener { _, hasFocus ->
                ivNumber.hide(!hasFocus)
            }
            ivAddress.setOnClickListener {
                edAddress.setText("")
            }
            ivName.setOnClickListener {
                edName.setText("")
            }
            ivNumber.setOnClickListener {
                edNumber.setText("")
            }
        }
        initWatcher()
    }


    private fun initTextChangedListener(view: EditText, type: String) {
        view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                when (type) {
                    "edProvince" -> viewModel.isEmptyProvince.value = view.text.isEmpty()
                    "edAddress" -> viewModel.isEmptyAddress.value = view.text.isEmpty()
                    "edName" -> viewModel.isEmptyName.value = view.text.isEmpty()
                    "edNumber" -> viewModel.isEmptyNumber.value = view.text.isEmpty()
                }
                viewModel.upDateSaveButtonStatus()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }


    private fun initWatcher() {
        viewBinding.apply {
            initTextChangedListener(edProvince, "edProvince")
            initTextChangedListener(edAddress, "edAddress")
            initTextChangedListener(edName, "edName")
            initTextChangedListener(edNumber, "edNumber")
        }
    }

    override fun initBaseViewModelObserver() {
        super.initBaseViewModelObserver()
        viewModel.apply {
            isAddOrEditFinish.observe(this@MyAddAddressActivity) {
                when (intent.getStringExtra(AccountConstant.ExtraParam.ADD_ADDRESS)) {
                    "FromConFirm" -> {
                        val event = EventConfirmAddress()
                        event.id = it.id
                        event.name = it.name
                        event.phone = it.mobileZone.plus(" " + it.mobile)
                        event.address = it.province.plus(it.city + it.district + it.address)
                        EventBus.getDefault().post(event)
                        finish()
                    }
                    else -> {
                        val event = EventRefreshAddressListData()
                        mEntity = it
                        event.entity = it
                        EventBus.getDefault().post(event)
                        finish()
                    }
                }
            }
            chooseAddressData.observe(this@MyAddAddressActivity) {
                chooseAddress(it)
            }
            isCanClickSaveButton.observe(this@MyAddAddressActivity) {
                upDateButtonState(it)
            }
        }
    }

    private fun upDateButtonState(isClick: Boolean) {
        when (isClick) {
            true -> {
                viewBinding.tvSave.background =
                    AppCompatResources.getDrawable(context, R.drawable.common_btn_r8_black)
                viewBinding.tvSave.isClickable = true
            }
            false -> {
                viewBinding.tvSave.background =
                    AppCompatResources.getDrawable(context, R.drawable.common_btn_r8_gray)
                viewBinding.tvSave.isClickable = false
            }
        }
    }

    private fun chooseAddress(entity: UserAddressEntity) {
        if (intent.getStringExtra(AccountConstant.ExtraParam.CHOOSE_ADDRESS) == "") {
            val resultIntent = Intent()
            resultIntent.putExtra(
                AccountConstant.ExtraParam.ADDRESS,
                entity.province.plus(entity.city).plus(entity.district)
            )
            resultIntent.putExtra(AccountConstant.ExtraParam.LOCATION, entity.address)
            resultIntent.putExtra(AccountConstant.ExtraParam.NAME, entity.name)
            resultIntent.putExtra(
                AccountConstant.ExtraParam.PHONE,
                entity.mobileZone.plus(" ").plus(entity.mobile)
            )
            setResult(1, resultIntent)
            finish()
        }
    }

    private fun chooseArea(view: View) {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            selectAddress()
        }
    }

    private fun selectAddress() {
        val cityConfig = CityConfig.Builder()
            .title(" ") //标题
            .titleTextSize(20) //标题文字大小
//            .titleTextColor(this.resources.getString(R.color.color_text_black1.toInt())) //标题文字颜  色
            .titleBackgroundColor("#FFFFFF") //标题栏背景色
            .confirTextColor(this.resources.getString(R.color.black.toInt())) //确认按钮文字颜色
            .confirmText("确认") //确认按钮文字
            .confirmTextSize(16) //确认按钮文字大小
            .cancelText("请选择地区") //取消按钮文字
            .cancelTextSize(20) //取消按钮文字大小
            .setCityWheelType(CityConfig.WheelType.PRO_CITY_DIS) //显示类，只显示省份一级，显示省市两级还是显示省市区三级
            .showBackground(true) //是否显示半透明背景
            .visibleItemsCount(4) //显示item的数量
            .province("四川省") //默认显示的省份
            .city("成都市") //默认显示省份下面的城市
            .district("武侯区") //默认显示省市下面的区县数据
            .provinceCyclic(true) //省份滚轮是否可以循环滚动
            .cityCyclic(true) //城市滚轮是否可以循环滚动
            .districtCyclic(true) //区县滚轮是否循环滚动
            .setCustomItemLayout(R.layout.account_choose_address_item) //自定义item的布局
            .setCustomItemTextViewId(R.id.item_city_name_tv) //自定义item布局里面的textViewid
            .drawShadows(true) //滚轮不显示模糊效果
            .setLineHeigh(0) //中间横线的高度
            .setShowGAT(true) //是否显示港澳台数据，默认不显示
            .build()

        mPicker?.setConfig(cityConfig)
        mPicker?.setOnCityItemClickListener(object : OnCityItemClickListener() {
            override fun onSelected(
                province: ProvinceBean?,
                city: com.lljjcoder.bean.CityBean?,
                district: DistrictBean?
            ) {
                viewBinding.edProvince.setText(
                    province?.name.plus(" ").plus(city?.name).plus(" ").plus(district?.name)
                )
            }

            override fun onCancel() {}
        })
        mPicker?.showCityPicker()
    }

    private fun clickSaveButton() {
        if (viewBinding.tvSave.isClickable) {
            if (mAddressID.isEmpty()) {
                mAddressID = "-1"
            }
            val addressList = viewBinding.edProvince.text.split(" ")
            val entity = UserAddressEntity(
                mAddressID,
                viewBinding.edName.text.toString(),
                viewBinding.edNumber.text.toString(),
                viewBinding.tvShowZone.text.toString(),
                addressList[0],
                addressList[1],
                addressList[2],
                viewBinding.edAddress.text.toString(),
                isDefaultAddress
            )
            if (viewBinding.edNumber.text.toString().length != 11) {
                showToastShort("手机号不正确")
            } else {
                if (mAddressID != "-1") {
                    viewModel.editAddress(entity)
                } else {
                    viewModel.addNewAddress(entity)
                }
            }
        }
    }

    override fun moduleTag(): String {
        return AccountConstant.LABEL
    }
}