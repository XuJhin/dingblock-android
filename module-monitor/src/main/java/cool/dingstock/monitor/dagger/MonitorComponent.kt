package cool.dingstock.monitor.dagger

import cool.dingstock.appbase.net.retrofit.dagger.ApiModule
import cool.dingstock.monitor.adapter.item.MonitorProductItemBinder
import cool.dingstock.monitor.dialog.MonitorVipViewModel
import cool.dingstock.monitor.mine.monitor.MonitorMineVM
import cool.dingstock.monitor.setting.SettingViewModel
import cool.dingstock.monitor.ui.detail.MonitorDetailViewModel
import cool.dingstock.monitor.ui.log.MonitorLogViewModel
import cool.dingstock.monitor.ui.manager.MonitorManagerViewModel
import cool.dingstock.monitor.ui.monitorCity.MonitorCityViewModel
import cool.dingstock.monitor.ui.recommend.MonitorRecommendViewModel
import cool.dingstock.monitor.ui.recommend.item.HotRecommendChannelItemBinder
import cool.dingstock.monitor.ui.regoin.comment.HomeRaffleCommentVM
import cool.dingstock.monitor.ui.regoin.list.RaffleFragmentListViewModel
import cool.dingstock.monitor.ui.regoin.tab.RegionRaffleCommonViewModel
import cool.dingstock.monitor.ui.remindSetting.RemindSettingViewModel
import cool.dingstock.monitor.ui.rule.SelectChannelViewModule
import cool.dingstock.monitor.ui.rule.SettingRuleViewModel
import cool.dingstock.monitor.ui.shield.ShieldViewModel
import cool.dingstock.monitor.ui.topic.MonitorTopicViewModel
import cool.dingstock.monitor.ui.viewmodel.BaseMonitorViewHolder
import cool.dingstock.monitor.widget.drawer.MonitorDrawerView
import dagger.Component
import javax.inject.Singleton

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  12:26
 */
@Singleton
@Component(modules = [ApiModule::class])
interface MonitorComponent {
    fun inject(presenter: MonitorManagerViewModel)

    fun inject(viewModel: MonitorRecommendViewModel)

    fun inject(viewModel: MonitorTopicViewModel)

    fun inject(viewModel: MonitorVipViewModel)

    fun inject(viewModel: MonitorMineVM)

    fun inject(viewModel: MonitorDetailViewModel)

    fun inject(viewHolder: BaseMonitorViewHolder)

    fun inject(viewHolder: MonitorProductItemBinder)

    fun inject(shieldViewModel: ShieldViewModel)


    fun inject(monitorProductItem: RaffleFragmentListViewModel)

    fun inject(monitorProductItem: RegionRaffleCommonViewModel)

    fun inject(monitorProductItem: HotRecommendChannelItemBinder)

    fun inject(monitorProductItem: MonitorDrawerView)

    fun inject(vm: MonitorLogViewModel)

    fun inject(selectChannelModule: SelectChannelViewModule)

    fun inject(selectChannelModule: HomeRaffleCommentVM)

    fun inject(settingRuleViewModel: SettingRuleViewModel)

    fun inject(vm: SettingViewModel)

    fun inject(vm: MonitorCityViewModel)

    fun inject(vm: RemindSettingViewModel)
}