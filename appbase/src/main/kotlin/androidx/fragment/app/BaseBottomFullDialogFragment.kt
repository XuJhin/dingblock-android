package androidx.fragment.app

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import cool.dingstock.appbase.R
import cool.dingstock.appbase.util.StatusBarUtil
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 会让导航栏一起变色的一个dialog
 * */
abstract class BaseBottomFullDialogFragment : DialogFragment() {
    lateinit var rootView: View
    lateinit var containerView: ViewGroup
    var isCreateView = AtomicBoolean(false)
    var onDismissListener: DialogInterface.OnDismissListener? = null
    var isInit = AtomicBoolean(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogFullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!isCreateView.get()) {
            isCreateView.set(true)
            containerView =
                inflater.inflate(R.layout.base_bottom_dialog_layout, container, false) as ViewGroup
            rootView = inflater.inflate(getLayoutId(), containerView,true)
        }
        return containerView
    }

    abstract fun getLayoutId(): Int

    abstract fun getParentLayer(): View?

    abstract fun getContentLayer(): View?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        containerView?.setOnClickListener {
            if (isCancelable) {
                dismiss()
            }
        }
        getParentLayer()?.setOnClickListener {
            if (isCancelable) {
                dismiss()
            }
        }
        getContentLayer()?.setOnClickListener {

        }
        initDataEvent()
        isInit.set(true)

        dialog?.window?.let {
            StatusBarUtil.transparentStatus(it)
        }
        context?.let {
            dialog?.window?.navigationBarColor = ContextCompat.getColor(it,R.color.white)
        }
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val attributes = dialog?.window?.attributes
        attributes?.windowAnimations = R.style.DC_bottom_dialog_animation
        dialog?.window?.attributes = attributes


    }

    abstract fun initDataEvent()

    override fun onDetach() {
        super.onDetach()
        onDismissListener?.onDismiss(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(isCreateView.get()){
            (containerView.parent as? ViewGroup)?.removeView(containerView)
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (this.isAdded || this.isResumed) {
            return
        }
        super.show(manager, tag)
    }

}