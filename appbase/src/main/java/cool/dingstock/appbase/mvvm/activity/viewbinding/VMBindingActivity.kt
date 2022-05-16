package cool.dingstock.appbase.mvvm.activity.viewbinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.R
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.VMActivity
import java.lang.reflect.ParameterizedType


/**
 * 类名：VMBindingActivity
 * 包名：cool.dingstock.appbase.mvvm.activity.viewbinding
 * 创建时间：2021/6/18 12:09 下午
 * 创建人： WhenYoung
 * 描述：
 **/
abstract class VMBindingActivity<VM : BaseViewModel, VB : ViewBinding> : VMActivity<VM>() {
    lateinit var viewBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        val arr = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        val cls = arr?.get(1) as? Class<VB>
        val inflate = cls?.getDeclaredMethod(
            "inflate", LayoutInflater::class.java, ViewGroup::class.java,
            Boolean::class.java
        )
        val contentView = findViewById<ViewGroup>(android.R.id.content)
        viewBinding = inflate?.invoke(null, layoutInflater, contentView, false) as VB
        setContentView(viewBinding.root)
        mRootView = viewBinding.root as? ViewGroup
        resetStatusBarHeight()
        super.onCreate(savedInstanceState)
        initStatusView()
        initVariables(savedInstanceState)
        initListeners()
    }

    final override fun getLayoutId(): Int {
        return 0
    }
}