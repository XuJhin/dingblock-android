package cool.dingstock.appbase.widget.recyclerview.item;

import android.view.View;

import androidx.viewbinding.ViewBinding;


/**
 * Description: TODO
 * Author: Shper
 * Version: V0.1 2017/7/9
 */
public abstract class BaseLoadMore<D, VB extends ViewBinding> extends BaseItem<D, VB> {

    private static final int BASE_TYPE_LOAD_MORE = 3300000;

    public BaseLoadMore(D data) {
        super(data);
    }

    @Override
    public int getViewType() {
        int viewType = BASE_TYPE_LOAD_MORE;
        return viewType;
    }

    @Override
    public final void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionItemPosition) {
        holder.getItemView().setVisibility(View.GONE);
    }

    @Override
    public final void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionItemPosition) {
        holder.getItemView().setVisibility(View.VISIBLE);
        onSetViewsData(holder);
    }

    public abstract void onSetViewsData(BaseViewHolder holder);

    public abstract void startAnim();

    public abstract void stopAnim();

    public abstract void end();
}
