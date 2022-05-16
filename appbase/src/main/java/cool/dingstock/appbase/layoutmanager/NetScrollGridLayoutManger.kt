package cool.dingstock.appbase.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/27  18:01
 */
class NetScrollGridLayoutManger(context: Context,spanCount:Int,orientation : Int,reset: Boolean) : GridLayoutManager(context,spanCount,orientation,reset){
    var isScrollEnabled = true

    override fun canScrollHorizontally(): Boolean {
        return isScrollEnabled
    }

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled
    }


}