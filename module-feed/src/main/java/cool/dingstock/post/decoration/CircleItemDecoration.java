package cool.dingstock.post.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import cool.dingstock.lib_base.util.SizeUtils;

public class CircleItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int spacing;
    private int horizontalSpacing;
    private boolean includeEdge;

    public CircleItemDecoration(int spanCount, int spacing, int horizontalSpacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = SizeUtils.dp2px((float)spacing);
        this.horizontalSpacing = SizeUtils.dp2px((float)horizontalSpacing);
        this.includeEdge = includeEdge;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % this.spanCount;
        if (this.includeEdge) {
            outRect.left = this.spacing - column * this.spacing / this.spanCount;
            outRect.right = (column + 1) * this.spacing / this.spanCount;
            if (position < this.spanCount) {
                outRect.top = this.horizontalSpacing;
            }

            outRect.bottom = this.horizontalSpacing;
        } else {
            outRect.left = column * this.spacing / this.spanCount;
            outRect.right = this.spacing - (column + 1) * this.spacing / this.spanCount;
            if (position >= this.spanCount) {
                outRect.top = this.horizontalSpacing;
            }
        }

    }
}
