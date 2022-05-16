package cool.dingstock.home.ui.dingchao

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.google.gson.Gson
import cool.dingstock.appbase.constant.BoxConstant
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.home.BoxItemEntity
import cool.dingstock.appbase.entity.bean.home.CalenderProductEntity
import cool.dingstock.appbase.entity.bean.home.TransverseCardData
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.home.adapter.HomeTouchCardAdapter
import cool.dingstock.home.databinding.TransverseCardViewLayoutBinding
import cool.dingstock.home.utils.CardViewHelper
import cool.dingstock.lib_base.util.SizeUtils
import java.util.ArrayList


/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/27  14:49
 */
class HomeTopComponentView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {


    private val viewBinding: TransverseCardViewLayoutBinding =
        TransverseCardViewLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initView()
    }

    private fun initView() {
        viewBinding.btn.apply {
            val lp = layoutParams
            lp.width = CardViewHelper.cardHeight.toInt()
            lp.height = CardViewHelper.cardHeight.toInt()
            layoutParams = lp
        }
        viewBinding.touchCardLocationView.apply {
            val lp = layoutParams
            lp.width = CardViewHelper.rvWidth.toInt()
            lp.height = CardViewHelper.rvHeight.toInt()
            layoutParams = lp
        }
    }

    fun setRightData(entity: BoxItemEntity) {
        viewBinding.btn.load(entity.imageUrl)
        viewBinding.btn.setOnShakeClickListener {
            UTHelper.commonEvent(UTConstant.Home.HomeP_click_Tidalbox)
            DcUriRequest(context, entity.targetUrl).start()
        }

    }


    fun setMOnTouchListener(l: OnTouchListener?) {
//        viewBinding.touchCardView.setMOnTouchListener(l)
    }

    fun setData(cardItems: ArrayList<TransverseCardData>?) {
//        viewBinding.touchCardView.setData(cardItems)
    }

    fun getTouchCardView(): View {
        return viewBinding.touchCardLocationView
    }

}