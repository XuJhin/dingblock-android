package cool.dingstock.uicommon.product.dialog

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.BaseBottomFullViewBindingFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.OnItemClickListener
import cool.dingstock.appbase.constant.CalendarConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.calendar.SmsFiledEntity
import cool.dingstock.appbase.entity.bean.calendar.SmsInputTemplateEntity
import cool.dingstock.appbase.entity.bean.calendar.SmsParameterSelectorEntity
import cool.dingstock.appbase.entity.bean.calendar.SmsPersonEntity
import cool.dingstock.appbase.entity.bean.home.HomeField
import cool.dingstock.appbase.entity.bean.home.HomeRaffleSmsBean
import cool.dingstock.appbase.entity.event.calenar.SmsPersonChange
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.helper.SmsPersonHelper
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.calendar.item.ParameterSelectorItemBinder
import cool.dingstock.calendar.item.SmsPersonFiledItemBinder
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.uicommon.R
import cool.dingstock.uicommon.dagger.UICommonApiHelper
import cool.dingstock.uicommon.databinding.CalendarDialogSmsRegistrationLayoutBinding
import cool.dingstock.uicommon.product.item.SmsPersonItemBinder
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * 类名：Sms
 * 包名：cool.dingstock.calendar.sms.dialog
 * 创建时间：2021/7/1 5:38 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class SmsRegistrationDialog :
    BaseBottomFullViewBindingFragment<CalendarDialogSmsRegistrationLayoutBinding>() {

    @Inject
    lateinit var api: CalendarApi

    var needFiles = arrayListOf<HomeField>()
    private var mSmsBean: HomeRaffleSmsBean? = null
    private var raffleId: String = ""
    private var smsInputTemplateEntity: SmsInputTemplateEntity? = null

    var personList = arrayListOf<SmsPersonEntity>()


    val personAdapter by lazy { DcBaseBinderAdapter(arrayListOf()) }
    val personItemBinder by lazy { SmsPersonItemBinder() }
    val filedItemBinder by lazy { SmsPersonFiledItemBinder() }
    private val filedAdapter by lazy { DcBaseBinderAdapter(arrayListOf()) }
    val parameterAdapter by lazy { DcBaseBinderAdapter(arrayListOf()) }
    val parameterItemBinder by lazy { ParameterSelectorItemBinder() }

    val selectParameterDialog by lazy {
        SelectParameterDialog()
    }

    val smsTipsDialog by lazy {
        return@lazy context?.run {
            SmsTipsDialog(this)
        }
    }


    val vipTipsDialog by lazy {
        return@lazy context?.run {
            VipTipsDialog(this)
        }
    }


    var selPosition = -1
    var selEntity: SmsPersonEntity? = null
    var needNameSpace = false

    //尺码相关
    var selSize = ""


    override fun initDataEvent() {

        personList.clear()
        personList.addAll(SmsPersonHelper.getPersonArray())
        viewBinding.noPersonAddFra.hide(personList.size != 0)
        viewBinding.addLayer.hide(personList.size == 0)
        viewBinding.registrationDesc.hide(true)
        viewBinding.sendSmsBtn.isEnabled = selEntity != null && !TextUtils.isEmpty(selSize)
        initRv()
        if (personList.size > 0) {
            selEntity = personList[0]
            selEntity?.selected = true
            selPosition = 0
        }
        personAdapter.setList(personList)
        selEntity?.let {
            onSelPerson(it)
        }

        initListener()
    }


    private fun initRv() {
        personAdapter.addItemBinder(personItemBinder)
        viewBinding.registrationRv.adapter = personAdapter
        viewBinding.registrationRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        filedAdapter.addItemBinder(filedItemBinder)
        viewBinding.registrationDescRv.adapter = filedAdapter
        viewBinding.registrationDescRv.layoutManager = LinearLayoutManager(context)
        parameterAdapter.addItemBinder(parameterItemBinder)
        viewBinding.selectedParameterRv.adapter = parameterAdapter
        viewBinding.selectedParameterRv.layoutManager = LinearLayoutManager(context)

    }

    fun initListener() {
        viewBinding.helpIv.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Calendar.SMSLottery_click_QuestionMark)
            smsTipsDialog?.show()
        }
        viewBinding.noPersonAddFra.setOnShakeClickListener {
            context?.let {
                UTHelper.commonEvent(UTConstant.Calendar.SMSLottery_click_AddTo)
                DcUriRequest(it, CalendarConstant.Uri.SMS_PERSON_EDIT)
                    .putExtra(
                        CalendarConstant.DataParam.KEY_BASIC_INPUT_LIST,
                        smsInputTemplateEntity?.basicInfo
                    )
                    .putExtra(
                        CalendarConstant.DataParam.KEY_OTHER_INPUT_LIST,
                        smsInputTemplateEntity?.otherInfo
                    )
                    .start()
            }

        }
        viewBinding.addLayer.setOnShakeClickListener {
            context?.let {
                UTHelper.commonEvent(UTConstant.Calendar.SMSLottery_click_AddTo)
                if (!LoginUtils.isLoginAndRequestLogin(it)) {
                    return@setOnShakeClickListener
                }
                if (LoginUtils.getCurrentUser()?.isVip() != true) {
                    //展示开通会员弹窗
                    vipTipsDialog?.show()
                    return@setOnShakeClickListener
                }
                DcUriRequest(it, CalendarConstant.Uri.SMS_PERSON_EDIT)
                    .putExtra(
                        CalendarConstant.DataParam.KEY_BASIC_INPUT_LIST,
                        smsInputTemplateEntity?.basicInfo
                    )
                    .putExtra(
                        CalendarConstant.DataParam.KEY_OTHER_INPUT_LIST,
                        smsInputTemplateEntity?.otherInfo
                    )
                    .start()
            }
        }
        personItemBinder.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseBinderAdapter,
                holder: BaseViewHolder,
                position: Int
            ) {
                context?.let {
                    UTHelper.commonEvent(
                        UTConstant.Calendar.SMSLottery_click_ChooseRegistrant,
                        "order",
                        "${position + 1}"
                    )
                    if (position != selPosition) {
                        personList.forEach {
                            it.selected = false
                        }
                        personList.get(position).selected = true
                        personAdapter.notifyDataSetChanged()
                        selPosition = position
                        selEntity = personList.get(position)
                        onSelPerson(personList.get(position))
                    }
                }
            }
        }
        viewBinding.editLayer.setOnShakeClickListener {
            context?.let {
                DcUriRequest(it, CalendarConstant.Uri.SMS_PERSON_EDIT)
                    .putExtra(
                        CalendarConstant.DataParam.KEY_BASIC_INPUT_LIST,
                        smsInputTemplateEntity?.basicInfo
                    )
                    .putExtra(
                        CalendarConstant.DataParam.KEY_OTHER_INPUT_LIST,
                        smsInputTemplateEntity?.otherInfo
                    )
                    .putExtra(CalendarConstant.DataParam.KEY_SMS_PERSON, selEntity)
                    .start()
            }
        }
        parameterItemBinder.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseBinderAdapter,
                holder: BaseViewHolder,
                position: Int
            ) {
                (parameterAdapter.data[position] as? SmsParameterSelectorEntity)?.let { entity ->
                    UTHelper.commonEvent(UTConstant.Calendar.SMSLottery_click_ChooseSize)
                    smsInputTemplateEntity?.sizes?.let {
                        //目前就只有一个选择size 所以直接跳转
                        selectParameterDialog.setData(
                            entity.name,
                            smsInputTemplateEntity!!.sizes!!,
                            entity.value
                        )
                        selectParameterDialog.setOnSelConfirm {
                            entity.value = it
                            selSize = it
                            parameterAdapter.notifyDataItemChanged(position)
                            selEntity?.let { entity -> onSelPerson(entity) }
                        }
                        selectParameterDialog.show(childFragmentManager, "selectParameterDialog")
                    }
                }
            }
        }
        viewBinding.sendSmsBtn.setOnShakeClickListener {
            sendSms()
        }
    }


    @SuppressLint("SetTextI18n")
    fun onSelPerson(entity: SmsPersonEntity) {
        viewBinding.sendSmsBtn.isEnabled = selEntity != null && !TextUtils.isEmpty(selSize)
        viewBinding.registrationDesc.hide(false)
        val filedEntities = arrayListOf<SmsFiledEntity>()
        needFiles.forEach {
            it.key?.let { key ->
                var value = entity.desc?.get(key)
                if(it.key == "smsRaffleName"){
                    value = entity.name
                }
                //单独处理尺码
                if (it.key == "smsRaffleSize") {

                }else {
                    if (!TextUtils.isEmpty(value)) {
                        it.value = SmsFiledEntity(key, it.name ?: "", value, it)
                        filedEntities.add(it.value!!)
                    } else {
                        it.value = SmsFiledEntity(key, it.name ?: "", it.placeholder, it)
                        filedEntities.add(it.value!!)
                    }
                }
            }
        }
        filedAdapter.setList(filedEntities)
    }

    fun setSmsData(raffleId: String, smsBean: HomeRaffleSmsBean, entity: SmsInputTemplateEntity) {
        this.raffleId = raffleId
        this.mSmsBean = smsBean
        needFiles.clear()
        mSmsBean?.sections?.forEach {
            it.fields?.forEach { homeField ->
                if (homeField.key == "smsRaffleSize") {
                    //默认给尺码选择器 选中一个默认
                    selSize = homeField.placeholder ?: ""
                }
                needFiles.add(homeField)
            }
        }

        smsInputTemplateEntity = entity
        val sizeSelEntity = SmsParameterSelectorEntity("请选择尺码")
        sizeSelEntity.value = selSize
        val list = arrayListOf(sizeSelEntity)
        parameterAdapter.setList(list)
    }

    fun sendSms() {
        context?.let {
            if (selPosition > 0) {
                if (!LoginUtils.isLoginAndRequestLogin(it)) {
                    return
                }
                if (LoginUtils.getCurrentUser()?.isVip() != true) {
                    //展示开通会员弹窗
                    vipTipsDialog?.show()
                    UTHelper.commonEvent(
                        UTConstant.Calendar.SMSLottery_click_SendMessages,
                        "BackAction",
                        "弹出会员弹窗"
                    )
                    return
                }
            }
            val sb = StringBuilder()
            needFiles.forEach {
                when (it.key) {
                    "smsRaffleSize" -> {
                        if (TextUtils.isEmpty(selSize)) {
                            ToastUtil.getInstance()._short(context, "请选择尺码")
                            return
                        }
                        sb.append(selSize).append(it.suffix ?: "")
                    }
                    else -> {
                        val value = it.value?.value
                        if (TextUtils.isEmpty(value)) {
                            UTHelper.commonEvent(
                                UTConstant.Calendar.SMSLottery_click_SendMessages,
                                "BackAction",
                                "toast：该店铺登记需要${it.name}，请完善信息"
                            )
                            ToastUtil.getInstance()._short(context, "该店铺登记需要${it.name}，请完善信息")
                            return
                        }
                        sb.append(value).append(it.suffix ?: "")
                    }
                }
            }
            val bodyStr = sb.toString()
            if (TextUtils.isEmpty(bodyStr)) {
                return
            }
            if (TextUtils.isEmpty(mSmsBean!!.shopPhoneNum)) {
                ToastUtil.getInstance()
                    ._short(context, context?.getString(R.string.sms_phone_num_empty))
                return
            }
            val smsToUri = Uri.parse("smsto:" + mSmsBean!!.shopPhoneNum)
            val intent = Intent(Intent.ACTION_VIEW, smsToUri)
            intent.putExtra("sms_body", bodyStr)
            startActivity(intent)
            UTHelper.commonEvent(
                UTConstant.Calendar.SMSLottery_click_SendMessages,
                "BackAction",
                "正常跳转"
            )
            api.smsUpload(raffleId).subscribe({
                if (!it.err) {
                    Logger.e("上报成功${raffleId}")
                } else {
                    Logger.e("上报失败${raffleId},${it.msg}")
                }
            }, {
                Logger.e("上报失败${raffleId},${it.message}")
            })
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun smsPersonChange(event: SmsPersonChange) {
        personList.clear()
        personList.addAll(SmsPersonHelper.getPersonArray())

        viewBinding.noPersonAddFra.hide(personList.size != 0)
        viewBinding.addLayer.hide(personList.size == 0)
        viewBinding.registrationDesc.hide(true)

        if (personList.size == 0) {
            selEntity = null
            personAdapter.setList(personList)
            viewBinding.sendSmsBtn.isEnabled = selEntity != null && !TextUtils.isEmpty(selSize)
            return
        }
        var position = 0
        personList.forEachIndexed { index, entity ->
            if (selEntity?.id == entity.id) {
                position = index
                return@forEachIndexed
            }
        }
        selPosition = position
        selEntity = personList[position]
        selEntity?.selected = true
        personAdapter.setList(personList)
        selEntity?.let {
            onSelPerson(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UICommonApiHelper.apiPostComponent.inject(this)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}