package cool.dingstock.calendar.sneaker.index.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cool.dingstock.appbase.annotations.LoadMoreStatus;
import cool.dingstock.appbase.entity.bean.home.HomeBrandBean;
import cool.dingstock.appbase.entity.bean.home.HomeProduct;
import cool.dingstock.appbase.entity.bean.home.HomeProductGroup;
import cool.dingstock.appbase.entity.bean.home.HomeTypeBean;
import cool.dingstock.appbase.widget.stickyheaders.SectioningAdapter;
import cool.dingstock.calendar.R;
import cool.dingstock.uicommon.vh.CommonEmptyViewHolder;
import cool.dingstock.uicommon.vh.CommonLoadMoreHolder;
import cool.dingstock.calendar.sneaker.index.viewholder.HomeSneakerHeadViewHolder;
import cool.dingstock.calendar.sneaker.index.viewholder.HomeSneakerItemViewHolder;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.Logger;


public class SneakerSectionAdapter extends SectioningAdapter {

    private static final int NORMAL_TYPE = 0;
    private static final int LOADING_TYPE = 1;
    private static final int EMPTY = 2;
    private static final int FOOTER_TYPE = 3;
    private @LoadMoreStatus
    int mLoadMoreStatus;

    View anchor;

    private final List<HomeProductGroup> allProductList = new ArrayList<>();
    private CommonLoadMoreHolder loadMoreHolder;
    private final List<HomeBrandBean> allBrand = new ArrayList<>();
    private final List<HomeTypeBean> allType = new ArrayList<>();
    private final Set<String> brandIdSet = new HashSet<>();


    public void setAnchor(View anchor) {
        this.anchor = anchor;
    }

    public SneakerSectionAdapter() {
    }

    public void setData(List<HomeProductGroup> productGroupList) {
        if (CollectionUtils.isNotEmpty(productGroupList)) {
            productGroupList.get(productGroupList.size() - 1).getProducts().add(HomeProduct.Companion.newInstance());
            this.allProductList.addAll(productGroupList);
            initBrands(allProductList);
        } else {
            this.allProductList.clear();
            this.allBrand.clear();
            this.allType.clear();
            this.brandIdSet.clear();
        }
        this.notifyAllSectionsDataSetChanged();
    }

    private void initBrands(List<HomeProductGroup> allProductList) {
        for (HomeProductGroup homeProductGroup : allProductList) {
            if (homeProductGroup.getTypes() != null && homeProductGroup.getTypes().size() > 0) {
                for (String type: homeProductGroup.getTypes()) {
                    boolean contains = false;
                    for (HomeTypeBean typeBean: allType) {
                        if (TextUtils.equals(type, typeBean.getName())) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        allType.add(new HomeTypeBean(type, true));
                    }
                }
            }
            List<HomeBrandBean> brands = homeProductGroup.getBrands();
            if (brands == null || brands.size() <= 0) {
                continue;
            }
            for (HomeBrandBean homeBrandBean : brands) {
                if (homeBrandBean == null) {
                    continue;
                }
                String id = homeBrandBean.getId();
                if (id == null) {
                    id = homeBrandBean.getObjectId();
                }
                if (id == null) {
                    id = "";
                }
                if (brandIdSet.contains(id)) {
                    continue;
                }
                brandIdSet.add(homeBrandBean.getId());
                allBrand.add(homeBrandBean);
            }
        }
    }

    public List<HomeBrandBean> getAllBrand() {
        return allBrand;
    }

    public List<HomeTypeBean> getAllType() {
        return allType;
    }

    @Override
    public int getNumberOfSections() {
        if (CollectionUtils.isEmpty(allProductList)) {
            return 0;
        }
        return allProductList.size() + 1;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        if (CollectionUtils.isEmpty(allProductList)) {
            return 0;
        }
        if (sectionIndex == allProductList.size()) {
            return 1;
        }
        return allProductList.get(sectionIndex).getProducts().size();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        if (CollectionUtils.isEmpty(allProductList)) {
            return false;
        }
        if (sectionIndex > allProductList.size() - 1) {
            return false;
        }
        return !TextUtils.isEmpty(allProductList.get(sectionIndex).getHeader());
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex) {
        if (allProductList.size() == 0) {
            return EMPTY;
        }
        if (sectionIndex == allProductList.size() && itemIndex == 0) {
            return LOADING_TYPE;
        }
        if (allProductList.get(sectionIndex).getProducts().get(itemIndex).isFooter()) {
            return FOOTER_TYPE;
        }
        return NORMAL_TYPE;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        switch (itemType) {
            case EMPTY:
                return new CommonEmptyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.common_recycler_empty, parent, false));
            case NORMAL_TYPE:
                return new HomeSneakerItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_item_sneaker,
                                parent, false));
            case LOADING_TYPE:
                return new CommonLoadMoreHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.common_recycler_load_more,
                                parent, false));
            case FOOTER_TYPE:
                return new CommonLoadMoreHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_item_footer_layout,
                                parent, false));
            default:
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        switch (itemType) {
            case NORMAL_TYPE:
                HomeProductGroup homeProductGroup = allProductList.get(sectionIndex);
                if (null == homeProductGroup) {
                    return;
                }
                List<HomeProduct> products = homeProductGroup.getProducts();
                if (CollectionUtils.isEmpty(products)) {
                    return;
                }
                HomeProduct homeProduct = products.get(itemIndex);
                HomeSneakerItemViewHolder homeSneakerItemViewHolder = (HomeSneakerItemViewHolder) viewHolder;
                homeSneakerItemViewHolder.onBindItemViewHolder(homeProduct);
                break;
            case LOADING_TYPE:
                loadMoreHolder = (CommonLoadMoreHolder) viewHolder;
                loadMoreHolder.bind(mLoadMoreStatus);
                Logger.d("onBindItemViewHolder Loading ");
                break;
            case EMPTY:
                break;
            default:
        }
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        HomeSneakerHeadViewHolder homeSneakerHeadViewHolder =
                new HomeSneakerHeadViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_head_sneaker_date, parent, false));
        homeSneakerHeadViewHolder.setAnchor(anchor);
        return homeSneakerHeadViewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        Log.e("onBindHeaderViewHolder", "sectionIndex" + sectionIndex);
        HomeProductGroup homeProductGroup = allProductList.get(sectionIndex);
        if (null == homeProductGroup) {
            Logger.e("onBindHeaderViewHolder homeProductGroup null sectionIndex=" + sectionIndex);
            return;
        }
        HomeSneakerHeadViewHolder sneakerHeadViewHolder = (HomeSneakerHeadViewHolder) viewHolder;
        sneakerHeadViewHolder.onBindItemViewHolder(sectionIndex, homeProductGroup.getHeader(), allBrand);
    }

    public void showLoadMore() {
        if (this.loadMoreHolder != null) {
            this.loadMoreHolder.startAnim();
        }
        this.mLoadMoreStatus = LoadMoreStatus.LOADING;
    }


    public void hideLoadMore() {
        if (this.loadMoreHolder != null) {
            this.loadMoreHolder.stopAnim();
        }
        this.mLoadMoreStatus = LoadMoreStatus.IDLE;
    }

    public void endLoadMore() {
        if (this.loadMoreHolder != null) {
            this.loadMoreHolder.end();
        }
        this.mLoadMoreStatus = LoadMoreStatus.END;
    }

}
