package cool.dingstock.home.dagger


/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/15  17:35
 */
object HomeApiHelper {
    val apiHomeComponent: HomeComponent by lazy {
        DaggerHomeComponent.create()
    }
}