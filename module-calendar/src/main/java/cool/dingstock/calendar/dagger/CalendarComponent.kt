package cool.dingstock.calendar.dagger

import cool.dingstock.appbase.net.retrofit.dagger.ApiModule
import cool.dingstock.calendar.sms.SmsRegistrationViewModel
import cool.dingstock.calendar.sneaker.CalendarIndexActivity
import cool.dingstock.calendar.sneaker.heavy.HeavySneakersVm
import cool.dingstock.calendar.sneaker.index.CalenderViewModel
import cool.dingstock.calendar.sneaker.index.HomeSneakersFragmentPresenter
import cool.dingstock.calendar.sneaker.product.HomeProductDetailViewModel
import cool.dingstock.calendar.ui.pager.CalendarFragmentHeavyGoodVM
import dagger.Component
import javax.inject.Singleton

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  12:26
 */
@Singleton
@Component(modules = [ApiModule::class])
interface CalendarComponent {
    fun inject(vm: CalenderViewModel)
    fun inject(vm: HomeSneakersFragmentPresenter)
    fun inject(vm: HomeProductDetailViewModel)
    fun inject(vm: CalendarIndexActivity)
    fun inject(vm: HeavySneakersVm)
    fun inject(vm: SmsRegistrationViewModel)
    fun inject(vm: CalendarFragmentHeavyGoodVM)
}