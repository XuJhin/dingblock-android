package cool.dingstock.appbase.entity.bean.party

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/4  17:44
 *
 *  {
"avatarUrl": "http://oss-community.dingstock.net/avatar/160775889_1605257724831.png",
"nickName": "马保国",
"imageUrl": "https://oss.dingstock.net/website/app/activity/xiaoxili/DB4612_300.png",
"name": "Air Jordan 1 High OG WMNS“Lucky Green”",
"userId": "sdsNVyNf0z"
}
 *
 */
@Parcelize
data class PartyDialogEntity(var avatarUrl:String?=null
                             ,var  nickName:String?=null
                             ,var  title:String?=null
                             ,var  button:String?=null
                             ,var imageUrl:String?=null
                             ,var name:String?=null
                             ,var userId:String?=null
                             ,var link:String?=null
                             ) : Parcelable