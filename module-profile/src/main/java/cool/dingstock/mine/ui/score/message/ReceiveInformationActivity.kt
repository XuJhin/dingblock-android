package cool.dingstock.mine.ui.score.message

import android.os.Bundle
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.mine.ScoreRefreshEvent
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.mine.dagger.MineApiHelper
import cool.dingstock.mine.databinding.ActivityReceiveInformationBinding
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [MineConstant.Path.RECEIVE_INFORMATION])
class ReceiveInformationActivity : VMBindingActivity<BaseViewModel, ActivityReceiveInformationBinding>() {

    @Inject
    lateinit var mineApi: MineApi

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }

    var id: String? = ""

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        id = intent?.getStringExtra("id")
        viewBinding.apply {
            titleBar.title = "收货信息"
            titleBar.setLeftOnClickListener {
                finish()
            }
            edPeople.setOnFocusChangeListener { _, hasFocus ->
                ivPeople.hide(!hasFocus)
            }
            edNumber.setOnFocusChangeListener { _, hasFocus ->
                ivNumber.hide(!hasFocus)
            }
            edAddress.setOnFocusChangeListener { _, hasFocus ->
                ivAddress.hide(!hasFocus)
            }
            ivPeople.setOnClickListener {
                edPeople.setText("")
            }
            ivNumber.setOnClickListener {
                edNumber.setText("")
            }
            ivAddress.setOnClickListener {
                edAddress.setText("")
            }
            tvSubmit.setOnShakeClickListener {
                if (edPeople.text.toString() == "" || edNumber.text.toString() == "" || edAddress.text.toString() == "") {
                    showToastShort("请填写正确的收货信息")
                } else {
                    submitInformation()
                }
            }
        }

    }

    private fun submitInformation() {
        id?.let {
            mineApi.submitinformation(it, viewBinding.edPeople.text.toString(), viewBinding.edNumber.text.toString(), viewBinding.edAddress.text.toString())
                    .subscribe({
                        if (!it.err) {
                            EventBus.getDefault().post(ScoreRefreshEvent("REFRESH"))
                            showToastShort("提交成功")
                        } else {
                            showToastShort("请填写正确的收货信息")
                        }
                    }, {
                        showToastShort("请填写正确的收货信息")
                    })
        }
    }

    override fun initListeners() {
    }

    override fun moduleTag(): String {
        return ModuleConstant.MINE_MODULE
    }
}







