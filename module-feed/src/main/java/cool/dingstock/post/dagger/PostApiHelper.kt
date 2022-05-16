package cool.dingstock.post.dagger

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/15  17:35
 */
object PostApiHelper {
    val apiPostComponent: PostComponent by lazy {
        DaggerPostComponent.create()
    }
}