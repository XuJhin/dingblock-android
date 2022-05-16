package cool.dingstock.appbase.net.api.account

import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.entity.bean.PageEntity
import cool.dingstock.appbase.entity.bean.account.DcLoginUser
import cool.dingstock.appbase.entity.bean.account.LayeredConfirmEntity
import cool.dingstock.appbase.entity.bean.account.UserInfoEntity
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.home.NoticeItemEntity
import cool.dingstock.appbase.entity.bean.mine.AccountSettingEntity
import cool.dingstock.appbase.entity.bean.shop.*
import cool.dingstock.appbase.entity.bean.vip.KOLEntity
import cool.dingstock.appbase.entity.bean.vip.VipPriceEntity
import cool.dingstock.appbase.net.retrofit.api.BaseApi
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  10:59
 */
class AccountApi @Inject constructor(retrofit: Retrofit) : BaseApi<AccountApiService>(retrofit) {

    fun createReceiveOrder(
        id: String,
        count: Int,
        addressId: String
    ): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder().add("id", id)
            .add("count", count)
            .add("addressId", addressId)
            .toBody()
        return service.createReceiveOrder(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun getAddressList(): Flowable<BaseResult<List<UserAddressEntity>>> {
        return service.getAddressList().compose(RxSchedulers.netio_main()).handError()
    }

    fun deleteAddress(id: String): Flowable<BaseResult<Any>> {
        return service.deleteAddress(id).compose(RxSchedulers.netio_main()).handError()
    }

    fun addNewAddress(entity: UserAddressEntity): Flowable<BaseResult<AddressId>> {
        val body = ParameterBuilder()
            .add("name", entity.name)
            .add("mobile", entity.mobile)
            .add("mobileZone", entity.mobileZone)
            .add("province", entity.province)
            .add("city", entity.city)
            .add("district", entity.district)
            .add("address", entity.address)
            .add("isDefault", entity.isDefault)
            .toBody()
        return service.addNewAddress(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun editAddress(entity: UserAddressEntity): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("id", entity.id)
            .add("name", entity.name)
            .add("mobile", entity.mobile)
            .add("mobileZone", entity.mobileZone)
            .add("province", entity.province)
            .add("city", entity.city)
            .add("district", entity.district)
            .add("address", entity.address)
            .add("isDefault", entity.isDefault)
            .toBody()
        return service.editAddress(body).compose(RxSchedulers.netio_main()).handError()
    }


    fun changeToken(oldToken: String): Flowable<BaseResult<DcLoginUser>> {
        val body = ParameterBuilder()
            .add("token", oldToken)
            .toBody()
        return service.changeToken(body).compose(RxSchedulers.netio_main()).handError()
    }


    fun loginMobile(
        areaCode: String,
        phoneNum: String,
        code: String,
        deviceId: String,
        authType: String,
        weChatId: String,
        weChatToken: String
    ): Flowable<BaseResult<DcLoginUser>> {
        var weChat: ParameterBuilder? = null
        if (authType == AccountConstant.AuthType.WECHAT) {
            weChat = ParameterBuilder()
                .add("id", weChatId)
                .add("access_token", weChatToken)
        }
        val sms = ParameterBuilder()
            .add("id", "$areaCode$phoneNum")
            .add("zone", "$areaCode")
            .add("mobile", phoneNum)
            .add("code", code)
        val body = ParameterBuilder()
            .add("deviceId", deviceId)
            .add(
                "authData", ParameterBuilder()
                    .add("sms", sms)
                    .add("wechat", weChat)
            )
            .toBody()
        return service.loginMobile(body).compose(RxSchedulers.netio_main()).handError()
    }


    fun loginWehChat(
        weChatId: String,
        weChatToken: String,
        deviceId: String
    ): Flowable<BaseResult<DcLoginUser>> {
        val body = ParameterBuilder()
            .add("id", weChatId)
            .add("access_token", weChatToken)
            .add("deviceId", deviceId)
            .toBody()
        return service.loginWeChat(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun loginCid(cid: String, deviceId: String): Flowable<BaseResult<DcLoginUser>> {
        val body = ParameterBuilder()
            .add("cid", cid)
            .add("deviceId", deviceId)
            .toBody()
        return service.loginCid(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun loginLout(): Flowable<BaseResult<Any>> {
        return service.loginOut().compose(RxSchedulers.netio_main()).handError()
    }

    fun registerOut(): Flowable<BaseResult<Any>> {
        return service.registerOut().compose(RxSchedulers.netio_main()).handError()
    }

    fun setUserParameter2Net(key: String, value: Any?): Flowable<BaseResult<DcLoginUser>> {
        val body = ParameterBuilder()
            .add(key, value)
            .toBody()
        return service.update(body)
            .compose(RxSchedulers.netio_main())
            .flatMap {
                if (!it.err) {
                    it.res?.save()
                }
                return@flatMap Flowable.just(it)
            }.handError()
    }

    fun setUserParameter2Net(map: Map<String, Any>): Flowable<BaseResult<DcLoginUser>> {
        val body = ParameterBuilder(map)
            .toBody()
        return service.update(body)
            .compose(RxSchedulers.netio_main())
            .flatMap {
                if (!it.err) {
                    it.res?.save()
                }
                return@flatMap Flowable.just(it)
            }.handError()
    }

    fun setUserParameter2Net(parameterBuilder: ParameterBuilder): Flowable<BaseResult<DcLoginUser>> {
        val body = parameterBuilder
            .toBody()
        return service.update(body)
            .compose(RxSchedulers.netio_main())
            .flatMap {
                if (!it.err) {
                    it.res?.save()
                }
                return@flatMap Flowable.just(it)
            }.handError()
    }

    fun getUserByNet(): Flowable<BaseResult<DcLoginUser>> {
        return service.getUser()
            .compose(RxSchedulers.netio_main())
            .flatMap {
                if (!it.err) {
                    it.res?.save()

                }
                return@flatMap Flowable.just(it)
            }.handError()
    }

    fun getUserInfo(): Flowable<BaseResult<UserInfoEntity>> {
        return service.getUserInfo()
            .compose(RxSchedulers.netio_main())
            .handError()
    }

    fun requestNotice(
        nextKey: Long?,
        type: String?
    ): Flowable<BaseResult<PageEntity<NoticeItemEntity>>> {
        return service.requestNotice(nextKey, type).compose(RxSchedulers.netio_main()).handError()
    }

    fun getCaptchaRequirement(): Flowable<BaseResult<Boolean>> {
        return service.getCaptchaRequirement().compose(RxSchedulers.netio_main()).handError()
    }

    fun getSmsCode(
        zone: String?,
        phone: String?,
        captchaAppId: String?,
        captchaTicket: String?,
        captchaRandStr: String?
    ): Flowable<BaseResult<String>> {
        val body = ParameterBuilder()
            .add("zone", zone)
            .add("mobilePhoneNumber", phone)
            .add("random", captchaRandStr)
            .add("ticket", captchaTicket)
            .add("appid", captchaAppId)
            .toBody()
        return service.getSmsCode(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun kolExists(): Flowable<BaseResult<KOLEntity>> {
        return service.kolExists().compose(RxSchedulers.netio_main()).handError()
    }

    fun vipPrices(): Flowable<BaseResult<ArrayList<VipPriceEntity>>> {
        return service.vipPrices().compose(RxSchedulers.netio_main()).handError()
    }

    fun checkCode(code: String): Flowable<BaseResult<ArrayList<VipPriceEntity>>> {
        return service.checkCode(code).compose(RxSchedulers.netio_main()).handError()
    }

    fun layerConfirm(type: String): Flowable<BaseResult<LayeredConfirmEntity>> {
        return service.layerConfirm(type).compose(RxSchedulers.netio_main()).handError()
    }

    fun activate(code: String): Flowable<BaseResult<String>> {
        val body = ParameterBuilder().add("code", code).toBody()
        return service.activate(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun getPendantHome() = flow {
        emit(service.getPendantHome())
    }.flowOn(Dispatchers.IO)

    fun wearPendant(id: String, equip: Boolean) = flow {
        val body = ParameterBuilder()
            .add("id", id)
            .add("isEquipped", equip)
            .toBody()
        emit(service.wearPendant(body))
    }.flowOn(Dispatchers.IO)

    fun fetchAccount(): Flowable<BaseResult<AccountSettingEntity>> {
        return service.fetchAccount().compose(RxSchedulers.netio_main()).handError()
    }

    fun checkCurrentPhone(code: String, mobile: String, zone: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("code", code)
            .add("mobile", mobile)
            .add("zone", zone)
            .toBody()
        return service.checkCurrentPhone(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun updateBindPhone(code: String, mobile: String, zone: String): Flowable<BaseResult<DcLoginUser>> {
        val body = ParameterBuilder()
            .add("code", code)
            .add("mobile", mobile)
            .add("zone", zone)
            .toBody()
        return service.updateBindPhone(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun unBindWeChat(): Flowable<BaseResult<Any>> {
        return service.unBindWeChat().compose(RxSchedulers.netio_main()).handError()
    }

    fun bindWeChat(token: String, id: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("access_token", token)
            .add("id", id)
            .toBody()
        return service.bindWeChat(body).compose(RxSchedulers.netio_main()).handError()
    }

    fun checkUserMsgUpdate(nickName: String): Flowable<BaseResult<Any>> {
        val body = ParameterBuilder()
            .add("nickName", nickName)
            .toBody()
        return service.checkUserMsgUpdate(body).compose(RxSchedulers.netio_main()).handError()
    }
}