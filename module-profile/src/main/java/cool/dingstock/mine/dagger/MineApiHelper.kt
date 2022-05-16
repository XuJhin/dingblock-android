package cool.dingstock.mine.dagger

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/15  17:35
 */
object MineApiHelper {
    val apiMineComponent: MineComponent by lazy {
        DaggerMineComponent.create()
    }
}