package cool.dingstock.uicommon.dagger

import cool.dingstock.appbase.net.retrofit.dagger.ApiModule
import cool.dingstock.uicommon.product.dialog.SmsRegistrationDialog
import cool.dingstock.uicommon.setting.cancellation.CancellationVM
import dagger.Component
import javax.inject.Singleton

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  12:26
 */
@Singleton
@Component(modules = [ApiModule::class])
interface UICommonComponent {
    fun inject(dialog: SmsRegistrationDialog)
    fun inject(vm: CancellationVM)
}