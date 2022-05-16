package cool.dingstock.home.adapter

import androidx.lifecycle.Lifecycle
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.home.helper.NewSaleReload
import cool.dingstock.lib_base.util.Logger

class NewSaleAdapter(list: MutableList<Any> = ArrayList()) : DcBaseBinderAdapter(list as ArrayList<Any>) {
    private val newSaleReload = NewSaleReload(this)

    fun registerDynamicReload(lifecycle: Lifecycle) {
        registerReloadDelegations(newSaleReload)
        registerReloadLifecycle(lifecycle)
    }
}