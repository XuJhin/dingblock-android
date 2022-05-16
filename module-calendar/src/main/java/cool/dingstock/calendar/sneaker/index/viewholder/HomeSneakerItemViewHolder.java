package cool.dingstock.calendar.sneaker.index.viewholder;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.bumptech.glide.load.resource.bitmap.CenterInside;

import java.util.ArrayList;

import com.lihang.ShadowLayout;
import cool.dingstock.appbase.SpanUtils;
import cool.dingstock.appbase.constant.CalendarConstant;
import cool.dingstock.appbase.constant.CircleConstant;
import cool.dingstock.appbase.constant.HomeConstant;
import cool.dingstock.appbase.constant.ServerConstant;
import cool.dingstock.appbase.constant.UTConstant;
import cool.dingstock.appbase.entity.bean.home.HomeProduct;
import cool.dingstock.appbase.entity.bean.home.fashion.VideoEntity;
import cool.dingstock.appbase.ext.ImageViewExtKt;
import cool.dingstock.appbase.net.api.account.AccountHelper;
import cool.dingstock.appbase.net.api.calendar.CalendarHelper;
import cool.dingstock.appbase.net.parse.ParseCallback;
import cool.dingstock.appbase.router.DcUriRequest;
import cool.dingstock.appbase.textview.AlignImageSpan;
import cool.dingstock.appbase.toast.TopToast;
import cool.dingstock.appbase.ut.UTHelper;
import cool.dingstock.appbase.util.LoginUtils;
import cool.dingstock.appbase.widget.stickyheaders.SectioningAdapter;
import cool.dingstock.calendar.R;
import cool.dingstock.calendar.databinding.HomeItemSneakerBinding;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.util.Logger;
import cool.dingstock.lib_base.util.SizeUtils;
import cool.dingstock.lib_base.util.ToastUtil;
import cool.mobile.account.share.ShareHelper;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class HomeSneakerItemViewHolder extends SectioningAdapter.ItemViewHolder {

    ImageView productIv;
    TextView nameTxt;
    TextView infoTxt;
    TextView priceTxt;
    View tradingLayer;
    TextView tradingTv;
    LinearLayout commentLayer;
    TextView commentTxt;
    LinearLayout likeLayer;
    TextView likeTxt;
    ImageView likeIcon;
    ViewGroup disLikeLayer;
    TextView disLikeTxt;
    ImageView disLikeIcon;
    View rootCardView;
    ShadowLayout layoutOnline;

    private HomeProduct mHomeProduct;

    public HomeSneakerItemViewHolder(View itemView) {
        super(itemView);
        HomeItemSneakerBinding vb = HomeItemSneakerBinding.bind(itemView);
        productIv = vb.homeItemRegionRaffleProductIv;
        nameTxt = vb.homeItemRegionRaffleProductNameTxt;
        infoTxt = vb.homeItemRegionRaffleInfoTxt;
        priceTxt = vb.homeItemRegionRaffleProductPriceTxt;
        tradingLayer = vb.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleProductTradingLayer;
        tradingTv = vb.homeItemRegionRaffleCommentAllLayer.tradingTv;
        commentLayer = vb.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleCommentLayer;
        commentTxt = vb.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleCommentTxt;
        likeLayer = vb.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleLikeLayer;
        likeTxt = vb.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleLikeTxt;
        likeIcon = vb.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleLikeIcon;
        disLikeLayer = vb.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleDislikeLayer;
        disLikeTxt = vb.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleDislikeTxt;
        disLikeIcon = vb.homeItemRegionRaffleCommentAllLayer.homeItemRegionRaffleDislikeIcon;
        rootCardView = vb.rootCardView;
        layoutOnline = vb.layoutOnline;
    }

    public void onBindItemViewHolder(HomeProduct homeProduct) {
        this.mHomeProduct = homeProduct;
        ImageViewExtKt.load(productIv, homeProduct.getImageUrl(), SizeUtils.dp2px(2), RoundedCornersTransformation.CornerType.ALL,
                new CenterInside());
        nameTxt.setText(mHomeProduct.getName());
        String price = mHomeProduct.getPrice();
        if (TextUtils.isEmpty(price) || "null".equalsIgnoreCase(price)) {
            priceTxt.setText("");
        } else {
            priceTxt.setText("·发售价:" + price);
        }
        if (mHomeProduct.getDealCount() == null || mHomeProduct.getDealCount() == 0) {
            tradingTv.setText("交易");
        } else {
            tradingTv.setText("交易(" + mHomeProduct.getDealCount() + ")");
        }

        infoTxt.setText(String.valueOf(homeProduct.getRaffleCount()));
        commentTxt.setText(String.valueOf(homeProduct.getCommentCount()));
        setLikeAndDislikeData(likeIcon, likeTxt, disLikeIcon, disLikeTxt);
        if (mHomeProduct.getHasOnlineRaffle()) {
            layoutOnline.setVisibility(View.VISIBLE);
        } else {
            layoutOnline.setVisibility(View.GONE);
        }
        //分享
//        productShareV.setOnClickListener(v -> {
//            String path = "pages/start/start?detailId=" + homeProduct.getObjectId();
//            String title = homeProduct.getName() + "有新的发售信息";
//            String content = homeProduct.getName() + "有新的发售信息";
//            String imageUrl = homeProduct.getImageUrl();
//            ShareHelper shareHelper = new ShareHelper();
//            shareHelper.shareToMiniProgram(path, title, content, imageUrl, getContext());
//        });
        tradingLayer.setOnClickListener(v -> {
            UTHelper.commonEvent(UTConstant.Calendar.CalendarP_click_Transaction);
            new DcUriRequest(getContext(), CircleConstant.Uri.DEAL_DETAILS)
                    .putUriParameter(CircleConstant.UriParams.ID, mHomeProduct.getObjectId())
                    .start();
        });


        //评论
        commentLayer.setOnClickListener(v ->
                Router(HomeConstant.Uri.REGION_COMMENT)
                        .dialogBottomAni()
                        .putUriParameter(CircleConstant.UriParams.ID, homeProduct.getObjectId())
                        .putExtra(CalendarConstant.DataParam.KEY_PRODUCT, JSONHelper.toJson(homeProduct.sketch()))
                        .start());

        rootCardView.setOnClickListener(v -> {
            UTHelper.commonEvent(UTConstant.SaleDetails.SaleDetailsP_source, "position", "球鞋日历列表");
            UTHelper.commonEvent(UTConstant.Calendar.CalendarP_click_SneakersList);
            Router(HomeConstant.Uri.DETAIL)
                    .putUriParameter(HomeConstant.UriParam.KEY_PRODUCT_ID, mHomeProduct.getObjectId())
                    .putExtra(HomeConstant.UriParam.KEY_PRODUCT_RAFFLE_COUNT, mHomeProduct.getRaffleCount())
                    .start();
        });
        //视频
        ArrayList<VideoEntity> videoMaps = homeProduct.getVideoMaps();
        if (videoMaps != null && videoMaps.size() > 0) {
            //在Text后面添加字
            String name = mHomeProduct.getName() + " ";
            SpannableStringBuilder sb = new SpannableStringBuilder(name);
            String imaStr = "img";
            sb.append(imaStr);
            AlignImageSpan imageSpan = new AlignImageSpan(getContext(), R.drawable.product_video_icon);
            sb.setSpan(imageSpan, name.length(), name.length() + imaStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            nameTxt.setText(sb);
        }
    }

    private void productAction(String action, String productId) {
        if (null == AccountHelper.getInstance().getUser()) {
            return;
        }
        CalendarHelper.INSTANCE.productAction(action, productId, new ParseCallback<String>() {
            @Override
            public void onSucceed(String data) {
                if (null == itemView) {
                    return;
                }
                updateProductView(productId, action);
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                Logger.e("productAction action=" + action
                        + " productId=" + productId + " failed --");
            }
        });
    }

    private void setLikeAndDislikeData(ImageView productLikeIcon,
                                       TextView productLikeTxt,
                                       ImageView productDislikeIcon,
                                       TextView productDislikeTxt) {

        productLikeTxt.setText(String.valueOf(mHomeProduct.getLikeCount()));
        productLikeIcon.setSelected(mHomeProduct.getLiked());
        productLikeTxt.setSelected(mHomeProduct.getLiked());
        likeLayer.setOnClickListener(v -> {
            if (!LoginUtils.INSTANCE.isLoginAndRequestLogin(getContext())) {
                return;
            }
            productAction(!mHomeProduct.getLiked() ? ServerConstant.Action.LIKE :
                    ServerConstant.Action.RETRIEVE_LIKE, mHomeProduct.getObjectId());

        });

        productDislikeTxt.setText(String.valueOf(mHomeProduct.getDislikeCount()));
        productDislikeIcon.setSelected(mHomeProduct.getDisliked());
        productDislikeTxt.setSelected(mHomeProduct.getDisliked());
        disLikeLayer.setOnClickListener(v -> {
                    if (LoginUtils.INSTANCE.isLoginAndRequestLogin(getContext())) {
                        productAction(!mHomeProduct.getDisliked() ? ServerConstant.Action.DISLIKE :
                                ServerConstant.Action.RETRIEVE_DISLIKE, mHomeProduct.getObjectId());
                    }
                }
        );

    }

    private void updateProductView(String productId, String action) {
        if (TextUtils.isEmpty(action) || TextUtils.isEmpty(productId)) {
            return;
        }
        switch (action) {
            case ServerConstant.Action.LIKE:
                refreshLike(true);
                break;
            case ServerConstant.Action.RETRIEVE_LIKE:
                refreshLike(false);
                break;
            case ServerConstant.Action.DISLIKE:
                refreshDisLike(true);
                break;
            case ServerConstant.Action.RETRIEVE_DISLIKE:
                refreshDisLike(false);
                break;
            default:
        }
    }

    private void refreshLike(boolean isLike) {
        mHomeProduct.setLiked(isLike);
        int likeCount = mHomeProduct.getLikeCount();
        mHomeProduct.setLikeCount(isLike ? ++likeCount : --likeCount);

        if (mHomeProduct.getDisliked() && isLike) {
            refreshDisLike(false);
        }
        setLikeAndDislikeData(likeIcon, likeTxt, disLikeIcon, disLikeTxt);
    }

    private void refreshDisLike(boolean isDisLike) {
        mHomeProduct.setDisliked(isDisLike);
        int dislikeCount = mHomeProduct.getDislikeCount();
        mHomeProduct.setDislikeCount(isDisLike ? ++dislikeCount : --dislikeCount);
        if (mHomeProduct.getLiked() && isDisLike) {
            refreshLike(false);
        }
        setLikeAndDislikeData(likeIcon, likeTxt, disLikeIcon, disLikeTxt);
    }

    public String getString(@StringRes int resId, Object... formatArgs) {
        return getContext().getString(resId, formatArgs);
    }

    public Context getContext() {
        return itemView.getContext();
    }

    public void showToastShort(final @StringRes int resId) {
        showToastShort(getString(resId));
    }

    public String getString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    public void showToastShort(final CharSequence text) {
        TopToast.INSTANCE.showToast(getContext(), text.toString(), Toast.LENGTH_SHORT);
    }

    public DcUriRequest Router(@NonNull String uri) {
        return new DcUriRequest(getContext(), uri);
    }

}
