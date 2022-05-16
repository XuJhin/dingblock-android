package cool.dingstock.imagepicker.custom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import cool.dingstock.imagepicker.R;

import java.util.ArrayList;

import cool.dingstock.imagepicker.adapter.PickerItemAdapter;
import cool.dingstock.imagepicker.bean.ImageItem;
import cool.dingstock.imagepicker.bean.selectconfig.BaseSelectConfig;
import cool.dingstock.imagepicker.data.ICameraExecutor;
import cool.dingstock.imagepicker.data.IReloadExecutor;
import cool.dingstock.imagepicker.data.ProgressSceneEnum;
import cool.dingstock.imagepicker.presenter.IPickerPresenter;
import cool.dingstock.imagepicker.views.PickerUiConfig;
import cool.dingstock.imagepicker.views.PickerUiProvider;
import cool.dingstock.imagepicker.views.base.PickerControllerView;
import cool.dingstock.imagepicker.views.base.PickerFolderItemView;
import cool.dingstock.imagepicker.views.base.PickerItemView;
import cool.dingstock.imagepicker.views.base.PreviewControllerView;
import cool.dingstock.imagepicker.views.base.SingleCropControllerView;
import cool.dingstock.imagepicker.views.wx.WXFolderItemView;
import cool.dingstock.imagepicker.views.wx.WXTitleBar;

/**
 * Description: 自定义样式,目前mars就采用此样式
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class CustomImgPickerPresenter implements IPickerPresenter {

    @Override
    public void displayImage(View view, ImageItem item, int size, boolean isThumbnail) {
        Object object = item.getUri() != null ? item.getUri() : item.path;
        Glide.with(view.getContext()).load(object).apply(new RequestOptions()
                .format(isThumbnail ? DecodeFormat.PREFER_RGB_565 : DecodeFormat.PREFER_ARGB_8888))
                .override(isThumbnail ? size : Target.SIZE_ORIGINAL)
                .into((ImageView) view);
    }

    @NonNull
    @Override
    public PickerUiConfig getUiConfig(@Nullable Context context) {
        PickerUiConfig uiConfig = new PickerUiConfig();
        uiConfig.setShowStatusBar(true);
        assert context != null;
        uiConfig.setThemeColor(ContextCompat.getColor(context, R.color.color_ding_theme));
        uiConfig.setStatusBarColor(ContextCompat.getColor(context, R.color.white));
        //选择器背景
        uiConfig.setPickerBackgroundColor(ContextCompat.getColor(context, R.color.white));
        //选择器文件夹打开方向
        uiConfig.setFolderListOpenDirection(PickerUiConfig.DIRECTION_BOTTOM);
        uiConfig.setFolderListOpenMaxMargin(0);
        uiConfig.setPickerUiProvider(new PickerUiProvider() {
            @Override
            public PickerControllerView getTitleBar(Context context) {
                WXTitleBar titleBar = (WXTitleBar) super.getTitleBar(context);
                titleBar.setCompleteText("下一步");
                titleBar.setCompleteBackground(null, null);
                titleBar.setCompleteTextColor(ContextCompat.getColor(context, R.color.color_ding_theme),
                        ContextCompat.getColor(context, R.color.color_ding_theme_1));
                titleBar.centerTitle();
                titleBar.setShowArrow(true);
                titleBar.setCanToggleFolderList(true);
                titleBar.setBackIconID(R.mipmap.picker_icon_back_black);
                return titleBar;
            }

            @Override
            public PickerItemView getItemView(Context context) {
                return new CustomPickerItem(context);
            }

            @Override
            public PickerControllerView getBottomBar(Context context) {
                return new CustomBottomBar(context);
            }

            @Override
            public PreviewControllerView getPreviewControllerView(Context context) {
                return new CustomPreviewControllerView(context);
            }

            @Override
            public SingleCropControllerView getSingleCropControllerView(Context context) {
                return new CustomCropControllerView(context);
            }

            @Override
            public PickerFolderItemView getFolderItemView(Context context) {
                WXFolderItemView itemView = (WXFolderItemView) super.getFolderItemView(context);
                itemView.setIndicatorColor(context.getResources().getColor(R.color.color_ding_theme));
                return itemView;
            }
        });
        return uiConfig;
    }

    @Override
    public void tip(Context context, String msg) {
    }

    @Override
    public void overMaxCountTip(Context context, int maxCount) {
    }

    @Override
    public DialogInterface showProgressDialog(@Nullable Activity activity, ProgressSceneEnum progressSceneEnum) {
        return ProgressDialog.show(activity, null, progressSceneEnum == ProgressSceneEnum.crop ? "正在剪裁..." : "正在加载...");
    }

    @Override
    public boolean interceptPickerCompleteClick(@Nullable Activity activity, ArrayList<ImageItem> selectedList, BaseSelectConfig selectConfig) {
        return false;
    }

    @Override
    public boolean interceptPickerCancel(@Nullable Activity activity, ArrayList<ImageItem> selectedList) {
        return false;
    }

    /**
     * <p>
     * 图片点击事件拦截，如果返回true，则不会执行选中操纵，如果要拦截此事件并且要执行选中
     * 请调用如下代码：
     * <p>
     * adapter.preformCheckItem()
     * <p>
     * <p>
     * 此方法可以用来跳转到任意一个页面，比如自定义的预览
     *
     * @param activity        上下文
     * @param imageItem       当前图片
     * @param selectImageList 当前选中列表
     * @param allSetImageList 当前文件夹所有图片
     * @param selectConfig    选择器配置项，如果是微信样式，则selectConfig继承自MultiSelectConfig
     *                        如果是小红书剪裁样式，则继承自CropSelectConfig
     * @param adapter         当前列表适配器，用于刷新数据
     * @param isClickCheckBox 是否点击item右上角的选中框
     * @param reloadExecutor  刷新器
     * @return 是否拦截
     */
    @Override
    public boolean interceptItemClick(@Nullable Activity activity, ImageItem imageItem, ArrayList<ImageItem> selectImageList, ArrayList<ImageItem> allSetImageList, BaseSelectConfig selectConfig, PickerItemAdapter adapter, boolean isClickCheckBox, @Nullable IReloadExecutor reloadExecutor) {
        return false;
    }

    @Override
    public boolean interceptCameraClick(@Nullable Activity activity, ICameraExecutor takePhoto) {
        return false;
    }
}
