package cool.dingstock.appbase.mvvm.status

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/8  11:34
 */
sealed class ViewStatus(val msg: String) {

    class Loading(msg: String) : ViewStatus(msg)

    class Empty(msg: String) : ViewStatus(msg)

    class Success(msg: String) : ViewStatus(msg)

    class Error(msg: String) : ViewStatus(msg)

}