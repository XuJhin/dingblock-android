package androidx.fragment.app

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import cool.dingstock.appbase.R
import cool.dingstock.appbase.util.StatusBarUtil
import java.lang.reflect.ParameterizedType
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 类名：BaseBottomFullViewbindingFragment
 * 包名：androidx.fragment.app
 * 创建时间：2021/7/1 5:42 下午
 * 创建人： WhenYoung
 * 描述：
 **/
abstract class BaseBottomFullViewBindingFragment<VB : ViewBinding> :
    DialogFragment() {

    protected lateinit var viewBinding: VB


    lateinit var rootView: View
    lateinit var containerView: ViewGroup
    var isCreateView = AtomicBoolean(false)
    var onDismissListener: DialogInterface.OnDismissListener? = null
    var isInit = AtomicBoolean(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogFullScreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        containerView?.setOnClickListener {
            if (isCancelable) {
                dismiss()
            }
        }
        rootView.setOnClickListener {

        }
        initDataEvent()
        isInit.set(true)
    }

    override fun onStart() {
        super.onStart()


        dialog?.window?.let {
            StatusBarUtil.transparentStatus(it)
        }
        context?.let {
            dialog?.window?.navigationBarColor = ContextCompat.getColor(it, R.color.white)
//            dialog?.window?.navigationBarDividerColor = ContextCompat.getColor(it,R.color.white)
        }
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setGravity(Gravity.BOTTOM)
        val attributes = dialog?.window?.attributes
        attributes?.windowAnimations = R.style.DC_bottom_dialog_animation
        dialog?.window?.attributes = attributes

    }

    override fun onDetach() {
        super.onDetach()
        onDismissListener?.onDismiss(dialog)
    }


    override fun show(manager: FragmentManager, tag: String?) {
        if (this.isAdded || this.isResumed) {
            return
        }
        super.show(manager, tag)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!isCreateView.get()) {
            isCreateView.set(true)
            containerView =
                inflater.inflate(
                    R.layout.base_bottom_dialog_layout,
                    container,
                    false
                ) as ViewGroup
            val arr = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
            val cls = arr?.get(0) as? Class<VB>
            val inflate = cls?.getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )
            viewBinding = inflate?.invoke(null, inflater, containerView, true) as VB
            rootView = viewBinding.root
            initViews()
        }
        return containerView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (containerView.parent as? ViewGroup)?.removeView(containerView)
    }

    open fun initViews(){

    }

    abstract fun initDataEvent()

}
