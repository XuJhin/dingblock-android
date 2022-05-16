package cool.dingstock.calendar.sneaker.index;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import cool.dingstock.appbase.entity.bean.common.DateBean;
import cool.dingstock.appbase.mvp.lazy.DCLazyFragmentPresenter;
import cool.dingstock.calendar.dagger.CalendarApiHelper;
import cool.dingstock.appbase.net.api.home.HomeApi;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.util.Logger;

public class HomeSneakersFragmentPresenter extends DCLazyFragmentPresenter<HomeSneakersFragment> {


    @Inject
    HomeApi homeApi;



    private int page;
    private List<DateBean> monthList;
    private final List<String> filterList = new ArrayList<>();

    public HomeSneakersFragmentPresenter(HomeSneakersFragment fragment) {
        super(fragment);
        CalendarApiHelper.INSTANCE.getApiHomeComponent().inject(this);
    }

    @Override
    public void onLoadData() {
        super.onLoadData();
//        getMonthList();
    }

    public void refresh() {

    }



    private void hideLoading() {
        this.getFragment().hideLoadingView();
    }

    public void loadSneakerByMonth(Integer page, String date) {
//        HomeHelper.getInstance().loadSneakerCalendar
    }


}
