package cool.dingstock.mine.ui.avater

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.event.update.EventUpdateAvatar
import cool.dingstock.appbase.exception.DcException
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import cool.dingstock.lib_base.util.FileUtilsCompat
import cool.dingstock.lib_base.util.StringUtils
import cool.dingstock.mine.dagger.MineApiHelper
import io.reactivex.rxjava3.core.Flowable
import org.greenrobot.eventbus.EventBus
import java.io.File
import javax.inject.Inject

class AvatarPreviewViewModel: BaseViewModel() {
    @Inject
    lateinit var commonApi: CommonApi

    @Inject
    lateinit var accountApi: AccountApi

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }

    val userAvatarLiveData = MutableLiveData<String>()
    val uploadImageFailed = MutableLiveData<String>()

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
    private fun uploadUserImage(file: File) {
        if (!LoginUtils.isLogin()) {
            return
        }
        val newFileName =
            "" + LoginUtils.getCurrentUser()?.id.hashCode() + "_" + System.currentTimeMillis() + file.name.substring(
                file.name.lastIndexOf(".")
            )
        commonApi.getAvatarOssUrl(newFileName)
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
                    it.res?.let { user ->
                        user.avatarUrl?.let { avatar -> userAvatarLiveData.postValue(avatar) }
                        EventBus.getDefault().post(EventUpdateAvatar(user.id, user.avatarUrl))
                    }
                } else {
                    uploadImageFailed.postValue(it.msg)
                }
            }, {
                uploadImageFailed.postValue(it.message)
            })
    }
}