package cool.dingstock.appbase.config

import cool.dingstock.lib_base.stroage.ConfigSPHelper

object AppConfigManager {

    /**
     * 用户自己控制
     */
    var userSelfSetting: Boolean = false

    var userTurnOnVoice: Boolean = false

    object AppSetting {

        /**
         * 是否将监控设为主页
         */
        var isMonitorStart: Boolean
            get() {
                return ConfigSPHelper.getInstance()
                    .getBoolean(AppSpConstant.AppConfig.MONITOR_TAB_KEY)
            }
            set(value) {
                ConfigSPHelper.getInstance().save(AppSpConstant.AppConfig.MONITOR_TAB_KEY, value)
            }
    }
}
