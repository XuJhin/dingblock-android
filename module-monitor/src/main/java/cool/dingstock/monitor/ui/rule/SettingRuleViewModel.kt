package cool.dingstock.monitor.ui.rule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cool.dingstock.appbase.entity.bean.monitor.MonitorRuleBean
import cool.dingstock.appbase.entity.bean.monitor.MonitorSaveRuleBean
import cool.dingstock.appbase.entity.bean.sku.Size
import cool.dingstock.appbase.entity.event.monitor.EventMonitorRuleSetting
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.net.api.monitor.MonitorApi
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.monitor.dagger.MonitorApiHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class SettingRuleViewModel: BaseViewModel() {
    @Inject
    lateinit var monitorApi: MonitorApi

    init {
        MonitorApiHelper.apiMonitorComponent.inject(this)
    }

    var channelId: MutableLiveData<String> = MutableLiveData()
    var ruleBean: MutableLiveData<MonitorRuleBean> = MutableLiveData()
    val saveRuleBean: MutableStateFlow<MonitorSaveRuleBean> = MutableStateFlow(MonitorSaveRuleBean("", listOf(), listOf()))
    init {
        viewModelScope.launch {
            saveRuleBean.sample(2000L)
                .collectLatest {
                    saveChannelRule()
                }
        }
    }

    fun getChannelRule() = viewModelScope.launch {
        channelId.value?.let { id ->
            monitorApi.getChannelRule(id)
                .catch { e ->
                    ToastUtil.getInstance()._short(BaseLibrary.getInstance().context, e.message ?: "数据异常")
                }
                .collect {
                    if(!it.err) {
                        it.res?.run {
                            if (sizes == null) {
                                sizes = mutableListOf()
                            }
                            if (keywords == null) {
                                keywords = mutableListOf()
                            }
                            var allSelected = true
                            for (size in sizes!!) {
                                if (size.selected) {
                                    allSelected = false
                                    break
                                }
                            }
                            sizes!!.add(0, Size(allSelected, "全部"))
                            ruleBean.postValue(this)
                        }
                    } else {
                        ToastUtil.getInstance()._short(BaseLibrary.getInstance().context, it.msg)
                    }
                }
        }
    }

    private fun saveChannelRule() = viewModelScope.launch {
        channelId.value?.let { id ->
            saveRuleBean.value.let { saveRule ->
                if (saveRule.channelId.isNotEmpty()) {
                    monitorApi.saveChannelRule(id, saveRule.keywords, saveRule.sizes)
                        .catch { e ->
                            ToastUtil.getInstance()
                                ._short(BaseLibrary.getInstance().context, e.message ?: "数据异常")
                        }
                        .collectLatest {
                            if (!it.err) {
                                ToastUtil.getInstance()
                                    ._short(BaseLibrary.getInstance().context, "保存成功")
                                var sizeSetting = 0
                                ruleBean.value?.let { rule ->
                                    if (!rule.sizeSupport) {
                                        sizeSetting = -1
                                    } else {
                                        if (saveRule.sizes.isNotEmpty() && !saveRule.sizes.contains("")) {
                                            sizeSetting = 1
                                        }
                                    }
                                }
                                EventBus.getDefault().post(EventMonitorRuleSetting(id, if (saveRule.keywords.isNotEmpty()) 1 else 0, sizeSetting))
                            } else {
                                ToastUtil.getInstance()
                                    ._short(BaseLibrary.getInstance().context, it.msg)
                            }
                        }
                }
            }
        }
    }
}