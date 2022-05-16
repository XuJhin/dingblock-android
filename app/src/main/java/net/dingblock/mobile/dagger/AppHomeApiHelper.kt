package net.dingblock.mobile.dagger

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/15  17:35
 */
object AppHomeApiHelper {
    val apiHomeComponent: AppHomeComponent by lazy {
        DaggerAppHomeComponent.create()
    }
}