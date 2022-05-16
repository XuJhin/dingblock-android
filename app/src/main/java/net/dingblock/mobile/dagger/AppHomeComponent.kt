package net.dingblock.mobile.dagger

import cool.dingstock.appbase.net.retrofit.dagger.ApiModule
//import net.dingblock.mobile.presenter.HomeIndexPresenter
import dagger.Component
import javax.inject.Singleton

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  12:26
 */
@Singleton
@Component(modules = [ApiModule::class])
interface AppHomeComponent {

//    fun inject(presenter: HomeIndexPresenter)

}