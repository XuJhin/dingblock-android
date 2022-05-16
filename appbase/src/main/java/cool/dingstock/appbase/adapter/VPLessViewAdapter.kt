package cool.dingstock.appbase.adapter

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import cool.dingstock.appbase.mvp.BaseFragment


/**
 * 当viewPager的数量较少时,使用此Adapter
 * 由于传递了BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT，
 * 不可见的fragment将不会执行onResume方法
 * 在onResume中作网络请求，即可实现懒加载
 */
class VPLessViewAdapter(fm: FragmentManager, private val fragmentList: MutableList<BaseFragment>)
    : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun saveState(): Parcelable? {
        return null
    }
}