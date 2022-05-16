package cool.dingstock.appbase.entity.bean.home

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/24  18:13
 */
@Parcelize
data class CommonImgDialogEntity(
        val startAt:Long,
        val endAt:Long,
        val linkUrl:String,
        val width:Int,
        val height:Int,
        val bgImgUrl:String,
        val weight:Int
) : Parcelable