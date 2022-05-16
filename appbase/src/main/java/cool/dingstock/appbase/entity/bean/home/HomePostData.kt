package cool.dingstock.appbase.entity.bean.home

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import kotlinx.parcelize.Parcelize


@Parcelize
data class HomePostData(
        @SerializedName(value = "posts", alternate = ["list"])
        var posts: MutableList<CircleDynamicBean> = arrayListOf(),
        var nextKey: Long = 0L,
        var type: String?
) : Parcelable
