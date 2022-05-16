package cool.dingstock.calendar.sneaker.index.serverloader

import androidx.fragment.app.Fragment
import com.sankuai.waimai.router.annotation.RouterService
import cool.dingstock.appbase.constant.CalendarConstant
import cool.dingstock.appbase.entity.bean.home.HomeCategoryBean
import cool.dingstock.appbase.mvp.BaseFragment
import cool.dingstock.appbase.serviceloader.ISneakersFragmentServer
import cool.dingstock.calendar.sneaker.index.HomeSneakersFragment

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/14  19:10
 */
@RouterService(interfaces = [ISneakersFragmentServer::class], key = [CalendarConstant.ServerLoader.SNEAKERS_FRAGMENT], singleton = true)
class SneakersFragmentServer : ISneakersFragmentServer {

    override fun getSneakersFragment(homeCategoryBean: HomeCategoryBean, needMargeTop: Boolean): BaseFragment {
        return HomeSneakersFragment.getInstance(homeCategoryBean).setNeedMargeTop(true)
    }

}