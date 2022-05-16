package cool.dingstock.appbase.widget.photoselect.entity
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/13  15:18
 */
@Parcelize
data class PhotoBean(var id:String?=null
                     ,var path:String?=null
                     ,var isChoose:Boolean = false
                     ,var fileName:String? = null
                     ,var imageUri:Uri? =null
) : Parcelable {
    /**
     * @param path
     * @param fileName
     * @param uri
     */
    constructor(path: String, fileName: String, uri: Uri?):this() {
        id = path + fileName
        this.path = path
        this.fileName = fileName
        imageUri = uri
    }

    constructor(path: String, fileName: String):this() {
        id = path + fileName
        this.path = path
        this.fileName = fileName
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val (id1) = o as PhotoBean
        return id == id1
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    fun javaCopy():PhotoBean{
        return copy()
    }

}