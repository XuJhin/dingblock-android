package cool.dingstock.appbase.entity.bean.home;

import android.os.Parcel;
import android.os.Parcelable;
import kotlinx.parcelize.Parcelize


@Parcelize
data class HomeArticleBean(

    /**
     * titleColor : #FF0000
     * imageUrl : https://mmbiz.qpic.cn/mmbiz_gif/PHyEic33FWsOLnZZHAEIDXV5Q7LvZIxQulylE36ffECfia9EK3KN7HO79yeaahIoibsGMJzibUA2IRDaCgfh3reJnw/640?wx_fmt=gif&wxfrom=5&wx_lazy=1
     * link : https://mp.weixin.qq.com/s?__biz=MzU3MjgyMDU5Ng==&mid=2247483951&idx=1&sn=c7c21e60dadb0b6275e922cf95c33f17&chksm=fcca5f34cbbdd6229ddf6ef3977d712e80f36f2136f0792e15258ed2adddd3fd48b6dac678e4&mpshare=1&scene=1&srcid=&key=67f1291a8f8b0e0c083ef2a37cf2a887a1ac
     * title : 开箱 | Off White x AF1一次看完全部四个配色
     */

    var titleColor  : String?,
    var imageUrl  : String?,
    var link  : String?,
    var title  : String?,
    var subtitle  : String?,
    var tag  : String?


) : Parcelable
