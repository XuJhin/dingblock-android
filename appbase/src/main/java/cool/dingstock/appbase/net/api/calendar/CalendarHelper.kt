package cool.dingstock.appbase.net.api.calendar

import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.circle.CircleProductCommentPage
import cool.dingstock.appbase.entity.bean.circle.ProductCommentEntity
import cool.dingstock.appbase.entity.bean.home.HomeRaffle
import cool.dingstock.appbase.net.retrofit.exception.DcError
import cool.dingstock.appbase.net.parse.ParseCallback
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

object CalendarHelper {
    val net by lazy {
        Net()
    }


    class Net {
        @Inject
        lateinit var api: CalendarApi

        init {
            AppBaseApiHelper.appBaseComponent.inject(this)
        }
    }


    /**
     * 参加/不参加 活动
     *
     * @param raffle   活动id
     * @param joined   action
     * @param callback 回调
     */
    fun raffleAction(raffle: String?, joined: Boolean): Flowable<BaseResult<String>> {
        return net.api.raffleAction(raffle, joined)

//        ParseRequest.newBuilder()
//                .function(ServerConstant.Function.RAFFLE_ACTION)
//                .param(ServerConstant.ParamKEY.ACTION, if (joined) "joined" else "unjoined")
//                .param("raffle", raffle)
//                .enqueue(String::class.java, callback)
    }


    /**
     * 对产品进行交互（喜欢|不喜欢...）
     *
     * @param action    动作
     * @param productId 产品Id
     * @param callback  回调
     */
    fun productAction(action: String?, productId: String?, callback: ParseCallback<String>) {
        net.api.productAction(action, productId)
                .subscribe({ res ->
                    if (!res.err && res.res != null) {
                        callback.onSucceed(res.res)
                    } else {
                        callback.onFailed("" + res.code, res.msg)
                    }
                }, { err ->
                    callback.onFailed("" + DcError.UNKNOW_ERROR_CODE, err.message ?: "")
                })
    }


    /**
     * 产品评论详情
     *
     * @param productId 产品id
     * @param callback  回调
     */
    fun productComments(productId: String?): Flowable<BaseResult<ProductCommentEntity>> {
        return net.api.productComments(productId)
    }

    /**
     * 产品评论详情
     *
     * @param productId 产品id
     * @param callback  回调
     */
    fun productCommentPage(productId: String?, nextKey:Long?, nextStr: String?): Flowable<BaseResult<CircleProductCommentPage>> {
        return net.api.productCommentPage(productId, nextKey, nextStr)
    }

    /**
     * 商品评论点赞
     *
     * @param productId 产品id
     * @param callback  回调
     */
    fun favoredCommentHomeRaffle(commentId: String?, favored: Boolean) : Flowable<BaseResult<String>>{
        return net.api.favoredCommentHomeRaffle(commentId,favored)
    }

    /**
     * 商品评论举报
     *
     * @param productId 产品id
     * @param callback  回调
     */
    fun productPostCommentReport(commentId: String?) : Flowable<BaseResult<String>>{
        return net.api.productPostCommentReport(commentId)
    }

    /**
     * 鞋库评论点赞
     *
     * @param productId 产品id
     * @param callback  回调
     */
    fun favoredCommentShoesSeries(commentId: String?, favored: Boolean) : Flowable<BaseResult<String>>{
        return net.api.favoredCommentShoesSeries(commentId,favored)
    }
}