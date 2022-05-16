package cool.dingstock.appbase.entity.bean.upload

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageEntity(
        val thumbnail: String,
        val url: String,
        val format: String,
        val width: Int,
        val height: Int
) : Parcelable
