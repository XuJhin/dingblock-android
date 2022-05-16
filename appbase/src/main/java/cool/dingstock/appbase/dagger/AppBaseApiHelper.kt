package cool.dingstock.appbase.dagger

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/24  15:36
 */
object AppBaseApiHelper {
    val appBaseComponent : AppBaseComponent by lazy {
        DaggerAppBaseComponent.create()
    }
}
