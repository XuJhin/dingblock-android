package cool.dingstock.appbase.entity.event.shoes

import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailCommentsBean


/**
 * 类名：EventSeriesComment
 * 包名：cool.dingstock.appbase.entity.event.shoes
 * 创建时间：2022/1/7 10:54 上午
 * 创建人： WhenYoung
 * 描述：
 **/
data class EventSeriesComment(val seriesId: String, val bean: CircleDynamicDetailCommentsBean) {
}

data class EventCommitRatingScore(val seriesId: String,val rating: Int) {

}