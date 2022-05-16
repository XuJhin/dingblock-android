package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * marginRight : 10
 * imageUrl : https://dingstock.obs.cn-east-2.myhuaweicloud.com/website/image/logo.png
 * width : 90
 * marginTop : 10
 * height : 90
 */
@Parcelize
data class CircleOverlayBean(

        private var marginRight: Int = 0,
        private var imageUrl: String? = null,
        private var width: Int = 0,
        private var marginTop: Int = 0,
        private var height: Int = 0) : Parcelable
