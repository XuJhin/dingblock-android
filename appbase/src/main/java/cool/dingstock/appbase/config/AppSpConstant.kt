package cool.dingstock.appbase.config

/**
 * 同意存放App 配置信息
 */
object AppSpConstant {

    /**
     * 该用户是否已显示过“推荐关注页面”
     * 已显示过将不再显示
     * 使用时 会在后面追加用户id 如show_recommend_1001
     */
    const val RECOMMEND_SHOW = "show_recommend"


    //监控设置
    const val MONITOR_TAB_KEY = "monitorTabFirst"
    const val MONITOR_DIRECT_LINK = "monitor_direct_link"

    //通知设置
    const val DISTURB_START_TIME = "disturb_start_time"
    const val DISTURB_END_TIME = "disturb_end_time"
    const val DISTURB_SWITCH = "disturb_switch"

    //视频播放设置
    const val WIFI_AUTO_PLAY = "wifi_auto_play"
    const val CELLULAR_AUTO_PLAY = "cellular_auto_play"
    const val DEFAULT_OPEN_SOUND = "default_open_sound"

    //Gif设置
    const val WIFI_AUTO_GIF = "wifi_auto_gif"
    const val CELLULAR_AUTO_GIF = "cellular_auto_gif"

    /**
     * App设置
     */
    interface AppConfig {
        companion object {
            const val MONITOR_TAB_KEY: String = "monitorTabFirst"
        }

    }

    /**
     * 支付设置
     */
    interface PayConfig {

    }

    /**
     * 监控设置
     */
    interface MonitorConfig {

    }

    /**
     * 通知设置
     */
    interface NotificationConfig {}

    /**
     * 广告配置
     */
    interface AdConfig {}
}