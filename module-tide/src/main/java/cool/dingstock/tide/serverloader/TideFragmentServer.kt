package cool.dingstock.tide.serverloader

import com.sankuai.waimai.router.annotation.RouterService
import cool.dingstock.appbase.constant.TideConstant
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.appbase.serviceloader.ITideFragmentServer
import cool.dingstock.tide.index.TideHomeIndexFragment


/**
 * 类名：TideFragmentServer
 * 包名：cool.dingstock.tide.index
 * 创建时间：2021/7/20 11:55 上午
 * 创建人： WhenYoung
 * 描述：
 **/
@RouterService(interfaces = [ITideFragmentServer::class], key = [TideConstant.ServerLoader.TIDE_FRAGMENT], singleton = true)
class TideFragmentServer : ITideFragmentServer {
    override fun getTideHomeIndexFragment(): BaseFragment {
        return TideHomeIndexFragment.newInstance()
    }
}