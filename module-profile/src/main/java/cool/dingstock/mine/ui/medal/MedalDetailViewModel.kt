package cool.dingstock.mine.ui.medal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import cool.dingstock.appbase.entity.bean.circle.BitmapsEntity
import cool.dingstock.appbase.entity.bean.circle.MedalEntity
import cool.dingstock.appbase.entity.bean.circle.MedalPreViewUserEntity
import cool.dingstock.appbase.entity.event.update.EventMedalStateChange
import cool.dingstock.appbase.entity.event.update.EventMedalWear
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.appbase.net.api.mine.MineApi
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.mine.dagger.MineApiHelper
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject

class MedalDetailViewModel : BaseViewModel() {
    @Inject
    lateinit var mineApi: MineApi

    @Inject
    lateinit var homeApi: HomeApi

    val userMedals = MutableLiveData<ArrayList<MedalEntity>>()
    val userMsg = MutableLiveData<MedalPreViewUserEntity>()
    val isSuitMedalSuccess = MutableLiveData<Boolean>()
    val isGetMedalSuccess = MutableLiveData<Boolean>()
    val scoreNum = MutableLiveData<Int>()
    val bitmapLiveData = MutableLiveData<BitmapsEntity>()

    var userId: String = ""
    var medalId: String = ""
    var isMineMedalPage: Boolean = true
    var isGetMedal: Boolean = false //是否操作了领取勋章

    var currentMedalIconUrl: String? = null

    init {
        MineApiHelper.apiMineComponent.inject(this)
    }

    fun fetchUserMedal(userId: String, medalId: String) {
        if (userId.isEmpty() || medalId.isEmpty()) {
            return
        }
        mineApi.fetchMineMedals(userId, medalId)
            .subscribe({
                if (!it.err) {
                    userMedals.postValue(it.res?.achievement ?: arrayListOf())
                    if (isMineMedalPage) {
                        it.res?.user?.let { user ->
                            userMsg.postValue(user)
                        }
                    }
                } else {
                    userMedals.postValue(arrayListOf())
                    shortToast(it.msg)
                }
            }, {
                userMedals.postValue(arrayListOf())
                shortToast(it.message)
            })
    }


    fun getNewMedal(medalId: String) {
        if (medalId.isEmpty()) {
            return
        }
        mineApi.getNewMedal(medalId)
            .subscribe({
                if (!it.err) {
                    EventBus.getDefault()
                        .post(EventMedalStateChange(userId = userId, isGetMedal = true))
                    isGetMedalSuccess.postValue(true)
                    shortToast("领取成功")
                } else {
                    shortToast(it.msg)
                }
            }, {
                shortToast(it.message)
            })
    }


    fun suitUpMedal(medalId: String, isWear: Boolean) {
        if (medalId.isEmpty()) {
            return
        }
        mineApi.suitUpMedal(medalId, isWear)
            .subscribe({
                if (!it.err) {
                    isSuitMedalSuccess.postValue(isWear)
                    EventBus.getDefault().post(EventMedalWear(userId, if (isWear) currentMedalIconUrl else null))
                    shortToast(if (isWear) "佩戴成功" else "已取消佩戴")
                } else {
                    shortToast(it.msg)
                }
            }, {
                shortToast(it.message)
            })
    }

    fun fetchScoreInfo() {
        if (!LoginUtils.isLogin()) {
            return
        }
        mineApi.mineScoreInfo()
            .subscribe({
                if (!it.err) {
                    scoreNum.postValue(it.res?.score)
                } else {

                }
            }, {

            })
    }

    fun getBitmaps(firstUrl: String? = "", secondUrl: String? = "") {
        Thread {
            var imageOldUrl: URL? = null
            var imageNewUrl: URL? = null
            try {
                if (firstUrl != "") {
                    imageOldUrl = URL(firstUrl)
                }
                if (secondUrl != "") {
                    imageNewUrl = URL(secondUrl)
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            try {
                var bitmapOld: Bitmap? = null
                var bitmapNew: Bitmap? = null
                if (firstUrl != "") {
                    bitmapOld = fetchBitmap(imageOldUrl)
                }
                if (secondUrl != "") {
                    bitmapNew = fetchBitmap(imageNewUrl)
                }
                bitmapLiveData.postValue(BitmapsEntity(bitmapOld, bitmapNew))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun fetchBitmap(url: URL?): Bitmap {
        val conn: HttpURLConnection = url?.openConnection() as HttpURLConnection
        conn.doInput = true
        conn.connect()
        val stream: InputStream = conn.inputStream
        val bitmap = BitmapFactory.decodeStream(stream)
        stream.close()
        return bitmap
    }
}