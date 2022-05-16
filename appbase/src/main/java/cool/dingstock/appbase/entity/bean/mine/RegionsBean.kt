package cool.dingstock.appbase.entity.bean.mine;


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Objects;





@Parcelize
data class RegionsBean (
    /**
     * name : 西安
     * sizeIndex : 17
     * type : city
     * hot : true
     * objectId : 2LUYlBeYqm
     * subscribed : false
     */
    var name  : String?,
    var index  : Float?,
    var type  : String?,
    var hot  : Boolean?,
    var objectId  : String?,
    var iconUrl  : String?,
    var group  : String?,
    var district  : String?,

    var selected  : Boolean?
) : Parcelable {
    override fun hashCode(): Int {
        return Objects.hash(objectId)
    }
}


