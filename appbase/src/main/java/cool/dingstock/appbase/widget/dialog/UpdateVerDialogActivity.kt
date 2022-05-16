package cool.dingstock.appbase.widget.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.base.BaseObservablePopWindow
import cool.dingstock.appbase.constant.CommonConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.databinding.ActivityUpdateVerDialogBinding
import cool.dingstock.appbase.entity.bean.home.UpdateType
import cool.dingstock.appbase.entity.bean.home.UpdateVerEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.foundation.manager.translate

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [CommonConstant.Path.UPDATE_VER_DIALOG]
)
class UpdateVerDialogActivity :
    BaseObservablePopWindow<BaseViewModel, ActivityUpdateVerDialogBinding>() {

    var updateVerEntity: UpdateVerEntity? = null

    override fun setSystemStatusBar() {
       translate()
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        viewBinding.apply {
            tv.movementMethod = LinkMovementMethod.getInstance()
            updateVerEntity = intent.getParcelableExtra(CommonConstant.Extra.UPDATE_VER_ENTITY)
            if (updateVerEntity == null) {
                finish()
                return
            }
            tv.text = updateVerEntity?.updateContent
            when (updateVerEntity?.updateType) {
                UpdateType.force -> {//强制更新
                    hideBtn.hide(true)
                }
                UpdateType.strong -> {//
                    hideBtn.hide(false)
                    hideBtn.text = "下次再说"
                }
                UpdateType.weak -> {
                    hideBtn.hide(false)
                    hideBtn.text = "不再提醒"
                }
                else -> {
                    hideBtn.hide(false)
                    hideBtn.text = "不在提醒"
                }
            }
            hideBtn.setOnClickListener {
                finish()
            }
            updateBtn.setOnClickListener {
                MobileHelper.getInstance().getDownLoadUrl(object : ParseCallback<String?> {
                    override fun onSucceed(data: String?) {
                        if (TextUtils.isEmpty(data)) {
                            return
                        }
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } catch (e: Exception) {
                        }
                    }

                    override fun onFailed(errorCode: String, errorMsg: String) {}
                })
            }
        }
    }

    override fun initListeners() {
    }

    override fun onBackPressed() {
        //如果是强制更新 就不显示
        if (updateVerEntity?.updateType != UpdateType.force) {
            finish()
        }
    }

    override fun moduleTag() = ModuleConstant.DIALOG
}