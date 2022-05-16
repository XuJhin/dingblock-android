package cool.dingstock.post.ui.post.nearby

import android.Manifest
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.permissionx.guolindev.PermissionX
import com.sankuai.waimai.router.annotation.RouterUri
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener
import com.shuyu.gsyvideoplayer.GSYVideoManager
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.circle.LocationEntityPermission
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.util.LocationHelper
import cool.dingstock.appbase.util.OnLocationResultListener
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.util.isWhiteMode
import cool.dingstock.appbase.widget.CommonEmptyView
import cool.dingstock.lib_base.util.SizeUtils
import cool.dingstock.post.R
import cool.dingstock.post.adapter.DynamicBinderAdapter
import cool.dingstock.post.databinding.ActivityNearbyDetailsBinding
import cool.dingstock.post.helper.setVideoAutoPlay
import cool.dingstock.post.item.DynamicItemBinder
import kotlin.math.abs
import kotlin.math.absoluteValue

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [CircleConstant.Path.NEARBY_DETAILS]
)
class NearbyDetailsActivity : VMBindingActivity<NearbyDetailsVM, ActivityNearbyDetailsBinding>() {

    val adapter = DynamicBinderAdapter(arrayListOf())

    var mOffset = 0f
    private var mScrollY = 0
    private val imageDistance: Int = 300.azDp.toInt()

    override fun moduleTag(): String = ModuleConstant.POST

    override fun initViewAndEvent(savedInstanceState: Bundle?) {

        viewModel.locationId = uri.getQueryParameter(CircleConstant.UriParams.ID) ?: ""

        StatusBarUtil.transparentStatus(this)
        initAdapter()
        requestLocationPermission()
        viewModel.apply {
            locationLiveData.observe(this@NearbyDetailsActivity) {
                viewBinding.tvLocationDesc.text = it.postCountStr
                viewBinding.tvLocationTitle.text = it.recommendLocation
                viewBinding.tvTitle.text = it.recommendLocation
            }
            loadMoreList.observe(this@NearbyDetailsActivity) {
                adapter.addData(it)
                adapter.loadMoreModule.loadMoreComplete()
                if (it.isEmpty()) {
                    adapter.loadMoreModule.loadMoreEnd(false)
                }
            }
            dataList.observe(this@NearbyDetailsActivity) {
                viewBinding.smartRefreshLayout.finishRefresh()
                adapter.setList(it)
            }
        }
    }


    override fun initListeners() {
        viewBinding.ivBack.setOnClickListener {
            finish()
        }
        viewBinding.smartRefreshLayout.apply {
            setHeaderInsetStart(50f)
            setOnMultiListener(object : SimpleMultiListener() {
                override fun onHeaderMoving(header: RefreshHeader?, isDragging: Boolean, percent: Float, offset: Int, headerHeight: Int, maxDragHeight: Int) {
                    mOffset = offset / 2f
                    viewBinding.headerBgIv.translationY = (mOffset - mScrollY)
                    viewBinding.titleBar.alpha = 1 - percent.coerceAtMost(1f)
                }

                override fun onRefresh(refreshLayout: RefreshLayout) {
                    if (GSYVideoManager.instance().playTag == viewBinding.rvLocationPost.adapter?.hashCode()?.toString() &&
                        GSYVideoManager.instance().playPosition >= 0) {
                        GSYVideoManager.releaseAllVideos()
                    }
                    viewModel.loadFirstPost()
                }
            })
        }
        viewBinding.homeFragmentAppBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener{
            val totalDistance = SizeUtils.dp2px(100f)
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                val distance: Float = abs(verticalOffset).toFloat()
                if (distance < imageDistance) {
                    viewBinding.headerBgIv.translationY = -distance
                } else {
                    viewBinding.headerBgIv.translationY = -imageDistance.toFloat()
                }

                when (val offset = verticalOffset.absoluteValue) {
                    0 -> {
                        viewBinding.ivBack.alpha = 1f
                        viewBinding.titleBar.setBackgroundResource(R.color.transparent)
                        viewBinding.tvTitle.alpha = 0f
                        StatusBarUtil.setDarkMode(this@NearbyDetailsActivity)
                    }
                    in 0..totalDistance -> {
                        val alpha = offset / totalDistance.toFloat()
                        val colorAlpha = (255f * alpha).toInt()
                        val rgb = if (isWhiteMode()) 255 else 0
                        viewBinding.titleBar.setBackgroundColor(Color.argb(colorAlpha, rgb, rgb, rgb))

                        viewBinding.tvTitle.alpha = alpha
                    }
                    else -> {
                        val rgb = if (isWhiteMode()) 255 else 0
                        viewBinding.titleBar.setBackgroundColor(Color.argb(255, rgb, rgb, rgb))
                        window?.setWindowAnimations(R.style.dc_bottomSheet_animation)
                        viewBinding.tvTitle.alpha = 1f
                        StatusBarUtil.setLightMode(this@NearbyDetailsActivity)
                    }
                }


            }
        })

    }


    private fun initAdapter() {
        adapter.addItemBinder(DynamicItemBinder(this))
        adapter.registerDynamicReload(lifecycle)
        adapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.loreMorePost()
        }
        adapter.setEmptyView(CommonEmptyView(this))
        viewBinding.rvLocationPost.adapter = adapter
        viewBinding.rvLocationPost.layoutManager = LinearLayoutManager(context)
        viewBinding.rvLocationPost.setVideoAutoPlay(lifecycle)
    }

    private fun requestLocationPermission() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .request { allGranted, _, _ ->
                if (allGranted) {
                    startLocation()
                } else {
                    mStatusView?.hideLoadingView()
                    adapter.setList(arrayListOf(LocationEntityPermission()))
                    adapter.loadMoreModule.apply {
                        loadMoreComplete()
                        loadMoreEnd(true)
                    }
                }
            }
    }

    private fun startLocation() {
        mStatusView?.showLoadingView()
        LocationHelper(this, object : OnLocationResultListener {
            override fun onLocationResult(location: Location?) {
                viewModel.location = location
                viewModel.loadFirstPost()
            }

            override fun onLocationChange(location: Location?) {
            }

            override fun onLocationFail() {
                mStatusView?.showErrorView("获取定位失败")
            }
        }).startLocation()
    }



}