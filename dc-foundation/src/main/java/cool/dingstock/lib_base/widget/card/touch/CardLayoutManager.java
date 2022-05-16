package cool.dingstock.lib_base.widget.card.touch;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import cool.dingstock.lib_base.util.SizeUtils;

public class CardLayoutManager extends RecyclerView.LayoutManager {
    public CardConfig cardConfig = new CardConfig();

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 自定义LayoutManager核心是摆放控件，所以onLayoutChildren方法是我们要改写的核心
     *
     * @param recycler
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //缓存
        detachAndScrapAttachedViews(recycler);

        //获取所有item(包括不可见的)个数
        int count = getItemCount();
        //由于我们是倒序摆放，所以初始索引从后面开始
        int initIndex = count - cardConfig.SHOW_MAX_COUNT;
        if (initIndex < 0) {
            initIndex = 0;
        }

        for (int i = initIndex; i < count; i++) {
            //从缓存中获取view
            View view = recycler.getViewForPosition(i);
            //添加到recyclerView
            addView(view);
            //测量一下view
            measureChild(view, 0, 0);

            //居中摆放，getDecoratedMeasuredWidth方法是获取带分割线的宽度，比直接使用view.getWidth()精确
            int realWidth = getDecoratedMeasuredWidth(view);
            int realHeight = getDecoratedMeasuredHeight(view);
            int heightPadding = (int) ((getHeight() - realHeight) / 2f);

            int widthPadding = 0;

            //摆放child
            layoutDecorated(view, widthPadding, heightPadding,
                    widthPadding + realWidth, heightPadding + realHeight);
            //根据索引，来位移和缩放child
            int level = count - i - 1;
            //level范围（CardConfig.SHOW_MAX_COUNT-1）- 0
            // 最下层的不动和最后第二层重叠
            if (level == cardConfig.SHOW_MAX_COUNT - 1) {
                level--;
            }
            int x = SizeUtils.dp2px(cardConfig.TRANSLATION_X);
            //被缩放之后的宽度
            int scaleWidth = (int) (realWidth * (1 - level * cardConfig.SCALE));
            int relTranslation = (int) ((realWidth - scaleWidth) / 2f) + x * level;
            view.setTranslationX(relTranslation);
            view.setScaleX(1 - level * cardConfig.SCALE);
            view.setScaleY(1 - level * cardConfig.SCALE);
        }
    }
}
