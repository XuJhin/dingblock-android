package cool.dingstock.home.ui.dingchao

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.youth.banner.Banner
import com.youth.banner.loader.ImageLoaderInterface
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.HomeData
import cool.dingstock.appbase.entity.bean.home.HomeItem
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.mvp.lazy.VmLazyFragment
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.home.R
import cool.dingstock.home.databinding.HomeHeadItemBinding
import cool.dingstock.lib_base.util.SizeUtils

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/27  14:57
 */
class HomeItemFragment : VmBindingLazyFragment<BaseViewModel, HomeHeadItemBinding>() {
    private val HOME_ADV_URL = "homeAdvUrl"
    private val HOME_ADV_DELETE = "homeAdvDelete"
    var homeTopComonent: HomeTopComponentView? = null
    private var homeItem: HomeItem? = null
    private var homeItemBanner: Banner? = null

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        homeTopComonent = rootView?.findViewById(R.id.transverseCardView)
        homeItemBanner = rootView?.findViewById(R.id.home_adv_banner)

        val targetWidth = 375.azDp
        val screenWidth = SizeUtils.getWidth()
        val scale = screenWidth / targetWidth
        //设置文字大小
        autoAdaptation(viewBinding.itemTitle1, viewBinding.subtitleTv1, scale)
        autoAdaptation(viewBinding.itemTitle2, viewBinding.subtitleTv2, scale)
        autoAdaptation(viewBinding.itemTitle3, viewBinding.subtitleTv3, scale)
    }

    override fun initListeners() {

    }

    override fun onLazy() {
    }

    fun setData(homeData: HomeData) {
        this.homeItem = homeData.items
        if (homeItem?.cardItems == null) {
            homeTopComonent?.hide(true)
        }


        homeTopComonent?.hide(false)
        homeTopComonent?.setData(homeItem?.cardItems)
        homeData.boxItem?.let {
            homeTopComonent?.setRightData(it)
        }

        viewBinding.itemIv1.load(homeData.funcItem1?.imageUrl)
        viewBinding.itemTitle1.text = homeData.funcItem1?.title ?: ""
        viewBinding.subtitleTv1.text = homeData.funcItem1?.subtitle ?: ""
        viewBinding.itemIv1.setOnShakeClickListener {
            UTHelper.commonEvent(
                "${UTConstant.Home.HomeP_click_HomeItemT_}${homeData.funcItem1?.id}",
                "position",
                "1",
                "title",
                homeData.funcItem1?.title
            )
            DcRouter(homeData.funcItem1?.targetUrl ?: "")
                .start()
        }


        viewBinding.itemIv2.load(homeData.funcItem2?.imageUrl)
        viewBinding.itemTitle2.text = homeData.funcItem2?.title ?: ""
        viewBinding.subtitleTv2.text = homeData.funcItem2?.subtitle ?: ""
        viewBinding.itemIv2.setOnShakeClickListener {
            UTHelper.commonEvent(
                "${UTConstant.Home.HomeP_click_HomeItemT_}${homeData.funcItem2?.id}",
                "position",
                "2",
                "title",
                homeData.funcItem2?.title
            )
            DcRouter(homeData.funcItem2?.targetUrl ?: "")
                .start()
        }

        viewBinding.itemIv3.load(homeData.funcItem3?.imageUrl)
        viewBinding.itemTitle3.text = homeData.funcItem3?.title ?: ""
        viewBinding.subtitleTv3.text = homeData.funcItem3?.subtitle ?: ""
        viewBinding.itemIv3.setOnShakeClickListener {
            UTHelper.commonEvent(
                "${UTConstant.Home.HomeP_click_HomeItemT_}${homeData.funcItem3?.id}",
                "position",
                "3",
                "title",
                homeData.funcItem3?.title
            )
            DcRouter(homeData.funcItem3?.targetUrl ?: "")
                .start()
        }



        if (homeItem?.bannerItems == null || homeItem?.bannerItems?.size == 0) {
            viewBinding.homeAdvCard.hide(true)
        } else {
            viewBinding.homeAdvCard.hide(false)
            homeItemBanner?.apply {
                setImageLoader(object : ImageLoaderInterface<ShapeableImageView> {
                    override fun displayImage(
                        context: Context?,
                        path: Any?,
                        imageView: ShapeableImageView?
                    ) {
                        if (imageView != null) {
                            Glide.with(this@HomeItemFragment).load(path).into(imageView)
                        }
                    }

                    override fun createImageView(context: Context?): ShapeableImageView {
                        return ShapeableImageView(context).apply {
                            shapeAppearanceModel = ShapeAppearanceModel.builder()
                                .setAllCorners(CornerFamily.ROUNDED, 8.dp)
                                .build()
                        }
                    }
                })
                setImages(homeItem?.bannerItems?.map { it.imageUrl })
                setOnBannerListener { index ->
                    homeItem?.bannerItems?.get(index)?.targetUrl?.let {
                        UTHelper.commonEvent(
                            UTConstant.Home.HomeP_click_Banner + "$index",
                            "route",
                            homeItem?.bannerItems?.get(index)?.id ?: ""
                        )
                        context?.let { context ->
                            DcUriRequest(context, it).start()
                        }
                    }
                }
                start()
            }
        }
    }


    fun autoAdaptation(titleTv: TextView, subTileTextView: TextView, scale: Float) {

        titleTv.setTextSize(13 * scale)
        titleTv.paint.isFakeBoldText = true
        subTileTextView.setTextSize(10 * scale)
        val lp = subTileTextView.layoutParams as ViewGroup.MarginLayoutParams
        lp.marginEnd = (42 * scale).azDp.toInt()
        subTileTextView.layoutParams = lp

    }


}

