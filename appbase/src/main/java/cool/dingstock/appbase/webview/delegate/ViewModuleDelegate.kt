package cool.dingstock.appbase.webview.delegate

import com.lljjcoder.style.citypickerview.CityPickerView

interface ViewModuleDelegate {

    fun showToast(message: String)
    fun showLoadingDialog()
    fun hideLoadingDialog()
    fun finish()
    fun setRightTxt(text: String)
    fun getCityPicker(): CityPickerView
    fun showCityPickerView()
    fun setTitleBar(needHidden: Boolean?, backBtnColor: String?)

}