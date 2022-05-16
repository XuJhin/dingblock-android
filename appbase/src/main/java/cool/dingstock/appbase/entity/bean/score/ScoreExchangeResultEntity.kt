package cool.dingstock.appbase.entity.bean.score

data class ScoreExchangeResultEntity(
        val title: String,
        val desc: String,
        val imgUrl: String,
        val buttonStr: String,
        val type: Type,
        val code:String?,
        val link: String){
    enum class Type{
        vip,coupon,reality,achievement
    }
}