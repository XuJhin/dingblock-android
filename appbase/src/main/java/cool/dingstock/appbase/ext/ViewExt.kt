package cool.dingstock.appbase.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.R
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun View.visual(b: Boolean = true) {
    if (b) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.INVISIBLE
    }
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.hide(hide: Boolean = true) {
    if (hide) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}

fun AppCompatTextView.drawableStart(@DrawableRes drawableRes: Int) {
    val drawable: Drawable? = ContextCompat.getDrawable(this.context, drawableRes)
    drawable?.setBounds(
        0, 0, drawable.minimumWidth,
        drawable.minimumHeight
    )
    this.setCompoundDrawables(null, null, drawable, null)
}

fun View.aniAlphaShow() {
    if (visibility == View.VISIBLE) {
        visibility = View.VISIBLE
        setTag(R.id.view_enable_show, true)
        alpha = 1f
        return
    }
    clearAnimation()
    val alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
    alphaAnimator.duration = 300
    alphaAnimator.start()
    visibility = View.VISIBLE
    setTag(R.id.view_enable_show, true)
}

fun View.aniAlphaHide() {
    if (visibility == View.GONE) {
        return
    }
    if (getTag(R.id.view_enable_show) == false) {
        return
    }
    clearAnimation()
    val alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
    alphaAnimator.duration = 300
    alphaAnimator.start()
    setTag(R.id.view_enable_show, false)
    alphaAnimator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            if (getTag(R.id.view_enable_show) == false) {
                visibility = View.GONE

            }
        }
    })
}


inline fun <reified VB : ViewBinding> Activity.inflate() = lazy(LazyThreadSafetyMode.NONE) {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

inline fun <reified VB : ViewBinding> Dialog.inflate() = lazy(LazyThreadSafetyMode.NONE) {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

inline fun <reified VB : ViewBinding> inflateBindingLazy(
    layoutInflater: LayoutInflater,
    parent: View? = null,
    addParent: Boolean = false
): VB {
    val x by lazy { inflateBinding<VB>(layoutInflater, parent, addParent) }
    return x
}

inline fun <reified VB : ViewBinding> inflateBindingLazyBy(
    layoutInflater: LayoutInflater,
    parent: View? = null,
    addParent: Boolean = false
) =
    InflateBindingDelegate(VB::class.java, layoutInflater, parent, addParent)


inline fun <reified VB : ViewBinding> inflateBinding(
    layoutInflater: LayoutInflater,
    parent: View? = null,
    addParent: Boolean = false
) =
    VB::class.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    ).invoke(null, layoutInflater, parent, addParent) as VB

inline fun <reified VB : ViewBinding> bindView() = FragmentBindingDelegate(VB::class.java)

inline fun <reified VB : ViewBinding> bindItemView(view: View) = lazy {
    VB::class.java.getMethod("bind", View::class.java).invoke(null, view) as VB
}

@Suppress("UNCHECKED_CAST")
class FragmentBindingDelegate<VB : ViewBinding>(
    private val clazz: Class<VB>
) : ReadOnlyProperty<Fragment, VB> {

    private var isInitialized = false
    private var _binding: VB? = null
    private val binding: VB get() = _binding!!

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (!isInitialized) {
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    _binding = null
                    isInitialized = false
                }
            })
            _binding = clazz.getMethod("bind", View::class.java)
                .invoke(null, thisRef.requireView()) as VB
            isInitialized = true
        }
        return binding
    }
}

class InflateBindingDelegate<VB : ViewBinding>(
    clazz: Class<VB>,
    layoutInflater: LayoutInflater,
    parent: View? = null,
    addParent: Boolean = false
) : ReadOnlyProperty<Any, VB> {

    val vb by lazy {
        clazz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, layoutInflater, parent, addParent) as VB
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): VB {
        return vb
    }

}

fun EditText.limitDecimal(intLimit: Int = Int.MAX_VALUE, limitDecimal: Int = 2) {
    doOnTextChanged { text, _, _, _ ->
        text?.let {
            //如果第一个数字为0，第二个不为点，就不允许输入
            if (text.startsWith("0") && text.toString().trim().length > 1) {
                if (text.substring(1, 2) != ".") {
                    this.setText(text.subSequence(0, 1))
                    setSelection(1)
                    return@doOnTextChanged
                }
            }
            //如果第一为点，直接显示0.
            if (text.startsWith(".")) {
                this.setText("0.")
                setSelection(2)
                return@doOnTextChanged
            }
            if (text.contains(".")) {
                if (text.length - 1 - text.indexOf(".") > limitDecimal) {
                    val s = text.subSequence(0, text.indexOf(".") + limitDecimal + 1)
                    this.setText(s)
                    setSelection(s.length)
                }
            }
            val split = text.split(".")
            if (split[0].length > intLimit) {
                var s = split[0].substring(0, split[0].length - 1)
                if (split.size > 1) s += ".${split[1]}"
                this.setText(s)
                setSelection(s.length)
            }
        }
    }
}

fun View.setShape(radius:Float,@ColorInt color:Int){
    val shape = ShapeDrawable(RoundRectShape(
        floatArrayOf(
            radius,
            radius,
            radius,
            radius,
            radius,
            radius,
            radius,
            radius
        ), null, null
    ))
    shape.paint.color = color
    background = shape
}











