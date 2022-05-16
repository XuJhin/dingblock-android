package cool.dingstock.appbase.entity.bean.circle;


import com.google.gson.annotations.SerializedName
import javax.annotation.Nullable;

import cool.dingstock.appbase.entity.bean.upload.ImageEntity;

data class CircleDynamicDetailCommentsBean (
    /**
     * favorCount : 0
     * content : 谍照都说丑。发售抢成狗。
     * user : ("nickName":"_wang","avatarUrl":"https://dingstock-avatar.obs.cn-east-2.myhuaweicloud.com/dingstockayu/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190713180509.jpg","objectId":"qtl8aeSQya")
     * favored : false
     * objectId : xYyLmL1bUu
     * createdAt : 1563274236
     */
    var favorCount  : Int = 0 ,
    var favored  : Boolean = false,
    @Nullable
    var content  : String?,
    @SerializedName(value = "user", alternate = ["userMap"])
    var user  : CircleDynamicDetailUserBean?,
    var mentioned  : CircleMentionedBean?,
    var subComments  : ArrayList<CircleDynamicDetailCommentsBean>?,
    var subCommentsCount  : Int = 0,
    @SerializedName(value = "objectId", alternate = ["id"])
    var objectId  : String?,
    var createdAt  : Long =0,
    var hotComment  : HotComment?,
    var staticImg  : ImageEntity?,
    var dynamicImg  : ImageEntity?,

    //扩展字段
    var sectionKey  : Int = 0,
    //主体Id 如果是评论动态，主体Id为动态ID，如果评论 评论，主体Id为被评论的评论Id，（重要！！！！）并且如果评论的是二级评论，那么主体id 为评论详情Id，mentionedId为被评论的二级评论Id
    var mainId:String?,

)
