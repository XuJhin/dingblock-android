package cool.dingstock.appbase.entity.bean.home;

import android.os.Parcel;
import android.os.Parcelable;
import kotlinx.parcelize.Parcelize


@Parcelize
data class HomePlayground (
    /**
     * objectId : 123
     * type : raffle
     * title : 会员专属抽奖
     * titleColor :
     * imageUrl : https://lc-gmid1k1s.cn-e1.lcfile.com/0518c2f233019f9ec5d9.jpeg
     * vipLimited : true
     * subtitle : Supreme 2019SS 开季Tee 随机一件
     * startTime : 1551191023
     * endTime : 1551191023
     * joined : false
     */

    var objectId  : String?,
    var type  : String?,
    var title  : String?,
    var tintColor  : String?,
    var imageUrl  : String?,
    var restricted  : Boolean?,
    var subtitle  : String?,
    var startTime  : Long=0,
    var endTime  : Long=0,
    var joined  : Boolean=false
) : Parcelable
