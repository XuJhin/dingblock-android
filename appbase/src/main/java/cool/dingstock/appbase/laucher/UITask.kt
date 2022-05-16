package cool.dingstock.appbase.laucher

import com.chad.library.adapter.base.module.LoadMoreModuleConfig.defLoadMoreView
import com.kongzue.dialogx.DialogX
import cool.dingstock.launch.DcITask
import cool.dingstock.lib_base.rv.DingLoadMore

class UITask : DcITask() {

    override fun run() {
        defLoadMoreView = DingLoadMore()
    }
}