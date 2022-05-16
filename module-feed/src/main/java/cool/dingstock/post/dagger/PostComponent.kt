package cool.dingstock.post.dagger

import cool.dingstock.appbase.net.retrofit.dagger.ApiModule
import cool.dingstock.post.activity.MoreVideoViewModel
import cool.dingstock.post.dialog.PostItemViewModel
import cool.dingstock.post.item.DynamicItemBinder
import cool.dingstock.post.list.PostViewModel
import cool.dingstock.post.list.deal.HomeDealPostListVM
import cool.dingstock.post.list.followed.HomeFollowedPostListVm
import cool.dingstock.post.list.recommend.HomeRecommendPostListVM
import cool.dingstock.post.list.topic.HomeTopicPostListVM
import cool.dingstock.post.ui.post.comment.CircleSubCommentDetailVM
import cool.dingstock.post.ui.post.deal.defects.DealDefectsFragmentVM
import cool.dingstock.post.ui.post.deal.defects.item.DealDefectsItemBinder
import cool.dingstock.post.ui.post.deal.index.DealDetailsIndexVM
import cool.dingstock.post.ui.post.deal.newdeal.DealNewFragmentVM
import cool.dingstock.post.ui.post.deal.newdeal.item.DealNewItemBinder
import cool.dingstock.post.ui.post.detail.CircleDynamicDetailViewModel
import cool.dingstock.post.ui.post.nearby.NearbyDetailsVM
import dagger.Component
import javax.inject.Singleton

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  12:26
 */
@Singleton
@Component(modules = [ApiModule::class])
interface PostComponent {
    fun inject(viewModel: MoreVideoViewModel)
    fun inject(viewModel: PostItemViewModel)
    fun inject(viewModel: HomeFollowedPostListVm)
    fun inject(binder: DealNewItemBinder)
    fun inject(binder: DealDefectsItemBinder)
    fun inject(viewModel: HomeTopicPostListVM)
    fun inject(viewModel: HomeDealPostListVM)
    fun inject(viewModel: PostViewModel)
    fun inject(viewModel: DealNewFragmentVM)
    fun inject(viewModel: DealDefectsFragmentVM)
    fun inject(binder: DynamicItemBinder)
    fun inject(binder: CircleDynamicDetailViewModel)
    fun inject(binder: NearbyDetailsVM)
    fun inject(binder: CircleSubCommentDetailVM)
    fun inject(binder: DealDetailsIndexVM)
    fun inject(vm: HomeRecommendPostListVM)
}