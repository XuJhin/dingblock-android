package cool.dingstock.appbase.laucher

import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.launch.DcITask
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager

class MediaPlayerTask: DcITask() {
    override fun run() {
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
        ConfigSPHelper.getInstance().save("videoCellularShowToast", true)
        ConfigSPHelper.getInstance().save("videoNoCellularShowToast", true)
        BaseLibrary.getInstance().context?.let {
            GSYVideoManager.instance().isNeedMute = !SettingHelper.getInstance().isDefaultOpenSound
        }
    }
}