package cool.dingstock.tide.dagger

import cool.dingstock.appbase.net.retrofit.dagger.ApiModule
import cool.dingstock.tide.index.TideHomeIndexVM
import dagger.Component

/**
 * @author wangjiang
 *  CreateAt Time 2021/6/1  10:19
 */
@javax.inject.Singleton
@Component(modules = [ApiModule::class])
interface TideComponent {

    fun inject(vm: TideHomeIndexVM)

}