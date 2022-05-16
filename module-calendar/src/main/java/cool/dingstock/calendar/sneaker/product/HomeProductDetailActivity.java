package cool.dingstock.calendar.sneaker.product;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.sankuai.waimai.router.annotation.RouterUri;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cool.dingstock.appbase.adapter.CommonImgVpAdapter;
import cool.dingstock.appbase.constant.CalendarConstant;
import cool.dingstock.appbase.constant.CircleConstant;
import cool.dingstock.appbase.constant.CommonConstant;
import cool.dingstock.appbase.constant.HomeConstant;
import cool.dingstock.appbase.constant.ModuleConstant;
import cool.dingstock.appbase.constant.PostConstant;
import cool.dingstock.appbase.constant.PriceConstant;
import cool.dingstock.appbase.constant.RouterConstant;
import cool.dingstock.appbase.constant.ServerConstant;
import cool.dingstock.appbase.constant.ShoesConstant;
import cool.dingstock.appbase.constant.UTConstant;
import cool.dingstock.appbase.customerview.CommonNavigatorNew;
import cool.dingstock.appbase.entity.bean.calendar.PriceListResultEntity;
import cool.dingstock.appbase.entity.bean.home.AlarmFromWhere;
import cool.dingstock.appbase.entity.bean.home.AlarmRefreshEvent;
import cool.dingstock.appbase.entity.bean.home.HomeBrandBean;
import cool.dingstock.appbase.entity.bean.home.HomeProduct;
import cool.dingstock.appbase.entity.bean.home.HomeProductDetail;
import cool.dingstock.appbase.entity.bean.home.HomeRaffle;
import cool.dingstock.appbase.entity.bean.home.HomeRegion;
import cool.dingstock.appbase.entity.bean.home.bp.GoodDetailEntity;
import cool.dingstock.appbase.entity.bean.home.fashion.VideoEntity;
import cool.dingstock.appbase.helper.MonitorRemindHelper;
import cool.dingstock.appbase.imageload.GlideHelper;
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity;
import cool.dingstock.appbase.share.ShareParams;
import cool.dingstock.appbase.share.ShareType;
import cool.dingstock.appbase.ut.UTHelper;
import cool.dingstock.appbase.util.CalendarReminderUtils;
import cool.dingstock.appbase.util.ClipboardHelper;
import cool.dingstock.appbase.util.KtExtendUtilsKt;
import cool.dingstock.appbase.util.LoginUtils;
import cool.dingstock.appbase.util.StatusBarUtil;
import cool.dingstock.appbase.widget.IconTextView;
import cool.dingstock.appbase.widget.TitleBar;
import cool.dingstock.appbase.widget.commondialog.CommonDialog;
import cool.dingstock.appbase.widget.dialog.common.DcTitleDialog;
import cool.dingstock.appbase.widget.recyclerview.adapter.BaseRVAdapter;
import cool.dingstock.appbase.widget.tablayout.TabScaleAdapter;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.adapter.footer.ProductDetailFooter;
import cool.dingstock.calendar.databinding.HomeActivityProudctDetailBinding;
import cool.dingstock.imagepicker.utils.PPermissionUtils;
import cool.dingstock.imagepre.ImagePreview;
import cool.dingstock.imagepre.bean.ImageInfo;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.util.CollectionUtils;
import cool.dingstock.lib_base.util.Logger;
import cool.dingstock.lib_base.util.SizeUtils;
import cool.dingstock.lib_base.util.TimeUtils;
import cool.dingstock.lib_base.util.ToastUtil;
import cool.dingstock.uicommon.adapter.StickyHeaderDecoration;
import cool.dingstock.uicommon.calendar.dialog.ComparisonPriceDialog;
import cool.dingstock.uicommon.helper.AnimationDrawHelper;
import cool.dingstock.uicommon.product.dialog.SmsRegistrationDialog;
import cool.dingstock.uicommon.product.item.HomeRaffleDetailItem;
import cool.mobile.account.share.ShareDialog;
import cool.mobile.account.share.ShareHelper;

@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = {HomeConstant.Path.DETAIL})
public class HomeProductDetailActivity extends VMBindingActivity<HomeProductDetailViewModel, HomeActivityProudctDetailBinding> {

    private static final String[] PERMISSIONS_CALENDAR = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR};
    AppBarLayout appBarLayout;
    MagicIndicator magicIndicator;
    TitleBar titleBar;
    View shareIcon;
    IconTextView backIcon;
    //商品图片
    ViewPager productIv;
    //发售信息列表
    RecyclerView raffleRv;
    //商品信息的root
    ConstraintLayout contentRootLayer;
    //商品的文字信息
    ViewGroup skuLayer;
    TextView productSkuTxt;
    TextView productNameTxt;
    TextView productPriceTxt;
    TextView raffleInfoTxt;
    View storeCountLayer;
    View oneKeySearchPriceLayer;
    View dealNumberLayer;
    View flGoodId;
    View flSalePrice;
    TextView marketPriceTv;
    TextView dealTv;
    View marketPriceLeftIcon;
    //底部信息
    ImageView likeIcon;
    TextView likeTxt;
    LinearLayout layoutDislike;
    LinearLayout layoutLike;
    ImageView dislikeIcon;
    TextView dislikeTxt;
    LinearLayout priceLayer;
    TextView commentTxt;
    ViewGroup commentLayer;
    ViewGroup videoLayer;
    ViewGroup moreVideoFra;
    View videoPlayerFra;
    ImageView videoFrameImg;
    TextView videoTitleTv;
    TextView imgIndexTv;
    TextView imgCountTv;
    LinearLayoutCompat llShoeCertification;
    CommonImgVpAdapter commonImgVpAdapter;
    private int mRequestCode = 101;

    private LinearLayoutManager layoutManager;
    private StickyHeaderDecoration decoration;

    private BaseRVAdapter<HomeRaffleDetailItem> rvAdapter;

    private List<HomeProductDetail> mHomeProductDetailList;
    private FragmentContainerHelper containerHelper;

    private int scrollToPosition;
    private boolean canScroll;
    private boolean isUserTriggers = false;
    private int lastPos;
    private int lastSectionHeight;

    private int raffleCount;
    private HomeProduct mHomeProduct;
    private HomeRaffle mActionHomeRaffle;
    private SmsRegistrationDialog smsRegistrationDialog = new SmsRegistrationDialog();
    private ComparisonPriceDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            dialog = (ComparisonPriceDialog) getSupportFragmentManager().findFragmentByTag("ComparisonPriceDialog");
        }
        if (dialog == null) {
            dialog = new ComparisonPriceDialog();
        } else {
            dialog.dismiss();
            getPrice();
        }
    }

    @Override
    protected void setSystemStatusBar() {
        super.setSystemStatusBar();
        StatusBarUtil.setNavigationBarColor(this, getCompatColor(R.color.calendar_bottom_action_bg_color));
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_gray));
    }

    @Override
    public void initViewAndEvent(@Nullable Bundle savedInstanceState) {
        appBarLayout = viewBinding.homeProductDetailAppbar;
        magicIndicator = viewBinding.homeActivityProductDetailTab;
        titleBar = viewBinding.homeProductDetailTitleBar;
        shareIcon = viewBinding.homeProductDetailShareFra;
        backIcon = viewBinding.homeProductDetailBackIcon;
        productIv = viewBinding.homeProductDetailIv;
        raffleRv = viewBinding.homeActivityProductDetailRv;
        contentRootLayer = viewBinding.homeProductDetailContentRootlayer;
        skuLayer = viewBinding.productInfo.homeProductDetailProductSkuLayer;
        productSkuTxt = viewBinding.productInfo.homeProductDetailProductSkuTxt;
        productNameTxt = viewBinding.productInfo.homeProductDetailProductNameTxt;
        productPriceTxt = viewBinding.productInfo.homeProductDetailProductPriceTxt;
        raffleInfoTxt = viewBinding.homeProductDetailProductRaffleCountTxt;
        storeCountLayer = viewBinding.storeCountLayer;
        oneKeySearchPriceLayer = viewBinding.productInfo.onekeySearchPriceLayer;
        dealNumberLayer = viewBinding.productInfo.dealNumberLayer;
        flGoodId = viewBinding.productInfo.flGoodId;
        flSalePrice = viewBinding.productInfo.flSalePrice;
        marketPriceTv = viewBinding.productInfo.marketPriceTv;
        dealTv = viewBinding.productInfo.dealTv;
        marketPriceLeftIcon = viewBinding.productInfo.marketPriceLeftIcon;
        likeIcon = viewBinding.productAction.homeProductDetailLikeIcon;
        likeTxt = viewBinding.productAction.homeProductDetailLikeTxt;
        layoutDislike = viewBinding.productAction.homeProductDetailDislikeLayer;
        layoutLike = viewBinding.productAction.homeProductDetailLikeLayer;
        dislikeIcon = viewBinding.productAction.homeProductDetailDislikeIcon;
        dislikeTxt = viewBinding.productAction.homeProductDetailDislikeTxt;
        priceLayer = viewBinding.productAction.homeProductDetailPriceLayer;
        commentLayer = viewBinding.productAction.homeProductDetailCommentLayer;
        commentTxt = viewBinding.productAction.homeProductDetailCommentTxt;
        videoLayer = viewBinding.productVideo.videoLayer;
        moreVideoFra = viewBinding.productVideo.moreFra;
        videoPlayerFra = viewBinding.productVideo.videoPlayerFra;
        videoFrameImg = viewBinding.productVideo.videoFrameImg;
        videoTitleTv = viewBinding.productVideo.videoTitleTv;
        imgIndexTv = viewBinding.imgIndexTv;
        imgCountTv = viewBinding.imgCountTv;
        llShoeCertification = viewBinding.llShoeCertification;

        viewModel.setProductId(getUri().getQueryParameter(HomeConstant.UriParam.KEY_PRODUCT_ID));
        raffleCount = getIntent().getIntExtra(HomeConstant.UriParam.KEY_PRODUCT_RAFFLE_COUNT, -1);
        if (TextUtils.isEmpty(viewModel.getProductId())) {
            finish();
            return;
        }
        initRaffleRV();
        initTitleBar();
        initVideoView();
        showLoadingView();

        viewModel.getHomeDetailData().observe(this, data -> {
            hideLoadingView();
            setProductInfo(data.getProduct());
            setItemList(data.getRaffles());
            setPriceTabVisible(data.getShowPriceFab());
        });

        viewModel.getActionSuccess().observe(this, this::updateProductView);
        viewModel.getPriceList().observe(this, priceList -> {
            hideLoadingDialog();
            if (priceList != null) {
                showPriceDialog(priceList);
            }
        });
        viewModel.getGoodDetailLiveData().observe(this, this::flashicon);

        showLoadingView();
        viewModel.getHomeDetail();
    }

    private void initVideoView() {
        moreVideoFra.setOnClickListener(view -> {
            if (mHomeProduct == null) {
                return;
            }
            HashMap<String, String> map = new HashMap<>();
            map.put(PostConstant.UriParams.ID, mHomeProduct.getTalkId());
            DcRouter(PostConstant.Uri.MORE_VIEW)
                    .appendParams(map)
                    .start();
        });
    }

    private void initTitleBar() {
        titleBar.setTitle(getString(R.string.
                home_detail_title));
        titleBar.setLineVisibility(false);
        ImageView iv = new ImageView(this);
        KtExtendUtilsKt.setSvgColorRes(iv, R.drawable.ic_share_icon, R.color.series_details_btn_bg);
        titleBar.setRightView(iv);
    }


    private void initRaffleRV() {
        decoration = new StickyHeaderDecoration(0) {
            @Override
            public String getHeaderName(int pos) {
                HomeRaffleDetailItem itemView = rvAdapter.getItemView(pos);
                if (null == itemView) {
                    return null;
                }
                HomeRaffle homeRaffle = itemView.getData();
                if (null == homeRaffle) {
                    return null;
                }
                String headerName = homeRaffle.getHeaderName();
                if (null == headerName) {
                    return null;
                }
                for (HomeProductDetail homeProductDetail : mHomeProductDetailList) {
                    if (headerName.equals(homeProductDetail.getRegion().getName())
                            && mHomeProductDetailList.indexOf(homeProductDetail) == 0) {
                        return "";
                    }
                }
                return headerName;
            }
        };
        decoration.setHeaderHeight(SizeUtils.dp2px(44));
        decoration.setHeaderContentColor(getCompatColor(R.color.color_gray));
        decoration.setTextColor(getCompatColor(R.color.color_text_black1));
        decoration.setTextSize(SizeUtils.sp2px(14));
        decoration.setTextPaddingLeft(SizeUtils.dp2px(12));
        rvAdapter = new BaseRVAdapter<>();
        layoutManager = new LinearLayoutManager(getContext());
        raffleRv.setLayoutManager(layoutManager);
        raffleRv.setAdapter(rvAdapter);
        raffleRv.addItemDecoration(decoration);
    }

    @Override
    protected void initListeners() {
        backIcon.setOnClickListener(v -> finish());
        setOnErrorViewClick(v -> {
            showLoadingView();
            viewModel.getHomeDetail();
        });

        appBarLayout.addOnOffsetChangedListener((appbar, verticalOffset) -> {
            if (null == titleBar) {
                return;
            }
            int totalScrollRange = appbar.getTotalScrollRange();
            if (totalScrollRange == 0) {
                titleBar.setVisibility(View.VISIBLE);
                return;
            }
            int percent = Math.abs(verticalOffset) / totalScrollRange;
            if (percent == 1) {
                titleBar.setVisibility(View.VISIBLE);
            } else {
                if (null != titleBar) {
                    titleBar.clearAnimation();
                    titleBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        raffleRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (canScroll) {
                    canScroll = false;
                    moveToPosition(scrollToPosition);
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isUserTriggers = true;
                    Logger.d("onScrollStateChanged", "SCROLL_STATE_DRAGGING");
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isUserTriggers = false;
                    Logger.d("onScrollStateChanged", "SCROLL_STATE_IDLE");
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isUserTriggers) {
                    int position = layoutManager.findFirstVisibleItemPosition();
                    if (lastPos != position) {
                        switchTab(lastPos, position);
                        Logger.d("onScrolled  lastPos != position ------------");
                    }
                    lastPos = position;
                }
            }
        });

        layoutLike.setOnClickListener(v -> {
            if (null == mHomeProduct) {
                return;
            }
            if (LoginUtils.INSTANCE.getCurrentUser() != null) {
                viewModel.productAction(mHomeProduct.getLiked()
                        ? ServerConstant.Action.RETRIEVE_LIKE
                        : ServerConstant.Action.LIKE);
            }
        });

        layoutDislike.setOnClickListener(v -> {
            if (null == mHomeProduct) {
                return;
            }
            if (LoginUtils.INSTANCE.getCurrentUser() != null) {
                viewModel.productAction(mHomeProduct.getDisliked()
                        ? ServerConstant.Action.RETRIEVE_DISLIKE
                        : ServerConstant.Action.DISLIKE);
            }

        });
        //评论
        commentLayer.setOnClickListener(v -> {
            if (null == mHomeProduct) {
                return;
            }
            HashMap<String, String> map = new HashMap<>();
            map.put(CircleConstant.UriParams.ID, mHomeProduct.getObjectId());
            DcRouter(HomeConstant.Uri.REGION_COMMENT)
                    .dialogBottomAni()
                    .appendParams(map)
                    .putExtra(CalendarConstant.DataParam.KEY_PRODUCT, JSONHelper.toJson(mHomeProduct.sketch()))
                    .start();
        });
        //比价跳转
        priceLayer.setOnClickListener(v -> {
            HashMap<String, String> map = new HashMap<>();
            map.put(PriceConstant.UriParam.ID, viewModel.getProductId());
            DcRouter(PriceConstant.Uri.DETAIL)
                    .appendParams(map)
                    .start();
        });
        //分享到小程序
        shareIcon.setOnClickListener(v -> {
            UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_click_MainPictureShare);
            shareWxMiniProgram();
        });
        titleBar.setRightOnClickListener(v -> shareWxMiniProgram());
        KtExtendUtilsKt.setOnShakeClickListener(oneKeySearchPriceLayer, v -> {
            UTHelper.commonEvent(UTConstant.Calendar.SaleDetailsP_click_DWprice);
            getPrice();
        });

        KtExtendUtilsKt.setOnShakeClickListener(dealNumberLayer, v -> {
            UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_click_transaction);
//            DcRouter(CircleConstant.Uri.FIND_TOPIC_DETAIL)
//                    .putUriParameter(CircleConstant.UriParams.PRODUCT_ID, mHomeProduct.getObjectId())
//                    .putUriParameter(CircleConstant.UriParams.KEYWORD, mHomeProduct.getName())
//                    .putUriParameter(CircleConstant.UriParams.TOPIC_DETAIL_ID, MobileHelper.getInstance().getConfigData().getDealTalkId())
//                    .start();

            DcRouter(CircleConstant.Uri.DEAL_DETAILS)
                    .putUriParameter(CircleConstant.UriParams.ID, mHomeProduct.getObjectId())
                    .start();
        });
        KtExtendUtilsKt.setOnShakeClickListener(llShoeCertification, v -> {
            UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_click_Authentication);
            DcRouter(ShoesConstant.Uri.SERIES_DETAILS)
                    .putUriParameter(ShoesConstant.UriParameter.PRODUCT_ID, mHomeProduct.getObjectId())
                    .putUriParameter(ShoesConstant.Parameter.SERIES_ID, mHomeProduct.getSeriesId())
                    .start();
        });
    }

    private void getPrice() {
        if (LoginUtils.INSTANCE.getCurrentUser() != null) {
            showLoadingDialog();
            viewModel.priceList();
        }
    }

    private void switchTab(int lastPos, int position) {
        String lastPosName = rvAdapter.getItemView(lastPos).getData().getHeaderName();
        String positionName = rvAdapter.getItemView(position).getData().getHeaderName();
        if (!lastPosName.equals(positionName)) {
            Logger.d("lastPosName=" + lastPosName + " positionName=" + positionName);
            for (HomeProductDetail homeProductDetail : mHomeProductDetailList) {
                if (homeProductDetail.getRegion().getName().equals(positionName)) {
                    containerHelper.handlePageSelected(mHomeProductDetailList.indexOf(homeProductDetail));
                }
            }
        }
    }

    private void shareWxMiniProgram() {
        if (null == mHomeProduct) {
            return;
        }
        String path = "pages/start/start?detailId=" + mHomeProduct.getObjectId();
        String title = mHomeProduct.getName() + "有新的发售信息";
        String content = mHomeProduct.getName() + "有新的发售信息";
        String imageUrl = mHomeProduct.getImageUrl();
        ShareHelper shareHelper = new ShareHelper();
        shareHelper.shareToMiniProgram(path, title, content, imageUrl, getContext());
    }

    private void moveToPosition(int position) {
        // 第一个可见的view的位置
        int firstItem = layoutManager.findFirstVisibleItemPosition();
        // 最后一个可见的view的位置
        int lastItem = layoutManager.findLastVisibleItemPosition();
        if (position <= firstItem) {
            // 如果跳转位置firstItem 之前(滑出屏幕的情况)，就smoothScrollToPosition可以直接跳转，
            raffleRv.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 跳转位置在firstItem 之后，lastItem 之间（显示在当前屏幕），smoothScrollBy来滑动到指定位置
            int top = raffleRv.getChildAt(position - firstItem).getTop() - SizeUtils.dp2px(40);
            raffleRv.smoothScrollBy(0, top);
        } else {
            raffleRv.smoothScrollToPosition(position);
            scrollToPosition = position;
            canScroll = true;
        }

    }

    @Override
    public String moduleTag() {
        return ModuleConstant.HOME_MODULE;
    }

    @SuppressLint("SetTextI18n")
    public void setProductInfo(HomeProduct homeProduct) {
        if (raffleCount != -1) {
            homeProduct.setRaffleCount(raffleCount);
        }
        this.mHomeProduct = homeProduct;
        ArrayList<ImageView> imageViews = new ArrayList<>();
        commonImgVpAdapter = new CommonImgVpAdapter(imageViews);
        productIv.setAdapter(commonImgVpAdapter);
        productNameTxt.setText(homeProduct.getName());
        productNameTxt.setOnLongClickListener(v -> {
            String text = productNameTxt.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                ClipboardHelper.INSTANCE.showMenu(getContext(), text, productNameTxt, null);
            }
            return true;
        });
        productSkuTxt.setOnLongClickListener(v -> {
            String text = productSkuTxt.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                ClipboardHelper.INSTANCE.showMenu(getContext(), text, productSkuTxt, null);
            }
            return true;
        });


        if (homeProduct.getExtraImageUrls() != null && homeProduct.getExtraImageUrls().size() > 0) {
            imgCountTv.setText("/" + homeProduct.getExtraImageUrls().size());
            imgIndexTv.setText(1 + "");
            for (String string : homeProduct.getExtraImageUrls()) {
                ImageView imageView = initImg(homeProduct.getExtraImageUrls(), string);
                imageViews.add(imageView);
            }
        } else {
            imgCountTv.setText("/1");
            imgIndexTv.setText(1 + "");
            ArrayList<String> urls = new ArrayList<>();
            urls.add(homeProduct.getImageUrl());
            ImageView imageView = initImg(urls, homeProduct.getImageUrl());
            imageViews.add(imageView);
        }
        commonImgVpAdapter.notifyDataSetChanged();
        productIv.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imgIndexTv.setText(position + 1 + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        String sku = homeProduct.getSku();
        if (TextUtils.isEmpty(sku)) {
            skuLayer.setVisibility(View.GONE);
        } else {
            skuLayer.setVisibility(View.VISIBLE);
            productSkuTxt.setText(sku);


        }
        String price = homeProduct.getPrice();
        if (TextUtils.isEmpty(price) || "null".equalsIgnoreCase(price)) {
            productPriceTxt.setText("" + "未知");
        } else {
            productPriceTxt.setText("" + price);
        }
        if (TextUtils.isEmpty(homeProduct.getMarketPrice())) {
            marketPriceTv.setText("-");
            marketPriceLeftIcon.setVisibility(View.GONE);
        } else {
            marketPriceTv.setText(homeProduct.getMarketPrice());
            marketPriceLeftIcon.setVisibility(View.VISIBLE);
        }

        if (homeProduct.getDealCount() == null || homeProduct.getDealCount() == 0) {
            dealNumberLayer.setVisibility(View.GONE);
            flGoodId.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 140f));
            flSalePrice.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 87f));
            oneKeySearchPriceLayer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 103f));
        } else {
            dealNumberLayer.setVisibility(View.VISIBLE);
            flGoodId.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 90f));
            flSalePrice.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 70f));
            oneKeySearchPriceLayer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 85f));
            dealNumberLayer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 50f));
            dealTv.setText(homeProduct.getDealCount() + "条");
        }


        raffleInfoTxt.setText(homeProduct.getRaffleCount() + "条发售");
        if (homeProduct.getRaffleCount() == 0) {
            storeCountLayer.setVisibility(View.GONE);
        } else {
            storeCountLayer.setVisibility(View.VISIBLE);
        }
        refreshLikeAndDisLike(homeProduct);
        commentTxt.setText(String.valueOf(homeProduct.getCommentCount()));

        List<String> extraImageUrlList = homeProduct.getExtraImageUrls();
        //视频
        ArrayList<VideoEntity> videoMaps = homeProduct.getVideoMaps();
        if (videoMaps == null || videoMaps.size() == 0) {
            videoLayer.setVisibility(View.GONE);
        } else {
            if (videoMaps.size() == 1) {
                moreVideoFra.setVisibility(View.GONE);
            } else {
                moreVideoFra.setVisibility(View.VISIBLE);
            }
            VideoEntity videoEntity = videoMaps.get(0);
            videoLayer.setVisibility(View.VISIBLE);
            videoTitleTv.setText(videoEntity.getTitle());
            if (videoEntity.getImage() != null) {
                GlideHelper.INSTANCE.loadRadiusImage(Uri.parse(videoEntity.getImage()), videoFrameImg, getContext(), 4f);
            }
            videoLayer.setOnClickListener(v -> routeToVideoPlay(videoEntity.getLink(), videoEntity.getImage(), videoEntity.getTitle(), videoEntity.getPostId()));
        }

        if (homeProduct.getShowSeriesEntrance()) {
            llShoeCertification.setVisibility(View.VISIBLE);
        } else {
            llShoeCertification.setVisibility(View.GONE);
        }
    }

    private void refreshLikeAndDisLike(HomeProduct homeProduct) {
        if (null == homeProduct) {
            return;
        }
        if (null == likeIcon || null == dislikeIcon || null == likeTxt || null == dislikeTxt) {
            return;
        }

        likeIcon.setSelected(homeProduct.getLiked());
        dislikeIcon.setSelected(homeProduct.getDisliked());
        likeTxt.setSelected(homeProduct.getLiked());
        dislikeTxt.setSelected(homeProduct.getDisliked());
        likeTxt.setText(String.valueOf(homeProduct.getLikeCount()));
        dislikeTxt.setText(String.valueOf(homeProduct.getDislikeCount()));
    }

    @NotNull
    private ImageView initImg(ArrayList<String> imgs, String imgUrl) {
        ImageView imageView = new ImageView(this);
        ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(240f));
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        GlideHelper.INSTANCE.loadRadiusImage(imgUrl, imageView, this, 0);
        imageView.setOnClickListener(v -> {
            int position = 0;
            ArrayList<ImageInfo> list = new ArrayList<>();
            for (int i = 0; i < imgs.size(); i++) {
                String currentImgUrl = imgs.get(i);
                if (imgUrl.equalsIgnoreCase(currentImgUrl)) {
                    position = i;
                }
                ImageInfo imageInfo = new ImageInfo();

                Uri oldUrl = Uri.parse(currentImgUrl);
                String newUrl = Uri.parse(oldUrl.toString())
                        .buildUpon()
                        .appendQueryParameter("POS", String.valueOf(i))
                        .build().toString();

                imageInfo.setThumbnailUrl(newUrl);
                imageInfo.setOriginUrl(newUrl);
                list.add(imageInfo);
            }
            int realIndex = Integer.parseInt(imgIndexTv.getText().toString());
            if (realIndex - 1 < imgs.size() && realIndex - 1 >= 0) {
                position = realIndex - 1;
            }
            ImagePreview.getInstance()
                    .setContext(HomeProductDetailActivity.this)
                    .setIndex(position)
                    .setEnableDragClose(true)
                    .setFolderName("DingChao")
                    .setShowCloseButton(true)
                    .setLoadStrategy(ImagePreview.LoadStrategy.NetworkAuto)
                    .setErrorPlaceHolder(R.drawable.img_load)
                    .setImageInfoList(list)
                    .setBgColor("ffffff")
                    .start();
        });
        return imageView;
    }

    private void routeToVideoPlay(String url, String imageUrl, String title, String postId) {
        HashMap<String, String> map = new HashMap<>();
        map.put(CommonConstant.UriParams.CIRCLE_VIDEO_COVER, imageUrl == null ? "" : imageUrl);
        map.put(CommonConstant.UriParams.CIRCLE_VIDEO_TITLE, title == null ? "" : title);
        map.put(CommonConstant.UriParams.CIRCLE_DYNAMIC_ID, postId == null ? "" : postId);
        DcRouter(CommonConstant.Uri.VIDEO_VIEW)
                .appendParams(map)
                .start();
    }

    public void setPriceTabVisible(boolean isVis) {
//        priceLayer.setVisibility(isVis ? View.VISIBLE : View.GONE);
    }

    private void setTab(List<String> titleList) {
        CommonNavigatorNew commonNavigator = new CommonNavigatorNew(getContext());
        TabScaleAdapter tabPageAdapter = new TabScaleAdapter(titleList);
        tabPageAdapter.setStartAndEndTitleSize(14, 16);
        tabPageAdapter.setSelectedColor(getCompatColor(R.color.color_text_black1));
        tabPageAdapter.hideIndicator();
        tabPageAdapter.setTabSelectListener(index -> {
            containerHelper.handlePageSelected(index);
            switchToItem(index);
        });
        commonNavigator.setAdapter(tabPageAdapter);
        magicIndicator.setNavigator(commonNavigator);

        containerHelper = new FragmentContainerHelper();
        containerHelper.attachMagicIndicator(magicIndicator);
    }

    private void switchToItem(int tabIndex) {
        HomeProductDetail homeProductDetail = mHomeProductDetailList.get(tabIndex);
        if (null == homeProductDetail) {
            return;
        }
        List<HomeRaffle> raffleList = homeProductDetail.getRaffles();
        if (CollectionUtils.isEmpty(raffleList)) {
            return;
        }
        HomeRaffle homeRaffle = raffleList.get(0);
        if (homeRaffle != null && homeRaffle.getBrand() != null) {
            Logger.d("switchToItem to tabIndex=" + tabIndex + " firstItem=" + homeRaffle.getBrand().getName());
        }
        for (HomeRaffleDetailItem homeRaffleItem : rvAdapter.getItemList()) {
            if (homeRaffle == homeRaffleItem.getData()) {
                moveToPosition(rvAdapter.getItemList().indexOf(homeRaffleItem));
            }
        }
    }

    public void updateProductView(String action) {
        if (null == mHomeProduct) {
            return;
        }
        switch (action) {
            case ServerConstant.Action.RETRIEVE_LIKE:
            case ServerConstant.Action.LIKE:
                int likeCount = mHomeProduct.getLikeCount();
                mHomeProduct.setLikeCount(mHomeProduct.getLiked() ? --likeCount : ++likeCount);
                mHomeProduct.setLiked(!mHomeProduct.getLiked());
                if (mHomeProduct.getDisliked() && mHomeProduct.getLiked()) {
                    mHomeProduct.setDisliked(false);
                    mHomeProduct.setDislikeCount(mHomeProduct.getDislikeCount() - 1);
                }
                refreshLikeAndDisLike(mHomeProduct);
                break;
            case ServerConstant.Action.RETRIEVE_DISLIKE:
            case ServerConstant.Action.DISLIKE:
                int dislikeCount = mHomeProduct.getDislikeCount();
                mHomeProduct.setDislikeCount(mHomeProduct.getDisliked() ? --dislikeCount : ++dislikeCount);
                mHomeProduct.setDisliked(!mHomeProduct.getDisliked());
                if (mHomeProduct.getLiked() && mHomeProduct.getDisliked()) {
                    mHomeProduct.setLiked(false);
                    mHomeProduct.setLikeCount(mHomeProduct.getLikeCount() - 1);
                }
                refreshLikeAndDisLike(mHomeProduct);
                break;
            default:
        }
    }

    private void openChooseDialog(HomeRaffle homeRaffle) {
        if (homeRaffle.getId() == null) return;
        showLoadingDialog();
        viewModel.calendarApi.smsInputTemplate(homeRaffle.getId()).subscribe(res -> {
            hideLoadingDialog();
            if (!res.getErr() && res.getRes() != null) {
                if (homeRaffle.getSms() != null) {
                    smsRegistrationDialog.setSmsData(homeRaffle.getId(), homeRaffle.getSms(), res.getRes());
                    smsRegistrationDialog.show(getSupportFragmentManager(), "smsRegistrationDialog");
                }
            } else {
                ToastUtil.getInstance()._short(getContext(), res.getMsg());
            }
        }, err -> {
            hideLoadingDialog();
            ToastUtil.getInstance()._short(getContext(), err.getMessage());
        });
    }
//		homeRaffle?.sms?.let { homeRaffleSmsBean ->
//				//可以改到viewModel中去
//				showLoadingDialog()
//			viewModel.calendarApi.smsInputTemplate(homeRaffle.id!!)
//                    .subscribe({
//					hideLoadingDialog()
//			if (!it.err && it.res != null) {
//				smsRegistrationDialog.setSmsData(homeRaffle.id!!, homeRaffleSmsBean, it.res!!)
//				smsRegistrationDialog.show(supportFragmentManager, "smsRegistrationDialog")
//			} else {
//				ToastUtil.getInstance()._short(context, it.msg)
//			}
//                    }, {
//				hideLoadingDialog()
//				ToastUtil.getInstance()._short(context, it.message)
//			})
//		}


    public void setItemList(List<HomeProductDetail> homeProductDetailList) {
        if (CollectionUtils.isEmpty(homeProductDetailList)) {
            rvAdapter.showEmptyView("已结束");
            return;
        }
        AnimationDrawHelper animationDrawHelper = new AnimationDrawHelper();
        boolean isEmpty = true;
        for (HomeProductDetail hpd : homeProductDetailList) {
            if (hpd != null && hpd.getRaffles() != null && hpd.getRaffles().size() > 0) {
                isEmpty = false;
                break;
            }
        }
        if (isEmpty) {
            rvAdapter.showEmptyView("已结束");
            return;
        }
        rvAdapter.hideEmptyView();
        this.mHomeProductDetailList = homeProductDetailList;
        List<String> titleList = new ArrayList<>();
        for (HomeProductDetail homeProductDetail : homeProductDetailList) {
            HomeRegion region = homeProductDetail.getRegion();
            if (null == region) {
                continue;
            }
            List<HomeRaffleDetailItem> itemList = new ArrayList<>();
            List<HomeRaffle> raffles = homeProductDetail.getRaffles();
            if (raffles == null) {
                continue;
            }
            for (int i = 0; i < raffles.size(); i++) {
                HomeRaffle homeRaffle = raffles.get(i);
                homeRaffle.setHeaderName(region.getName());
                HomeRaffleDetailItem homeRaffleItem = new HomeRaffleDetailItem(homeRaffle);
                homeRaffleItem.setSource("发售详情");
                homeRaffleItem.setAnimationDrawHelper(animationDrawHelper);
                homeRaffleItem.setLast(i == raffles.size() - 1);
                homeRaffleItem.setStart(i == 0);
                homeRaffleItem.setShowWhere(HomeRaffleDetailItem.ShowWhere.PRODUCT_DETAILS);
                homeRaffleItem.setMListener(new HomeRaffleDetailItem.ActionListener() {
                    @Override
                    public void onShareClick(HomeRaffle homeRaffle) {
                        showShareView(homeRaffle);
                    }

                    @Override
                    public void onSmsClick(HomeRaffle homeRaffle) {
                        if (!LoginUtils.INSTANCE.isLoginAndRequestLogin(getContext())) {
                            return;
                        }
                        if (System.currentTimeMillis() < homeRaffle.getSmsStartAt()) {
                            new CommonDialog.Builder(getContext())
                                    .content("短信抽签未开始\n短信抽签将于" + TimeUtils.formatTimestampS2NoYea2r(homeRaffle.getSmsStartAt()) + "开始，请稍后再试～")
                                    .confirmTxt("提醒我")
                                    .cancelTxt("取消")
                                    .onConfirmClick(v -> {
                                        if (LoginUtils.INSTANCE.getCurrentUser() != null) {
                                            requestCalendarPermission(homeRaffle);
                                        }
                                    })
                                    .builder()
                                    .show();
                            return;
                        }
                        if (homeRaffle.getShowOldPage() != null && homeRaffle.getShowOldPage()) {
                            if (LoginUtils.INSTANCE.getCurrentUser() != null) {
                                routerToSms(homeRaffle);
                            }
                        } else {
                            openChooseDialog(homeRaffle);
                        }
                    }

                    @Override
                    public void onAlarmClick(HomeRaffle homeRaffle, int pos) {
                        if (LoginUtils.INSTANCE.getCurrentUser() != null) {
                            String id = "";
                            String productName = "";
                            String brandName = "";
                            String method = "";
                            String price = "";

                            if (homeRaffle.getId() != null) {
                                id = homeRaffle.getId();
                            }
                            if (viewModel.getProductName() != null) {
                                productName = viewModel.getProductName();
                            }
                            if (homeRaffle.getBrand() != null && homeRaffle.getBrand().getName() != null) {
                                brandName = homeRaffle.getBrand().getName();
                            }
                            if (homeRaffle.getMethod() != null) {
                                method = homeRaffle.getMethod();
                            }
                            if (homeRaffle.getPrice() != null) {
                                price = homeRaffle.getPrice();
                            }

                            MonitorRemindHelper monitorRemindHelper = new MonitorRemindHelper(AlarmFromWhere.PRODUCT_DETAIL, pos, "");

                            String remindMsg = monitorRemindHelper.generateRemindMsg(
                                    productName,
                                    brandName,
                                    homeRaffle.getNotifyDate(),
                                    method,
                                    price);

                            if (homeRaffle.getReminded()) {
                                monitorRemindHelper.cancelRemind(
                                        getContext(),
                                        id,
                                        remindMsg
                                );
                            } else {
                                monitorRemindHelper.setRemind(
                                        getContext(),
                                        getSupportFragmentManager(),
                                        id,
                                        remindMsg,
                                        homeRaffle.getNotifyDate()
                                );
                            }
                        }
                    }

                    @Override
                    public void onFlashClick(HomeRaffle homeRaffle) {
                        if (LoginUtils.INSTANCE.getCurrentUser() != null) {
                            viewModel.parseLink(homeRaffle.getBpLink());
                        }
                    }
                });
                itemList.add(homeRaffleItem);
            }
            rvAdapter.addItemViewList(itemList);
            titleList.add(region.getName());
            //space footer
            if (homeProductDetailList.indexOf(homeProductDetail) == homeProductDetailList.size() - 1) {
                lastSectionHeight += SizeUtils.dp2px(130) * (homeProductDetail.getRaffles().size());
                if (homeProductDetailList.size() > 1) {
                    lastSectionHeight += SizeUtils.dp2px(40);
                }
                raffleRv.post(() -> {
                    //为底下控制栏留空隙
                    rvAdapter.addFootView(new ProductDetailFooter(SizeUtils.dp2px(80)));
                });
            }
        }
        //tab
        setTab(titleList);

    }

    public void flashicon(GoodDetailEntity goodDetailEntity) {
        DcRouter(HomeConstant.Uri.BP_GOODS_DETAIL)
                .putExtra(HomeConstant.UriParam.KEY_GOOD_DETAIL, goodDetailEntity)
                .start();
    }

    private void requestCalendarPermission(HomeRaffle homeRaffle) {
        mActionHomeRaffle = homeRaffle;
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_CALENDAR, ++mRequestCode);
        } else {
            remind();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode) {
            boolean resultFlag = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != 0) {
                    resultFlag = false;
                    break;
                }
            }
            if (resultFlag) {
                remind();
            } else {
                showToastLong(R.string.home_setup_failed);
            }
            mActionHomeRaffle = null;
        } else {//添加日历事件申请权限被拒绝后
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    new CommonDialog.Builder(this)
                            .content("需要获得授权访问您的日历")
                            .confirmTxt("前往授权")
                            .cancelTxt("取消")
                            .onConfirmClick((v) -> {
                                PPermissionUtils routeHelper = new PPermissionUtils(getContext());
                                routeHelper.gotoPermissionSet();
                            })
                            .builder()
                            .show();
                    return;
                }
            }
        }
    }

    private void remind() {
        if (null == mHomeProduct || null == mActionHomeRaffle) {
            return;
        }
        HomeBrandBean brand = mActionHomeRaffle.getBrand();
        String place = null == brand ? "" : brand.getName();
        String releaseDateString = mActionHomeRaffle.getReleaseDateString();
        String method = mActionHomeRaffle.getMethod();
        String des = mHomeProduct.getName()
                + "\n" + place
                + "\n" + releaseDateString
                + "\n" + method;
        Long notifyData;
        //如果后端设置了短信时间并且未到达短信开始时间,将短信时间设置到提醒日历
        if (mActionHomeRaffle.getSmsStartAt() > System.currentTimeMillis()) {
            notifyData = mActionHomeRaffle.getSmsStartAt();
        } else {
            notifyData = TimeUtils.format2Mill(mActionHomeRaffle.getNotifyDate());
        }
        boolean addSuccess = CalendarReminderUtils
                .addCalendarEvent(this, des, "", notifyData);
        showToastShort(addSuccess ? R.string.home_setup_success : R.string.home_setup_failed);
        mActionHomeRaffle = null;
    }

    private void routerToSms(HomeRaffle homeRaffle) {
        Long smsStartAt = homeRaffle.getSmsStartAt();
        if (null != smsStartAt && smsStartAt > System.currentTimeMillis()) {
            showTimingDialog(smsStartAt, homeRaffle);
        } else {
            DcRouter(HomeConstant.Uri.SMS_EDIT)
                    .putExtra(CalendarConstant.DataParam.KEY_SMS, homeRaffle.getSms())
                    .start();
        }
    }

    private void showTimingDialog(Long smsStartAt, HomeRaffle homeRaffle) {
        String niceDate = TimeUtils.formatTimestampCustom(smsStartAt, "MM月dd日 HH:mm");
        String messageContent = "抽签将在" + niceDate + " 开始," + "请稍后再试";
        new DcTitleDialog.Builder(this)
                .title("短信抽签还未开始")
                .content(messageContent)
                .cancelTxt("取消")
                .confirmTxt("提醒我")
                .onConfirmClick((v) -> {
                    requestCalendarPermission(homeRaffle);
                })
                .builder()
                .show();
    }

    private void showShareView(HomeRaffle homeRaffle) {
        if (null == homeRaffle || null == mHomeProduct) {
            return;
        }
        String content = String.format(getString(R.string.raffle_share_content_template),
                homeRaffle.getBrand().getName(),
                mHomeProduct.getName());

        ShareType type = ShareType.Link;
        ShareParams params = new ShareParams();
        params.setTitle(getString(R.string.raffle_share_title));
        params.setContent(content);
        params.setImageUrl(mHomeProduct.getImageUrl());
        params.setLink(TextUtils.isEmpty(homeRaffle.getShareLink()) ? homeRaffle.getLink() : homeRaffle.getShareLink());
        type.setParams(params);

        ShareDialog shareDialog = new ShareDialog(getContext());
        shareDialog.setShareType(type);
        shareDialog.show();
    }

    public void showPriceDialog(PriceListResultEntity entity) {
        dialog.setData(entity);
        dialog.show(getSupportFragmentManager(), "ComparisonPriceDialog");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshAlarm(AlarmRefreshEvent event) {
        if (event.getFromWhere() == AlarmFromWhere.PRODUCT_DETAIL) {
            rvAdapter.getItemView(event.getPos()).getData().setReminded(event.isLightUpAlarm());
            rvAdapter.notifyItemChanged(event.getPos());
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
