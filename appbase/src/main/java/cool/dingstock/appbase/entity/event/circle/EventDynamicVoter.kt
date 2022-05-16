package cool.dingstock.appbase.entity.event.circle

import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.circle.VoteOptionEntity

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/17  14:49
 */
class EventDynamicVoter (val dynamicId:String,val votes : MutableList<VoteOptionEntity>)