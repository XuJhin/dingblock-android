package cool.dingstock.foundation.base

import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback

interface IDialogStatus {
    fun showLoadingDialog(msg: String = "Loading", dismissAction: (() -> Unit)? = null)
    fun showSuccessDialog(msg: String = "Loading", dismissAction: (() -> Unit)? = null)
    fun showWarningDialog(msg: String = "Loading", dismissAction: (() -> Unit)? = null)
    fun showFailedDialog(msg: String = "Loading", dismissAction: (() -> Unit)? = null)
}

class CommonDialog : IDialogStatus {
    override fun showLoadingDialog(msg: String, dismissAction: (() -> Unit)?) {
        TipDialog.show(msg)
            .setCancelable(true)
            .setDialogLifecycleCallback(object : DialogLifecycleCallback<WaitDialog>() {
                override fun onShow(dialog: WaitDialog?) {
                    super.onShow(dialog)
                }

                override fun onDismiss(dialog: WaitDialog?) {
                    super.onDismiss(dialog)
                    dismissAction?.invoke()
                }
            })
    }

    override fun showSuccessDialog(msg: String, dismissAction: (() -> Unit)?) {
        TipDialog.show(msg, WaitDialog.TYPE.SUCCESS)
            .setCancelable(true)
            .setDialogLifecycleCallback(object : DialogLifecycleCallback<WaitDialog>() {
                override fun onShow(dialog: WaitDialog?) {
                    super.onShow(dialog)
                }

                override fun onDismiss(dialog: WaitDialog?) {
                    super.onDismiss(dialog)
                    dismissAction?.invoke()
                }
            })
    }

    override fun showWarningDialog(msg: String, dismissAction: (() -> Unit)?) {
        TipDialog.show(msg, WaitDialog.TYPE.WARNING)
            .setCancelable(true)
            .setDialogLifecycleCallback(object : DialogLifecycleCallback<WaitDialog>() {
                override fun onShow(dialog: WaitDialog?) {
                    super.onShow(dialog)
                }

                override fun onDismiss(dialog: WaitDialog?) {
                    super.onDismiss(dialog)
                    dismissAction?.invoke()
                }
            })
    }

    override fun showFailedDialog(msg: String, dismissAction: (() -> Unit)?) {
        TipDialog.show(msg, WaitDialog.TYPE.ERROR)
            .setCancelable(true)
            .setDialogLifecycleCallback(object : DialogLifecycleCallback<WaitDialog>() {
                override fun onShow(dialog: WaitDialog?) {
                    super.onShow(dialog)
                }

                override fun onDismiss(dialog: WaitDialog?) {
                    super.onDismiss(dialog)
                    dismissAction?.invoke()
                }
            })
    }
}