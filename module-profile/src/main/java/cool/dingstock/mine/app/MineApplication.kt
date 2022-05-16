package cool.dingstock.mine.app

import cool.dingstock.appbase.app.BaseDcApplication
import cool.dingstock.lib_base.util.Logger
import cool.mobile.account.initor.AccountInitor

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/15  10:25
 */
class MineApplication : BaseDcApplication(){
    override fun initModuleInitor() {
        Logger.e("MineApplication")
        AccountInitor.init(this)
    }

}