package cool.dingstock.mine.ui.index

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.mine.LotteryNoteBean
import cool.dingstock.appbase.entity.event.update.EventRefreshLotteryNote
import cool.dingstock.appbase.entity.event.update.EventRefreshMineCollectionAndMinePage
import cool.dingstock.appbase.mvp.lazy.VmBindingLazyFragment
import cool.dingstock.appbase.mvvm.status.PageLoadState
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.widget.date_time_picker.DateTimePicker
import cool.dingstock.appbase.widget.date_time_picker.dialog.CardDatePickerDialog
import cool.dingstock.lib_base.util.TimeUtils
import cool.dingstock.mine.R
import cool.dingstock.mine.itemView.LotteryNoteItemBinder
import cool.dingstock.mine.databinding.FragmentLotteryNoteBinding
import cool.dingstock.mine.dialog.NoteMoreDialog
import cool.dingstock.mine.dialog.SelectLotteryTypeDialog
import cool.dingstock.mine.enums.LotteryNoteState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*

class MineLotteryNoteFragment() :
    VmBindingLazyFragment<MineLotteryNotesViewModel, FragmentLotteryNoteBinding>() {
    private val noteAdapter by lazy {
        DcBaseBinderAdapter(arrayListOf())
    }

    private val noteItemBinder by lazy {
        LotteryNoteItemBinder(
            onFailed = { position, data ->
                UTHelper.commonEvent(UTConstant.Mine.MyP_click_raffle_Lose)
                update(position, data, LotteryNoteState.NOT_GET)
            }, onSucceed = { position, data ->
                UTHelper.commonEvent(UTConstant.Mine.MyP_click_raffle_Win)
                update(position, data, LotteryNoteState.GET)
            }, onJumpToDeal = { data ->
                data.product?.let {
                    if (data.state == "get") {
                        UTHelper.commonEvent(UTConstant.Mine.MyP_click_raffle_Editor)
                        DcRouter(CircleConstant.Uri.CIRCLE_DYNAMIC_EDIT)
                            .putUriParameter(CircleConstant.UriParams.IS_DEAL, "true")
                            .putExtra(CircleConstant.Extra.DEAL_GOODS, it)
                            .start()
                    } else {
                        UTHelper.commonEvent(UTConstant.Mine.MyP_click_raffle_TransactionTopic)
                        DcRouter(CircleConstant.Uri.FIND_TOPIC_DETAIL)
                            .putUriParameter(
                                CircleConstant.UriParams.TOPIC_DETAIL_ID,
                                MobileHelper.getInstance().configData.dealTalkId
                            )
                            .putUriParameter(CircleConstant.UriParams.PRODUCT_ID, it.id)
                            .putUriParameter(CircleConstant.UriParams.KEYWORD, it.name)
                            .start()
                    }
                }
            }, onMore = { position, data ->
                context?.let { ctx ->
                    NoteMoreDialog(
                        ctx, !data.state.isNullOrEmpty(),
                        onReselect = {
                            UTHelper.commonEvent(UTConstant.Mine.MyP_click_raffle_Reselection)
                            reselect(position, data)
                        }, onDelete = {
                            UTHelper.commonEvent(UTConstant.Mine.MyP_click_raffle_Delete)
                            delete(data)
                        }).show()
                }
            })
    }

    override fun initVariables(
        rootView: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        EventBus.getDefault().register(this)
        noteAdapter.addItemBinder(LotteryNoteBean::class.java, noteItemBinder)
        noteAdapter.loadMoreModule.isEnableLoadMore = true
        noteAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.loadData()
        }
        with(viewBinding) {
            noteRv.adapter = noteAdapter
            noteRv.layoutManager = LinearLayoutManager(context)
        }
        with(viewModel) {
            postType = arguments?.getString(PAGE_TYPE, "") ?: ""
            liveDataPostRefresh.observe(viewLifecycleOwner) {
                hideLoadingView()
                updateData(it)
            }
            liveDataPostLoadMore.observe(viewLifecycleOwner) {
                loadMoreData(it)
            }
            liveDataEndLoadMore.observe(viewLifecycleOwner) {
                if (it) {
                    endLoadMore()
                }
            }
            pageLoadState.observe(viewLifecycleOwner) {
                when (it) {
                    is PageLoadState.Error -> {
                        if (it.isRefresh) {
                            showErrorView(it.msg)
                        } else {
                            finishLoadMore()
                        }
                    }
                    is PageLoadState.Empty -> {
                        if (it.isRefresh) {
                            hideLoadingView()
                            showRvEmpty()
                        } else {
                            finishLoadMore()
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initListeners() {
        setOnErrorViewClick {
            showLoadingView()
            viewModel.refresh()
        }
        with(viewBinding) {
            chooseDate.setOnClickListener {
                context?.let { ctx ->
                    CardDatePickerDialog.builder(ctx)
                        .apply {
                            backNow = false
                            setDisplayType(
                                arrayListOf(
                                    DateTimePicker.YEAR,
                                    DateTimePicker.MONTH,
                                )
                            )
                            if (viewModel.selectedDate != 0L) {
                                setDefaultTime(viewModel.selectedDate)
                            } else {
                                setDefaultTime(System.currentTimeMillis())
                            }
                            setMinTime(
                                SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    Locale.getDefault()
                                ).parse("2018-01-01")?.time ?: 0L
                            )
                            setMaxTime(System.currentTimeMillis())
                            setThemeColor(getCompatColor(R.color.common_dc_theme_color))
                            showFocusDateInfo(false)
                            showDateLabel(true)
                            setTitle("选择年月")
                            setNeedChoseTv(false)
                            setBackGroundModel(CardDatePickerDialog.STACK)
                        }.build().apply {
                            setOnChooseListener(object : CardDatePickerDialog.OnChooseListener {
                                override fun onChoose(millisecond: Long) {
                                    viewModel.selectedDate = TimeUtils.getMouthFirstDay(millisecond)
                                    viewBinding.chooseDateTv.text =
                                        TimeUtils.formatTimestampS4(millisecond)
                                    viewModel.refresh()
                                }
                            })
                            setOnDismissListener {
                                chooseDateIv.animate().rotation(0f)
                            }
                        }.show()
                    chooseDateIv.animate().rotation(180f)
                }
            }
            chooseType.setOnClickListener {
                context?.let { ctx ->
                    SelectLotteryTypeDialog(ctx, {
                        chooseTypeIv.animate().rotation(0f)
                    }) {
                        chooseTypeTv.text = it.key
                        viewModel.state = it.value
                        viewModel.refresh()
                    }.show()
                    chooseTypeIv.animate().rotation(180f)
                }
            }
        }
    }

    override fun onLazy() {
        initDate()
        showLoadingView()
        viewModel.loadData()
    }

    private fun initDate() {
        val current = System.currentTimeMillis()
        viewModel.selectedDate = TimeUtils.getMouthFirstDay(current)
        viewBinding.chooseDateTv.text = TimeUtils.formatTimestampS4(current)
    }

    private fun delete(data: LotteryNoteBean) {
        lifecycleScope.launch {
            viewModel.deleteRecord(data.id)
                .collectLatest {
                    if (it.err) {
                        showToastShort(it.msg)
                    } else {
                        noteAdapter.remove(data)
                    }
                }
        }
    }

    private fun reselect(position: Int, data: LotteryNoteBean) {
        lifecycleScope.launch {
            viewModel.updateRecord(data.id, LotteryNoteState.ALL.value)
                .collectLatest {
                    if (it.err) {
                        showToastShort(it.msg)
                    } else {
                        if (viewModel.state != null) {
                            noteAdapter.remove(data)
                        } else {
                            it.res?.let { res ->
                                data.state = res.state
                                data.chapterDate = res.chapterDate
                                noteAdapter.setData(position, data)
                            }
                        }
                    }
                }
        }
    }

    private fun update(position: Int, data: LotteryNoteBean, state: LotteryNoteState) {
        lifecycleScope.launch {
            viewModel.updateRecord(data.id, state.value)
                .collectLatest {
                    if (it.err) {
                        showToastShort(it.msg)
                    } else {
                        it.res?.let { res ->
                            data.state = res.state
                            data.chapterDate = res.chapterDate
                            noteAdapter.setData(position, data)
                        }
                    }
                }
        }
    }

    private fun finishLoadMore() {
        noteAdapter.loadMoreModule.apply {
            loadMoreComplete()
            loadMoreEnd(false)
        }
    }

    private fun showRvEmpty() {
        noteAdapter.setList(emptyList())
        noteAdapter.setEmptyView(R.layout.common_empty_layout)
    }

    private fun updateData(list: MutableList<LotteryNoteBean>) {
        if (list.isNullOrEmpty()) {
            return
        }
        noteAdapter.apply {
            setList(list)
            loadMoreModule.loadMoreComplete()
        }
    }

    private fun loadMoreData(list: List<LotteryNoteBean>) {
        noteAdapter.loadMoreModule.loadMoreComplete()
        if (list.isNullOrEmpty()) {
            endLoadMore()
            return
        }
        noteAdapter.apply {
            val insertStart = noteAdapter.data.size
            addData(insertStart, list)
        }
    }

    private fun endLoadMore() {
        noteAdapter.loadMoreModule.loadMoreComplete()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshMineHub(event: EventRefreshMineCollectionAndMinePage) {
        if (event.pageType == viewModel.postType) {
            initDate()
            viewModel.state = null
            viewBinding.chooseTypeTv.text = "全部"
            viewModel.refresh()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshLotteryNote(event: EventRefreshLotteryNote) {
        viewModel.refresh()
    }

    companion object {
        private const val PAGE_TYPE = "PAGE_TYPE"

        fun getInstance(pageType: String): MineLotteryNoteFragment {
            return MineLotteryNoteFragment().apply {
                arguments = Bundle().apply {
                    putString(PAGE_TYPE, pageType)
                }
            }
        }
    }
}