package cool.dingstock.appbase.mvp.lazy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import java.lang.reflect.ParameterizedType


/**
 * 类名：VmBindingLazyFragment
 * 包名：cool.dingstock.appbase.mvp.lazy
 * 创建时间：2021/6/18 3:34 下午
 * 创建人： WhenYoung
 * 描述：
 **/
abstract class VmBindingLazyFragment<VM : BaseViewModel, VB : ViewBinding> : VmLazyFragment<VM>() {

    private var _binding: VB? = null
    val viewBinding: VB get() = _binding!!

    final override fun getLayoutId(): Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val arr = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        val cls = arr?.get(1) as? Class<VB>
        val inflate = cls?.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        _binding = inflate?.invoke(null, inflater, container, false) as VB
        rootView = viewBinding.root
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}