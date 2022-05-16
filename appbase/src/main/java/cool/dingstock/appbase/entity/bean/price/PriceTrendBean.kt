package cool.dingstock.appbase.entity.bean.price;






data class PriceTrendBean (

    var lowest  : Int=0,
    var date  : Long=0,
    var highest  : Int=0,

    //本地添加
    var platformBean  : PlatformBean?

){
    fun getAveragePrice() :Int?{
        if (lowest == 0 || highest == 0) {
            return (lowest?.and(highest?:0))
        }
        return (lowest?.and (highest?:0))?:0/2
    }
}
