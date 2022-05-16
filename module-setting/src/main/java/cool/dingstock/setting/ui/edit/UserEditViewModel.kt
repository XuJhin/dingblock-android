package cool.dingstock.setting.ui.edit

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.google.gson.internal.LinkedTreeMap
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.account.NickNameEntity
import cool.dingstock.appbase.entity.bean.account.UserInfoEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.event.update.EventUpdateAvatar
import cool.dingstock.appbase.entity.bean.monitor.IsShowDialogEntity
import cool.dingstock.appbase.exception.DcException
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.FileUtilsCompat
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.setting.dagger.SettingApiHelper
import io.reactivex.rxjava3.core.Flowable
import org.greenrobot.eventbus.EventBus
import java.io.File
import javax.inject.Inject

class UserEditViewModel : BaseViewModel() {
    @Inject
    lateinit var accountApi: AccountApi

    @Inject
    lateinit var commonApi: CommonApi

    init {
        SettingApiHelper.apiSettingComponent.inject(this)
    }

    val errLiveData = MutableLiveData<String>()
    val userInfoLiveData = MutableLiveData<UserInfoEntity>()
    val userAvatarLiveData = MutableLiveData<File>()
    val uploadImageFailed = MutableLiveData<String>()
    val updateUserInfoLiveData = MutableLiveData<String>()
    val nickNameLiveData = MutableLiveData<NickNameEntity>()

    val nickNameUpdateSuccess = "success"

    /**
     * 获取当前用户信息
     */
    fun requestUserInfo() {
        accountApi.getUserInfo()
            .subscribe({ res ->
                if (!res.err) {
                    res.res?.let { userInfoLiveData.postValue(it) }
                } else {
                    errLiveData.postValue(res.msg)
                }
            }, { err ->
                errLiveData.postValue(err.message)
            })
    }

    fun updateUserInfo(nickname: String?, desc: String?) {
        val builder = BaseApi.ParameterBuilder()
            .add("nickName", nickname)
            .add("desc", desc)
        val subscribe = accountApi.setUserParameter2Net(builder)
            .subscribe({
                updateUserInfoLiveData.postValue("")
                LoginUtils.getCurrentUser()?.nickName = nickname
                LoginUtils.getCurrentUser()?.save()
            }, {
                uploadImageFailed.postValue(it.message)
            })
    }

    /**
     * 压缩图片
     */
    fun compressPhoto(filePath: String, uriPath: String) {
        if (TextUtils.isEmpty(filePath)) {
            return
        }
        compressImage(filePath, uriPath)
    }

    private fun compressImage(filePath: String, uriPath: String) {
        FileUtilsCompat.lubanComposeFilePath(BaseLibrary.getInstance().context, filePath, uriPath)
            .subscribe({
                uploadUserImage(it)
            }, {

            })
    }

    /**
     * 上传图片
     */
    fun uploadUserImage(file: File) {
        if (!LoginUtils.isLogin()) {
            return
        }
        val newFileName =
            "" + LoginUtils.getCurrentUser()?.id.hashCode() + "_" + System.currentTimeMillis() + file.name.substring(
                file.name.lastIndexOf(".")
            )
        val subscribe = commonApi.getAvatarOssUrl(newFileName)
            .flatMap out@{ baseResult ->
                if (!baseResult.err) {
                    baseResult.res?.let { str ->
                        return@out commonApi.uploadImage(str, file)
                            .flatMap inner@{
                                if (it.code == 200) {
                                    var path = baseResult.res ?: ""
                                    path = path.substring(0, path.indexOf("?"))
                                    return@inner Flowable.just(path)
                                } else {
                                    return@inner Flowable.error<String>(DcException(-1, "未获取文件路径"))
                                }
                            }
                    }
                }
                return@out Flowable.error<String>(DcException(-1, "未获取文件路径"))
            }
            .flatMap {
                if (!StringUtils.isEmpty(it)) {
                    return@flatMap accountApi.setUserParameter2Net("avatarUrl", it)
                }
                return@flatMap Flowable.error<BaseResult<DcLoginUser>>(DcException(-1, "图片上传未成功"))
            }
            .compose(RxSchedulers.netio_main())
            .subscribe({
                if (!it.err) {
                    userAvatarLiveData.postValue(file)
                    it.res?.let { user ->
                        EventBus.getDefault().post(EventUpdateAvatar(user.id, user.avatarUrl))
                    }
                } else {
                    uploadImageFailed.postValue(it.msg)
                }
            }, {
                uploadImageFailed.postValue(it.message)
            })
    }

    fun checkUserMsgUpdate(nickname: String) {
        accountApi.checkUserMsgUpdate(nickname)
            .subscribe({
                if (!it.err) {
                    try {
                        nickNameLiveData.postValue(NickNameEntity(it.res.toString(), nickname))
                    } catch (e: Exception) {
                        nickNameLiveData.postValue(NickNameEntity(nickNameUpdateSuccess, nickname))
                    }
                } else {
                    nickNameLiveData.postValue(NickNameEntity(it.msg, nickname))
                }
            }, {
                nickNameLiveData.postValue(NickNameEntity(it.message, nickname))
            })
    }
}