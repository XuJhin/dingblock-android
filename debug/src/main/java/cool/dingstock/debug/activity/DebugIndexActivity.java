package cool.dingstock.debug.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cool.dingstock.appbase.constant.HomeConstant;
import cool.dingstock.appbase.constant.ServerConstant;
import cool.dingstock.appbase.mvvm.activity.BaseViewModel;
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity;
import cool.dingstock.appbase.router.DcRouterUtils;
import cool.dingstock.appbase.util.LoginUtils;
import cool.dingstock.appbase.widget.TitleBar;
import cool.dingstock.appbase.widget.camera.DCCameraActivity;
import cool.dingstock.appbase.widget.dialog.ActionSheetDialog;
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter;
import cool.dingstock.appbase.widget.recyclerview.head.DebugAppHead;
import cool.dingstock.debug.adapter.item.DebugIndexItem;
import cool.dingstock.debug.bean.DebugIndexBean;
import cool.dingstock.debug.databinding.DebugActivityIndexBinding;
import cool.dingstock.lib_base.stroage.ConfigSPHelper;
import me.ele.uetool.UETool;

public class DebugIndexActivity extends VMBindingActivity<BaseViewModel, DebugActivityIndexBinding> {
	
	private static boolean debugToolShow = false;
	RecyclerView debugRv;
	TitleBar titleBar;
	private BaseRVAdapter<DebugIndexItem> rvAdapter;

	@Override
	public void initViewAndEvent(@Nullable Bundle savedInstanceState) {
		debugRv = viewBinding.debugIndexRv;
		titleBar = viewBinding.debugIndexTitleBar;

		rvAdapter = new BaseRVAdapter<>();
		debugRv.setLayoutManager(new LinearLayoutManager(getContext()));
		debugRv.setAdapter(rvAdapter);
		setItemList();
	}
	
	private void setItemList() {
		rvAdapter.addHeadView(new DebugAppHead(""));
		List<DebugIndexItem> itemList = new ArrayList<>();
		//ENV DEBUG
		SharedPreferences share = getSharedPreferences("net_env", Context.MODE_PRIVATE);
		SharedPreferences share1 = getSharedPreferences("parse_env", Context.MODE_PRIVATE);
		String serverUrl = share.getString("env", "https://api.dingstock.net");
		String serverUrl1 = share1.getString("env", "https://apiv2.dingstock.net");
		
		DebugIndexBean envIndexBean = new DebugIndexBean();
		envIndexBean.setActionType("actionSheet");
		envIndexBean.setType("env");
		envIndexBean.setTitle("开发环境" +
				(
						serverUrl.equals(ServerConstant.SERVER_PRODUCT_NEW) ? "（线上）"
								: serverUrl.equals(ServerConstant.SERVER_PRERELEASE_NEW) ? "（预发布）"
								: serverUrl.equals(ServerConstant.SERVER_DEBUG_NEW) ? "（开发）"
								: serverUrl.equals(ServerConstant.SERVER_DEBUG_SHOP) ? "(开发  海淘)"
								: ""
				)
		);
		DebugIndexItem envItem = new DebugIndexItem(envIndexBean);
		itemList.add(envItem);
		
		//UI DEBUG
		DebugIndexBean uiDebugIndexBean = new DebugIndexBean();
		uiDebugIndexBean.setActionType("switch");
		uiDebugIndexBean.setType("debug_ui");
		uiDebugIndexBean.setTitle("UI测试");
		uiDebugIndexBean.setSwitchStatus(debugToolShow);
		DebugIndexItem uiDebugItem = new DebugIndexItem(uiDebugIndexBean);
		uiDebugItem.setMListener(checked -> {
			if (debugToolShow) {
				UETool.dismissUETMenu();
				debugToolShow = false;
			} else {
				UETool.showUETMenu();
				debugToolShow = true;
			}
		});
		itemList.add(uiDebugItem);
		
		//小程序跳转
		DebugIndexBean miniBean = new DebugIndexBean();
		miniBean.setActionType("link");
		miniBean.setType("mini");
		miniBean.setTitle("小程序跳转");
		DebugIndexItem miniItem = new DebugIndexItem(miniBean);
		itemList.add(miniItem);
		
		//支付宝支付
		DebugIndexBean cameraBean = new DebugIndexBean();
		cameraBean.setActionType("link");
		cameraBean.setType("camera");
		cameraBean.setTitle("摄像头");
		DebugIndexItem cameraItem = new DebugIndexItem(cameraBean);
		itemList.add(cameraItem);
		
		//H5调试
		DebugIndexBean h5Bean = new DebugIndexBean();
		h5Bean.setActionType("link");
		h5Bean.setType("h5");
		h5Bean.setTitle("H5调试");
		
		DebugIndexItem h5Item = new DebugIndexItem(h5Bean);
		itemList.add(h5Item);
		
		SharedPreferences shareMpEnv = getSharedPreferences("mpEnv", Context.MODE_PRIVATE);
		boolean isRelease = shareMpEnv.getBoolean("isRelease", true);
		//小程序 环境切换
		DebugIndexBean mpEnv = new DebugIndexBean();
		mpEnv.setActionType("mpEnv");
		mpEnv.setType("mpEnv");
		mpEnv.setTitle("小程序环境" + (isRelease ? "（正式）" : "（体验）"));
		itemList.add(new DebugIndexItem(mpEnv));
		
		rvAdapter.setItemViewList(itemList);
	}
	
	@Override
	protected void initListeners() {
		rvAdapter.setOnItemViewClickListener((item, sectionKey, sectionItemPosition) -> {
			switch (item.getData().getType()) {
				case "mini":
					routerMini();
					break;
				case "env":
					switchEnv();
					break;
				case "h5":
					routeH5();
					break;
				case "camera":
					Intent intent = new Intent(this, DCCameraActivity.class);
					startActivity(intent);
					break;
				case "mpEnv":
					switchMpEnv();
					break;
			}
		});
	}
	
	private void switchMpEnv() {
		ActionSheetDialog.show(this, Arrays.asList("正式", "体验"), (text, index) -> {
			SharedPreferences shareMpEnv = getSharedPreferences("mpEnv", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = shareMpEnv.edit();
			switch (index) {
				case 0:
					editor.putBoolean("isRelease", true);
					break;
				case 1:
					editor.putBoolean("isRelease", false);
					break;
			}
			editor.apply();
		}, true);
	}
	
	private void routeH5() {
		DcRouter(DcRouterUtils.EXTERNAL_WEBVIEW_HOST + "?url=" + "https://dingstock.obs.cn-east-2.myhuaweicloud.com/website/app/active/double11.html").start();
	}
	
	private void routerMini() {
		DcRouter("https://dingstock.obs.cn-east-2.myhuaweicloud.com/website/app/active/double11.html").start();
	}
	
	private void switchEnv() {
		ActionSheetDialog.show(this, Arrays.asList("线上", "预发布", "开发", "海淘测试"), (text, index) -> {
			SharedPreferences sharedPreferences = getSharedPreferences("parse_env", Context.MODE_PRIVATE);
			SharedPreferences sharedPreferences_New = getSharedPreferences("net_env", Context.MODE_PRIVATE);
			String lastUrl = sharedPreferences_New.getString("env", ServerConstant.SERVER_DEBUG_NEW);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			SharedPreferences.Editor editor_new = sharedPreferences_New.edit();
			switch (index) {
				case 0:
					resetInfo();
					editor.putString("env", ServerConstant.SERVER_PRODUCT);
					editor_new.putString("env", ServerConstant.SERVER_PRODUCT_NEW);
					break;
				case 1:
					resetInfo();
					editor.putString("env", ServerConstant.SERVER_PRERELEASE);
					editor_new.putString("env", ServerConstant.SERVER_PRERELEASE_NEW);
					break;
				case 2:
					resetInfo();
					editor.putString("env", ServerConstant.SERVER_DEBUG);
					editor_new.putString("env", ServerConstant.SERVER_DEBUG_NEW);
					break;
				case 3:
					resetInfo();
					editor.putString("env", ServerConstant.SERVER_DEBUG_SHOP);
					editor_new.putString("env", ServerConstant.SERVER_DEBUG_SHOP);
					break;
			}
			editor.apply();
			editor_new.apply();
			String currentUrl = sharedPreferences_New.getString("env", ServerConstant.SERVER_DEBUG_NEW);
			if (!currentUrl.equalsIgnoreCase(lastUrl)) {
				LoginUtils.INSTANCE.loginOut();
			}
		}, true);
	}
	
	/**
	 * 重置用户信息
	 */
	private void resetInfo() {
		ConfigSPHelper.getInstance().save(HomeConstant.SP.AD_UNIT_DELAY_FIRST, -1L);
	}
	
	@Override
	public String moduleTag() {
		return null;
	}
}
