package cool.dingstock.post.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import cool.dingstock.lib_base.util.SizeUtils;

public class CircleRecommendDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int childAdapterPosition = parent.getChildAdapterPosition(view);

        int lastCount = Objects.requireNonNull(parent.getAdapter()).getItemCount() - 1;

        if (childAdapterPosition == 0) {
            outRect.left = SizeUtils.dp2px(20);
            return;
        }
        if (childAdapterPosition == lastCount) {
            outRect.left = SizeUtils.dp2px(10);
            outRect.right = SizeUtils.dp2px(20);
            return;
        }
        outRect.left = SizeUtils.dp2px(10);
    }
}
