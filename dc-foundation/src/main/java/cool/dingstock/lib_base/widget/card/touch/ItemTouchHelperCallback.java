package cool.dingstock.lib_base.widget.card.touch;

import android.animation.Animator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import cool.dingstock.lib_base.R;
import cool.dingstock.lib_base.util.Logger;
import cool.dingstock.lib_base.util.ScreenUtils;
import cool.dingstock.lib_base.util.SizeUtils;

public class ItemTouchHelperCallback<T, VH extends RecyclerView.ViewHolder> extends MyItemTouchHelper.Callback {

    public CardConfig cardConfig = new CardConfig();
    private DragStateChangeListener listener;
    private DragStateEnum state = DragStateEnum.STATIC;
    private TouchCardAdapter<T, VH> mAdapter;

    private RecyclerView rv;

    public ItemTouchHelperCallback(TouchCardAdapter<T, VH> mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        rv = recyclerView;
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (state != DragStateEnum.STATIC) {
            state = DragStateEnum.STATIC;
            if (listener != null) {
                listener.onStateChange(state);
            }
        }
        viewHolder.itemView.setRotation(0f);
        int count = recyclerView.getChildCount();

        for (int i = 0; i < count; i++) {
            //获取的view从下层到上层
            View view = recyclerView.getChildAt(i);
            int level = cardConfig.SHOW_MAX_COUNT - i - 1;
            //level范围（CardConfig.SHOW_MAX_COUNT-1）-0，每个child最大只移动一个CardConfig.TRANSLATION_Y和放大CardConfig.SCALE

            int x = SizeUtils.dp2px(cardConfig.TRANSLATION_X);
            int realWidth = recyclerView.getLayoutManager().getDecoratedMeasuredWidth(view);
            //被缩放之后的宽度
            int scaleWidth = (int) (realWidth * (1 - level * cardConfig.SCALE));
            int relTranslation = (realWidth - scaleWidth) / 2 + x * level;

            ConstraintLayout constraintLayout = (ConstraintLayout) ((CardView) ((ViewGroup) view).getChildAt(0)).getChildAt(0);
            if (constraintLayout != null) {
                View mask = constraintLayout.getChildAt(2);
                if (level == 3) {
                    mask.setBackgroundColor(ContextCompat.getColor(recyclerView.getContext(), R.color.home_card_bg_level_3));
                } else if (level == 2) {
                    mask.setBackgroundColor(ContextCompat.getColor(recyclerView.getContext(), R.color.home_card_bg_level_2));
                } else if (level == 1) {
                    mask.setBackgroundColor(ContextCompat.getColor(recyclerView.getContext(), R.color.home_card_bg_level_1));
                }
            }
            if (level == cardConfig.SHOW_MAX_COUNT - 1) { // 最下层的不动和最后第二层重叠
                int mRealWidth = recyclerView.getLayoutManager().getDecoratedMeasuredWidth(view);
                //被缩放之后的宽度
                int mScaleWidth = (int) (mRealWidth * (1 - (level - 1) * cardConfig.SCALE));
                int mRelTranslation = (mRealWidth - mScaleWidth) / 2 + x * (level - 1);
                view.setTranslationX(mRelTranslation);
                view.setScaleX(1 - cardConfig.SCALE * (level - 1));
                view.setScaleY(1 - cardConfig.SCALE * (level - 1));
            } else if (level > 0) {
                //计算目标level的距离
                int targetScaleWidth = (int) (realWidth * (1 - (level - 1) * cardConfig.SCALE));
                int targetRelTranslation = (int) ((realWidth - targetScaleWidth) / 2f + x * (level - 1));
                int trueTranslation = targetRelTranslation + (relTranslation - targetRelTranslation);
                view.setTranslationX(trueTranslation);
                view.setScaleX(1 - level * cardConfig.SCALE);
                view.setScaleY(1 - level * cardConfig.SCALE);
            }
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        View itemView = viewHolder.itemView;
        T remove = mAdapter.getData().remove(viewHolder.getLayoutPosition());
        mAdapter.getData().add(0, remove);
        mAdapter.notifyDataSetChanged();
        if (state != DragStateEnum.STATIC) {
            state = DragStateEnum.STATIC;
            if (listener != null) {
                listener.onStateChange(state);
            }
        }
        int height = itemView.getHeight();
        int width = itemView.getWidth();
        double a = Math.atan((double) height / width) + Math.toRadians(itemView.getRotation());
        double z = Math.hypot(width / 2f, height / 2f);
        int[] location = new int[2];
        itemView.getLocationOnScreen(location);
        float x = location[0] - (width / 2f - (float) (z * Math.cos(a)));
        float y = location[1] + ((float) (z * Math.sin(a)) - height / 2f);
        Logger.d("itemView, X:" + itemView.getX() + ", Y:" + itemView.getY() + ", rawX:" + location[0] + ", rawY:" + location[1]);
        FrameLayout root = (FrameLayout) rv.getParent().getParent();
        if (root != null) {
            rv.removeView(itemView);
            itemView.setX(x);
            itemView.setY(y);
            root.addView(itemView);
        }
        itemView.animate().setDuration(400).setInterpolator(new AccelerateInterpolator(1.2f))
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        int[] location = new int[2];
                        itemView.getLocationOnScreen(location);
                        Logger.d("itemView, X:" + itemView.getX() + ", Y:" + itemView.getY() + ", rawX:" + location[0] + ", rawY:" + location[1]);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (root != null) {
                            root.removeView(itemView);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                })
                .translationY(ScreenUtils.getScreenHeight(itemView.getContext()))
                .start();
        Logger.e("itemTouchHelper", "onSwiped");

    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Logger.e("itemTouchHelper", "onChildDraw");
        if (state != DragStateEnum.DRAGING) {
            state = DragStateEnum.DRAGING;
            if (listener != null) {
                listener.onStateChange(state);
            }
        }
        //计算移动距离
        float distance = (float) Math.hypot(dX, dY);
        float maxDistance = recyclerView.getWidth() / 2f;

        //比例
        float fraction = distance / maxDistance;
        if (fraction > 1) {
            fraction = 1;
        }
        float fractionR = dX / recyclerView.getWidth();
        if (fractionR < -1)
            fractionR = -1;
        else if (fractionR > 1)
            fractionR = 1;
        viewHolder.itemView.setRotation(30f * fractionR);
        //为每个child执行动画
        int count = recyclerView.getChildCount();

        for (int i = 0; i < count; i++) {
            //获取的view从下层到上层
            View view = recyclerView.getChildAt(i);

            int level = cardConfig.SHOW_MAX_COUNT - i - 1;
            //level范围（CardConfig.SHOW_MAX_COUNT-1）-0，每个child最大只移动一个CardConfig.TRANSLATION_Y和放大CardConfig.SCALE

            int x = SizeUtils.dp2px(cardConfig.TRANSLATION_X);
            int realWidth = recyclerView.getLayoutManager().getDecoratedMeasuredWidth(view);
            //被缩放之后的宽度
            int scaleWidth = (int) (realWidth * (1 - level * cardConfig.SCALE));
            int relTranslation = (realWidth - scaleWidth) / 2 + x * level;

            ConstraintLayout constraintLayout = (ConstraintLayout) ((CardView) ((ViewGroup) view).getChildAt(0)).getChildAt(0);
            if (constraintLayout != null) {
                View mask = constraintLayout.getChildAt(2);
                if (level == 1) {
                    mask.setBackgroundColor(getCurrentColor(fraction, ContextCompat.getColor(recyclerView.getContext(), R.color.home_card_bg_level_1),
                            Color.parseColor("#00000000")));
                } else if (level == 2) {
                    mask.setBackgroundColor(getCurrentColor(fraction, ContextCompat.getColor(recyclerView.getContext(), R.color.home_card_bg_level_2),
                            ContextCompat.getColor(recyclerView.getContext(), R.color.home_card_bg_level_1)));
                } else if (level == 3) {
                    mask.setBackgroundColor(getCurrentColor(fraction, ContextCompat.getColor(recyclerView.getContext(), R.color.home_card_bg_level_3),
                            ContextCompat.getColor(recyclerView.getContext(), R.color.home_card_bg_level_2)));
                }
            }
            if (level == cardConfig.SHOW_MAX_COUNT - 1) { // 最下层的不动和最后第二层重叠
                int mRealWidth = recyclerView.getLayoutManager().getDecoratedMeasuredWidth(view);
                //被缩放之后的宽度
                int mScaleWidth = (int) (mRealWidth * (1 - (level - 1) * cardConfig.SCALE));
                int mRelTranslation = (mRealWidth - mScaleWidth) / 2 + x * (level - 1);
                view.setTranslationX(mRelTranslation);
                view.setScaleX(1 - cardConfig.SCALE * (level - 1));
                view.setScaleY(1 - cardConfig.SCALE * (level - 1));
            } else if (level > 0) {
                //计算目标level的距离
                int targetScaleWidth = (int) (realWidth * (1 - (level - 1) * cardConfig.SCALE));
                int targetRelTranslation = (int) ((realWidth - targetScaleWidth) / 2f + x * (level - 1));
                int trueTranslation = (int) (targetRelTranslation + (relTranslation - targetRelTranslation) * (1 - fraction));
                view.setTranslationX(trueTranslation);
                view.setScaleX(1 - level * cardConfig.SCALE + fraction * cardConfig.SCALE);
                view.setScaleY(1 - level * cardConfig.SCALE + fraction * cardConfig.SCALE);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return super.getSwipeThreshold(viewHolder);
    }

    public void setListener(DragStateChangeListener listener) {
        this.listener = listener;
    }

    private int getCurrentColor(float fraction, int startColor, int endColor) {
        int currentRed = Color.red(startColor) + (int) (fraction * (Color.red(endColor) - Color.red(startColor)));
        int currentGreen = Color.green(startColor) + (int) (fraction * (Color.green(endColor) - Color.green(startColor)));
        int currentBlue = Color.blue(startColor) + (int) (fraction * (Color.blue(endColor) - Color.blue(startColor)));
        int currentAlpha = Color.alpha(startColor) + (int) (fraction * (Color.alpha(endColor) - Color.alpha(startColor)));
        return Color.argb(currentAlpha, currentRed, currentGreen, currentBlue);
    }

    public enum DragStateEnum {
        DRAGING, STATIC
    }

    public interface DragStateChangeListener {
        void onStateChange(DragStateEnum state);
    }
}
