package androidx.fragment.app

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.R
import java.lang.reflect.ParameterizedType

/**
 * 底部有空间的dialog，会让导航栏透明 保持风格统一
 * */
abstract class BottomSpaceDialog<VB:ViewBinding> : DialogFragment() {
    lateinit var viewBinding:VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val params = dialog?.window?.attributes
		params?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
		params?.windowAnimations = R.style.dc_bottomSheet_animation
		params?.width = ViewGroup.LayoutParams.MATCH_PARENT
		dialog?.window?.apply {
			attributes = params
			requestFeature(Window.FEATURE_NO_TITLE)
			setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		}
        val arr = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        val cls = arr?.get(0) as? Class<VB>
        val inflate = cls?.getDeclaredMethod("inflate",LayoutInflater::class.java,ViewGroup::class.java,Boolean::class.java)
        viewBinding = inflate?.invoke(null,inflater,container,false) as VB
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEventView()
    }

    abstract fun initEventView()

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(true)
        val window = dialog?.window!!
        window.setGravity(Gravity.BOTTOM)
        window.decorView.setPadding(0, 0, 0, 0)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }


}