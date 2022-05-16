package cool.dingstock.calendar.item;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import cool.dingstock.appbase.entity.bean.home.HomeField;
import cool.dingstock.appbase.widget.recyclerview.item.BaseItem;
import cool.dingstock.appbase.widget.recyclerview.item.BaseViewHolder;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.databinding.HomeItemSmsFieldBinding;
import cool.dingstock.lib_base.stroage.ConfigSPHelper;
import cool.dingstock.lib_base.util.StringUtils;

public class HomeRaffleFieldItem extends BaseItem<HomeField, HomeItemSmsFieldBinding> {

    public static final int VIEW_TYPE = 10;

    public HomeRaffleFieldItem(HomeField data) {
        super(data);
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.home_item_sms_field;
    }

    @Override
    public void onReleaseViews(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {

    }

    @Override
    public void onSetViewsData(BaseViewHolder holder, int sectionKey, int sectionViewPosition) {
        HomeField homeField = getData();
        viewBinding.homeItemSmsFieldNameTxt.setText(homeField.getName());
        String tip = homeField.getTip();
        if (TextUtils.isEmpty(tip)) {
            viewBinding.homeItemSmsFieldTipTxt.setVisibility(View.GONE);
        } else {
            viewBinding.homeItemSmsFieldTipTxt.setVisibility(View.VISIBLE);
            viewBinding.homeItemSmsFieldTipTxt.setText(tip);
        }

        viewBinding.homeItemSmsFieldEdit.setOnEditorActionListener((v, actionId, event) -> {
            View nextView = v.focusSearch(View.FOCUS_DOWN);
            if (nextView != null) {
                nextView.requestFocus(View.FOCUS_DOWN);
            }
            //这里一定要返回true
            return true;
        });
        viewBinding.layoutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftInputFromWindow();
            }
        });

        viewBinding.homeItemSmsFieldTipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftInputFromWindow();
            }
        });
        String key = getData().getKey();
        String spStr = ConfigSPHelper.getInstance().getString(key);
        if (!TextUtils.isEmpty(spStr)) {
            viewBinding.homeItemSmsFieldEdit.setText(spStr);
        }
        String placeholder = homeField.getPlaceholder();
        if(!StringUtils.isEmpty(placeholder)){
            viewBinding.homeItemSmsFieldEdit.setText(placeholder);
        }
        viewBinding.homeItemSmsFieldEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(key)) {
                    ConfigSPHelper.getInstance().save(key, s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (!TextUtils.isEmpty(viewBinding.homeItemSmsFieldEdit.getText().toString())) {
            viewBinding.homeItemSmsFieldEdit.setSelection(viewBinding.homeItemSmsFieldEdit.getText().toString().length());
        }
    }

    private void showSoftInputFromWindow() {
        viewBinding.homeItemSmsFieldEdit.setFocusable(true);
        viewBinding.homeItemSmsFieldEdit.setFocusableInTouchMode(true);
        viewBinding.homeItemSmsFieldEdit.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) viewBinding.homeItemSmsFieldEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(viewBinding.homeItemSmsFieldEdit, 0);
    }


    public String getEditText() {
        String editStr = viewBinding.homeItemSmsFieldEdit.getText().toString();
        return TextUtils.isEmpty(editStr) ? "" : editStr.trim();
    }
}
