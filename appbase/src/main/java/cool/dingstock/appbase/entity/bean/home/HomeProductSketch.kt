package cool.dingstock.appbase.entity.bean.home;

import android.os.Parcelable;

import java.util.ArrayList;


import cool.dingstock.appbase.entity.bean.home.fashion.VideoEntity;
import kotlinx.parcelize.Parcelize


@Parcelize
data class HomeProductSketch(
    /**
     * createdAt : 2019-03-02T08:06:50.281Z
     * updatedAt : 2019-03-06T09:10:27.636Z
     * name : Yeezy Boost 350 V2 Hyperspace
     * imageUrl : https://dingstock.obs.cn-east-2.myhuaweicloud.com/Sneakers/Yeezy%20Boost%20350%20V2%20Hyperspace.jpg
     * subscribeCount : 1
     * subscribed : false
     * raffleCount : 1
     * objectId : hABJnGHiKc
     */
    var name: String?,
    var imageUrl: String?,
    var price: String?,
    var raffleCount: Int = 0,
) : Parcelable
