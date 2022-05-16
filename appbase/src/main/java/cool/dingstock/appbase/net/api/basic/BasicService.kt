package cool.dingstock.appbase.net.api.basic

import cool.dingstock.appbase.entity.bean.base.BaseResult
import cool.dingstock.appbase.entity.bean.home.HomeData
import cool.dingstock.appbase.entity.bean.home.HomePostData
import cool.dingstock.appbase.entity.bean.score.ScoreIndexUserInfoEntity
import cool.dingstock.appbase.net.retrofit.RequestHeader
import io.reactivex.rxjava3.core.Flowable
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Query

interface BasicService {

    /**
     * 获取主页配置
     */
    @GET("/xserver/home/info/v2")
    fun home(
        @HeaderMap headerMap: HashMap<String, String> = RequestHeader.createHeader()
    ): Flowable<BaseResult<HomeData>>

    @POST("/xserver/user/fortune/checkin")
    fun scoreSign(@HeaderMap headerMap: HashMap<String, String> = RequestHeader.createHeader()): Flowable<BaseResult<ScoreIndexUserInfoEntity>>

    /**
     * 获取推荐接口
     */
    @GET("xserver/community/post/recommend/new")
    fun recommendPosts(
        @Query("type") type: String?,
        @Query("nextKey") nextKey: Long?,
        @HeaderMap headerMap: HashMap<String, String> = RequestHeader.createHeader()
    ): Flowable<BaseResult<HomePostData>>

    /**
     * 获取最新
     */
    @GET("xserver/community/post/page")
    fun newPost(
        @Query("nextKey") nextKey: Long?,
        @HeaderMap headerMap: HashMap<String, String> = RequestHeader.createHeader()
    ): Flowable<BaseResult<HomePostData>>

}