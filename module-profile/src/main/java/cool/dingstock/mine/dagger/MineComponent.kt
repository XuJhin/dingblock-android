package cool.dingstock.mine.dagger

import cool.dingstock.appbase.net.retrofit.dagger.ApiModule
import cool.dingstock.mine.ui.avater.AvatarPreviewViewModel
import cool.dingstock.mine.ui.avater.ModifyPendantViewModel
import cool.dingstock.mine.ui.collection.MineCollectionViewModel
import cool.dingstock.mine.ui.exchange.good.MineExchangeGoodsViewModel
import cool.dingstock.mine.ui.follow.FollowActivity
import cool.dingstock.mine.ui.follow.FollowViewModel
import cool.dingstock.mine.ui.index.MineFragment
import cool.dingstock.mine.ui.index.MineFragmentViewModel
import cool.dingstock.mine.ui.index.MineLotteryNotesViewModel
import cool.dingstock.mine.ui.medal.MedalDetailViewModel
import cool.dingstock.mine.ui.message.NoticeViewModel
import cool.dingstock.mine.ui.medal.MedalListViewModel
import cool.dingstock.mine.ui.score.detail.ScoreDetailActivity
import cool.dingstock.mine.ui.score.index.ScoreIndexVM
import cool.dingstock.mine.ui.score.message.ReceiveInformationActivity
import cool.dingstock.mine.ui.score.record.ScoreExchangeRecordActivity
import cool.dingstock.mine.ui.vip.VipCenterVm
import dagger.Component
import javax.inject.Singleton

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  12:26
 */
@Singleton
@Component(modules = [ApiModule::class])
interface MineComponent {
    fun inject(mineFragment: MineFragment)

    fun inject(mineFragment: FollowActivity)

    fun inject(mineFragmentViewModel: MineFragmentViewModel)

    fun inject(vm: NoticeViewModel)

    fun inject(vm: FollowViewModel)

    fun inject(vm: VipCenterVm)

    fun inject(vm: ScoreIndexVM)

    fun inject(vm: ScoreDetailActivity)

    fun inject(vm: ScoreExchangeRecordActivity)

    fun inject(vm: MineExchangeGoodsViewModel)

    fun inject(receiveInformationActivity: ReceiveInformationActivity)

//    fun inject(vm: MineDynamicViewModel)

    fun inject(vm: MineCollectionViewModel)

    fun inject(vm: MineLotteryNotesViewModel)

    fun inject(vm: MedalListViewModel)

    fun inject(vm: MedalDetailViewModel)

    fun inject(vm: AvatarPreviewViewModel)

    fun inject(vm: ModifyPendantViewModel)
}