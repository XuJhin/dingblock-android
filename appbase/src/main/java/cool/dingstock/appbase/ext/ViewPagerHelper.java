package cool.dingstock.appbase.ext;

import androidx.viewpager2.widget.ViewPager2;

import net.lucode.hackware.magicindicator.MagicIndicator;

public class ViewPagerHelper {
	public static void bind(final MagicIndicator magicIndicator, ViewPager2 viewPager) {
		viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
			
			@Override
			public void onPageSelected(int position) {
				magicIndicator.onPageSelected(position);
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				magicIndicator.onPageScrollStateChanged(state);
			}
		});
	}
}
