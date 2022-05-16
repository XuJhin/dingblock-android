package cool.dingstock.calendar.sneaker.index.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cool.dingstock.appbase.entity.bean.home.HomeBrandBean;
import cool.dingstock.appbase.widget.stickyheaders.SectioningAdapter;
import cool.dingstock.calendar.R;

public class HomeSneakerHeadViewHolder extends SectioningAdapter.HeaderViewHolder {
	
	TextView dateTxt;
	TextView tvDateMonth;
	View ingLayer;
	TextView ingTv;
	
	View anchor;
	private List<HomeBrandBean> brands;
	
	public HomeSneakerHeadViewHolder(View itemView) {
		super(itemView);
		tvDateMonth = itemView.findViewById(R.id.home_head_sneaker_date_month);
		dateTxt = itemView.findViewById(R.id.home_head_sneaker_date_txt);
		ingLayer = itemView.findViewById(R.id.product_title_ing_layer);
		ingTv = itemView.findViewById(R.id.ing_tv);
	}
	
	public void setAnchor(View anchor) {
		this.anchor = anchor;
	}
	
	public void onBindItemViewHolder(int sectionIndex, String header, List<HomeBrandBean> brands) {
		String dateDay = "";
		String dateMonth = "";
		if (header.contains("/")) {
			String[] split = header.split("/");
			if (split.length > 0) {
				dateDay = split[0];
				dateMonth = split[split.length - 1];
			}
		}
		if (TextUtils.isEmpty(dateDay)) {
			dateTxt.setText(header);
			ingTv.setText(header);
			tvDateMonth.setVisibility(View.GONE);
			dateTxt.setVisibility(View.GONE);
			ingLayer.setVisibility(View.VISIBLE);
		} else {
			dateTxt.setText(dateDay);
			tvDateMonth.setText("/" + dateMonth);
			dateTxt.setVisibility(View.VISIBLE);
			tvDateMonth.setVisibility(View.VISIBLE);
			ingLayer.setVisibility(View.GONE);
		}
	}
	
}
