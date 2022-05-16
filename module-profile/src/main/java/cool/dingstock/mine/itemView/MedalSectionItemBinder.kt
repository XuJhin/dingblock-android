package cool.dingstock.mine.itemView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseBinderAdapter
import cool.dingstock.appbase.adapter.itembinder.BaseViewBindingItemBinder
import cool.dingstock.appbase.constant.MineConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.mine.MedalBean
import cool.dingstock.appbase.entity.bean.mine.MedalSection
import cool.dingstock.appbase.entity.bean.mine.MedalStatus
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.mine.databinding.ItemMedalSectionBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

class MedalSectionItemBinder(
    private val userId: String,
    private val countDownEnd: () -> Unit
): BaseViewBindingItemBinder<MedalSection, ItemMedalSectionBinding>() {
    val medalAdapters = mutableListOf<BaseBinderAdapter>()

    override fun provideViewBinding(parent: ViewGroup, viewType: Int): ItemMedalSectionBinding {
        return ItemMedalSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onConvert(vb: ItemMedalSectionBinding, data: MedalSection) {
        with(vb) {
            achievementTitleTv.text = data.sectionTitle
            medalRv.layoutManager = GridLayoutManager(context, 3)
            if (medalRv.adapter == null) {
                val metalBinder = MedalItemBinder(countDownEnd).apply {
                    setOnItemClickListener { adapter, _, position ->
                        adapter.data[position].let {
                            if (it is MedalBean) {
                                UTHelper.commonEvent(UTConstant.Medal.MyMedalP_click_Medal, "ID", it.id)
                                DcUriRequest(context, MineConstant.Uri.MEDAL_DETAIL)
                                    .putUriParameter(MineConstant.PARAM_KEY.ID, userId)
                                    .putUriParameter(MineConstant.PARAM_KEY.MEDAL_ID, it.id)
                                    .putExtra(MineConstant.ExtraParam.FROM_LIST, true)
                                    .start()
                            }
                        }
                    }
                }
                val medalAdapter = BaseBinderAdapter().apply {
                    addItemBinder(MedalBean::class.java, metalBinder)
                }
                medalRv.adapter = medalAdapter
                medalAdapters.add(medalAdapter)
            }
            (medalRv.adapter!! as BaseBinderAdapter).setList(data.achievements)
        }
    }
}