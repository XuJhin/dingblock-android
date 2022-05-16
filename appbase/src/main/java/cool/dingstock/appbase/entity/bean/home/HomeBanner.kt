package cool.dingstock.appbase.entity.bean.home;

import com.google.gson.annotations.SerializedName;


data class HomeBanner(
        var id: String? = null,
        var imageUrl: String?,
        @SerializedName(value = "targetUrl", alternate = ["link"])
        var targetUrl: String?,
        )
