package cool.dingstock.post.list.nearby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.base.BaseVPLazyFragment
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.circle.HomeNearbyLocationEntity
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.lib_base.util.CollectionUtils
import cool.dingstock.post.R
import cool.dingstock.post.databinding.FragmentHomeNearbyBinding
import cool.dingstock.post.databinding.HomeNearbyHeaderItemLayoutBinding
import cool.dingstock.post.databinding.HomeNearbyHeaderLayoutBinding
import cool.dingstock.post.list.HomePostListFragment
import cool.dingstock.post.list.PostConstant

class HomeNearbyFragment : BaseVPLazyFragment<HomeNearbyVM, FragmentHomeNearbyBinding>() {

    var homePostFragment: HomePostListFragment? = null

    var headerVb: HomeNearbyHeaderLayoutBinding? = null

    val addressAdapter = DcBaseBinderAdapter(arrayListOf())


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.fragments.size > 0) {
            for (fragment in childFragmentManager.fragments) {
                if (fragment is HomePostListFragment && fragment.postType == PostConstant.PostType.Nearby) {
                    homePostFragment = fragment
                    headerVb =
                        HomeNearbyHeaderLayoutBinding.inflate(LayoutInflater.from(context))
                    homePostFragment?.addHeader(headerVb!!.root)
                    homePostFragment?.setOnNearbyLoadSuccess {
                        headerVb?.root?.hide(CollectionUtils.isEmpty(it))
                        addressAdapter.setList(it)
                    }
                    homePostFragment?.reload()
                }
            }
        }
        if (homePostFragment == null) {
            homePostFragment = HomePostListFragment.getInstance(PostConstant.PostType.Nearby)
            childFragmentManager.beginTransaction()
                .add(R.id.content_layer, homePostFragment!!)
                .commit()
            homePostFragment?.setOnNearbyLoadSuccess{
                headerVb?.root?.hide(CollectionUtils.isEmpty(it))
                addressAdapter.setList(it)
            }
            headerVb = HomeNearbyHeaderLayoutBinding.inflate(LayoutInflater.from(context))
            homePostFragment?.addHeader(headerVb!!.root)
        }
        headerVb?.apply {
            rv.adapter = addressAdapter
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        addressAdapter.addItemBinder(HomeNearbyLocationEntity::class.java,
            object : BaseViewBindingItemBinder<HomeNearbyLocationEntity, HomeNearbyHeaderItemLayoutBinding>() {
                override fun provideViewBinding(
                    parent: ViewGroup,
                    viewType: Int
                ): HomeNearbyHeaderItemLayoutBinding = HomeNearbyHeaderItemLayoutBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )

                override fun onConvert(vb: HomeNearbyHeaderItemLayoutBinding, data: HomeNearbyLocationEntity) {
                    vb.countTv.text = data.postCountStr
                    vb.nameTv.text = data.recommendLocation
                    vb.root.setOnShakeClickListener {
                        //详情
                        UTHelper.commonEvent(UTConstant.Home.HomeP_click_Nearby_AreaEntrance,"type",data.recommendLocation)
                        DcUriRequest(context,CircleConstant.Uri.NEARBY_DETAILS)
                            .putUriParameter(CircleConstant.UriParams.ID,data.id)
                            .start()
                    }
                }
            })

    }


    override fun lazyInit() {
        super.lazyInit()
        mStatusView?.hideLoadingView()
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            HomeNearbyFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}