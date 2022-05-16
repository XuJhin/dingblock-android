package cool.dingstock.appbase.widget.leonids

import android.app.Activity
import android.content.Context
import android.os.*
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import cool.dingstock.appbase.constant.iconInts
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.BaseLibrary

fun View.startLikeAnimation(duration: Long) {
    ParticleSystem(context as Activity, 100, iconInts, duration).apply {
        setScaleRange(0.7f, 1.3f)
        setSpeedModuleAndAngleRange(0.5f, 1.0f, 180, 280)
        setAcceleration(0.002f, 90)
        setRotationSpeedRange(90f, 180f)
        setFadeOut(200, AccelerateInterpolator())
        oneShot(this@startLikeAnimation, 10, DecelerateInterpolator())
    }
}

object LeonidsUtil {

    private val vibrator: Vibrator = BaseLibrary.getInstance().context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private var clickNum = 0
    private var animatorRunning = false
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == clickNum) {
                animatorRunning = false
                clickNum = 0
            }
        }
    }

    private fun start(view: View) {
        animatorRunning = true
        animAndVibrator(view)
        clickNum++
        handler.sendEmptyMessageDelayed(clickNum, 800L)
    }

    fun animAndVibrator(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(5L, 15))
        }
        view.startLikeAnimation(800L)
    }


    fun execute(view: View, favored: Boolean, event: () -> Unit) {
        if (favored) {
            if (animatorRunning) {
                if (LoginUtils.isLoginAndRequestLogin(view.context)) {
                    start(view)
                }
            } else {
                event()
            }
        } else {
            if (LoginUtils.isLoginAndRequestLogin(view.context)) {
                start(view)
            }
            event()
        }
    }

    fun executeLongClick(view: View, favored: Boolean, event: () -> Unit) {
        view.setOnLongClickListener {
            if (!LoginUtils.isLoginAndRequestLogin(view.context)) {
                event()
                return@setOnLongClickListener true
            }
            var runnable: Runnable? = null
            runnable = Runnable {
                if (it.isPressed) {
                    animAndVibrator(view)
                    it.postDelayed(runnable, 100)
                } else {
                    if (!favored) {
                        event()
                    }
                }
            }
            it.postDelayed(runnable, 100)
            true
        }
    }
}