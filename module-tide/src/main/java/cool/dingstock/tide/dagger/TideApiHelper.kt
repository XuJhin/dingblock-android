package cool.dingstock.tide.dagger

/**
 * @author wangjiang
 *  CreateAt Time 2021/6/1  10:19
 */
object TideApiHelper {
    val apiShopComponent: TideComponent by lazy {
        DaggerTideComponent.create()
    }
}