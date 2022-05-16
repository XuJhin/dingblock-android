package cool.dingstock.calendar.ui.raffle

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseBinderAdapter
import com.gyf.immersionbar.ImmersionBar
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.entity.bean.home.CalenderProductEntity
import cool.dingstock.appbase.helper.snap.AlignSnapHelper
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.widget.recyclerview.decotation.LinearItemDecoration
import cool.dingstock.calendar.R
import cool.dingstock.calendar.databinding.CalendarFragmentHeavyReleaseBinding
import cool.dingstock.calendar.ui.sheet.SheetFragment
import cool.dingstock.uikit.bottomsheet.BottomSheetLayout
import cool.dingstock.uikit.bottomsheet.ViewTransformer

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [HomeConstant.Path.HEAVY_RELEASE]
)
class HeavyReleaseActivity :
    VMBindingActivity<HeavyRaffleVM, CalendarFragmentHeavyReleaseBinding>() {
    private val linearSnapHelper = AlignSnapHelper(Gravity.CENTER)
    var dataList: ArrayList<CalenderProductEntity> = arrayListOf()
    private val topAdapter: BaseBinderAdapter = BaseBinderAdapter()
    var bottomSheetFragment: SheetFragment? = null

    lateinit var adapterManager: LinearLayoutManager

    override fun moduleTag(): String {
        return ModuleConstant.CALENDAR
    }

    override fun setSystemStatusBar() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarDarkFont(false)
            .navigationBarColor(R.color.white)
            .navigationBarDarkIcon(false)
            .init()
    }

    override fun setupOutAnim() {
        overridePendingTransition(0, R.anim.activity_exit_alpha)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

    }

    override fun onSaveInstanceState(outState: Bundle) {

    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        val list: ArrayList<CalenderProductEntity>? =
            intent.extras?.getParcelableArrayList("list")
        if (list != null) {
            dataList.addAll(list)
        }
        val item: CalenderProductEntity? = intent.extras?.getParcelable("cell")
        viewModel.initData(dataList, item)
        initObserverDataChange()
        topAdapter.apply {
            addItemBinder(CalenderProductEntity::class.java, HeavyReleaseTopCell() {
                viewModel.selectedNewPosition(it)
            })
        }
        adapterManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        viewBinding.rvTopRelease.apply {
            adapter = topAdapter
            layoutManager = adapterManager
            linearSnapHelper.attachToRecyclerView(this)
            (this.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            addItemDecoration(LinearItemDecoration(getCompatDrawable(R.drawable.divider_calendar_raffle)).apply {
                drawStart = true
                drawEnd = true
            })
        }
        adapterManager.scrollToPosition(viewModel.currentPost)
        linearSnapHelper.smoothScrollToPosition(viewModel.currentPost)
        topAdapter.setList(viewModel.dataList)

        if (bottomSheetFragment == null) {
            supportFragmentManager.findFragmentByTag(SheetFragment.TAG)
        }
        if (bottomSheetFragment == null) {
            bottomSheetFragment = SheetFragment()
        }
        bottomSheetFragment?.show(supportFragmentManager, R.id.layout_bottom)
        viewBinding.layoutBottom.apply {
            peekSheetTranslation = this.height.toFloat()
            addOnSheetDismissedListener { this@HeavyReleaseActivity.finish() }
            setDefaultViewTransformer(object : ViewTransformer {
                override fun transformView(
                    translation: Float,
                    maxTranslation: Float,
                    peekedTranslation: Float,
                    parent: BottomSheetLayout?,
                    view: View?
                ) {

                }

                override fun getDimAlpha(
                    translation: Float,
                    maxTranslation: Float,
                    peekedTranslation: Float,
                    parent: BottomSheetLayout?,
                    view: View?
                ): Float {
                    return 0f
                }
            })
        }
    }

    private fun initObserverDataChange() {
        viewModel.topRvPosition.observe(this, Observer {
            val lastPosition = it.x
            val currentPosition = it.y
            if (lastPosition == currentPosition) {
                return@Observer
            }
            if (lastPosition > -1) {
                val entity = topAdapter.getItem(lastPosition) as CalenderProductEntity
                if (entity.hasSelected) {
                    entity.hasSelected = false
                    topAdapter.notifyItemChanged(lastPosition)
                }
            }
            if (currentPosition > -1) {
                val entity = topAdapter.getItem(currentPosition) as CalenderProductEntity
                if (entity.hasSelected) {
                    return@Observer
                }
                entity.hasSelected = true
                topAdapter.notifyItemChanged(currentPosition)
                linearSnapHelper.smoothScrollToPosition(currentPosition)
            }
        })
    }

    override fun initListeners() {
    }

}