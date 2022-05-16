package cool.dingstock.appbase.mvvm.status

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/8  11:34
 */
sealed class LoadingDialogStatus(val msg: String) {

    class Loading(msg: String = "加载中…") : LoadingDialogStatus(msg)

    class Success(msg: String = "请求成功…") : LoadingDialogStatus(msg)

    class Error(msg: String = "请求失败…") : LoadingDialogStatus(msg)

    object Hide : LoadingDialogStatus("")

}