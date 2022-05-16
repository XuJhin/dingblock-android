package cool.dingstock.home.widget.card

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import cool.dingstock.appbase.entity.bean.home.TransverseCardData
import cool.dingstock.appbase.ext.inflateBindingLazy
import cool.dingstock.appbase.ext.load
import cool.dingstock.home.R
import cool.dingstock.home.databinding.HomeCardItemLayoutBinding

@SuppressLint("ViewConstructor")
class HomeCardItem(context: Context,att: AttributeSet?) : FrameLayout(context,att) {
    var mRootView:View
    val viewBinding = inflateBindingLazy<HomeCardItemLayoutBinding>(LayoutInflater.from(context),null,false)

    init {
        mRootView = viewBinding.root
        addView(mRootView)
    }

    fun setSize(width:Int,height:Int){
        val layoutParams = LayoutParams(width, height)
        layoutParams.gravity = Gravity.CENTER
        val iv = mRootView.findViewById<View>(R.id.sneakers_iv)
        val ivLp = iv.layoutParams as? ConstraintLayout.LayoutParams
        val ivWidth = (width * (105f / 228f)).toInt()
        val ivHeight = (ivWidth * (80f / 105f)).toInt()
        ivLp?.width = ivWidth
        ivLp?.height = ivHeight
        ivLp?.marginEnd = (ivWidth * (21f / 105f)).toInt()
        ivLp?.bottomMargin = (ivHeight * (3f / 80f)).toInt()
        mRootView.layoutParams = layoutParams
    }

    fun setData(level: Int, data: TransverseCardData) {
        when(level) {
            3 -> viewBinding.maskIv.setBackgroundColor(ContextCompat.getColor(context, R.color.home_card_bg_level_3))
            2 -> viewBinding.maskIv.setBackgroundColor(ContextCompat.getColor(context, R.color.home_card_bg_level_2))
            1 -> viewBinding.maskIv.setBackgroundColor(ContextCompat.getColor(context, R.color.home_card_bg_level_1))
            else -> viewBinding.maskIv.setBackgroundColor(Color.parseColor("#00000000"))
        }
        viewBinding.bgIv.load(data.topBgUrl)
        val imgUrl = data.imageUrl?.trim()
        if (TextUtils.isEmpty(imgUrl)) {
            viewBinding.sneakersIv.setImageResource(R.color.transparent)
        } else {
            viewBinding.sneakersIv.load(imgUrl)
        }
    }

}