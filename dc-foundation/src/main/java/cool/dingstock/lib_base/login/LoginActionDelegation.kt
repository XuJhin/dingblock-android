package cool.dingstock.lib_base.login

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/12  9:56
 */
abstract class LoginActionDelegation {

    abstract fun getLoginClazz(): Class<out ILoginUser>

    abstract fun isLogin(): Boolean


}