package cool.dingstock.appbase.widget.recyclerview.item;


import androidx.viewbinding.ViewBinding;

import java.util.Random;

/**
 * Description: TODO
 * Author: Shper
 * Version: V0.1 2017/7/9
 */
public abstract class BaseError<D, VB extends ViewBinding> extends BaseItem<D, VB> {

    private static final int BASE_TYPE_ERROR = 3200000 + new Random().nextInt(1000);

    public BaseError(D data) {
        super(data);
    }

    @Override
    public int getViewType() {
        int viewType = BASE_TYPE_ERROR;
        return viewType;
    }

    @Override
    public final void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
    }

    @Override
    public final void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
        onSetViewsData(holder);
    }

    public abstract void onSetViewsData(BaseViewHolder holder);

}
