package cool.dingstock.appbase.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import cool.dingstock.appbase.constant.UIConstant
import cool.dingstock.appbase.widget.menupop.OptionMenu
import cool.dingstock.appbase.widget.menupop.PopupMenuView
import cool.dingstock.appbase.widget.menupop.PopupView
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.util.Logger

object ClipboardHelper {
    const val DingStockLabel = "DingStockLabel"

    /**
     * 复制文本信息
     */
    fun copyInfo(content: String?) {
        // 得到剪贴板管理器
        val cmb =
            BaseLibrary.getInstance().context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        if (null == cmb) {
            Logger.w("The clipboard service is null.")
            return
        }
        val clipData = ClipData.newPlainText(DingStockLabel, content)
        cmb.setPrimaryClip(clipData)
    }

    fun copyMsg(context: Context, msg: String, nextAction: () -> Unit) {
        //获取剪贴板管理器：
        val cm: ClipboardManager? =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText(DingStockLabel, msg)
        // 将ClipData内容放到系统剪贴板里。
        cm?.setPrimaryClip(mClipData)
        nextAction()
    }

    fun getCopyMsg(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData: ClipData? = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val text = clipData.getItemAt(0).text
            return text?.toString()
        }
        return null
    }

    fun getCopyMsgLabel(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData: ClipData? = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            return clipData.description?.label?.toString()
        }
        return null
    }

    fun clearClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText(DingStockLabel, null)
        clipboard.setPrimaryClip(mClipData)
    }


    fun showMenu(context: Context, text: String, view: View, action: (() -> Unit)? = null) {
        val popupMenuView = PopupMenuView(context)
        val optionMenuList: MutableList<OptionMenu> = ArrayList()
        val optionMenu = OptionMenu("复制")
        optionMenu.id = UIConstant.MenuId.COPY
        optionMenuList.add(optionMenu)
        popupMenuView.menuItems = optionMenuList
        popupMenuView.setSites(PopupView.SITE_TOP)
        popupMenuView.show(view)
        popupMenuView.setOnMenuClickListener { _: Int, _: OptionMenu? ->
            copyInfo(text)
            action?.invoke()
            popupMenuView.dismiss()
            true
        }
    }

}