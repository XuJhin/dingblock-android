package cool.dingstock.uicommon.helper

import cool.dingstock.appbase.helper.DarkMode
import cool.dingstock.appbase.helper.DarkModeHelper
import cool.dingstock.appbase.helper.TimeTagHelper

class AnimationDrawHelper {
    //    var isFisrtShow  = true
    private var isShowSms = false
    private var smsChecked = false

    private var isShowFlash = false
    private var flashCheckd = false

    private var isDarkMode = false

    private val smsMap by lazy {
        HashMap<String, Boolean>()
    }

    private val flashMap by lazy {
        HashMap<String, Boolean>()
    }

    fun isDarkMode(): Boolean {
        return DarkMode.valueOf(DarkModeHelper.getDarkMode().name) == DarkMode.DARK_MODE
    }

    fun showEndSmsDraw() {
        TimeTagHelper.updateTimeTag(TimeTagHelper.smsDrawHint, System.currentTimeMillis())
    }

    fun checkEnableShowSms(): Boolean {
        if (smsChecked) {
            return isShowSms
        }
        smsChecked = true
        isShowSms =
            TimeTagHelper.checkTimeTag(TimeTagHelper.smsDrawHint, TimeTagHelper.TimeTag.ONCE_DAY)
        return isShowSms
    }

    fun showEndFlashDraw() {
        TimeTagHelper.updateTimeTag(TimeTagHelper.flashDrawHint, System.currentTimeMillis())
    }

    fun checkEnableShowFlash(): Boolean {
        if (flashCheckd) {
            return isShowFlash
        }
        flashCheckd = true
        isShowFlash =
            TimeTagHelper.checkTimeTag(TimeTagHelper.flashDrawHint, TimeTagHelper.TimeTag.ONCE_DAY)
        return isShowFlash
    }

    fun putShowSms(id: String, boolean: Boolean) {
        smsMap[id] = boolean
    }

    fun checkEnableShowSms(id: String?): Boolean {
        return smsMap[id] ?: false
    }


    fun putShowFlash(id: String, boolean: Boolean) {
        flashMap[id] = boolean
    }

    fun checkEnableShowFlash(id: String?): Boolean {
        return flashMap[id] ?: false
    }

    fun clear() {
        flashMap.clear()
        smsMap.clear()
    }

}