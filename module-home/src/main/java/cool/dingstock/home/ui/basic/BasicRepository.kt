package cool.dingstock.home.ui.basic

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.home.HomeData
import cool.dingstock.appbase.entity.bean.home.HomePostData
import cool.dingstock.appbase.entity.bean.score.ScoreIndexUserInfoEntity
import cool.dingstock.appbase.net.api.basic.BasicService
import cool.dingstock.appbase.net.api.home.HomeHelper
import cool.dingstock.appbase.net.retrofit.api.Api
import cool.dingstock.appbase.net.retrofit.api.handError
import cool.dingstock.lib_base.thread.scheduler.RxSchedulers
import io.reactivex.rxjava3.core.Flowable

class BasicRepository {

    val service = Api.create<BasicService>()

    /**
     * 获取未读消息数
     * */
    fun home(): Flowable<BaseResult<HomeData>> {
        return service.home().compose(RxSchedulers.io_main()).handError()
            .filter {
                if (!it.err && it.res != null) {
                    HomeHelper.getInstance().cacheHomeData = it.res
                }
                return@filter true
            }
    }

    /**
     * 推荐
     * */
    fun recommendPosts(type: String?, nextKey: Long?): Flowable<BaseResult<HomePostData>> {
        val key = if (nextKey == 0L) null else nextKey
        return service
            .recommendPosts(type, key)
            .compose(RxSchedulers.io_main()).handError()
    }

    /**
     * 最新
     * */
    fun newPost(nextKey: Long?): Flowable<BaseResult<HomePostData>> {
        val key = if (nextKey == 0L) null else nextKey
        return service.newPost(key).compose(RxSchedulers.io_main()).handError()
    }


    fun scoreSign(): Flowable<BaseResult<ScoreIndexUserInfoEntity>> {
        return service.scoreSign().compose(RxSchedulers.netio_main()).handError()
    }
}