package cool.dingstock.appbase.net.api.common

import com.fm.openinstall.model.AppData
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.common.CommonConfigEntity
import cool.dingstock.appbase.entity.bean.config.*
import cool.dingstock.appbase.entity.bean.home.bp.GoodDetailEntity
import cool.dingstock.appbase.entity.bean.party.PartyDialogEntity
import cool.dingstock.appbase.entity.bean.pay.WeChatOrderData
import cool.dingstock.appbase.entity.bean.upload.UploadFileEntity
import cool.dingstock.appbase.exception.DcException
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.appbase.net.retrofit.manager.RxRetrofitServiceManager
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import cool.dingstock.appbase.push.DCPushManager
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.util.StringUtils
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import retrofit2.Retrofit
import java.io.File
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  10:59
 */
class CommonApi @Inject constructor(retrofit: Retrofit) : BaseApi<CommonApiService>(retrofit) {

    fun getAvatarOssUrl(fileName: String): Flowable<BaseResult<String>> {
        return service.getOssFileUrl("avatar/$fileName", "image/jpeg").compose(RxSchedulers.io())
            .handError()
    }


    fun getIdCardOssUrl(fileName: String): Flowable<BaseResult<String>> {
        return service.getOssFileUrl("oversea/IDCardImage/$fileName", "image/jpeg")
            .compose(RxSchedulers.io()).handError()
    }

    /**
     * @param uploadUrl 上传的路径
     * @param file 文件名称
     * @param success 成功回调
     * @param failed 失败回调
     */
    fun uploadImage(
        uploadUrl: String,
        file: File,
    ): Flowable<Response> {
        return Flowable.create<Response>({
            val client = RxRetrofitServiceManager.getInstance().okHttpClient
            val mediaType = "image/jpeg".toMediaTypeOrNull()
            val fileBody = file.asRequestBody(mediaType)
            val request = Request.Builder()
                .url(uploadUrl)
                .method("PUT", fileBody)
                .addHeader("Content-Type", "image/jpeg")
                .build()
            val execute = client.newCall(request).execute()
            it.onNext(execute)
            it.onComplete()
        }, BackpressureStrategy.BUFFER).compose(RxSchedulers.io()).handError()

    }

    /**
     * @param uploadUrl 上传的路径
     * @param file 文件名称
     */
    fun uploadImgAndDelete(
        uploadUrl: String,
        file: File,
        type: String
    ): Flowable<Response> {
        return Flowable.create<Response>({
            val client = RxRetrofitServiceManager.getInstance().okHttpClient
            val mediaType = type.toMediaTypeOrNull()
            val fileBody = file.asRequestBody(mediaType)
            val request = Request.Builder()
                .url(uploadUrl)
                .method("PUT", fileBody)
                .addHeader("Content-Type", "image/jpeg")
                .build()
            val execute = client.newCall(request).execute()
            if (file.exists()) {
                file.delete()
            }
            it.onNext(execute)
            it.onComplete()

        }, BackpressureStrategy.BUFFER).handError()

    }

    /**
     * @param uploadUrl 上传的路径
     * @param file 文件名称
     */
    fun uploadImg(
        uploadUrl: String,
        file: File,
        type: String
    ): Flowable<Response> {
        return Flowable.create<Response>({
            val client = RxRetrofitServiceManager.getInstance().okHttpClient
            val mediaType = type.toMediaTypeOrNull()
            val fileBody = file.asRequestBody(mediaType)
            val request = Request.Builder()
                .url(uploadUrl)
                .method("PUT", fileBody)
                .addHeader("Content-Type", "image/jpeg")
                .build()
            val execute = client.newCall(request).execute()
            it.onNext(execute)
            it.onComplete()
        }, BackpressureStrategy.BUFFER).handError()

    }

    fun uploadImgListAndDelete(path: String, files: ArrayList<File>): Flowable<ArrayList<String>> {
        val list = ArrayList<UploadFileEntity>()
        for (file in files) {
            list.add(UploadFileEntity(file, ""))
        }
        return Flowable.fromIterable(list)
            .compose(RxSchedulers.io())
            .flatMap { entity ->
                val newName =
                    path + "/" + LoginUtils.getCurrentUser()?.id.hashCode() + entity.file.name
                var type = "image/jpeg"
                type = if (entity.file.name.endsWith(".gif")) {
                    "image/gif"
                } else {
                    "image/jpeg"
                }
                entity.type = type
                return@flatMap service
                    .getOssFileUrl(newName, type)
                    .flatMap inner@{
                        if (!StringUtils.isEmpty(it.res)) {
                            entity.url = it.res ?: ""
                            return@inner Flowable.just(entity)
                        } else {
                            if (entity.file.exists()) {
                                entity.file.delete()
                            }
                            return@inner Flowable.error<UploadFileEntity>(
                                DcException(
                                    -1,
                                    "文件路径获取失败"
                                )
                            )
                        }
                    }
            }.flatMap { entity ->
                return@flatMap uploadImg(entity.url, entity.file, entity.type)
                    .flatMap inner@{
                        if (it.code == 200) {
                            entity.url = entity.url.substring(0, entity.url.indexOf("?"))
                            return@inner Flowable.just(entity)
                        } else {
                            return@inner Flowable.error<UploadFileEntity>(DcException(-1, "文件上传失败"))
                        }
                    }
            }.collectInto(ArrayList<UploadFileEntity>(), { list1, t2 ->
                list1.add(t2)
            })
            .toFlowable()
            .flatMap {
                val urls = arrayListOf<String>()
                for (file in files) {
                    for (entity in it) {
                        if (entity.file.absolutePath == file.absolutePath) {
                            urls.add(entity.url)
                        }
                    }
                }
                return@flatMap Flowable.just(urls)
            }.handError()
    }

    fun uploadImgList(path: String, files: ArrayList<File>): Flowable<ArrayList<String>> {
        val list = ArrayList<UploadFileEntity>()
        for (file in files) {
            list.add(UploadFileEntity(file, ""))
        }
        return Flowable.fromIterable(list)
            .compose(RxSchedulers.io())
            .flatMap { entity ->
                val newName =
                    path + "/" + LoginUtils.getCurrentUser()?.id.hashCode() + entity.file.name
                var type = "image/jpeg"
                type = if (entity.file.name.endsWith(".gif")) {
                    "image/gif"
                } else {
                    "image/jpeg"
                }
                entity.type = type
                return@flatMap service
                    .getOssFileUrl(newName, type)
                    .flatMap inner@{
                        if (!StringUtils.isEmpty(it.res)) {
                            entity.url = it.res ?: ""
                            return@inner Flowable.just(entity)
                        } else {
                            return@inner Flowable.error<UploadFileEntity>(
                                DcException(
                                    -1,
                                    "文件路径获取失败"
                                )
                            )
                        }
                    }
            }.flatMap { entity ->
                return@flatMap uploadImg(entity.url, entity.file, entity.type)
                    .flatMap inner@{
                        if (it.code == 200) {
                            entity.url = entity.url.substring(0, entity.url.indexOf("?"))
                            return@inner Flowable.just(entity)
                        } else {
                            return@inner Flowable.error<UploadFileEntity>(DcException(-1, "文件上传失败"))
                        }
                    }
            }.collectInto(ArrayList<UploadFileEntity>(), { list1, t2 ->
                list1.add(t2)
            })
            .toFlowable()
            .flatMap {
                val urls = arrayListOf<String>()
                for (file in files) {
                    for (entity in it) {
                        if (entity.file.absolutePath == file.absolutePath) {
                            urls.add(entity.url)
                        }
                    }
                }
                return@flatMap Flowable.just(urls)
            }.handError()
    }

    fun resolveCommon(
        urlOrGoodsId: String,
        errMsg: String? = ""
    ): Flowable<BaseResult<GoodDetailEntity>> {
        val toBody = ParameterBuilder().add("keyword", urlOrGoodsId).add("errMsg", errMsg ?: "")
            .toBody()
        return service.resolveCommon(toBody).compose(RxSchedulers.netio_main()).handError()
    }

    fun resolveTkl(urlOrGoodsId: String): Flowable<BaseResult<String?>> {
        val toBody = ParameterBuilder().add("keyword", urlOrGoodsId)
            .toBody()
        return service.resolveTkl(toBody).compose(RxSchedulers.netio_main()).handError()
    }

    fun reportResolve(
        goodsId: String,
        goodDetailEntity: GoodDetailEntity
    ): Flowable<BaseResult<Any>> {
        val toBody = ParameterBuilder().add("data", goodDetailEntity)
            .add("id", goodsId)
            .toBody()
        return service.reportResolve(toBody).compose(RxSchedulers.io()).handError()
    }


    fun obtainParityWord(pwdUrl: String?): Flowable<BaseResult<String?>> {
        val toBody = ParameterBuilder().add("pwdUrl", pwdUrl)
            .toBody()
        return service.obtainParityWord(toBody).compose(RxSchedulers.netio_main()).handError()
    }

    fun resolveParityWord(pwd: String): Flowable<BaseResult<PartyDialogEntity?>> {
        val toBody = ParameterBuilder().add("pwd", pwd)
            .toBody()
        return service.resolveParityWord(toBody).compose(RxSchedulers.netio_main()).handError()
    }

    fun completeParityWord(pwd: String): Flowable<BaseResult<Any?>> {
        val toBody = ParameterBuilder().add("keyword", pwd)
            .toBody()
        return service.completeParityWord(toBody).compose(RxSchedulers.netio_main()).handError()
    }

    fun commonInfo(type: String): Flowable<BaseResult<CommonConfigEntity>> {
        return service.commonInfo(type).compose(RxSchedulers.netio_main()).handError()
    }

    suspend fun bjLineTime(): BaseResult<Long> {
        return service.bjLineTime("BeijingTime")
    }


    fun appConfig(): Flowable<BaseResult<ConfigData?>> {
        return service.appConfig().compose(RxSchedulers.io()).handError()
    }

    fun monitorConfig(): Flowable<BaseResult<MonitorConfig>> {
        return service.monitorConfig().compose(RxSchedulers.io()).handError()
    }

    fun payAliInfo(goodsId: String, provider: String): Flowable<BaseResult<String>> {
        return service.aliPayInfo(goodsId, provider).compose(RxSchedulers.netio_main()).handError()
    }

    fun payWeChatInfo(goodsId: String, provider: String): Flowable<BaseResult<WeChatOrderData>> {
        return service.wechatPayInfo(goodsId, provider).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun openinstall(appData: AppData): Flowable<BaseResult<String>> {
        val toBody = ParameterBuilder()
            .add("channel", appData.getChannel())
            .add("data", appData.getData())
            .add("deviceId", DCPushManager.getInstance().txDeviceToken)
            .toBody()
        return service.openinstall(toBody).compose(RxSchedulers.netio_main()).handError()
    }


    fun emojiConfig(): Flowable<BaseResult<List<EmojiConfig>>> {
        return service.emojiConfig().compose(RxSchedulers.io()).handError()
    }


    fun shareConfig(): Flowable<BaseResult<List<ShareConfig>>> {
        return service.shareConfig().compose(RxSchedulers.io()).handError()
    }

    fun setRemind(
        raffleId: String,
        remindType: String,
        isDefault: Boolean,
        remindAt: ArrayList<Long>
    ): Flowable<BaseResult<Any>> {
        val toBody = ParameterBuilder()
            .add("raffleId", raffleId)
            .add("remindType", remindType)
            .add("isDefault", isDefault)
            .add("remindAt", remindAt)
            .toBody()
        return service.setRemind(toBody).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun cancelRemind(raffleId: String): Flowable<BaseResult<Any>> {
        val toBody = ParameterBuilder()
            .add("raffleId", raffleId)
            .toBody()
        return service.cancelRemind(toBody).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun updateRemindSetting(
        remindType: String,
        remindAt: ArrayList<Long>
    ): Flowable<BaseResult<Any>> {
        val toBody = ParameterBuilder()
            .add("remindType", remindType)
            .add("remindAt", remindAt)
            .toBody()
        return service.updateRemindSetting(toBody).compose(RxSchedulers.netio_main())
            .handError()
    }

    fun fetchRemindSetting(): Flowable<BaseResult<RemindConfigEntity>> {
        return service.fetchRemindSetting().compose(RxSchedulers.netio_main()).handError()
    }
}
