package cool.dingstock.appbase.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import cool.dingstock.lib_base.util.CollectionUtils

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/28  11:21
 */
class CommonWidthLoopVpAdapter(val viewList: ArrayList<View>) : PagerAdapter(){
    var width = 0


    override fun getCount(): Int {
        if (CollectionUtils.isEmpty(viewList)) {
            return 0
        }
        if (viewList.size > 1) {
            return Int.MAX_VALUE
        }
        return if (viewList.size == 1) {
            Int.MAX_VALUE
        } else 0
    }

    override fun getItemPosition(`object`: Any): Int {
        return viewList.indexOf(`object`)
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    }


    override fun instantiateItem(container: ViewGroup, position: Int):Any{
        //对ViewPager页号求模取出View列表中要显示的项
        var position = position
        position %= viewList.size
        if (position < 0) {
            position += viewList.size
        }
        val view = viewList[position]
        val vp = view.parent
        if (vp != null) {
            val parent = vp as ViewGroup
            parent.removeView(view)
        }
        container.addView(view)
        return view
    }


    override fun getPageWidth(position: Int): Float {
        if(width != 0){
            return width.toFloat()
        }
        return super.getPageWidth(position)
    }

}