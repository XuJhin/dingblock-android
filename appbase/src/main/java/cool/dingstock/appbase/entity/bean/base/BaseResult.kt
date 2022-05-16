package cool.dingstock.appbase.entity.bean.base

import com.google.gson.annotations.SerializedName

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/15  11:57
 */
data class BaseResult<T>(
    var err: Boolean = true,
    var res: T? = null,
    var code: Int = 0,
    var msg: String = ""
) {
    constructor() : this(true, null, 0, "")
}

data class BasePageEntity<T>(
    @SerializedName("list", alternate = ["products", "posts"])
    var list: ArrayList<T>, val nextStr: String? = null, val pageNum: Int? = 0
) {
    val nextKey: Long? = 0
        get() {
            return field ?: -1
        }

}

data class BasePageStringEntity<T>(
    @SerializedName("list", alternate = ["products"])
    var list: ArrayList<T>, val nextKey: String? = "", val nextStr: String? = null
)