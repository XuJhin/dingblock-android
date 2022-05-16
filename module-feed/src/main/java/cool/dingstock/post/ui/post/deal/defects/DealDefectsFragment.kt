package cool.dingstock.post.ui.post.deal.defects

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.post.databinding.FragmentDealDefectsBinding
import cool.dingstock.post.ui.post.deal.defects.item.DealDefectsItemBinder
import cool.dingstock.post.ui.post.deal.index.DealDetailsIndexVM

class DealDefectsFragment : VmBindingLazyFragment<DealDefectsFragmentVM, FragmentDealDefectsBinding>() {


    companion object {
        @JvmStatic
        fun newInstance(type:String) =
            DealDefectsFragment().apply {
                arguments = Bundle().apply {
                    putString("type", type)
                }
            }
    }

    val activityVM by activityViewModels<DealDetailsIndexVM>()

    val dealNewBinder = DealDefectsItemBinder()
    val adapter = DcBaseBinderAdapter(arrayListOf()).apply {
        addItemBinder(dealNewBinder)
    }


    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        viewModel.id = activityVM.id
        viewModel.type = arguments?.getString("type")?:"轻微瑕疵"
        dealNewBinder.type = viewModel.type

        initAdapter()
        initObserver()
    }


    override fun initListeners() {
        adapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.loadMoreData()
        }
    }

    private fun initObserver() {
        viewModel.apply {
            loadDataLiveData.observe(this@DealDefectsFragment){
                adapter.setList(it)
            }
            loadMoreLiveData.observe(this@DealDefectsFragment){
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

    private fun initAdapter() {
        context?.let { ctx ->
            adapter.setEmptyView(CommonEmptyView(ctx))
            viewBinding.rv.apply {
                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                    .apply {
                        this.spanCount = 2
                    }
                adapter = this@DealDefectsFragment.adapter
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    val space = 7.5.dp
                    val edge = 15.dp
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        super.getItemOffsets(outRect, view, parent, state)
                        var left = 0
                        var right = 0
                        var top = 0
                        var bottom = 20.azDp
                        val layoutManager = parent.layoutManager ?: return
                        val childCount = parent.childCount
                        if (childCount == 0) {
                            return
                        }
                        val childPosition = parent.getChildAdapterPosition(view)
                        if (layoutManager is StaggeredGridLayoutManager) {
                            val layoutParams = view.layoutParams
                            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                                val column = layoutParams.spanIndex
                                val spanCount = layoutManager.spanCount
                                layoutManager.spanCount
                                if (layoutParams.isFullSpan) {
                                    left = edge.toInt()
                                    right = edge.toInt()
                                    top = 0.dp.toInt()
                                    bottom = 20.dp
                                } else {
                                    left = if (isEdgeLeft(column)) {
                                        edge.toInt()
                                    } else {
                                        space.toInt()
                                    }
                                    right = if (isEdgeRight(column, spanCount)) {
                                        edge.toInt()
                                    } else {
                                        space.toInt()
                                    }
                                    if (isFirstLine(view, childPosition, spanCount)) {
                                        top = 0.dp.toInt()
                                    }
                                }
                            }
                            outRect.set(left, top, right.toInt(), bottom.toInt())
                        }
                    }

                    private fun isFirstLine(
                        view: View,
                        childPosition: Int,
                        spanCount: Int
                    ): Boolean {
                        return childPosition < spanCount
                    }

                    private fun isEdgeRight(column: Int, spanCount: Int): Boolean {
                        val lastMod = spanCount - 1
                        return column == lastMod
                    }

                    private fun isEdgeLeft(column: Int): Boolean {
                        return column == 0
                    }
                })
            }
        }
    }

}