package cool.dingstock.appbase.toast;

import android.content.Context;

import cool.dingstock.lib_base.util.ToastUtil;


public enum TopToast {
    INSTANCE;// 实现单例
    public void showToast(Context ctx, String content, int duration) {
        ToastUtil.getInstance().makeTextAndShow(ctx,content,duration);
    }
}


