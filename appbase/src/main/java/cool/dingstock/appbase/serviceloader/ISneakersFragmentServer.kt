package cool.dingstock.appbase.serviceloader

import androidx.fragment.app.Fragment
import cool.dingstock.appbase.entity.bean.home.HomeCategoryBean
import cool.dingstock.appbase.mvp.BaseFragment

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/14  19:07
 */
interface ISneakersFragmentServer {
    fun getSneakersFragment(bean: HomeCategoryBean, needMargeTop: Boolean): BaseFragment
}