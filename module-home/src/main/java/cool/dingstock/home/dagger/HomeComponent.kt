package cool.dingstock.home.dagger

import cool.dingstock.appbase.net.retrofit.dagger.ApiModule
import cool.dingstock.home.base.HomeViewModel
import cool.dingstock.home.ui.dingchao.HomeDingChaoFragmentVm
import cool.dingstock.home.ui.fashion.brand.FashionBrandViewModel
import cool.dingstock.home.ui.fashion.index.FashionViewModel
import cool.dingstock.home.ui.home.HomeIndexFragmentVm
import cool.dingstock.home.ui.recommend.RecommendFollowViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  12:26
 */
@Singleton
@Component(modules = [ApiModule::class])
interface HomeComponent {
    fun inject(viewModel: RecommendFollowViewModel)
    fun inject(homeViewModel: HomeViewModel)
    fun inject(vm: HomeIndexFragmentVm)
    fun inject(vm: HomeDingChaoFragmentVm)
    fun inject(vm: FashionViewModel)
    fun inject(vm: FashionBrandViewModel)
}