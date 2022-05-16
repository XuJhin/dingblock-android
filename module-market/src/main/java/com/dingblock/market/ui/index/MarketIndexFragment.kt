package com.dingblock.market.ui.index

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.dingblock.market.R
import com.dingblock.market.databinding.MarketFragmentIndexBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.youth.banner.loader.ImageLoaderInterface
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.setCorner
import cool.dingstock.appbase.viewmodel.HomeIndexViewModel
import cool.dingstock.foundation.ext.imageview.corner
import cool.dingstock.foundation.ext.viewbinding.binding
import cool.dingstock.lib_base.util.Logger
import net.dingblock.mobile.base.fragment.BaseBindingFragment

/**
 * 市场主页
 */
class MarketIndexFragment : BaseBindingFragment<MarketFragmentIndexBinding>() {

    val vm: MarketIndexViewModel by viewModels()
    val actVM: HomeIndexViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.marketBanner.apply {
            setImageLoader(object : ImageLoaderInterface<ShapeableImageView> {
                override fun displayImage(
                    context: Context?,
                    path: Any?,
                    imageView: ShapeableImageView?
                ) {
                    if (imageView != null) {
                        Glide.with(this@MarketIndexFragment).load(path).into(imageView)
                    }
                }

                override fun createImageView(context: Context?): ShapeableImageView {

                    return ShapeableImageView(context).apply {
                        corner(8)
                    }
                }
            })
            setOnBannerListener { index ->
//                UTHelper.commonEvent(UTConstant.BP.BpSecKill_MOUTAI_Banner)
//                viewModel.homeBean.value?.headerInfo?.banners?.get(index)?.linkUrl?.let {
//                    DcRouter(it).start()
//                }
            }
        }
    }


    override fun onVisibleFirst() {
        super.onVisibleFirst()

        Logger.d("fragment 可见,MarketIndexFragment main First visible")
    }

    override fun onVisible() {
        super.onVisible()
        Logger.d("fragment 可见", "MarketIndexFragment main visible")
    }
}