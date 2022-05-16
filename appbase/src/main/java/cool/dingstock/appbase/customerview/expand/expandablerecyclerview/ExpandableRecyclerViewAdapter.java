package cool.dingstock.appbase.customerview.expand.expandablerecyclerview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.listeners.ExpandCollapseListener;
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.listeners.GroupExpandCollapseListener;
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.listeners.OnGroupClickListener;
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.listeners.OnGroupRangChangeListener;
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.models.ExpandableGroup;
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.models.ExpandableList;
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.models.ExpandableListPosition;
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.viewholders.ChildViewHolder;
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.viewholders.GroupViewHolder;

public abstract class ExpandableRecyclerViewAdapter<GVH extends GroupViewHolder, CVH extends ChildViewHolder,G,C>
        extends RecyclerView.Adapter implements ExpandCollapseListener, OnGroupClickListener {

    private static final String EXPAND_STATE_MAP = "expandable_recyclerview_adapter_expand_state_map";
    private Context context;
    protected ExpandableList expandableList;
    private ExpandCollapseController expandCollapseController;

    private OnGroupClickListener groupClickListener;
    private GroupExpandCollapseListener expandCollapseListener;
    private OnGroupRangChangeListener onGroupRangChangeListener;

    private boolean expandable = true;


    public  ExpandableRecyclerViewAdapter(Context context, List<? extends ExpandableGroup<G,C>> groups) {
        this.expandableList = new ExpandableList(groups);
        this.context = context;
        this.expandCollapseController = new ExpandCollapseController(expandableList, this);
    }


    public void notifyGroupRemove(int position) {
        ExpandableGroup expandableGroup = getGroupByPosition(position);
        if (isGroupExpanded(position)) {
            notifyItemRangeRemoved(position, expandableGroup.getItemCount() + 1);
        } else {
            notifyItemRemoved(position);
        }
        onGroupRangeChange();
    }


    public void notifyGroupRemove(ExpandableGroup expandableGroup) {
        int position = getPositionByGroup(expandableGroup);
        if (isGroupExpanded(position)) {
            notifyItemRangeRemoved(position, expandableGroup.getItems().size() + 1);
        } else {
            notifyItemRangeRemoved(position, expandableGroup.getLastVisibleCount() + 1);
        }
//    notifyGroupRemove(position);
        onGroupRangeChange();
    }

    public void notifyGroupRemove(int position, ExpandableGroup expandableGroup) {
        if (isGroupExpanded(position)) {
            notifyItemRangeRemoved(position, expandableGroup.getItemCount() + 1);
        } else {
            notifyItemRemoved(position);
        }
        onGroupRangeChange();
    }


    public void removeGroup(ExpandableGroup group) {
        notifyGroupRemove(group);
        expandableList.groups.remove(group);
    }

    /**
     * Implementation of Adapter.onCreateViewHolder(ViewGroup, int)
     * that determines if the list item is a group or a child and calls through
     * to the appropriate implementation of either {@link #onCreateGroupViewHolder(ViewGroup, int)}
     * or {@link #onCreateChildViewHolder(ViewGroup, int)}}.
     *
     * @param parent   The {@link ViewGroup} into which the new {@link View}
     *                 will be added after it is bound to an adapter position.
     * @param viewType The view type of the new {@code android.view.View}.
     * @return Either a new {@link GroupViewHolder} or a new {@link ChildViewHolder}
     * that holds a {@code android.view.View} of the given view type.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ExpandableListPosition.GROUP:
                GVH gvh = onCreateGroupViewHolder(parent, viewType);
                gvh.setOnGroupClickListener(this);
                return gvh;
            case ExpandableListPosition.CHILD:
                CVH cvh = onCreateChildViewHolder(parent, viewType);
                return cvh;
            default:
                throw new IllegalArgumentException("viewType is not valid");
        }

//    FrameLayout frameLayout = new FrameLayout(context);
//    frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
//    GVH gvh = onCreateGroupViewHolder(parent,viewType);
//    gvh.setOnGroupClickListener(this);
//    CVH cvh = onCreateChildViewHolder(parent, viewType);
//    frameLayout.addView(gvh.itemView);
//    frameLayout.addView(cvh.itemView);
//    AllViewHolder allViewHolder =new AllViewHolder(frameLayout,cvh,gvh);
//    gvh.setAllViewHolder(allViewHolder);
//    return allViewHolder;
    }


    /**
     * Implementation of Adapter.onBindViewHolder(RecyclerView.ViewHolder, int)
     * that determines if the list item is a group or a child and calls through
     * to the appropriate implementation of either {@link #onBindGroupViewHolder(GroupViewHolder,
     * int,
     * ExpandableGroup)}
     *
     * @param holder   Either the GroupViewHolder or the ChildViewHolder to bind data to
     * @param position The flat position (or index in the list of {@link
     *                 ExpandableList#getVisibleItemCount()} in the list at which to bind
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExpandableListPosition listPos = expandableList.getUnflattenedPosition(position);
        ExpandableGroup group = expandableList.getExpandableGroup(listPos);
        int ownItemViewType = listPos.type;
        switch (ownItemViewType) {
            case ExpandableListPosition.GROUP:
                GVH viewHolder = (GVH) holder;
                onBindGroupViewHolder(viewHolder, position, group);
                if (isGroupExpanded(group)) {
                    viewHolder.expand();
                } else {
                    viewHolder.collapse();
                }
//        onBindGroupViewHolder((GVH) allViewHolder.getGroupViewHolder(), position, group);
//        allViewHolder.getChildViewHolder().setVisibility(View.GONE);
//        allViewHolder.getGroupViewHolder().setVisibility(View.VISIBLE);
//        if (isGroupExpanded(group)) {
//          allViewHolder.getGroupViewHolder().expand();
//        } else {
//          allViewHolder.getGroupViewHolder().collapse();
//        }
                break;
            case ExpandableListPosition.CHILD:
                CVH cvh = (CVH) holder;
                onBindChildViewHolder(cvh, position, group, listPos.childPos);
//          allViewHolder.getChildViewHolder().setVisibility(View.VISIBLE);
//          allViewHolder.getGroupViewHolder().setVisibility(View.GONE);
//          onBindChildViewHolder(allViewHolder,(CVH) allViewHolder.getChildViewHolder(), position, group, listPos.childPos);
                break;
        }


    }

    /**
     * @return the number of group and child objects currently expanded
     * @see ExpandableList#getVisibleItemCount()
     */
    @Override
    public int getItemCount() {
        return expandableList.getVisibleItemCount();
    }

    /**
     * 通过position获取ExpandableGroup
     * <p>
     * 如果 当前position是Child那么就获取当前child的Group
     * <p>
     * 如果 当前position是Group那么久返回本身
     *
     * @param position 下标
     * @return 返回ExpandableGroup
     */
    public ExpandableGroup getGroupByPosition(int position) {
        ExpandableListPosition listPos = expandableList.getUnflattenedPosition(position);
        ExpandableGroup group = expandableList.getExpandableGroup(listPos);
        return group;
    }


    /**
     * 通过position获取当前Child在Group的下标
     * <p>
     * 如果 当前position是Group就会返回-1
     *
     * @param position 下标
     * @return 返回下标
     */
    public int getChildIndexInGroupByPosition(int position) {
        ExpandableListPosition listPos = expandableList.getUnflattenedPosition(position);
        if (getOwnItemViewType(position) == ExpandableListPosition.GROUP) {
            return -1;
        }
        return listPos.childPos;
    }

    /**
     * 通过group的下标
     *
     * @param group group
     * @return 返回下标
     */
    public int getPositionByGroup(ExpandableGroup group) {
        int index = expandableList.groups.indexOf(group);
        int position = 0;
        for (int j = 0; j < index; j++) {
            ExpandableGroup groupJ = expandableList.groups.get(j);
            position += 1;//group本身的title占一个位置，所以需要+1
            if (groupJ.isExpand()) {
                position += groupJ.getItems().size();
            } else {
                position += groupJ.getLastVisibleCount();
            }
        }
        return position;
    }


    /**
     * 获取child在整个列表的position
     *
     * @param child child
     * @return 对应position
     */
    public int getPositionByChild(Object child) {
        int position = -1;//item所在的position
        for (int i = 0; i < expandableList.groups.size(); i++) {
            ExpandableGroup expandableGroup = expandableList.groups.get(i);
            List items = expandableGroup.getItems();
            position++;//因为groupTitle的原因 position加一
            if (expandableGroup.isExpand()) {
                for (int j = 0; j < items.size(); j++) {
                    position++;//Meiz每找一个就自加一一次，
                    Object oj = items.get(j);
                    if (oj == child) {//找到了
                        return position;
                    }
                }
            } else {
                int itemCount = expandableGroup.getItemCount();
                for (int j = itemCount; j < items.size(); j++) {
                    position++;
                    Object oj = items.get(j);
                    if (oj == child) {
                        return position;
                    }
                }
            }
        }

        return -1;
    }


    protected void onGroupRangeChange() {
        if (onGroupRangChangeListener != null) {
            onGroupRangChangeListener.onGroupRangChange();
        }
    }


    /**
     * Gets the view type of the item at the given position.
     *
     * @param position The flat position in the list to get the view type of
     * @return {@value ExpandableListPosition#CHILD} or {@value ExpandableListPosition#GROUP}
     * @throws RuntimeException if the item at the given position in the list is not found
     */
    @Override
    public int getItemViewType(int position) {
        return expandableList.getUnflattenedPosition(position).type;
    }

    public int getOwnItemViewType(int position) {
        return getItemViewType(position);
    }

    /**
     * Called when a group is expanded
     *
     * @param positionStart the flat position of the first child in the {@link ExpandableGroup}
     * @param itemCount     the total number of children in the {@link ExpandableGroup}
     */
    @Override
    public void onGroupExpanded(int positionStart, int itemCount) {
        //update header
        int headerPosition = positionStart - 1;
        notifyItemChanged(headerPosition);

        // only insert if there items to insert
        if (itemCount > 0) {
            notifyItemRangeInserted(positionStart, itemCount);
            if (expandCollapseListener != null) {
                int groupIndex = expandableList.getUnflattenedPosition(positionStart).groupPos;
                expandCollapseListener.onGroupExpanded(getGroups().get(groupIndex));
            }
            onGroupRangeChange();
        }
    }

    /**
     * Called when a group is collapsed
     *
     * @param positionStart the flat position of the first child in the {@link ExpandableGroup}
     * @param itemCount     the total number of children in the {@link ExpandableGroup}
     */
    @Override
    public void onGroupCollapsed(int positionStart, int itemCount) {
        //update header
        int headerPosition = positionStart - 1;
        notifyItemChanged(headerPosition);

        // only remote if there items to remove
        if (itemCount > 0) {
            notifyItemRangeRemoved(positionStart, itemCount);
            if (expandCollapseListener != null) {
                //minus one to return the position of the header, not first child
                int groupIndex = expandableList.getUnflattenedPosition(positionStart - 1).groupPos;
                expandCollapseListener.onGroupCollapsed(getGroups().get(groupIndex));
            }
            onGroupRangeChange();
        }
    }

    /**
     * Triggered by a click on a {@link GroupViewHolder}
     *
     * @param flatPos the flat position of the {@link GroupViewHolder} that was clicked
     * @return false if click expanded group, true if click collapsed group
     */
    @Override
    public boolean onGroupClick(int flatPos) {
        if (expandable) {
            expandCollapseController.toggleGroup(flatPos);
        }
        if (groupClickListener != null) {
            groupClickListener.onGroupClick(flatPos);
        }
        return false;
    }

    /**
     * @param flatPos The flat list position of the group
     * @return true if the group is expanded, *after* the toggle, false if the group is now collapsed
     */
    public boolean toggleGroup(int flatPos) {
        return expandCollapseController.toggleGroup(flatPos);
    }

    /**
     * @param group the {@link ExpandableGroup} being toggled
     * @return true if the group is expanded, *after* the toggle, false if the group is now collapsed
     */
    public boolean toggleGroup(ExpandableGroup group) {
        return expandCollapseController.toggleGroup(group);
    }

    /**
     * @param flatPos the flattened position of an item in the list
     * @return true if {@code group} is expanded, false if it is collapsed
     */
    public boolean isGroupExpanded(int flatPos) {
        return expandCollapseController.isGroupExpanded(flatPos);
    }

    /**
     * @param group the {@link ExpandableGroup} being checked for its collapsed state
     * @return true if {@code group} is expanded, false if it is collapsed
     */
    public boolean isGroupExpanded(ExpandableGroup group) {
        return expandCollapseController.isGroupExpanded(group);
    }

    /**
     * Stores the expanded state map across state loss.
     * <p>
     * Should be called from whatever {@link Activity} that hosts the RecyclerView that {@link
     * ExpandableRecyclerViewAdapter} is attached to.
     * <p>
     * This will make sure to add the expanded state map as an extra to the
     * instance state bundle to be used in {@link #onRestoreInstanceState(Bundle)}.
     *
     * @param savedInstanceState The {@code Bundle} into which to store the
     *                           expanded state map
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
//    savedInstanceState.putBooleanArray(EXPAND_STATE_MAP, expandableList.g);
    }

    /**
     * Fetches the expandable state map from the saved instance state {@link Bundle}
     * and restores the expanded states of all of the list items.
     * <p>
     * Should be called from   in
     * the {@link Activity} that hosts the RecyclerView that this
     * {@link ExpandableRecyclerViewAdapter} is attached to.
     * <p>
     *
     * @param savedInstanceState The {@code Bundle} from which the expanded
     *                           state map is loaded
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.containsKey(EXPAND_STATE_MAP)) {
            return;
        }
//    expandableList. = savedInstanceState.getBooleanArray(EXPAND_STATE_MAP);
        notifyDataSetChanged();
    }

    public void setOnGroupClickListener(OnGroupClickListener listener) {
        groupClickListener = listener;
    }

    public void setOnGroupExpandCollapseListener(GroupExpandCollapseListener listener) {
        expandCollapseListener = listener;
    }

    public void setOnGroupRangChangeListener(OnGroupRangChangeListener onGroupRangChangeListener) {
        this.onGroupRangChangeListener = onGroupRangChangeListener;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public boolean isGroup(int position) {
        return getOwnItemViewType(position) == ExpandableListPosition.GROUP;
    }

    /**
     * The full list of {@link ExpandableGroup} backing this RecyclerView
     *
     * @return the list of {@link ExpandableGroup} that this object was instantiated with
     */
    public List<? extends ExpandableGroup> getGroups() {
        return expandableList.groups;
    }

    /**
     * Called from {@link #onCreateViewHolder(ViewGroup, int)} when  the list item created is a group
     *
     * @param viewType an int returned by {@link ExpandableRecyclerViewAdapter#getItemViewType(int)}
     * @param parent   the {@link ViewGroup} in the list for which a {@link GVH}  is being created
     * @return A {@link GVH} corresponding to the group list item with the  {@code ViewGroup} parent
     */
    public abstract GVH onCreateGroupViewHolder(ViewGroup parent, int viewType);

    /**
     * Called from {@link #onCreateViewHolder(ViewGroup, int)} when the list item created is a child
     *
     * @param viewType an int returned by {@link ExpandableRecyclerViewAdapter#getItemViewType(int)}
     * @param parent   the {@link ViewGroup} in the list for which a {@link CVH}  is being created
     * @return A {@link CVH} corresponding to child list item with the  {@code ViewGroup} parent
     */
    public abstract CVH onCreateChildViewHolder(ViewGroup parent, int viewType);

    /**
     * Called from onBindViewHolder(RecyclerView.ViewHolder, int) when the list item
     * bound to is a  child.
     * <p>
     * Bind data to the {@link CVH} here.
     *
     * @param holder       The {@code CVH} to bind data to
     * @param flatPosition the flat position (raw index) in the list at which to bind the child
     * @param group        The {@link ExpandableGroup} that the the child list item belongs to
     * @param childIndex   the index of this child within it's {@link ExpandableGroup}
     */
    public abstract void onBindChildViewHolder(CVH holder, int flatPosition, ExpandableGroup<G,C> group,
                                               int childIndex);

    /**
     * Called from onBindViewHolder(RecyclerView.ViewHolder, int) when the list item bound to is a
     * group
     * <p>
     * Bind data to the {@link GVH} here.
     *
     * @param holder       The {@code GVH} to bind data to
     * @param flatPosition the flat position (raw index) in the list at which to bind the group
     * @param group        The {@link ExpandableGroup} to be used to bind data to this {@link GVH}
     */
    public abstract void onBindGroupViewHolder(GVH holder, int flatPosition, ExpandableGroup<G,C> group);


}
