package cool.dingstock.mine.ui.avater

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseBinderAdapter
import cool.dingstock.appbase.adapter.CommonSpanItemDecoration
import cool.dingstock.appbase.entity.bean.mine.PendantDetail
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.mine.databinding.FragmentPendantListBinding
import cool.dingstock.mine.itemView.PendantItemBinder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

@SuppressLint("NotifyDataSetChanged")
class PendantListFragment: VmBindingLazyFragment<PendantListViewModel, FragmentPendantListBinding>() {
    private val activityViewModel by activityViewModels<ModifyPendantViewModel>()

    private val binder by lazy {
        PendantItemBinder().apply {
            setOnItemClickListener { adapter, _, position ->
                val item = adapter.data[position] as PendantDetail
                activityViewModel.selectPendant.value = item
            }
        }
    }

    private val adapter by lazy {
        BaseBinderAdapter(arrayListOf()).apply {
            addItemBinder(PendantDetail::class.java, binder)
        }
    }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        context?.let { ctx ->
            with(viewBinding.pendantRv) {
                adapter = this@PendantListFragment.adapter
                layoutManager = GridLayoutManager(ctx, 3)
                addItemDecoration(CommonSpanItemDecoration(3, 12, 26, false))
                (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.pendantList.filter {
                it.isNotEmpty()
            }.collect {
                adapter.setList(it)
            }
        }
        lifecycleScope.launch {
            activityViewModel.selectPendant
                .filter {
                    it != null
                }
                .collectLatest { pendant ->
                    adapter.data.forEach {
                        (it as PendantDetail).selected = it.id == pendant!!.id
                    }
                    adapter.notifyItemRangeChanged(0, adapter.data.size)
                }
        }
    }

    override fun initListeners() {

    }

    override fun onLazy() {
        arguments?.let { bundle ->
            bundle.getParcelableArrayList<PendantDetail>(PENDANT_DETAIL_LIST)?.let {
                viewModel.postPendantList(it)
                return
            }
        }
        showEmptyView("空空如也")
    }

    companion object {
        const val PENDANT_DETAIL_LIST = "PENDANT_DETAIL_LIST"
        fun getInstance(pendants: List<PendantDetail>?): PendantListFragment{
            return PendantListFragment().apply {
                arguments = Bundle().apply {
                    pendants?.let {
                        putParcelableArrayList(PENDANT_DETAIL_LIST, arrayListOf<PendantDetail>().apply {
                            addAll(it)
                        })
                    }
                }
            }
        }
    }
}