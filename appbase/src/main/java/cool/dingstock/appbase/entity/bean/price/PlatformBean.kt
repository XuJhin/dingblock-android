package cool.dingstock.appbase.entity.bean.price;

import android.os.Parcel;
import android.os.Parcelable;
import kotlinx.parcelize.Parcelize


@Parcelize
data class PlatformBean (
    /**
     * logoUrl : https://dingstock.obs.cn-east-2.myhuaweicloud.com/brands/150x150/nike.jpg
     * priceLink : https://google.com
     * logoLink : https://baidu.com
     * objectId : stockX
     * badge : ("link":"","iconUrl":"")
     */

    var logoUrl  : String?,
    var priceLink  : String?,
    var logoLink  : String?,
    var objectId  : String?,
    var badge  : BadgeBean?,
    var tintColor  : String?,
    var prefix  : String?,
    //本地mock
    var select  : Boolean=false

) : Parcelable
