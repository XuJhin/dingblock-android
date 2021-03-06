package cool.dingstock.appbase.widget.danmaku.danmaku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import cool.dingstock.appbase.R;


/**
 * Created by LittleFogCat.
 */
class DanmakuViewFactory {
    @SuppressLint("InflateParams")
    static DanmakuView createDanmakuView(Context context) {
        return (DanmakuView) LayoutInflater.from(context)
                .inflate(R.layout.danmaku_view, null, false);
    }

    static DanmakuView createDanmakuView(Context context, ViewGroup parent) {
        return (DanmakuView) LayoutInflater.from(context)
                .inflate(R.layout.danmaku_view, parent, false);
    }
}
