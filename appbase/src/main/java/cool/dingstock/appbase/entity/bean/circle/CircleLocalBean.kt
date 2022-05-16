package cool.dingstock.appbase.entity.bean.circle

import androidx.annotation.IntegerRes

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/10  17:33
 */
data class CircleLocalBean(
        var name:String?=null,
        var iconRes:String?=null,
        var type:String?=null,
        var guideIconRes:String?=null,

        //本地
        @IntegerRes var iconIntRes:Int =0,
        @IntegerRes var guideIconIntRes:Int=0,
        var photoFilePath :String?=null,
        var select: Boolean=false
){

}