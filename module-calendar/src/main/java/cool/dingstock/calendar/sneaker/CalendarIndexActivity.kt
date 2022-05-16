package cool.dingstock.calendar.sneaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import cool.dingstock.appbase.constant.HomeBusinessConstant
import cool.dingstock.appbase.entity.bean.home.HomeCategoryBean
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.appbase.storage.NetDataCacheHelper
import cool.dingstock.calendar.R
import cool.dingstock.calendar.sneaker.index.HomeSneakersFragment
import cool.dingstock.calendar.dagger.CalendarApiHelper
import javax.inject.Inject

class CalendarIndexActivity : AppCompatActivity() {

    @Inject
    lateinit var homeApi:HomeApi

    init {
        CalendarApiHelper.apiHomeComponent.inject(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        val subscribe = homeApi.home().subscribe({res->
            val data = res.res
            if (!res.err && data != null) {
                res.res?.categories?.let {
                    for (bean in it){
                        if(HomeBusinessConstant.CategoryType.SNEAKERS == bean.type ){
                            addFragment(bean)
                        }
                    }
                }

            } else {
            }

        },{error->
        })
    }

    private fun addFragment(bean: HomeCategoryBean) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        val fragment = HomeSneakersFragment.getInstance(bean).setNeedMargeTop(false)
        beginTransaction.add(R.id.root_view,fragment)
        beginTransaction.commit()
    }
}
