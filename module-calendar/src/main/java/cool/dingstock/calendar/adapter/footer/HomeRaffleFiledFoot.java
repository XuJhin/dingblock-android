package cool.dingstock.calendar.adapter.footer;

import cool.dingstock.appbase.widget.recyclerview.item.BaseFoot;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.databinding.HomeFootRaffleFieldBinding;


public class HomeRaffleFiledFoot extends BaseFoot<String, HomeFootRaffleFieldBinding> {

    public interface ActionClickListener{
        void onSendClick();
    }

    private ActionClickListener mListener;

    public void setMListener(ActionClickListener mListener) {
        this.mListener = mListener;
    }

    public HomeRaffleFiledFoot(String data) {
        super(data);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.home_foot_raffle_field;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionFootPosition) {


    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionFootPosition) {
        viewBinding.homeFootRaffleFieldEndTxt.setOnClickListener(v -> {
            if (null!=mListener){
                mListener.onSendClick();
            }
        });
    }
}
