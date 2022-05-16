package cool.dingstock.setting.ui.edit

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.FileProvider
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.SettingConstant
import cool.dingstock.appbase.entity.event.update.EventUserDataChange
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.getColorRes
import cool.dingstock.appbase.ext.getDrawableRes
import cool.dingstock.appbase.imageload.GlideHelper.loadCircle
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.widget.common_edit_dialog.CommonEditInputDialog
import cool.dingstock.imagepicker.ImagePicker
import cool.dingstock.imagepicker.bean.ImageItem
import cool.dingstock.imagepicker.bean.MimeType
import cool.dingstock.imagepicker.bean.selectconfig.CropConfig
import cool.dingstock.imagepicker.custom.CustomImgPickerPresenter
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.uicommon.setting.R
import cool.dingstock.uicommon.setting.databinding.SettingActivityUserEditLayoutBinding
import org.greenrobot.eventbus.EventBus
import java.io.File
import kotlin.math.ceil

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [SettingConstant.Path.USER_EDIT]
)
class SettingUserEditActivity :
    VMBindingActivity<UserEditViewModel, SettingActivityUserEditLayoutBinding>() {
    private var oldDesc: String? = null
    private var oldNickName: String? = null
    private var oldAvatar: String? = null
    private var updateNickNameDialog: CommonEditInputDialog? = null

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        val user = AccountHelper.getInstance().user
        initTitleBar()
        if (null == user) {
            showToastShort(R.string.common_user_not_login)
            finish()
            return
        }
        showLoadingView()
        initObserve()
        viewBinding.userPhoneTv.text = LoginUtils.getCurrentUser()?.mobilePhoneNumber ?: ""
        viewModel.requestUserInfo()
    }

    private fun initTitleBar() {
        val textView = TextView(this)
        textView.setText(R.string.setting_user_save)
        textView.setTextColor(getColorRes(R.color.white))
        textView.background = getDrawableRes(R.drawable.common_black1_bg_c)
        textView.layoutParams = ViewGroup.LayoutParams(53.dp.toInt(), 26.dp.toInt())
        textView.gravity = Gravity.CENTER
        viewBinding.titleBar.apply {
            setRightView(textView)
        }
    }

    private fun initObserve() {
        viewModel.apply {
            errLiveData.observe(this@SettingUserEditActivity) {
                hideLoadingView()
                showErrorView(it)
            }
            userInfoLiveData.observe(
                this@SettingUserEditActivity
            ) { (_, nickName, avatarUrl, desc) ->
                oldDesc = desc
                oldNickName = nickName
                oldAvatar = avatarUrl
                hideLoadingView()
                updateUserHeadIV(avatarUrl)
                updateUserNick(nickName)
                updateUserDesc(desc)
            }
            uploadImageFailed.observe(this@SettingUserEditActivity) { s ->
                showToastShort(s)
                hideLoadingDialog()
            }
            userAvatarLiveData.observe(this@SettingUserEditActivity) { file ->
                hideLoadingDialog()
                showSuccessDialog("更新成功")
                updateUserHeadIV(file)
            }
            updateUserInfoLiveData.observe(this@SettingUserEditActivity) {
                hideLoadingDialog()
                EventBus.getDefault().post(EventUserDataChange(true))
                ToastUtil.getInstance()._short(BaseLibrary.getInstance().context, "更新成功")
                finish()
            }
            nickNameLiveData.observe(this@SettingUserEditActivity) {
                if (updateNickNameDialog != null) {
                    if (it.msg == viewModel.nickNameUpdateSuccess) {
                        viewBinding.settingUserEditNickTxt.text = it.nickName
                        updateNickNameDialog!!.dismiss()
                    } else {
                        it.msg?.let { it1 -> updateNickNameDialog!!.setHintTxt(it1) }
                    }
                }
            }
        }
    }

    private fun updateUserHeadIV(file: File) {
        loadCircle(file, viewBinding.settingUserEditUserIv, this, R.drawable.default_avatar)
    }

    private fun updateUserDesc(desc: String) {
        viewBinding.edtSignature.setText(desc)
    }

    override fun initListeners() {
        viewBinding.layoutAvatar.setOnClickListener { selectPic() }
        viewBinding.layoutNickname.setOnClickListener { showNickEditDialog() }
        viewBinding.layoutVerify.setOnClickListener { routeToVerify() }
        viewBinding.titleBar.setRightOnClickListener(View.OnClickListener {
            if (viewBinding.settingUserEditNickTxt.text.toString().trim().isEmpty()) {
                showToastShort("请输入昵称")
                return@OnClickListener
            }
            showLoadingDialog()
            var nickName: String? = viewBinding.settingUserEditNickTxt.text.toString().trim()
            var desc: String? = viewBinding.edtSignature.text.toString()
            if (nickName == oldNickName) {
                nickName = null
            }
            if (desc == oldDesc) {
                desc = null
            }
            viewModel.updateUserInfo(nickName, desc)
        })
    }

    private fun routeToVerify() {
        DcRouter(SettingConstant.Uri.VERIFY).start()
    }

    private fun selectPic() {
        ImagePicker.withMulti(CustomImgPickerPresenter()) //指定presenter
            //设置选择的最大数
            .setMaxCount(1) //设置列数
            .setColumnCount(4) //设置要加载的文件类型，可指定单一类型
            .mimeTypes(MimeType.ofImage()) //设置需要过滤掉加载的文件类型
            .filterMimeTypes(MimeType.GIF)
            .showCamera(true) //显示拍照
            .cropSaveInDCIM(false) //设置剪裁比例
            .setCropRatio(1, 1) //设置剪裁框间距，单位px
            .cropRectMinMargin(120) //设置剪裁模式，留白或充满  CropConfig.STYLE_GAP 或 CropConfig.STYLE_FILL
            .cropStyle(CropConfig.STYLE_FILL) //设置留白模式下生成的图片背景色，支持透明背景
            .cropGapBackgroundColor(Color.TRANSPARENT)
            .crop(this) { items -> //图片剪裁回调，主线程
                handleImage(items)
            }
    }

    private fun handleImage(items: ArrayList<ImageItem>) {
        if (items.isEmpty()) {
            return
        }
        val filePath = items[0].cropUrl
        showLoadingDialog()
        val cropUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileProvider",
            File(filePath)
        ).toString()
        viewModel.compressPhoto(filePath, cropUri)
    }

    private fun showNickEditDialog() {
        val hintMsg: String
        val currentTime = System.currentTimeMillis()
        val nextChangeTime = (LoginUtils.getCurrentUser()?.nicknameLiftBanAt ?: 0L)

        hintMsg = if (nextChangeTime != 0L && nextChangeTime > currentTime) {
            val day = (nextChangeTime - currentTime) / 1000 / 60 / 60 / 24.0
            ceil(day).toInt().toString().plus("天后可修改昵称")
        } else {
            ""
        }

        updateNickNameDialog = CommonEditInputDialog.Builder(context)
            .title(getString(R.string.setting_user_nick_modify))
            .hint(getString(R.string.setting_user_nick_modify))
            .hintMsg(hintMsg)
            .content(viewBinding.settingUserEditNickTxt.text.toString())
            .nextChangeTime(nextChangeTime)
            .maxLength(8)
            .desc("非会员每90天修改一次昵称，盯链会员每60天修改一次昵称")
            .confirmTxt("确定")
            .confirmDismiss(false)
            .onConfirmClick(object : CommonEditInputDialog.OnConfirmClickListener {
                override fun onConfirmClick(edit: EditText, dialog: CommonEditInputDialog) {
                    val content = edit.text.toString()
                    if (TextUtils.isEmpty(content)) {
                        showToastShort(R.string.setting_user_nick_empty_tip)
                        return
                    }
                    val trimContent = content.trim { it <= ' ' }
                    if (TextUtils.isEmpty(trimContent)) {
                        showToastShort(R.string.setting_user_nick_empty_tip)
                        return
                    }
                    viewModel.checkUserMsgUpdate(trimContent)
                }
            })
            .builder()
        updateNickNameDialog!!.show()
    }

    override fun moduleTag(): String {
        return ModuleConstant.SETTING
    }

    private fun updateUserHeadIV(avatarUrl: String) {
        AccountHelper.getInstance().user ?: return
        loadCircle(avatarUrl, viewBinding.settingUserEditUserIv, this, R.drawable.default_avatar)
    }

    private fun updateUserNick(nickName: String) {
        AccountHelper.getInstance().user ?: return
        viewBinding.settingUserEditNickTxt.text = nickName
    }
}