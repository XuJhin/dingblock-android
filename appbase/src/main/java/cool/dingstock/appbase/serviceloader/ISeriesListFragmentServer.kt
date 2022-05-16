package cool.dingstock.appbase.serviceloader

import cool.dingstock.appbase.mvp.BaseFragment

interface ISeriesListFragmentServer {
    fun getSeriesListFragment(): BaseFragment
}