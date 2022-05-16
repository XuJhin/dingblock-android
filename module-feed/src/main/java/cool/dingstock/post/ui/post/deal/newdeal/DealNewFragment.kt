package cool.dingstock.post.ui.post.deal.newdeal

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.post.databinding.FragmentDealNewBinding
import cool.dingstock.post.ui.post.deal.index.DealDetailsIndexVM
import cool.dingstock.post.ui.post.deal.newdeal.item.DealNewItemBinder


class DealNewFragment : VmBindingLazyFragment<DealNewFragmentVM, FragmentDealNewBinding>() {


    companion object {
        @JvmStatic
        fun newInstance(type:String) =
            DealNewFragment().apply {
                arguments = Bundle().apply {
                    putString("type",type)
                }
            }
    }

    val dealNewBinder = DealNewItemBinder()
    val activityVM by activityViewModels<DealDetailsIndexVM>()

    val adapter = DcBaseBinderAdapter(arrayListOf()).apply {
        addItemBinder(dealNewBinder)
    }


    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        context?.let { ctx ->
            viewBinding.rv.adapter = adapter
            viewBinding.rv.layoutManager = LinearLayoutManager(ctx)
            adapter.setEmptyView(CommonEmptyView(ctx))
        }
        viewModel.type = arguments?.getString("type")?:"全新无瑕"
        dealNewBinder.type = viewModel.type
        viewModel.id = activityVM.id
        initObserver()
    }



    override fun initListeners() {
        adapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.loadMoreData()
        }
    }

    private fun initObserver() {
        viewModel.apply {
            loadDataLiveData.observe(this@DealNewFragment){
                adapter.setList(it)
            }
            loadMoreLiveData.observe(this@DealNewFragment){
                adapter.addData(it)
                adapter.loadMoreModule.loadMoreComplete()
                if(it.isEmpty()){
                    adapter.loadMoreModule.loadMoreEnd(false)
                }
            }
        }
    }


    override fun reload() {
        super.reload()
        viewModel.currentSize = activityVM.currentSize
        viewModel.loadData()
    }

    override fun onLazy() {
        reload()
    }

}