package cool.dingstock.appbase.entity.event.circle

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/17  14:57
 */
class EventFollowerChange (val userId:String, var isFollowed: Boolean,val followCount : Int = 0,val source:Source = Source.Other){
    enum class Source{
        HomePageFollow,Other
    }
}