package cool.dingstock.mine.ui.medal

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseBinderAdapter
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.mine.MedalBean
import cool.dingstock.appbase.entity.bean.mine.MedalSection
import cool.dingstock.appbase.entity.bean.mine.MedalStatus
import cool.dingstock.appbase.entity.event.update.EventMedalStateChange
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.mine.R
import cool.dingstock.mine.databinding.ActivityMedalListBinding
import cool.dingstock.mine.itemView.MedalSectionItemBinder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [MineConstant.Path.MEDAL_LIST]
)
class MedalListActivity: VMBindingActivity<MedalListViewModel, ActivityMedalListBinding>() {

    private val sectionBinder by lazy {
        MedalSectionItemBinder(viewModel.userId) {
            viewModel.getMedalList()
        }
    }

    private val sectionAdapter by lazy {
        BaseBinderAdapter().apply {
            addItemBinder(MedalSection::class.java, sectionBinder)
        }
    }

    override fun moduleTag(): String {
        return ModuleConstant.MINE_MODULE
    }

    override fun setSystemStatusBar() {
        StatusBarUtil.transparentStatus(this)
        StatusBarUtil.setDarkMode(this)
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        viewModel.userId = uri.getQueryParameter(MineConstant.PARAM_KEY.ID) ?: LoginUtils.getCurrentUser()?.id ?: ""
        with(viewBinding) {
            medalSectionRv.adapter = sectionAdapter
            medalSectionRv.layoutManager = LinearLayoutManager(this@MedalListActivity)
        }

        with(viewModel) {
            lifecycleScope.launch {
                timer.sample(1000L)
                    .filter { it != null }
                    .collectLatest {
                        sectionBinder.medalAdapters.forEach { medalAdapter ->
                            medalAdapter.data.forEachIndexed { index, data ->
                                if (data is MedalBean &&
                                    (data.madelStatus() == MedalStatus.COMPLETED || data.madelStatus() == MedalStatus.WEARING) &&
                                    data.validity ?: 0L > 0L) {
                                    medalAdapter.notifyItemChanged(index, data.validity)
                                }
                            }
                        }
                        timer.value = timer.value!!.plus(1L)
                    }
            }
            lifecycleScope.launchWhenResumed {
                medalList.filter { it != null }
                    .collectLatest {
                        Glide.with(this@MedalListActivity)
                            .load(it!!.avatarUrl)
                            .error(R.drawable.default_avatar)
                            .placeholder(R.drawable.default_avatar)
                            .into(viewBinding.avatarIv)
                        viewBinding.nicknameTv.text = it.nickName
                        viewBinding.nicknameTv.setTextColor(if(it.isVip == true) Color.parseColor("#EBD97B") else Color.WHITE)
                        sectionAdapter.setList(it.sections)
                        timer.value = 0L
                    }
            }
        }

        getList()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onStatusViewErrorClick() {
        getList()
    }

    private fun getList() {
        showLoadingView()
        viewModel.getMedalList()
    }

    override fun initListeners() {
        viewBinding.backIv.setOnClickListener {
            finish()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshMedal(event: EventMedalStateChange) {
        event.userId?.let {
            viewModel.userId = it
        }
        viewModel.getMedalList()
    }
}