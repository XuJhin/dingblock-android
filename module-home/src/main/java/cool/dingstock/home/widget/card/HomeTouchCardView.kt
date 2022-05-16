package cool.dingstock.home.widget.card

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import com.google.gson.Gson
import cool.dingstock.appbase.constant.CalendarConstant
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.CalenderProductEntity
import cool.dingstock.appbase.entity.bean.home.HomeCommonLinkEntity
import cool.dingstock.appbase.entity.bean.home.TransverseCardData
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.home.adapter.HomeTouchCardAdapter
import cool.dingstock.home.databinding.HomeTouchCardViewLayoutBinding
import cool.dingstock.home.utils.CardViewHelper
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.widget.card.touch.CardLayoutManager
import cool.dingstock.lib_base.widget.card.touch.ItemTouchHelperCallback
import cool.dingstock.lib_base.widget.card.touch.MyItemTouchHelper


/**
 * 类名：HomeTouchCardView
 * 包名：cool.dingstock.home.widget.card
 * 创建时间：2021/8/9 6:29 下午
 * 创建人： WhenYoung
 * 描述：
 **/
class HomeTouchCardView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private val PRODUCT = "product"
    private val COMMON = "common"

    private val viewBinding =
        HomeTouchCardViewLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private val touchCardAdapter by lazy {
        HomeTouchCardAdapter(context)
    }
    var mIsEnableDrag = true

    val itemTouchHelperCallback = ItemTouchHelperCallback(touchCardAdapter)


    private var mOnTouchListener: OnTouchListener? = null

    val sneakList = arrayListOf<CalenderProductEntity>()
    val gson = Gson()


    init {
        initView()
    }


    fun initView() {
        viewBinding.rv.apply {
            val lp = layoutParams
            lp.width = CardViewHelper.rvWidth.toInt()
            lp.height = CardViewHelper.rvHeight.toInt()
            layoutParams = lp
            adapter = touchCardAdapter
            layoutManager = CardLayoutManager()
        }
        val itemTouchHelper = MyItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(viewBinding.rv)

    }

    fun setData(arrayList: ArrayList<TransverseCardData>?) {
        touchCardAdapter.data.clear()
        if (arrayList == null || arrayList.size == 0) {
            hide(true)
            return
        }
        touchCardAdapter.data.addAll(arrayList)
        hide(false)
        sneakList.clear()
        arrayList.forEach {
            when (it.type) {
                PRODUCT -> {
                    it.entity?.let {
                        toProductEntity(it)?.let { cpe ->
                            sneakList.add(cpe)
                        }
                    }
                }
                COMMON -> {

                }
            }
        }
        val newList = arrayListOf<TransverseCardData>()
        newList.addAll(arrayList)
        while (newList.size < 5) {
            newList.addAll(newList)
        }
        touchCardAdapter.setOnItemClick {
            UTHelper.commonEvent(UTConstant.Home.HomeP_click_HotSale, it.id)
            when (it.type) {
                PRODUCT -> {
                    onProductItemClick(it)
                }
                COMMON -> {
                    onCommonItemClick(it)
                }
            }
        }
        touchCardAdapter.notifyDataSetChanged()
    }

    private fun onProductItemClick(entity: TransverseCardData) {
        if (entity.type == PRODUCT) {
            entity.entity?.let {
                toProductEntity(it)?.let { cpe ->
                    sneakList.forEachIndexed { index, calenderProductEntity ->
                        if (calenderProductEntity.id == cpe.id) {
                            DcUriRequest(context, HomeConstant.Uri.HEAVY)
                                .putExtra(CalendarConstant.DataParam.HEAVY_SNEAKER_LIST, sneakList)
                                .putExtra(
                                    CalendarConstant.DataParam.HEAVY_SNEAKER_LIST_POSITION,
                                    index
                                )
                                .start()
                            return
                        }
                    }
                }
            }
        }
    }

    private fun onCommonItemClick(entity: TransverseCardData) {
        if (entity.type == COMMON) {
            entity.entity?.let {
                toCommonEntity(it)?.let { cme ->
                    cme.targetUrl?.let { url ->
                        DcUriRequest(context, url)
                            .start()
                    }
                }
            }
        }

    }

    private fun toProductEntity(any: Any): CalenderProductEntity? {
        try {
            return gson.fromJson<CalenderProductEntity>(
                gson.toJson(any),
                CalenderProductEntity::class.java
            )
        } catch (e: Exception) {
        }
        return null
    }

    private fun toCommonEntity(any: Any): HomeCommonLinkEntity? {
        try {
            return gson.fromJson<HomeCommonLinkEntity>(
                gson.toJson(any),
                HomeCommonLinkEntity::class.java
            )
        } catch (e: Exception) {
        }
        return null
    }

    fun setEnableDrag(drag: Boolean){
        if(mIsEnableDrag != drag){
            mIsEnableDrag = drag
            viewBinding.rv.isNestedScrollingEnabled = drag
            viewBinding.rv.isEnabled = drag
            Logger.e("setEnableDrag:${drag}")
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(!mIsEnableDrag){
            return false
        }
        return super.dispatchTouchEvent(ev)
    }

}