package cool.dingstock.appbase.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;

public class RKAlertDialog extends AlertDialog {

    protected RKAlertDialog(@NonNull Context context) {
        super(context);
    }

    protected RKAlertDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected RKAlertDialog(@NonNull Context context, boolean cancelable,
                            @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static AlertDialog.Builder make(Context context) {
        return new RKAlertDialog.Builder(context);
    }

}