package cool.dingstock.appbase.net.api.box

import cool.dingstock.appbase.dagger.AppBaseApiHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

object BoxHelper {
    val net by lazy {
        Net()
    }


    class Net {
        @Inject
        lateinit var api: BoxApi

        init {
            AppBaseApiHelper.appBaseComponent.inject(this)
        }
    }

    suspend fun getDiscountPrice(scope: CoroutineScope, boxId: String, count: Int, couponId: String) =
        net.api.getDiscountPrice(boxId, count, couponId).stateIn(scope, SharingStarted.Lazily, null)
}