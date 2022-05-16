package cool.dingstock.appbase.delegate;

import com.lljjcoder.style.citypickerview.CityPickerView;

import cool.dingstock.appbase.base.BaseDcActivity;

public interface DCWebViewControllerDelegate {

    void showLoadingView();

    void hideLoadingView();

    void setTitleBarTitle(String title);

    void setRightTxt(String text);

    BaseDcActivity getDCActivity();

    void onWebViewLoadingFinish();

    CityPickerView getCityPickerView();

    void showCityPickerView();

    void setTitleBarLeft(Boolean needHidden, String backBtnColor);
}
