package cool.dingstock.uicommon.helper
import android.util.Log
import cool.dingstock.appbase.helper.TimeTagHelper
/**
 * @author wangjiang
 *  CreateAt Time 2021/5/25  14:20
 */


class VipBubHelper {
    private var isShowVipBub = false
    private var bubCheckd = false


    fun showEndBubDraw() {
        TimeTagHelper.updateTimeTag(TimeTagHelper.vipBubHint, System.currentTimeMillis())
//        TimeTagHelper.updateTimeTagByHashMap(TimeTagHelper.vipBubHint, id, System.currentTimeMillis())
    }

    fun checkEnableShowBub(): Boolean {
        if (bubCheckd) {
            return isShowVipBub
        }
        bubCheckd = true
        isShowVipBub = TimeTagHelper.checkTimeTag(TimeTagHelper.vipBubHint, TimeTagHelper.TimeTag.ONCE_DAY)
//        isShowVipBub = TimeTagHelper.checkTimeTagByHashMap(TimeTagHelper.vipBubHint, id, TimeTagHelper.TimeTag.ONCE_DAY)
        return isShowVipBub
    }
}