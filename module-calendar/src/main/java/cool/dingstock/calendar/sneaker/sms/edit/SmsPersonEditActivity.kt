package cool.dingstock.calendar.sneaker.sms.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.CalendarConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.calendar.SmsPersonEntity
import cool.dingstock.appbase.entity.bean.calendar.SmsPersonInputEntity
import cool.dingstock.appbase.entity.event.calenar.SmsPersonChange
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.helper.SmsPersonHelper
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.commondialog.CommonDialog
import cool.dingstock.calendar.databinding.ActivitySmsPersonEditBinding
import cool.dingstock.calendar.databinding.SmsPersonBasicInputItemLayoutBinding
import cool.dingstock.calendar.item.SmsPersonInputItemBinder
import cool.dingstock.calendar.widget.SmsEditHintBubblesView
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.lib_base.util.ToastUtil
import org.greenrobot.eventbus.EventBus

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [CalendarConstant.Path.SMS_PERSON_EDIT]
)
class SmsPersonEditActivity : VMBindingActivity<BaseViewModel, ActivitySmsPersonEditBinding>() {

    val lastShowDialog by lazy {
        SmsEditHintBubblesView(this)
    }

    val itemBinder by lazy {
        SmsPersonInputItemBinder()
    }

    val mAdapter by lazy {
        DcBaseBinderAdapter(arrayListOf())
    }


    var smsPersonEntity: SmsPersonEntity? = null
    val basicFiledArr = arrayListOf<SmsPersonInputEntity>()
    val otherFiledArr = arrayListOf<SmsPersonInputEntity>()


    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        viewBinding.titleBar.setTitle("填写信息")
        smsPersonEntity = intent.getParcelableExtra(CalendarConstant.DataParam.KEY_SMS_PERSON)
        basicFiledArr.clear()
        otherFiledArr.clear()
        val basicList: ArrayList<SmsPersonInputEntity>? =
            intent.getParcelableArrayListExtra(CalendarConstant.DataParam.KEY_BASIC_INPUT_LIST)
        val otherList: ArrayList<SmsPersonInputEntity>? =
            intent.getParcelableArrayListExtra(CalendarConstant.DataParam.KEY_OTHER_INPUT_LIST)
        basicList?.let {
            basicFiledArr.addAll(basicList)
        }
        otherList?.let {
            otherFiledArr.addAll(otherList)
        }
        mAdapter.addItemBinder(itemBinder)
        viewBinding.moreInfoRv.adapter = mAdapter
        viewBinding.moreInfoRv.layoutManager = LinearLayoutManager(context)
        viewBinding.deleteTv.hide(smsPersonEntity == null)
        switchOtherInfo(false)
        setData()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListeners() {

        lastShowDialog.setTouchInterceptor { v, event ->
            lastShowDialog.dismiss()
            false
        }
        viewBinding.otherInfoSwitch.setOnShakeClickListener {
            switchOtherInfo(viewBinding.moreInfoRv.visibility == View.GONE)
        }
        itemBinder.listener = object : SmsPersonInputItemBinder.OnFocusChangeListener {
            override fun onChange(
                hasFocus: Boolean,
                editView: EditText,
                data: SmsPersonInputEntity
            ) {
                if (hasFocus) {
                    lastShowDialog.dismiss()
                    lastShowDialog.contentTv.text = data.bubble
                    lastShowDialog.showTopWithView(
                        window.decorView,
                        editView,
                        -32.azDp.toInt()
                    )
                }
            }
        }
        viewBinding.nameEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewBinding.saveTv.isEnabled = !TextUtils.isEmpty(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        viewBinding.saveTv.setOnShakeClickListener {
            val name = viewBinding.nameEdit.text.toString().trim()
            if (TextUtils.isEmpty(name)) {
                ToastUtil.getInstance()._short(context, "请输入姓名")
                return@setOnShakeClickListener
            }
            val desc = hashMapOf<String, String?>()
            basicFiledArr.forEach {
                //因为姓名单独处理过了
                if (it.id != "smsRaffleName") {
                    if (it.require) {
                        if (TextUtils.isEmpty(it.value)) {
                            ToastUtil.getInstance()._short(context, "请输入${it.fieldName}")
                            return@setOnShakeClickListener
                        }
                    }
                    if (!TextUtils.isEmpty(it.value)) {
                        desc.put(it.id, it.value ?: "")
                    }
                }

            }
            otherFiledArr.forEach {
                if (it.require) {
                    if (TextUtils.isEmpty(it.value)) {
                        ToastUtil.getInstance()._short(context, "请输入${it.fieldName}")
                        return@setOnShakeClickListener
                    }
                }
                if (!TextUtils.isEmpty(it.value)) {
                    desc.put(it.id, it.value ?: "")
                }
            }
            if (smsPersonEntity == null) {
                smsPersonEntity =
                    SmsPersonEntity("SmsPerson" + System.currentTimeMillis(), name, hashMapOf())
            }
            desc.forEach {
                if (!TextUtils.isEmpty(it.value)) {
                    smsPersonEntity?.desc?.put(it.key, desc.get(it.key))
                }
            }
            smsPersonEntity?.name = name
            smsPersonEntity?.let {
                SmsPersonHelper.savePerson(it)
                EventBus.getDefault().post(SmsPersonChange(true))
            }
            finish()
        }
        viewBinding.deleteTv.setOnShakeClickListener {
            smsPersonEntity?.let {
                CommonDialog.Builder(context)
                    .content("确定删除登记信息？")
                    .confirmTxt("删除")
                    .cancelTxt("取消")
                    .onConfirmClick(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            SmsPersonHelper.deletePerson(it.id)
                            EventBus.getDefault().post(SmsPersonChange(true))
                            finish()
                        }
                    })
                    .builder()
                    .show()
            }
        }
        window.decorView.getViewTreeObserver().addOnGlobalLayoutListener(OnGlobalLayoutListener {
            val heightDiff: Int =
                window.decorView.rootView.getHeight() - window.decorView.getHeight()
            if (heightDiff > SizeUtils.dp2px(200f)) { // if more than 200 dp, it's probably a keyboard...
                //打开
                lastShowDialog.dismiss()
                lastShowDialog.refreshLocation()
            } else {
                //关闭cc
                lastShowDialog.dismiss()
                lastShowDialog.refreshLocation()
            }
        })
    }


    fun setFocusChange(edit: EditText, tips: String) {
        edit.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lastShowDialog.dismiss()
                if (!TextUtils.isEmpty(tips)) {
                    lastShowDialog.contentTv.text = tips
                    lastShowDialog.showTopWithView(
                        window.decorView,
                        v,
                        -32.azDp.toInt()
                    )
                }
            }
        }
    }

    fun setData() {
        viewBinding.nameEdit.setText(smsPersonEntity?.name ?: "")
        viewBinding.basicInputLineLayer.removeAllViews()
        viewBinding.saveTv.isEnabled = !TextUtils.isEmpty(smsPersonEntity?.name)
        basicFiledArr.forEach {
            it.value = smsPersonEntity?.desc?.get(it.fieldName)
            //单独处理name，因为name是必填项，且必不可少
            if (it.id == "smsRaffleName") {
                if (it.require) {
                    viewBinding.nameTitle.text = it.fieldName + "（必填）"
                } else {
                    viewBinding.nameTitle.text = it.fieldName
                }
                viewBinding.nameEdit.hint = it.inputValue
                setFocusChange(viewBinding.nameEdit, it.bubble)
                viewBinding.nameEdit.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        viewBinding.saveTv.isEnabled = !TextUtils.isEmpty(s)
                    }

                    override fun afterTextChanged(s: Editable?) {
                    }

                })
            } else {
                it.value = smsPersonEntity?.desc?.get(it.id)
                val smsPersonBasicInputBinding = SmsPersonBasicInputItemLayoutBinding.inflate(
                    LayoutInflater.from(context),
                    viewBinding.basicInputLineLayer,
                    false
                )
                viewBinding.basicInputLineLayer.addView(smsPersonBasicInputBinding.root)
                setFocusChange(smsPersonBasicInputBinding.inputEdit, it.bubble)
                if (it.require) {
                    smsPersonBasicInputBinding.title.text = it.fieldName + "（必填）"
                } else {
                    smsPersonBasicInputBinding.title.text = it.fieldName
                }
                smsPersonBasicInputBinding.inputEdit.hint = it.inputValue
                smsPersonBasicInputBinding.inputEdit.setText(it.value ?: "")
                smsPersonBasicInputBinding.inputEdit.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        it.value = s.toString()
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }
                })
            }
        }
        otherFiledArr.forEach {
            it.value = smsPersonEntity?.desc?.get(it.id)
        }


        mAdapter.setList(otherFiledArr)
    }

    fun switchOtherInfo(isOpen: Boolean) {
        if (isOpen) {
            viewBinding.moreInfoRv.hide(false)
            viewBinding.openIcon.isSelected = true
        } else {
            viewBinding.openIcon.isSelected = false
            viewBinding.moreInfoRv.hide(true)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            if (lastShowDialog.isShowing) {
                lastShowDialog.dismiss()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun moduleTag(): String {
        return ModuleConstant.CALENDAR
    }

}