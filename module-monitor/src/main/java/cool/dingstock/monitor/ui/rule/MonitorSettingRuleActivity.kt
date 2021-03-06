package cool.dingstock.monitor.ui.rule

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.contains
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseBinderAdapter
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.SpanUtils
import cool.dingstock.appbase.adapter.CommonSpanItemDecoration
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.MonitorConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.monitor.MonitorSaveRuleBean
import cool.dingstock.appbase.entity.bean.sku.Size
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.widget.common_edit_dialog.CommonEditDialog
import cool.dingstock.monitor.R
import cool.dingstock.monitor.adapter.item.MonitorSizeItemBinder
import cool.dingstock.monitor.databinding.ActivityChannelRuleSettingBinding
import cool.dingstock.monitor.databinding.ItemAddKeywordBinding
import cool.dingstock.monitor.databinding.ItemKeywordBinding

@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [MonitorConstant.Path.MONITOR_SETTING_RULE])
class MonitorSettingRuleActivity : VMBindingActivity<SettingRuleViewModel, ActivityChannelRuleSettingBinding>() {
    private val sizeAdapter by lazy {
        BaseBinderAdapter().apply {
            addItemBinder(Size::class.java, sizeBinder)
        }
    }

    private val sizeBinder by lazy {
        MonitorSizeItemBinder()
    }

    private val addKeywordView by lazy {
        ItemAddKeywordBinding.inflate(layoutInflater).apply {
            addBtn.setOnShakeClickListener {
                UTHelper.commonEvent(UTConstant.Monitor.CustomizeMonitorP_Set_Add)
                addKeyword()
            }
        }.root
    }

    override fun moduleTag(): String {
        return ModuleConstant.MONITOR
    }

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        uri.getQueryParameter("channel_id")?.let {
            viewModel.channelId.value = it
        }
        with(viewBinding) {
            SpanUtils.with(sizeExplainTv).apply {
                append("???????????????")
                append("??????????????????")
                setForegroundColor(getCompatColor(R.color.color_red))
                append("?????????????????????")
            }.create()
            SpanUtils.with(keywordExplainTv).apply {
                append("??????????????????????????????????????????")
                append("?????????????????????")
                setForegroundColor(getCompatColor(R.color.color_red))
                append("????????????????????????")
                append("??????????????????????????????????????????????????????")
                setForegroundColor(getCompatColor(R.color.color_red))
                append("????????????????????????????????????????????????????????????????????????????????????(???????????????????????????)")
            }.create()

            SpanUtils.with(tvHint).apply {
                append("??????????????????????????????????????????????????????????????????????????????????????????????????????")
                setForegroundColor(getCompatColor(R.color.color_red))
                append("\n????????????????????????????????????????????????????????????????????????")
                append("\n\n?????????????????????????????????AJ1???????????????42??????????????????42??????AJ1??????????????????????????????????????????????????????????????????")
            }.create()


            sizeBinder.setOnItemClickListener { _, _, position ->
                val data = sizeAdapter.data[position]
                if (data is Size) {
                    UTHelper.commonEvent(UTConstant.Monitor.CustomizeMonitorP_Set, "size", data.size)
                    when (data.size) {
                        "??????" -> {
                            if (!data.selected) {
                                sizeAdapter.data.forEach {
                                    (it as Size).selected = false
                                }
                                data.selected = true
                                sizeAdapter.notifyDataSetChanged()
                            }
                        }
                        else -> {
                            if (!data.selected) {
                                val all = sizeAdapter.data[0] as Size
                                if (all.selected) {
                                    all.selected = false
                                    sizeAdapter.notifyItemChanged(0)
                                }
                                data.selected = true
                                sizeAdapter.notifyItemChanged(position)
                            } else {
                                data.selected = false
                                sizeAdapter.notifyItemChanged(position)
                                var hasSize = false
                                for (size in sizeAdapter.data) {
                                    if ((size as Size).selected) hasSize = true
                                }
                                if (!hasSize) {
                                    (sizeAdapter.data[0] as Size).selected = true
                                    sizeAdapter.notifyItemChanged(0)
                                }
                            }
                        }
                    }
                }
                saveRule()
            }
            sizeRv.adapter = sizeAdapter
            sizeRv.layoutManager = GridLayoutManager(this@MonitorSettingRuleActivity, 6, RecyclerView.VERTICAL, false)
            sizeRv.addItemDecoration(CommonSpanItemDecoration(6, 8, 8, false))

        }
        with(viewModel) {
            channelId.observe(this@MonitorSettingRuleActivity) {
                getChannelRule()
            }
            viewModel.ruleBean.observe(this@MonitorSettingRuleActivity) {
                viewBinding.titleBar.title = it.channelName
                sizeAdapter.setList(it.sizes)
                sizeAdapter.notifyItemRangeInserted(0, it.sizes!!.size)
                addKeywordView(it.keywords!!)
                if (!it.sizeSupport) {
                    viewBinding.sizeTitleTv.isEnabled = false
                    viewBinding.sizeExplainTv.isEnabled = false
                    sizeBinder.sizeSupport = false
                    sizeAdapter.notifyDataSetChanged()
                }
                if (it.sizes!!.size == 1) {
                    viewBinding.sizeCard.hide()
                }
            }
        }
    }

    override fun initListeners() {

    }

    private fun saveRule() {
        viewModel.channelId.value?.let { id ->
            viewModel.ruleBean.value?.let { rule ->
                viewModel.saveRuleBean.value =
                        MonitorSaveRuleBean(id, mutableListOf<String>().apply { rule.keywords?.let { addAll(it) } }, if (rule.sizeSupport)
                            rule.sizes!!.filter { it.selected && it.size != "??????" }.map { it.size }
                        else listOf())
            }
        }
    }

    private fun addKeywordView(keywords: MutableList<String>) {
        with(viewBinding.keywordFl) {
            removeAllViews()
            if (keywords.isNotEmpty()) {
                for (keyword in keywords) {
                    addView(getKeywordViewBinding(keyword).root)
                }
            }
            if (keywords.size < 5) {
                addView(addKeywordView)
            }
        }
    }

    private fun getKeywordViewBinding(keyword: String): ItemKeywordBinding {
        return ItemKeywordBinding.inflate(layoutInflater).apply {
            text.text = keyword
            text.setOnShakeClickListener {
                viewModel.ruleBean.value?.keywords?.let {
                    val position = it.indexOfFirst { str -> str === text.text.toString() }
                    editKeyword(position, text.text.toString())
                }
            }
            deleteIv.setOnShakeClickListener {
                viewModel.ruleBean.value?.keywords?.let {
                    UTHelper.commonEvent(UTConstant.Monitor.CustomizeMonitorP_Set_Delete)
                    val position = it.indexOfFirst { str -> str === text.text.toString() }
                    deleteKeyword(position)
                }
            }
        }
    }

    private fun deleteKeyword(position: Int) {
        viewModel.ruleBean.value?.keywords?.let {
            it.removeAt(position)
            viewBinding.keywordFl.removeViewAt(position)
            if (it.size < 5 && !viewBinding.keywordFl.contains(addKeywordView)) {
                viewBinding.keywordFl.addView(addKeywordView)
            }
            saveRule()
        }
    }

    private fun editKeyword(position: Int, keyword: String) {
        CommonEditDialog.Builder(this)
                .hint("???????????????????????????")
                .title("????????????????????????")
                .confirmTxt("??????")
                .cancelTxt("??????")
                .content(keyword)
                .maxLength(20)
                .onConfirmClick(object : CommonEditDialog.OnConfirmClickListener {
                    override fun onConfirmClick(edit: EditText, dialog: CommonEditDialog) {
                        val editTxt = dialog.getEditTxt()
                        if (TextUtils.isEmpty(editTxt)) {
                            showToastShort("??????????????????")
                            return
                        }
                        viewModel.ruleBean.value?.keywords?.let {
                            UTHelper.commonEvent(UTConstant.Monitor.CustomizeMonitorP_Set, "keyword", editTxt)
                            it[position] = editTxt
                            ((viewBinding.keywordFl[position] as FrameLayout)[0] as TextView).text = editTxt
                            saveRule()
                        }
                    }
                })
                .builder()
                .show()
    }

    private fun addKeyword() {
        CommonEditDialog.Builder(this)
                .hint("???????????????????????????")
                .title("????????????????????????")
                .confirmTxt("??????")
                .cancelTxt("??????")
                .maxLength(20)
                .onConfirmClick(object : CommonEditDialog.OnConfirmClickListener {
                    override fun onConfirmClick(edit: EditText, dialog: CommonEditDialog) {
                        val editTxt = dialog.getEditTxt()
                        if (TextUtils.isEmpty(editTxt)) {
                            showToastShort("??????????????????")
                            return
                        }
                        viewModel.ruleBean.value?.keywords?.let {
                            UTHelper.commonEvent(UTConstant.Monitor.CustomizeMonitorP_Set, "keyword", editTxt)
                            it.add(editTxt)
                            viewBinding.keywordFl.addView(getKeywordViewBinding(editTxt).root, it.size - 1)
                            if (it.size == 5) {
                                viewBinding.keywordFl.removeView(addKeywordView)
                            }
                            saveRule()
                        }
                    }
                })
                .builder()
                .show()
    }
}