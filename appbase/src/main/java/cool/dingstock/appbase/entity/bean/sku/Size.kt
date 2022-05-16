package cool.dingstock.appbase.entity.bean.sku

data class Size(
    var selected: Boolean,
    val size: String,
    val isMany:Boolean = false
)


data class SenderAddress(var name:String,val isMany:Boolean = false)