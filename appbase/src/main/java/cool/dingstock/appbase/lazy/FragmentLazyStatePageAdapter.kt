package cool.dingstock.appbase.lazy

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

open class FragmentLazyStatePageAdapter(
        fragmentManager: FragmentManager,
        private val fragments: MutableList<Fragment>,
        private val titles: MutableList<String>
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int) = titles[position]

    override fun getItemPosition(`object`: Any) = POSITION_NONE
}