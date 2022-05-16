package net.dingblock.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sankuai.waimai.router.Router;

import cool.dingstock.appbase.constant.PushConstant;
import cool.dingstock.appbase.mvp.DCActivityManager;
import cool.dingstock.lib_base.util.Logger;
import net.dingblock.mobile.activity.BootActivity;

/**
 * 推送点击处理
 */
public class PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        goLink();
    }

    private void goLink() {
        Intent intent = getIntent();
        if (intent == null || intent.getData() == null) {
            finish();
            return;
        }
        String linkUrl = intent.getData().getQueryParameter("link");
        if (linkUrl == null) {
            finish();
            return;
        }

        boolean isAppLive = !DCActivityManager.getInstance().isActivityListEmpty();
        if (isAppLive) {
            Logger.d("PopupActivity is isAppLive true  " + linkUrl);
            if (!TextUtils.isEmpty(linkUrl)) {
                Activity activity = DCActivityManager.getInstance().getTopActivity();
                if (activity != null) {
                    Router.startUri(DCActivityManager.getInstance().getTopActivity(), linkUrl);
                }
            }
            finish();
        } else {
            Logger.e("PopupActivity is isAppLive false  " + linkUrl);
            Intent launchIntent = new Intent(this, BootActivity.class);
            launchIntent.putExtra(PushConstant.Key.KEY_URL, linkUrl);
            launchIntent.putExtra(PushConstant.Key.KEY_ACTION_VIEW, true);
            launchIntent.putExtra(PushConstant.Key.KEY_IGNORE_AD, true);
            startActivity(launchIntent);
            finish();
        }
    }
}
