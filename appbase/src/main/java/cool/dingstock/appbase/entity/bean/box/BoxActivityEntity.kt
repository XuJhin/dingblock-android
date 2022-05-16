package cool.dingstock.appbase.entity.bean.box


/**
 * 类名：BoxActivityEntity
 * 包名：cool.dingstock.appbase.entity.bean.box
 * 创建时间：2021/10/15 2:52 下午
 * 创建人： WhenYoung
 * 描述：
 **/
data class BoxActivityEntity(
    /* 2.9.8 start */
    val rightEnabled: Boolean? = false,/* 右侧按钮是否开启 */
    val rightIcon: String? = null,/* 右侧图标 */
    val rightLink: String? = null, /* 右侧连接 */
    /* 2.9.8 end */
    val icon: String? = null,
    val link: String? = null,
    val content: String? = null
) {
}