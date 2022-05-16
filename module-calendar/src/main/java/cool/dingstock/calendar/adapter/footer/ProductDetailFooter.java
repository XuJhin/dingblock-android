package cool.dingstock.calendar.adapter.footer;

import android.view.ViewGroup;

import cool.dingstock.appbase.widget.recyclerview.item.BaseFoot;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.databinding.HomeFooterSpaceBinding;

public class ProductDetailFooter extends BaseFoot<Integer, HomeFooterSpaceBinding> {

    public ProductDetailFooter(Integer data) {
        super(data);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.home_footer_space;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {

    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
        ViewGroup.LayoutParams layoutParams =
                holder.getItemView().getLayoutParams();
        layoutParams.height = getData();
        holder.getItemView().setLayoutParams(layoutParams);
    }
}
