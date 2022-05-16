package cool.dingstock.home.ui.recommend

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.adapter.itembinder.OnItemClickListener
import cool.dingstock.appbase.config.AppSpConstant
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.ApiResult
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.entity.event.relation.EventFollowChange
import cool.dingstock.appbase.list.activity.AbsObservableListActivity
import cool.dingstock.appbase.net.api.account.AccountHelper
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.home.R
import cool.dingstock.home.databinding.HomeActivityRecommendFollowBinding
import cool.dingstock.home.ui.recommend.item.CheckListener
import cool.dingstock.home.ui.recommend.item.RecommendAccountItemBinder
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.SizeUtils
import org.greenrobot.eventbus.EventBus

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/10/29 15:25
 * @Version:         1.7.2
 * @Description:     推荐关注用户
 */
@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [HomeConstant.Path.RECOMMEND_FOLLOW])
class RecommendFollowActivity : AbsObservableListActivity<RecommendFollowViewModel,HomeActivityRecommendFollowBinding>() {

    override fun onResume() {
        super.onResume()
        val user = AccountHelper.getInstance().user
        val key = "${AppSpConstant.RECOMMEND_SHOW}_${user.id}"
        ConfigSPHelper.getInstance().save(key, true)
    }

    override fun setSystemStatusBar() {
        super.setSystemStatusBar()
        StatusBarUtil.transparentStatus(this)
        window.setBackgroundDrawableResource(R.color.color_black_a50)
    }


    override fun bottomBarView(): View? {
        return viewBinding.bottomBarView
    }

    override fun bindItemView() {
        val recommendItemBinder = RecommendAccountItemBinder().apply {
            checkListener = object : CheckListener {
                override fun onCheckListener(id: String, selected: Boolean) {
                    val list = pageAdapter.data as MutableList<AccountInfoBriefEntity>
                    if (list.isNullOrEmpty()) {
                        viewModel.updateSelectedList(emptyList())
                    } else {
                        val map = list
                                .filter { !it.notFollow }
                                .map {
                                    it.id
                                }
                        viewModel.updateSelectedList(map)
                    }
                }
            }
            onItemClickListener = object : OnItemClickListener {
                override fun onItemClick(adapter: BaseBinderAdapter, holder: BaseViewHolder, position: Int) {
                    val item: AccountInfoBriefEntity = adapter.data[position] as AccountInfoBriefEntity
                    DcRouter(MineConstant.Uri.DYNAMIC)
                            .appendParams(hashMapOf(MineConstant.PARAM_KEY.ID to item.id))
                            .start()
                }
            }
        }
        pageAdapter.addItemBinder(AccountInfoBriefEntity::class.java, recommendItemBinder)
    }

    override fun fetchMoreData() {
        pageAdapter.loadMoreModule.apply {
            isEnableLoadMore = false
            loadMoreEnd(true)
        }
    }

    override fun generateViewModel(): RecommendFollowViewModel {
        return ViewModelProvider(this)[RecommendFollowViewModel::class.java]
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        super.initViewAndEvent(savedInstanceState)
        viewBinding.titleBarHomeRecommend.apply {
            setTitle(R.string.title_home_recommend_follow)
            setRightOnClickListener {
                fetchData(1)
            }
            val tvRight = TextView(this@RecommendFollowActivity)
            tvRight.apply {
                this.text = "换一批"
                setTextColor(getCompatColor(R.color.color_blue))
                textSize = 16f
            }
            val layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.rightMargin = SizeUtils.dp2px(8f)
            setRightView(tvRight, layoutParams)
            setLeftIcon(R.drawable.ic_icon_close)
            setLeftIconColorRes(R.color.color_text_black1)
            setLeftIconPadding(0)
        }
        viewBinding.rvRecommendFollow.apply {
            adapter = pageAdapter
            layoutManager = LinearLayoutManager(this@RecommendFollowActivity)
        }
        viewBinding.btnFollowSelected.setOnClickListener {
            if (viewModel.selectList.isNullOrEmpty()) {
                return@setOnClickListener
            }
            showLoadingDialog()
            viewModel.followAccounts()
        }
        fetchData()
        asyncUI()
    }

    private fun asyncUI() {
        viewModel.resultLiveData.observe(this, Observer {
            when (it) {
                is ApiResult.Success<*> -> {
                    hideLoadingView()
                    val list: MutableList<AccountInfoBriefEntity> = it.data as MutableList<AccountInfoBriefEntity>
                    updateDataList(list, false)
                    viewBinding.rvRecommendFollow.scrollTo(0, 0)
                }
                is ApiResult.Error -> {
                    showErrorView(it.throwable?.localizedMessage ?: "数据错误")
                }
            }
        })
        viewModel.submitClickable.observe(this, Observer {
            viewBinding.btnFollowSelected.isEnabled = it
        })
        viewModel.followLiveData.observe(this, Observer {
            when (it) {
                is ApiResult.Success<*> -> {
                    hideLoadingDialog()
                    showToastShort("关注成功")
                    EventBus.getDefault().post(EventFollowChange())
                    this.finish()
                }
                is ApiResult.Error -> {
                    hideLoadingDialog()
                    showToastShort("关注失败")
                }
            }
        })
    }

    private fun fetchData(times: Int = 0) {
        showLoadingView()
        viewModel.fetchRecommendAccounts(times)
    }

    override fun finish() {
        super.finish()
//        overridePendingTransition(0,R.anim.bottom_menu_exit)
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        overridePendingTransition(0,R.anim.bottom_menu_exit)
    }


    override fun initListeners() {
    }

    override fun moduleTag(): String {
        return ModuleConstant.HOME_MODULE
    }
}

