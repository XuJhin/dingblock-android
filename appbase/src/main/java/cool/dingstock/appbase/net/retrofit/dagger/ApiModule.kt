package cool.dingstock.appbase.net.retrofit.dagger

import cool.dingstock.appbase.net.api.account.AccountApi
import cool.dingstock.appbase.net.api.box.BoxApi
import cool.dingstock.appbase.net.api.bp.BpApi
import cool.dingstock.appbase.net.api.calendar.CalendarApi
import cool.dingstock.appbase.net.api.common.CommonApi
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.appbase.net.api.im.IMApi
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.appbase.net.api.shop.ShopApi
import cool.dingstock.appbase.net.api.tide.TideApi
import cool.dingstock.appbase.net.retrofit.manager.RxRetrofitServiceManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/14  12:12
 */
@Module
class ApiModule {
    @Singleton
    @Provides
    fun providerMonitorApi(retrofit: Retrofit): MonitorApi {
        return MonitorApi(retrofit)
    }

    @Singleton
    @Provides
    fun providerAccountApi(retrofit: Retrofit): AccountApi {
        return AccountApi(retrofit)
    }

    @Singleton
    @Provides
    fun providerMineApi(retrofit: Retrofit): MineApi {
        return MineApi(retrofit)
    }

    @Singleton
    @Provides
    fun providerCommonApi(retrofit: Retrofit): CommonApi {
        return CommonApi(retrofit)
    }

    @Singleton
    @Provides
    fun providerHomeApi(retrofit: Retrofit): HomeApi {
        return HomeApi(retrofit)
    }

    @Singleton
    @Provides
    fun providerBpApi(retrofit: Retrofit): BpApi {
        return BpApi(retrofit)
    }

    @Singleton
    @Provides
    fun providerCalendarApi(retrofit: Retrofit): CalendarApi {
        return CalendarApi(retrofit)
    }

    @Provides
    fun providerRetrofit(): Retrofit {
        return RxRetrofitServiceManager.getInstance().getmRetrofit()
    }

    @Singleton
    @Provides
    fun providerShopApi(retrofit: Retrofit): ShopApi {
        return ShopApi(retrofit)
    }

    @Singleton
    @Provides
    fun providerTideApi(retrofit: Retrofit): TideApi {
        return TideApi(retrofit)
    }

    @Singleton
    @Provides
    fun providerBoxApi(retrofit: Retrofit): BoxApi {
        return BoxApi(retrofit)
    }

    @Singleton
    @Provides
    fun providerIMApi(retrofit: Retrofit): IMApi {
        return IMApi(retrofit)
    }

}