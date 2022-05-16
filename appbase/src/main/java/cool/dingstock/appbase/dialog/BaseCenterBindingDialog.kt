package cool.dingstock.appbase.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType


/**
 * 类名：BaseCenterBindingDialog
 * 包名：cool.dingstock.appbase.dialog
 * 创建时间：2021/9/4 5:37 下午
 * 创建人： WhenYoung
 * 描述：
 **/
abstract class BaseCenterBindingDialog<VB : ViewBinding> : BaseCenterDialog {

    constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId)
    constructor(context: Context) : super(context)


    lateinit var viewBinding: VB

    final override fun getLayoutId(): Int = 0

    override fun providerContentView(group: View): View {
        val arr = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        val cls = arr?.get(0) as? Class<VB>
        val inflate = cls?.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java, Boolean::class.java
        )
        viewBinding = inflate?.invoke(null, LayoutInflater.from(context), group, false) as VB
        return viewBinding.root
    }
}