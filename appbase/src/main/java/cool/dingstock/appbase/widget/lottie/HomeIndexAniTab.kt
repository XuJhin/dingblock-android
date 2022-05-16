package cool.dingstock.appbase.widget.lottie

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.airbnb.lottie.LottieAnimationView
import cool.dingstock.appbase.ext.hide
import cool.dingstock.lib_base.util.Logger

/**
 * @author WhenYoung
 *  CreateAt Time 2021/1/29  16:39
 */
class HomeIndexAniTab(context: Context,attributeSet: AttributeSet) : FrameLayout(context,attributeSet){
    var lav : LottieAnimationView = LottieAnimationView(context)
    var otherLav : LottieAnimationView = LottieAnimationView(context)
    private var isPlaying = false
    private var mAni  = ""
    var isShowRocket = false

    init {
        addLav()
    }

    private fun addLav() {
        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        addView(lav, lp)
        lav.repeatCount = 0
        lav.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isPlaying = false
            }

            override fun onAnimationCancel(animation: Animator?) {
                isPlaying = false
            }

            override fun onAnimationStart(animation: Animator?) {
                isPlaying = true
            }
        })
        otherLav.repeatCount = 0
        val lp1 = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        addView(otherLav, lp1)
        otherLav.hide(true)
    }

    fun setImage(ani:String){
        this.mAni = ani
        lav.setAnimation(ani)
    }

    private fun playAnimation(){
        if(!isPlaying){
            if(lav.visibility == View.VISIBLE){
                lav.playAnimation()
            }
            if(otherLav.visibility == View.VISIBLE){
                otherLav.playAnimation()
            }
        }
    }

    override fun setSelected(isSelect : Boolean){
        Logger.d("setAniSelected:${isSelect},${mAni}")
        if(isSelect){
            lav.hide(isShowRocket)
            otherLav.hide(!isShowRocket)
            if(!isShowRocket){
                playAnimation()
            }
        }else{
            lav.cancelAnimation()
            lav.progress = 0f
            otherLav.cancelAnimation()
            otherLav.progress =1f
            lav.hide(false)
            otherLav.hide(true)
        }
    }


    fun showOtherAni(){
        isShowRocket = true
        lav.hide(true)
        otherLav.hide(false)
        otherLav.playAnimation()
    }

    fun setOtherAni(playAni:String){
        otherLav.setAnimation(playAni)
    }

    fun dismissOtherAni(){
        isShowRocket =false
        lav.hide(false)
        otherLav.hide(true)
        lav.progress = 1f
    }

    fun playOtherAni(){
        isShowRocket= false
        otherLav.hide(false)
        lav.hide(true)
        otherLav.removeAllAnimatorListeners()
        otherLav.playAnimation()
        otherLav.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isPlaying = false
            }

            override fun onAnimationCancel(animation: Animator?) {
                isPlaying = false

            }

            override fun onAnimationStart(animation: Animator?) {
                isPlaying = true
            }

        })
    }

}