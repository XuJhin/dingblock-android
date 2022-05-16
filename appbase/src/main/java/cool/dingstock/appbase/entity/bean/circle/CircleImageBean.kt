package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @param height :  图片高度
 * @param width :   图片宽度
 * @param thumbnail : 缩略图路径
 * @param format :  图片格式
 * @param url :     图片路径
 */
@Parcelize
data class CircleImageBean(
        var height: Int = 0,
        var width: Int = 0,
        var thumbnail: String? = null,
        var format: String? = null,
        var url: String? = null) : Parcelable
