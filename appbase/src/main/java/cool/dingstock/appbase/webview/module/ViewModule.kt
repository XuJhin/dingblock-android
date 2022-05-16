package cool.dingstock.appbase.webview.module

import android.content.Context
import com.lljjcoder.Interface.OnCityItemClickListener
import com.lljjcoder.bean.DistrictBean
import com.lljjcoder.bean.ProvinceBean
import cool.dingstock.appbase.webview.bridge.*
import cool.dingstock.appbase.webview.delegate.ViewModuleDelegate
import java.lang.ref.WeakReference

class ViewModule(
    private val delegate: ViewModuleDelegate,
    override var context: WeakReference<Context>?,
    override var bridge: IJsBridge?
) : IBridgeModule {

    override fun moduleName(): String = "view"

    @XBridgeMethod
    fun showLoadingDialog(event: BridgeEvent) {
        delegate.showLoadingDialog()
        bridge?.let { event.toResponse().successDefault()?.send(it) }
    }

    @XBridgeMethod
    fun hideLoadingDialog(event: BridgeEvent) {
        delegate.hideLoadingDialog()
        bridge?.let { event.toResponse().successDefault()?.send(it) }
    }


    @XBridgeMethod
    fun showToast(event: BridgeEvent) {
        val message = event.params?.get("text") as String?
        delegate.showToast(message ?: "")
        bridge?.let { event.toResponse().successDefault()?.send(it) }
    }


    @XBridgeMethod
    fun finish(event: BridgeEvent) {
        delegate.finish()
        bridge?.let { event.toResponse().successDefault()?.send(it) }
    }

    @XBridgeMethod
    fun onResume() {
        val actionBrideEvent =
            ActionBrideEvent.actionEventBuild(ActionBrideEvent.ActionType.ON_RESUME, this)
        bridge?.nativeAction(actionBrideEvent.toJson(), object : OnNativeActionCallback {
            override fun onCallback(actionEvent: ActionBrideEvent) {

            }
        })
    }

    @XBridgeMethod
    fun onPause() {
        val actionBrideEvent =
            ActionBrideEvent.actionEventBuild(ActionBrideEvent.ActionType.ON_PAUSE, this)
        bridge?.nativeAction(actionBrideEvent.toJson(), object : OnNativeActionCallback {
            override fun onCallback(actionEvent: ActionBrideEvent) {

            }
        })
    }


    @XBridgeMethod
    fun setRightTxt(event: BridgeEvent) {
        val params = event.params?.get("text")
        var str = ""
        if (params != null) {
            str = params.toString()
        }
        delegate.setRightTxt(str)
    }

    @XBridgeMethod
    fun addressSelector(event: BridgeEvent) {
        delegate.getCityPicker().setOnCityItemClickListener(object : OnCityItemClickListener() {
            override fun onSelected(
                province: ProvinceBean?,
                city: com.lljjcoder.bean.CityBean?,
                district: DistrictBean?
            ) {
                province?.name.plus(" ").plus(city?.name).plus(" ").plus(district?.name)
                bridge?.let {
                    event.toResponse().successDefault()?.apply {
                        result = hashMapOf(
                            "provice" to province?.name,
                            "city" to city?.name,
                            "area" to district?.name
                        )
                    }?.send(it)
                }
            }

            override fun onCancel() {
                bridge?.let { event.toResponse().error("cancel", "cancel")?.send(it) }
            }
        })
        delegate.showCityPickerView()
    }

    @XBridgeMethod
    fun onChangeBackIcon(event: BridgeEvent) {
        try {
            val needHidden = event.params?.get("needHidden") as Boolean?
            val backBtnColor = event.params?.get("color") as String?
            delegate.setTitleBar(needHidden, backBtnColor)
        } catch (e: Exception) {
        }
    }

    fun onRightClick(callback: OnNativeActionCallback) {
        val actionBrideEvent =
            ActionBrideEvent.actionEventBuild(ActionBrideEvent.ActionType.RIGHT_CLICK_ACTION, this)
        bridge?.nativeAction(actionBrideEvent.toJson(), callback)
    }

    fun onBackClick(callback: OnNativeActionCallback) {
        val actionBrideEvent =
            ActionBrideEvent.actionEventBuild(ActionBrideEvent.ActionType.BACK_CLICK_ACTION, this)
        bridge?.nativeAction(actionBrideEvent.toJson(), callback)
    }

    fun onVipReceived(callback: OnNativeActionCallback) {
        val actionBrideEvent =
            ActionBrideEvent.actionEventBuild(ActionBrideEvent.ActionType.VIP_RECEIVED, this)
        bridge?.nativeAction(actionBrideEvent.toJson(), callback)
    }

}