package cool.dingstock.appbase.customerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class EmptyLoadMore() : BaseLoadMoreView() {

    override fun getRootView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
            .inflate(cool.dingstock.lib_base.R.layout.empty_view_load_more, parent, false)
    }

    override fun getLoadingView(holder: BaseViewHolder): View {
        return holder.getView(cool.dingstock.lib_base.R.id.load_more_loading_view)
    }

    override fun getLoadComplete(holder: BaseViewHolder): View {
        return holder.getView(cool.dingstock.lib_base.R.id.load_more_load_complete_view)
    }

    override fun getLoadEndView(holder: BaseViewHolder): View {
        return holder.getView(cool.dingstock.lib_base.R.id.load_more_load_end_view)
    }

    override fun getLoadFailView(holder: BaseViewHolder): View {
        return holder.getView(cool.dingstock.lib_base.R.id.load_more_load_fail_view)
    }
}