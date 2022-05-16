package cool.dingstock.uicommon.helper

import cool.dingstock.appbase.helper.TimeTagHelper

class SmsDrawHelper {
    private var isShowSms = false
    private var checkd = false


    fun showEndSmsDraw(){
        TimeTagHelper.updateTimeTag(TimeTagHelper.smsDrawHint, System.currentTimeMillis())
    }

    fun checkEnableShowSms():Boolean{
        if(checkd){
            return isShowSms
        }
        checkd = true
        isShowSms = TimeTagHelper.checkTimeTag(TimeTagHelper.smsDrawHint,TimeTagHelper.TimeTag.APP_LIFE)
        return isShowSms
    }

}