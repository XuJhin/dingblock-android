package cool.dingstock.post.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import cool.dingstock.lib_base.util.SizeUtils;

public class CircleCameraDecoration extends RecyclerView.ItemDecoration {

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (null == adapter) {
            return;
        }
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.left = (SizeUtils.dp2px(20));
        } else {
            if (position == adapter.getItemCount() - 1) {
                outRect.left = (SizeUtils.dp2px(8));
                outRect.right = (SizeUtils.dp2px(20));
            } else {
                outRect.left = (SizeUtils.dp2px(8));
            }
        }
    }

}
