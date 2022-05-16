package cool.dingstock.appbase.widget.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.BaseBottomFullViewBindingFragment
import androidx.fragment.app.FragmentManager
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.visual
import cool.dingstock.appbase.util.SoftKeyBoardUtil
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.databinding.LinkAddDialogLayoutBinding
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.lib_base.util.StringUtils

/**
 * @author WhenYoung
 *  CreateAt Time 2020/9/29  11:03
 */
class LinkAddDialog : BaseBottomFullViewBindingFragment<LinkAddDialogLayoutBinding>() {
    lateinit var inflater: LayoutInflater
    private var pastString: String? = null
    var onAddLinkClickListener: OnAddLinkClickListener? = null

    private var isAdd = false//标记是否点击过添加按钮

//    override fun getParentLayer(): View {
//        return viewBinding.parentLayer
//    }
//
//    override fun getContentLayer(): View {
//        return viewBinding.contentLayer
//    }

    override fun initDataEvent() {
        initView()
    }

    private fun initView() {
        viewBinding.apply {
            closeIv.setOnClickListener {
                dismiss()
            }
            tvPaste.setOnClickListener {
                linkEdv.setText(tvRealLink.text.toString())
            }
            addTv.setOnClickListener {
                if (linkEdv.text.trim().toString().isEmpty()) {
                    ToastUtil.getInstance().makeTextAndShow(requireContext(), "请添加链接", Toast.LENGTH_SHORT)
                    return@setOnClickListener
                }
                SoftKeyBoardUtil.hideSoftKeyboard(context, rootView)
                isAdd = true
                onAddLinkClickListener?.onLinkAdd(linkEdv.text.trim().toString())
            }

            try {
                val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData: ClipData? = clipboard.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    val text = clipData.getItemAt(0).text
                    pastString = text.trim().toString()
                    if (pastString.isNullOrEmpty() || !StringUtils.concatWebLink(pastString)) {
                        layoutCopy.hide()
                    } else {
                        layoutCopy.visual()
                        tvRealLink.text = pastString
                    }
                } else {
                    pastString = null
                    layoutCopy.hide()
                }
            } catch (e: Exception) {
                pastString = null
                layoutCopy.hide()
            }
            linkEdv.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    addTv.isEnabled = !TextUtils.isEmpty(s.toString())
                }
            })
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        (rootView?.parent as? ViewGroup)?.removeView(rootView)
//    }

    interface OnAddLinkClickListener {
        fun onLinkAdd(link: String)
    }

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, "LinkAddDialog")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!isAdd) {
            UTHelper.commonEvent(UTConstant.Circle.Editor_click_icon_link, "关闭")
        }
    }
}