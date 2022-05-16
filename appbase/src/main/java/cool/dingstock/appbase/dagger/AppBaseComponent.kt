package cool.dingstock.appbase.dagger

import cool.dingstock.appbase.AppBaseLibrary
import cool.dingstock.appbase.helper.*
import cool.dingstock.appbase.helper.bp.BpLocalHelper
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.net.api.box.BoxHelper
import cool.dingstock.appbase.net.api.calendar.CalendarHelper
import cool.dingstock.appbase.net.api.circle.CircleHelper
import cool.dingstock.appbase.net.api.home.HomeHelper
import cool.dingstock.appbase.net.api.mine.MineHelper
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
import cool.dingstock.appbase.webview.module.AccountModule
import cool.dingstock.appbase.webview.module.PayModule
import cool.dingstock.appbase.widget.dialog.vipget.VipLayeredRemindDialogActivity
import cool.dingstock.appbase.net.retrofit.dagger.ApiModule
import cool.dingstock.appbase.router.interceptor.DcLeadParametersInterceptor
import cool.dingstock.mobile.PayHelper
import dagger.Component
import javax.inject.Singleton

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  12:26
 */
@Singleton
@Component(modules = [ApiModule::class])
interface AppBaseComponent {

    fun inject(module: PayModule)

    fun inject(module: AccountModule)

    fun inject(activity: VipLayeredRemindDialogActivity)

    fun inject(activity: HomeIndexViewModel)

    fun inject(net: PartyVerifyHelper.Net)

    fun inject(net: PopWindowManager.Net)

    fun inject(net: AccountHelper)

    fun inject(net: CircleHelper)

    fun inject(net: CalendarHelper.Net)

    fun inject(net: AppBaseLibrary)

    fun inject(net: MobileHelper)

    fun inject(net: MineHelper)

    fun inject(helper: PayHelper)

    fun inject(helper: SettingHelper)

    fun inject(helper: HomeHelper)

    fun inject(net: DcLeadParametersInterceptor.Net)

    fun inject(helper: BaseIMHelper)

    fun inject(net: BoxHelper.Net)

    fun inject(net: HomeSuggestHelper.Net)

    fun inject(net: BpLocalHelper)

    fun inject(helper: MonitorRemindHelper)
}