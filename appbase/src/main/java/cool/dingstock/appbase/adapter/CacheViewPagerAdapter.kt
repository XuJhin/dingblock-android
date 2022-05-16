package cool.dingstock.appbase.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.PagerAdapter
import cool.dingstock.appbase.R
import java.util.*

/**
 * 类名：CacheViewPagerAdapter
 * 包名：cool.dingstock.appbase.adapter
 * 创建时间：2022/1/4 10:36 上午
 * 创建人： WhenYoung
 * 描述：
 **/
abstract class CacheViewPagerAdapter<T, VB : ViewBinding>(
    val context: Context,
    val data: ArrayList<T>
) :
    PagerAdapter() {

    var scale: Float = 1f
    val mViewCache = LinkedList<VB>()
    var isNeedRefresh = false

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val vb: VB
        if (mViewCache.size == 0) {
            vb = onCreateView(container)
        } else {
            vb = mViewCache.removeFirst()
        }
        vb.root.setTag(R.id.cache_vp_tag, vb)
        onItemBind(position, data[position], vb)
        container.addView(vb.root)
        return vb.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        val contentView = any as View
        container.removeView(contentView)
        val vb = contentView.getTag(R.id.cache_vp_tag) as VB
        onItemDestroy(position, data[position], vb)
        mViewCache.add(vb)
    }

    override fun getItemPosition(`object`: Any): Int {
        return if (isNeedRefresh) {
            POSITION_NONE
        } else {
            super.getItemPosition(`object`)
        }
    }

    abstract fun onItemDestroy(position: Int, item: T, vb: VB)

    abstract fun onItemBind(position: Int, item: T, vb: VB)

    abstract fun onCreateView(group: ViewGroup): VB

    fun setList(list: ArrayList<T>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }
}