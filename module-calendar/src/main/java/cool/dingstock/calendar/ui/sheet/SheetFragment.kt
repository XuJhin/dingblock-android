package cool.dingstock.calendar.ui.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import cool.dingstock.appbase.adapter.ViewPage2FragmentAdapter
import cool.dingstock.calendar.databinding.BottomSheetHeavyReleaseBinding
import cool.dingstock.calendar.ui.pager.CalendarFragmentHeavyRaffle
import cool.dingstock.calendar.ui.raffle.HeavyRaffleVM
import cool.dingstock.uikit.bottomsheet.common.BottomSheetFragment

class SheetFragment : BottomSheetFragment() {
    companion object {
        const val TAG = "RaffleSheet"
    }

    private var _binding: BottomSheetHeavyReleaseBinding? = null
    private val binding: BottomSheetHeavyReleaseBinding
        get() = _binding!!

    private val viewModel: HeavyRaffleVM by activityViewModels()
    private val fragmentList: MutableList<Fragment> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = BottomSheetHeavyReleaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        observerDataChange()
    }

    private fun observerDataChange() {
        viewModel.vpPosition.observe(this, Observer {
            if (binding.vpHeavyRaffles.currentItem == it) {
                return@Observer
            } else {
                binding.vpHeavyRaffles.setCurrentItem(it, false)
            }
        })
//        viewModel.topRvPosition.observe(this, Observer {
//            if (binding.vpHeavyRaffles.currentItem == it.y) {
//                return@Observer
//            } else {
//                binding.vpHeavyRaffles.setCurrentItem(it.y, false)
//            }
//        })
    }

    private fun setupViewPager() {
        val viewPagerAdapter = ViewPage2FragmentAdapter(childFragmentManager, lifecycle)
        binding.vpHeavyRaffles.apply {
            adapter = viewPagerAdapter

            viewModel.dataList.forEach {
                fragmentList.add(CalendarFragmentHeavyRaffle.instant(it.id!!))
            }
            viewPagerAdapter.addFragment(fragmentList)
            viewPagerAdapter.notifyDataSetChanged()

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.notifyRvScroll(position)
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }
            })
        }
    }

}


