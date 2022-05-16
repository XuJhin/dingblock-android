package cool.dingstock.calendar.sms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sankuai.waimai.router.annotation.RouterUri;

import java.util.ArrayList;
import java.util.List;

import cool.dingstock.appbase.constant.CalendarConstant;
import cool.dingstock.appbase.constant.HomeConstant;
import cool.dingstock.appbase.constant.ModuleConstant;
import cool.dingstock.appbase.constant.RouterConstant;
import cool.dingstock.appbase.entity.bean.home.HomeField;
import cool.dingstock.appbase.entity.bean.home.HomeRaffleSection;
import cool.dingstock.appbase.entity.bean.home.HomeRaffleSmsBean;
import cool.dingstock.appbase.mvvm.activity.BaseViewModel;
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity;
import cool.dingstock.appbase.util.SoftKeyBoardUtil;
import cool.dingstock.appbase.widget.TitleBar;
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter;
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.adapter.footer.HomeRaffleFiledFoot;
import cool.dingstock.calendar.adapter.head.HomeRaffleFiledHead;
import cool.dingstock.calendar.databinding.HomeActivitySmsEditLayoutBinding;
import cool.dingstock.calendar.item.HomeRaffleFieldItem;
import cool.dingstock.lib_base.util.CollectionUtils;

@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = {HomeConstant.Path.SMS_EDIT})
public class HomeSmsEditActivity extends VMBindingActivity<BaseViewModel, HomeActivitySmsEditLayoutBinding> {

    TitleBar titleBar;
    RecyclerView rv;

    private BaseRVAdapter<BaseItem> mRvAdapter;

    private HomeRaffleSmsBean mSmsBean;


    @Override
    public void initViewAndEvent(@Nullable Bundle savedInstanceState) {
        titleBar = viewBinding.homeActivitySmsEditTitleBar;
        rv = viewBinding.homeActivitySmsEditRv;

        mSmsBean = getIntent().getParcelableExtra(CalendarConstant.DataParam.KEY_SMS);
        if (null == mSmsBean) {
            finish();
            return;
        }
        initTitle();
        initRv();
        setRvData();
    }

    private void initRv() {
        mRvAdapter = new BaseRVAdapter<>();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mRvAdapter);
    }


    private void initTitle() {
        titleBar.setTitle(mSmsBean.getShopName());
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        SoftKeyBoardUtil.hideSoftKeyboard(this, titleBar);
        return super.dispatchTouchEvent(ev);
    }


    private void setRvData() {
        List<HomeRaffleSection> sectionList = mSmsBean.getSections();
        if (CollectionUtils.isEmpty(sectionList)) {
            return;
        }
        for (HomeRaffleSection homeRaffleSection : sectionList) {
            int sectionKey = sectionList.indexOf(homeRaffleSection);
            String header = homeRaffleSection.getHeader();
            if (!TextUtils.isEmpty(header)) {
                mRvAdapter.addHeadView(sectionKey,
                        new HomeRaffleFiledHead(header));
            }
            List<HomeField> fieldList = homeRaffleSection.getFields();
            if (!CollectionUtils.isEmpty(fieldList)) {
                List<BaseItem> itemList = new ArrayList<>();
                for (HomeField homeField : fieldList) {
                    itemList.add(new HomeRaffleFieldItem(homeField));
                }
                mRvAdapter.setItemViewList(sectionKey, itemList);
            }
            if (sectionKey == sectionList.size() - 1) {
                HomeRaffleFiledFoot footView = new HomeRaffleFiledFoot("");
                footView.setMListener(this::sendSMS);
                mRvAdapter.addFootView(sectionKey, footView);
            }
        }

    }


    private void sendSMS() {
        String bodyStr = generateSmsBody();
        if (TextUtils.isEmpty(bodyStr)) {
            return;
        }
        if (TextUtils.isEmpty(mSmsBean.getShopPhoneNum())) {
            showToastShort(R.string.sms_phone_num_empty);
            return;
        }
        Uri smsToUri = Uri.parse("smsto:" + mSmsBean.getShopPhoneNum());
        Intent intent = new Intent(Intent.ACTION_VIEW, smsToUri);
        intent.putExtra("sms_body", bodyStr);
        startActivity(intent);
    }


    private String generateSmsBody() {
        List<BaseItem> allItemList = mRvAdapter.getAllItemList();
        if (CollectionUtils.isEmpty(allItemList)) {
            return "";
        }
        StringBuilder body = new StringBuilder();
        for (BaseItem baseItem : allItemList) {
            if (HomeRaffleFieldItem.VIEW_TYPE != baseItem.getViewType()) {
                continue;
            }
            HomeRaffleFieldItem homeRaffleFieldItem = (HomeRaffleFieldItem) baseItem;
            HomeField data = homeRaffleFieldItem.getData();
            String editText = homeRaffleFieldItem.getEditText();
            if (TextUtils.isEmpty(editText)) {
                showToastShort(data.getName() + getString(R.string.edit_not_content));
                return "";
            }
            body.append(editText)
                    .append(data.getSuffix());
        }
        return body.toString();
    }


    @Override
    protected void initListeners() {

    }

    @Override
    public String moduleTag() {
        return ModuleConstant.HOME_MODULE;
    }


}
