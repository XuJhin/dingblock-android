package cool.dingstock.appbase.widget.dialog

import android.os.Bundle
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.base.BaseObservablePopWindow
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.constant.CommonConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.databinding.ActivityCommonImgDialogBinding
import cool.dingstock.appbase.ext.load

@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [CommonConstant.Path.IMG_DIALOG])
class CommonImgDialogActivity : BaseObservablePopWindow<BaseViewModel, ActivityCommonImgDialogBinding>() {

    var imgUrl: String? = null
    var clickLink: String? = null
    var high: Int? = null
    var width: Int? = null

    override fun setSystemStatusBar() {
        StatusBarUtil.setTranslucent(this)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        imgUrl = uri.getQueryParameter(CommonConstant.UriParams.COMMON_IMG_URL)
        clickLink = uri.getQueryParameter(CommonConstant.UriParams.COMMON_IMG_CLICK_LINK)
        imgUrl?.let { url ->
            viewBinding.bgIv.load(url, false)
        }
    }

    override fun initListeners() {
        viewBinding.apply {
            closeIv.setOnClickListener {
                finish()
            }
            bgIv.setOnClickListener {
                clickLink?.let { link ->
                    finish()
                    DcRouter(link).start()
                }
            }
        }
    }

    override fun moduleTag(): String = "dialog"
}