package cool.dingstock.appbase.entity.bean.search

import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean
import cool.dingstock.appbase.entity.bean.home.HomeProduct
import cool.dingstock.appbase.entity.bean.mine.AccountInfoBriefEntity
import cool.dingstock.appbase.entity.bean.topic.TalkTopicEntity

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/23  15:18
 */
data class SearchResultEntity(
        var post: ArrayList<CircleDynamicBean>? = null,
        var user: ArrayList<AccountInfoBriefEntity>? = null,
        var talk: ArrayList<TalkTopicEntity>? = null,
        var product: ArrayList<HomeProduct>? = null,

)
