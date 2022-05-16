package cool.mobile.account.dagger

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/15  17:35
 */
object AccountApiHelper {
    val apiAccountComponent:ApiAccountComponent by lazy {
        DaggerApiAccountComponent.create()
    }
}