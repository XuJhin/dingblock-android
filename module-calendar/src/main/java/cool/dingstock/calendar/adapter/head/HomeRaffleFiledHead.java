package cool.dingstock.calendar.adapter.head;

import cool.dingstock.appbase.widget.recyclerview.item.BaseHead;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.databinding.HomeHeadRaffleFieldBinding;

public class HomeRaffleFiledHead extends BaseHead<String, HomeHeadRaffleFieldBinding> {


    public HomeRaffleFiledHead(String data) {
        super(data);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.home_head_raffle_field;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionHeadPosition) {

    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionHeadPosition) {
        viewBinding.homeHeadRaffleFieldTxt.setText(getData());
    }
}
