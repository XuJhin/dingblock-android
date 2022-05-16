package cool.dingstock.setting.dagger

import cool.dingstock.appbase.net.retrofit.dagger.ApiModule
import cool.dingstock.setting.ui.edit.SettingUserEditActivity
import cool.dingstock.setting.ui.edit.UserEditViewModel
import cool.dingstock.setting.ui.setting.account.AccountSettingViewModel
import cool.dingstock.setting.ui.setting.index.SettingIndexViewModel
import cool.dingstock.setting.ui.setting.updatePhone.CheckPhoneViewModel
import cool.dingstock.setting.ui.setting.updatePhone.SetNewPhoneViewModel
import cool.dingstock.setting.ui.shield.ShieldViewModel
import cool.dingstock.setting.ui.verify.VerifyViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  12:26
 */
@Singleton
@Component(modules = [ApiModule::class])
interface SettingComponent {
    fun inject(viewModel: UserEditViewModel)

    fun inject(activity: SettingUserEditActivity)

    fun inject(viewModel: ShieldViewModel)

    fun inject(viewModel: SettingIndexViewModel)

    fun inject(viewModel: VerifyViewModel)

    fun inject(vm: AccountSettingViewModel)

    fun inject(vm: CheckPhoneViewModel)

    fun inject(vm: SetNewPhoneViewModel)
}