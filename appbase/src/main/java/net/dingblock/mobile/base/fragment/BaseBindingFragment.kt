package net.dingblock.mobile.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

open class BaseBindingFragment<VB : ViewBinding> : BaseStatusFragment() {
    private var _binding: VB? = null
    val viewBinding: VB get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val arr = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        val cls = arr?.get(0) as? Class<VB>
        val inflate = cls?.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        _binding = inflate?.invoke(null, inflater, container, false) as VB
        rootView = viewBinding.root
        return rootView!!
    }


    protected fun getLayoutId(): Int = 0

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}