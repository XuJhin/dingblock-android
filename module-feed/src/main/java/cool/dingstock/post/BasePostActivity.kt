package cool.dingstock.post

import android.widget.Toast
import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.constant.AccountConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.list.activity.AbsListActivity
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.post.adapter.DynamicBinderAdapter
import cool.dingstock.post.base.BasePostViewModel
import cool.dingstock.post.item.DynamicItemBinder

abstract class BasePostActivity<VM : BasePostViewModel,VB:ViewBinding> : AbsListActivity<VM,VB>() {
    protected val itembinder by lazy { DynamicItemBinder(this) }

    override fun setupAdapter() {
        pageAdapter = DynamicBinderAdapter(ArrayList())
    }

    override fun moduleTag(): String {
        return ModuleConstant.CIRCLE
    }

    private fun routeToLogin() {
        DcUriRequest(this, AccountConstant.Uri.INDEX)
                .start()
    }

    override fun bindItemView() {
        pageAdapter.addItemBinder(CircleDynamicBean::class.java, itembinder)
        (pageAdapter as DynamicBinderAdapter).registerDynamicReload(lifecycle)
    }

    private fun showToast(tips: String?) {
         ToastUtil.getInstance().makeTextAndShow(this, tips, Toast.LENGTH_SHORT)
    }
}