package cool.dingstock.home.adapter.item;

import cool.dingstock.appbase.entity.bean.home.HomeGotemBean;
import cool.dingstock.appbase.imageload.GlideHelper;
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.home.R;
import cool.dingstock.home.databinding.HomeItemGotemBinding;

public class HomeGotemItem extends BaseItem<HomeGotemBean, HomeItemGotemBinding> {

    public HomeGotemItem(HomeGotemBean data) {
        super(data);
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.home_item_gotem;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {

    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
        GlideHelper.INSTANCE.loadRadiusImage(getData().getImageUrl(),viewBinding.homeItemGotemIv,getContext(),0);
    }
}
