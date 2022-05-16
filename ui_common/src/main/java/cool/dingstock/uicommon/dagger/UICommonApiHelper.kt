package cool.dingstock.uicommon.dagger

/**
 * @author WhenYoung
 *  CreateAt Time 2020/10/15  17:35
 */
object UICommonApiHelper {
    val apiPostComponent: UICommonComponent by lazy {
        DaggerUICommonComponent.create()
    }
}