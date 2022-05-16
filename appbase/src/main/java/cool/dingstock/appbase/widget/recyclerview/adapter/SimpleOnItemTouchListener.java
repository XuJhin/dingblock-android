package cool.dingstock.appbase.widget.recyclerview.adapter;

import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import cool.dingstock.lib_base.util.Logger;


/**
 * Description: TODO
 * Author: Shper
 * Version: V0.1 2017/8/31
 */
public class SimpleOnItemTouchListener implements RecyclerView.OnItemTouchListener {

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
        View childView = recyclerView.findChildViewUnder(event.getX(), event.getY());
        if (null == childView) {
            Logger.w("The childView is empty.");
            return false;
        }

        return onInterceptTouchEvent(recyclerView, childView, recyclerView.getChildViewHolder(childView), event);
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView,
                                         View childView,
                                         RecyclerView.ViewHolder viewHolder,
                                         MotionEvent event) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent event) {
        View childView = recyclerView.findChildViewUnder(event.getX(), event.getY());
        if (null == childView) {
            Logger.w("The childView is empty.");
            return;
        }

        onTouchEvent(recyclerView, childView, recyclerView.getChildViewHolder(childView), event);
    }

    public void onTouchEvent(RecyclerView recyclerView,
                             View childView,
                             RecyclerView.ViewHolder viewHolder,
                             MotionEvent event) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

}
