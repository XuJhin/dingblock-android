package cool.dingstock.foundation.ext.viewbinding

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.LazyThreadSafetyMode.*
import kotlin.properties.ReadOnlyProperty

import kotlin.reflect.KProperty
import kotlin.LazyThreadSafetyMode.NONE

fun <VB : ViewBinding> ComponentActivity.binding(inflate: (LayoutInflater) -> VB, setContentView: Boolean = true) = lazy(NONE) {
    inflate(layoutInflater).also { binding ->
        if (setContentView) setContentView(binding.root)
        if (binding is ViewDataBinding) binding.lifecycleOwner = this
    }
}

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(crossinline factory: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) {
        factory(layoutInflater)
    }


fun <VB : ViewBinding> Fragment.binding(bind: (View) -> VB) = FragmentBindingDelegate(bind)

fun <VB : ViewBinding> Fragment.binding(inflate: (LayoutInflater) -> VB) = FragmentInflateBindingDelegate(inflate)

class FragmentBindingDelegate<VB : ViewBinding>(private val bind: (View) -> VB) : ReadOnlyProperty<Fragment, VB> {
    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB =
        requireNotNull(thisRef.view) { "The constructor missing layout id or the property of ${property.name} has been destroyed." }
            .getBinding(bind).also { binding ->
                if (binding is ViewDataBinding) binding.lifecycleOwner = thisRef.viewLifecycleOwner
            }
}

class FragmentInflateBindingDelegate<VB : ViewBinding>(private val inflate: (LayoutInflater) -> VB) :
    ReadOnlyProperty<Fragment, VB> {
    private var binding: VB? = null
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (binding == null) {
            binding = try {
                inflate(thisRef.layoutInflater).also { binding ->
                    if (binding is ViewDataBinding) binding.lifecycleOwner = thisRef.viewLifecycleOwner
                }
            } catch (e: IllegalStateException) {
                throw IllegalStateException("The property of ${property.name} has been destroyed.")
            }
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    handler.post { binding = null }
                }
            })
        }
        return binding!!
    }
}



@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> View.getBinding(bind: (View) -> VB): VB =
    getTag(Int.MIN_VALUE) as? VB ?: bind(this).also { setTag(Int.MIN_VALUE, it) }