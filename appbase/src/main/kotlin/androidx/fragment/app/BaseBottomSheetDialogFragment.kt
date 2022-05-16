package androidx.fragment.app

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cool.dingstock.appbase.R
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.lib_base.util.ScreenUtils
import java.lang.reflect.ParameterizedType

abstract class BaseBottomSheetDialogFragment<VB: ViewBinding> : BottomSheetDialogFragment() {
    lateinit var binding: VB

    protected abstract var canNotScroll: Boolean

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (context == null) return super.onCreateDialog(savedInstanceState)
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialog).apply {
            window?.let {
                StatusBarUtil.transparentStatus(it)
                StatusBarUtil.setNavigationBarColor(it, ContextCompat.getColor(context, R.color.white))
            }
            behavior.isHideable = false
            behavior.peekHeight = ScreenUtils.getScreenHeight(context)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            setNotScroll()
        }
    }

    private fun BottomSheetDialog.setNotScroll() {
        if (canNotScroll) {
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (javaClass.genericSuperclass as ParameterizedType).let {
            it.actualTypeArguments.let { types ->
                binding = (types[0] as Class<VB>).getMethod("inflate", LayoutInflater::class.java,
                    ViewGroup::class.java, Boolean::class.java).invoke(null, inflater, container, false) as VB
                return binding.root
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.findViewById<FrameLayout>(R.id.design_bottom_sheet)?.apply {
            layoutParams = layoutParams.apply {
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }
}