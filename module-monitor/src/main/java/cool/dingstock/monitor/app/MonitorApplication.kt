package cool.dingstock.calendar.app

import cool.dingstock.appbase.app.BaseDcApplication
import cool.mobile.account.initor.AccountInitor

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/15  10:25
 */
class MonitorApplication : BaseDcApplication(){
    override fun initModuleInitor() {
        AccountInitor.init(this)
    }

}