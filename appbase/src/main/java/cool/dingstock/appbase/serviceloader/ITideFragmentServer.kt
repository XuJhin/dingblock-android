package cool.dingstock.appbase.serviceloader

import cool.dingstock.appbase.mvp.BaseFragment


/**
 * 类名：ITideServer
 * 包名：cool.dingstock.appbase.serviceloader
 * 创建时间：2021/7/20 11:54 上午
 * 创建人： WhenYoung
 * 描述：
 **/
interface ITideFragmentServer {
    fun getTideHomeIndexFragment(): BaseFragment
}