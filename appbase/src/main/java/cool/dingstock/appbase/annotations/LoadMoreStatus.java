package cool.dingstock.appbase.annotations;

import androidx.annotation.IntDef;

import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.Retention;

/**
 * Created by wangshuwen on 2018/6/2.
 */

@IntDef({LoadMoreStatus.LOADING, LoadMoreStatus.END, LoadMoreStatus.IDLE})
@Retention(AnnotationRetention.SOURCE)
public @interface LoadMoreStatus {
    int LOADING = 1;
    int END = 2;
    int IDLE = 0;
}
