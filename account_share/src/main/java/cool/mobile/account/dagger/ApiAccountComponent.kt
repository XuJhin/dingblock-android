package cool.mobile.account.dagger

import cool.dingstock.appbase.net.retrofit.dagger.ApiModule
import cool.mobile.account.share.ShareHelper
import cool.mobile.account.ui.address.ConFirmAddressActivity
import cool.mobile.account.ui.address.MyAddAddressViewModel
import cool.mobile.account.ui.address.MyAddressActivity
import cool.mobile.account.ui.country.CountryPickerActivity
import cool.mobile.account.ui.login.fragment.index.LoginIndexVM
import cool.mobile.account.ui.login.fragment.mobile.AccountLoginViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  12:26
 */
@Singleton
@Component(modules = [ApiModule::class])
interface ApiAccountComponent {
    fun inject(activity: MyAddAddressViewModel)
    fun inject(activity: MyAddressActivity)
    fun inject(activity: CountryPickerActivity)
    fun inject(accountLoginViewModel: AccountLoginViewModel)
    fun inject(vm: LoginIndexVM)
    fun inject(helper: ShareHelper)
    fun inject(activity: ConFirmAddressActivity)
}