package cool.dingstock.appbase.entity.bean.circle

import android.os.Parcelable
import cool.dingstock.appbase.entity.bean.upload.ImageEntity
import kotlinx.parcelize.Parcelize

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/18  15:56
 */
@Parcelize
data class HotComment(
        var id:String?=null,
        var createdAt:Long=0,
        var postId:String?=null,
        var userId:String?=null,
        var mentionedId:String?=null,
        var content:String?=null,
        var favorCount:Int = 0 ,
        var userMap:UserMap? = null,
        var blocked:Boolean=false,
        var staticImg: ImageEntity? = null,
        var dynamicImg: ImageEntity? = null
): Parcelable
