package cool.dingstock.appbase.entity.bean.home.fashion

import com.google.gson.annotations.SerializedName

data class FashionEntity(
        val id: String,
        val bgUrl: String,
        val createAt: Long,
        val name: String,
        val userId: String,
        val desc: String,
        val imageMap: ImageEntity?,
        val isDefaultImg: Boolean?,
        val isMutual: Boolean?,
        val imageUrl: String,
        val talkId: String,
        val blocked: Boolean,
        var selected: Boolean = false,
        val simpleDesc: String,
)

data class ImageEntity(
        val thumbnail: String,
        val url: String
)

data class FashionListEntity(
        @SerializedName(value = "result",alternate = ["list"])
        val result: MutableList<FashionEntity> = arrayListOf(),
        val nextKey: Long = 0,
        val nextStr: String? = ""
)