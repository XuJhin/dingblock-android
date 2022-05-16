package cool.dingstock.appbase.entity.bean.home.fashion

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  14:31
 */
@Parcelize
data class VideoEntity(var link:String?=null,
                       @SerializedName(value="image",alternate=["imageUrl"])
                       var image:String?=null,
                       var title:String?=null,
                       var postId:String?=null) : Parcelable


