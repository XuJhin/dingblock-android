package cool.dingstock.appbase.util;

import android.graphics.Typeface;

import java.util.concurrent.atomic.AtomicReference;

import cool.dingstock.lib_base.BaseLibrary;

public class TypefaceHUtil {


    private final static AtomicReference<Typeface> typefaceAtom = new AtomicReference<>();
    private final static AtomicReference<Typeface> aveTypefaceAtom = new AtomicReference<>();

    public static Typeface getIconFontTypeface() {
        if (null == typefaceAtom.get()) {
            typefaceAtom.getAndSet(Typeface.createFromAsset(BaseLibrary.getInstance().getContext().getAssets(),
                    "iconfont/iconfont.ttf"));
        }
        return typefaceAtom.get();
    }

    public static Typeface getAveTypeface() {
        if (null == aveTypefaceAtom.get()) {
            aveTypefaceAtom.getAndSet(Typeface.createFromAsset(BaseLibrary.getInstance().getContext().getAssets(),
                    "iconfont/AvenirNextCondensed-Medium.ttf"));
        }
        return aveTypefaceAtom.get();
    }

}
