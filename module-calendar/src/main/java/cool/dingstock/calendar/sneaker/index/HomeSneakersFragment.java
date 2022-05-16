package cool.dingstock.calendar.sneaker.index;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cool.dingstock.appbase.constant.HomeBusinessConstant;
import cool.dingstock.appbase.constant.HomeConstant;
import cool.dingstock.appbase.constant.UTConstant;
import cool.dingstock.appbase.entity.bean.common.DateBean;
import cool.dingstock.appbase.entity.bean.home.CalenderFeaturedEntity;
import cool.dingstock.appbase.entity.bean.home.CalenderProductEntity;
import cool.dingstock.appbase.entity.bean.home.HomeCategoryBean;
import cool.dingstock.appbase.entity.bean.home.HomeProductGroup;
import cool.dingstock.appbase.entity.event.home.EventBrandChooseChange;
import cool.dingstock.appbase.mvp.lazy.LazyDcFragment;
import cool.dingstock.appbase.ut.UTHelper;
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel;
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter;
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem;
import cool.dingstock.appbase.widget.stickyheaders.StickyHeaderLayoutManager;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.databinding.HomeFragmentSneakersLayoutBinding;
import cool.dingstock.calendar.sneaker.index.adapter.SneakerSectionAdapter;
import cool.dingstock.calendar.sneaker.index.item.CalenderRecommendItem;
import cool.dingstock.lib_base.thread.ThreadPoolHelper;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.Logger;
import cool.dingstock.lib_base.util.SizeUtils;
import cool.dingstock.lib_base.widget.tabs.DcTabLayout;
import cool.dingstock.uicommon.widget.MonthTabViewHolder;
import org.jetbrains.annotations.NotNull;

public class HomeSneakersFragment extends LazyDcFragment<HomeSneakersFragmentPresenter> {
    private static final int SHOW_TOP_COUNT = 15;

    private int currentMonthIndex = -1;
    private SneakerSectionAdapter rvAdapter;
    private StickyHeaderLayoutManager stickyHeaderLayoutManager;
    private CalenderViewModel calenderViewModel;
    private Boolean isFirstLoadMonthList = true;
    private HomeIndexViewModel homeIndexViewModel;
    private BaseRVAdapter<BaseItem> recommendAdapter;
    private RecyclerScrollListener recyclerScrollListener;
    private final ArrayList<CalenderProductEntity> recommendList = new ArrayList<>();

    private boolean needMargeTop = false;

    private HomeFragmentSneakersLayoutBinding viewBinding;

    public static HomeSneakersFragment getInstance(HomeCategoryBean homeCategoryBean) {
        if (null == homeCategoryBean) {
            return null;
        }
        HomeSneakersFragment fragment = new HomeSneakersFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(HomeBusinessConstant.KEY_CATEGORY, homeCategoryBean);
        fragment.setArguments(bundle);
        return fragment;
    }

    public int getCurrentMonthIndex() {
        return currentMonthIndex;
    }

    @Override
    protected boolean ignoreUt() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = HomeFragmentSneakersLayoutBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoadingView();
    }

    @Override
    protected void initStatusView() {
        super.initStatusView();
        mStatusView.setOnErrorViewClick(v -> refresh());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment_sneakers_layout;
    }

    @Override
    protected HomeSneakersFragmentPresenter initPresenter() {
        return new HomeSneakersFragmentPresenter(this);
    }

    @Override
    protected void initVariables(View rootView, ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (null == arguments) {
            Logger.e(" HomeCategoryBean  null  error");
            return;
        }
        viewBinding.homeFragmentSneakersRefresh.setEnabled(true);
        calenderViewModel = new ViewModelProvider(this).get(CalenderViewModel.class);
        getLifecycle().addObserver(calenderViewModel);
        EventBus.getDefault().register(this);
        initRv();
        initTitle();
        initScroll();
        updateUI();
    }

    private void initTitle() {
//        brandFilterView .setAnchor(tabLayout);
    }

    private void initScroll() {
        viewBinding.layoutAppBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> viewBinding.homeFragmentSneakersRefresh.setEnabled(verticalOffset >= 0));
        recyclerScrollListener = new RecyclerScrollListener(requireContext(), homeIndexViewModel);
        viewBinding.homeFragmentSneakersRv.addOnScrollListener(recyclerScrollListener);
    }

    /**
     * 更新UI
     */
    private void updateUI() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewBinding.homeFragmentSneakersRefresh.getLayoutParams();
        if (needMargeTop) {
            layoutParams.topMargin = SizeUtils.dp2px(49f) + SizeUtils.getStatusBarHeight(requireContext());
        } else {
            layoutParams.topMargin = SizeUtils.dp2px(0);
        }
        viewBinding.homeFragmentSneakersRefresh.setLayoutParams(layoutParams);
        updateSneakerList();
        updateCalender();
        updateState();
        if (homeIndexViewModel != null) {
            homeIndexViewModel.getSneakerCalendarScrollTop().observe(this, b -> {
                boolean visible = getPageVisible();
                if (visible && b) {
                    tryToScrollTop();
                } else {
                    return;
                }
            });
        }
    }

    private void tryToScrollTop() {
        RecyclerView.LayoutManager layoutManager = viewBinding.homeFragmentSneakersRv.getLayoutManager();
        if (layoutManager != null) {
            viewBinding.homeFragmentSneakersRv.stopScroll();
            layoutManager.scrollToPosition(0);
        }
        recyclerScrollListener.reset();
        ViewGroup.LayoutParams layoutParams = viewBinding.layoutAppBar.getLayoutParams();
        if (layoutParams == null) {
            return;
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) layoutParams).getBehavior();
        if (behavior instanceof AppBarLayout.Behavior) {
            AppBarLayout.Behavior appBarBehavior = (AppBarLayout.Behavior) behavior;
            if (appBarBehavior.getTopAndBottomOffset() == 0) {
                return;
            } else {
                appBarBehavior.setTopAndBottomOffset(0);
            }
        }
    }

    private void updateState() {
        calenderViewModel.getErrorLiveData().observe(this, this::showErrorView);
        calenderViewModel.getRequestFinish().observe(this, aBoolean -> {
            if (aBoolean) {
                hideLoadMore();
                hideLoadingDialog();
                viewBinding.homeFragmentSneakersRefresh.finishRefresh();
            }
        });
    }

    public void hideLoadMore() {
        if (null != rvAdapter) {
            rvAdapter.hideLoadMore();
        }
    }

    /**
     *
     */
    private void updateCalender() {
        calenderViewModel.getMonthListData().observe(this, dateBeanList -> {
            if (CollectionUtils.isEmpty(dateBeanList)) {
                return;
            }
            for (DateBean dateBean : dateBeanList) {
                DcTabLayout.Tab tab = viewBinding.homeFragmentSneakersTab.newTab();
                tab.setCustomView(R.layout.common_month_tab);
                MonthTabViewHolder holder = new MonthTabViewHolder(requireContext(), Objects.requireNonNull(tab.getCustomView()));
                holder.getTabTitle().setText(dateBean.getMonth() + "");
                viewBinding.homeFragmentSneakersTab.addTab(tab);
            }
            //tab设置
            ThreadPoolHelper.getInstance().runOnUiThread(() -> {
                if (getActivity() == null) {
                    return;
                }
                DcTabLayout.Tab tab = viewBinding.homeFragmentSneakersTab.getTabAt(calenderViewModel.getCurrentMonthIndex());
                if (null != tab) {
                    tab.select(true);
                }
            }, 100);
        });
    }

    private void updateSneakerList() {
        calenderViewModel.getSneakerCalenderLiveData().observe(this, calenderDataBean -> {
            List<HomeProductGroup> sectionsList = calenderDataBean.getSections();
            addSneakerList(sectionsList, calenderDataBean.isRefresh());
            CalenderFeaturedEntity featured = calenderDataBean.getFeatured();
            addRecommendList(featured);
        });
    }

    private void addRecommendList(CalenderFeaturedEntity featured) {
        recommendList.clear();
        if (featured == null || featured.getProducts() == null || featured.getProducts().size() == 0) {
            recommendAdapter.clearAllItemView();
            recommendAdapter.notifyDataSetChanged();
            viewBinding.recentRecommendLine.setVisibility(View.GONE);
            return;
        }
        viewBinding.recentRecommendLine.setVisibility(View.VISIBLE);
        List<BaseItem> itemList = new ArrayList<>();
        if (!featured.getProducts().isEmpty()) {
            for (CalenderProductEntity calenderProductEntity : featured.getProducts()) {
                CalenderRecommendItem calenderRecommendItem = new CalenderRecommendItem(calenderProductEntity);
                itemList.add(calenderRecommendItem);
                recommendList.add(calenderProductEntity);
            }
            viewBinding.recentRecommendLine.setVisibility(View.VISIBLE);
        } else {
            viewBinding.recentRecommendLine.setVisibility(View.GONE);
        }
        recommendAdapter.clearAllItemView();
        recommendAdapter.notifyDataSetChanged();
        recommendAdapter.setItemViewList(itemList);
        viewBinding.homeFragmentSneakersRv.requestLayout();
    }

    private void addSneakerList(List<HomeProductGroup> sectionsList, boolean isRefresh) {
        if (null == sectionsList || sectionsList.isEmpty()) {
            hideLoadingView();
            showEmpty();
        } else {
            viewBinding.homeFragmentSneakersRv.setVisibility(View.VISIBLE);
            viewBinding.emptySneakers.setVisibility(View.GONE);
            hideLoadingView();
            if (null != rvAdapter) {
                rvAdapter.setData(null);
            }
            setItemData(sectionsList, isRefresh);
        }
    }

    private void showEmpty() {
        viewBinding.homeFragmentSneakersRv.setVisibility(View.INVISIBLE);
        viewBinding.emptySneakers.setVisibility(View.VISIBLE);
    }

    public void setItemData(List<HomeProductGroup> productList, boolean isRefresh) {
        if (isRefresh) {
            if (null != rvAdapter) {
                rvAdapter.setData(null);
            }
            stickyHeaderLayoutManager.scrollToPosition(0);
        }
        if (CollectionUtils.isEmpty(productList)) {
            return;
        }
        if (null == rvAdapter) {
            rvAdapter = new SneakerSectionAdapter();
            viewBinding.homeFragmentSneakersRv.setAdapter(rvAdapter);
            rvAdapter.setAnchor(viewBinding.homeFragmentSneakersTab);
        }
        rvAdapter.setData(productList);
        viewBinding.homeHeadSneakerBrandLayer.setBrandLayerData(rvAdapter.getAllBrand(), rvAdapter.getAllType(), isRefresh);
    }

    @Override
    protected void initListeners() {
        viewBinding.homeFragmentSneakersRefresh.setOnRefreshListener(refreshLayout -> {
            calenderViewModel.refresh();
            viewBinding.homeHeadSneakerBrandLayer.clearFilter();
        });
        viewBinding.homeFragmentSneakersTab.addOnTabSelectedListener(new DcTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(DcTabLayout.Tab tab) {
                MonthTabViewHolder holder = new MonthTabViewHolder(getContext(), Objects.requireNonNull(tab.getCustomView()));
                holder.setSelect(true);
                String content = holder.getTabTitle().getText().toString();
                //选择月份
                boolean change = false;
                if (currentMonthIndex != -1 && currentMonthIndex != tab.getPosition()) {
                    change = true;
                }
                currentMonthIndex = tab.getPosition();
                if (change) {
                    //判断是否是 初次加载数据时默认选中还是用户手动选中
                    if (isFirstLoadMonthList) {
                        calenderViewModel.refresh();
                        isFirstLoadMonthList = false;
                    } else {
                        UTHelper.commonEvent(UTConstant.Calendar.CalendarP_click_Month, "month", content);
                        calenderViewModel.updateSelectedMonth(currentMonthIndex);
                        viewBinding.homeHeadSneakerBrandLayer.clearFilter();
                        recyclerScrollListener.reset();
                        homeIndexViewModel.setCalendarIsSHowRocket(false);
                    }
                }
            }

            @Override
            public void onTabUnselected(DcTabLayout.Tab tab) {
                MonthTabViewHolder holder = new MonthTabViewHolder(requireContext(), Objects.requireNonNull(tab.getCustomView()));
                holder.setSelect(false);
            }

            @Override
            public void onTabReselected(DcTabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        showLoadingView();
        calenderViewModel.onDataLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initRv() {
        stickyHeaderLayoutManager = new StickyHeaderLayoutManager();
        viewBinding.homeFragmentSneakersRv.setLayoutManager(stickyHeaderLayoutManager);
        recommendAdapter = new BaseRVAdapter<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        viewBinding.rvRecommendGoods.setLayoutManager(layoutManager);
        viewBinding.rvRecommendGoods.setAdapter(recommendAdapter);
        recommendAdapter.setOnItemViewClickListener((item, sectionKey, sectionItemPosition) -> {
            if (item instanceof CalenderRecommendItem) {
                UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_source, "position", "球鞋日历重磅(热门)");
                UTHelper.commonEvent(UTConstant.Calendar.CalendarP_click_HeavySneakers);
                DcRouter(HomeConstant.Uri.HEAVY_RELEASE)
                        .putExtra("list", recommendList)
                        .putExtra("cell", ((CalenderRecommendItem) item).getData())
                        .overridePendingTransition(R.anim.activity_enter_alpha, R.anim.activity_exit_alpha)
                        .start();
            }
        });
    }

    private void refresh() {
        showLoadingView();
        calenderViewModel.refresh();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        homeIndexViewModel = new ViewModelProvider(requireActivity()).get(HomeIndexViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public HomeSneakersFragment setNeedMargeTop(boolean needMargeTop) {
        this.needMargeTop = needMargeTop;
        return this;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBrandFilter(EventBrandChooseChange eventBrandFilter) {
        calenderViewModel.updateFilterList(
                Objects.requireNonNull(eventBrandFilter.getChangeList()),
                Objects.requireNonNull(eventBrandFilter.getTypeList()));
    }

    private static class RecyclerScrollListener extends RecyclerView.OnScrollListener {
        HomeIndexViewModel indexViewModel;
        Context context;

        RecyclerScrollListener(Context context, HomeIndexViewModel indexViewModel) {
            this.indexViewModel = indexViewModel;
            this.context = context;
        }

        private int totalY = 0;

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            totalY += dy;
            if (totalY > SizeUtils.dp2px(270f)) {
                indexViewModel.setCalendarIsSHowRocket(true);
            } else {
                indexViewModel.setCalendarIsSHowRocket(false);
            }
        }

        public void reset() {
            totalY = 0;
        }

    }

}
