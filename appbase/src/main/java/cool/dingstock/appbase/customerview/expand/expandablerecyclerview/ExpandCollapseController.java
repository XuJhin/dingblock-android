package cool.dingstock.appbase.customerview.expand.expandablerecyclerview;



import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.listeners.ExpandCollapseListener;
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.models.ExpandableGroup;
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.models.ExpandableList;
import cool.dingstock.appbase.customerview.expand.expandablerecyclerview.models.ExpandableListPosition;

/**
 * This class is the sits between the backing {@link ExpandableList} and the {@link
 * ExpandableRecyclerViewAdapter} and mediates the expanding and collapsing of {@link
 * ExpandableGroup}
 */
public class ExpandCollapseController {

  private ExpandCollapseListener listener;
  private ExpandableList expandableList;

  public ExpandCollapseController(ExpandableList expandableList, ExpandCollapseListener listener) {
    this.expandableList = expandableList;
    this.listener = listener;
  }

  /**
   * Collapse a group
   *
   * @param listPosition position of the group to collapse
   */
  private void collapseGroup(ExpandableListPosition listPosition) {
    ExpandableGroup group = expandableList.groups.get(listPosition.groupPos);
    group.setExpand(false);
//    expandableList.groups.get(listPosition.groupPos).fuseList();
    if (listener != null) {
      listener.onGroupCollapsed(expandableList.getFlattenedGroupIndex(listPosition) + 1,
              group.getItemCount());
    }
  }

  /**
   * Expand a group
   *
   * @param listPosition the group to be expanded
   */
  private void expandGroup(ExpandableListPosition listPosition) {
    expandableList.groups.get(listPosition.groupPos).setExpand(true);
    if (listener != null) {
      listener.onGroupExpanded(expandableList.getFlattenedGroupIndex(listPosition) + 1,
          expandableList.groups.get(listPosition.groupPos).getItemCount());
    }
    // 融合list，让放在最后点缀的几个item 融合到整个列表里面去,一定要在展开动作执行完成之后在融合
    expandableList.groups.get(listPosition.groupPos).fuseList();
  }

  /**
   * @param group the {@link ExpandableGroup} being checked for its collapsed state
   * @return true if {@code group} is expanded, false if it is collapsed
   */
  public boolean isGroupExpanded(ExpandableGroup group) {
    int groupIndex = expandableList.groups.indexOf(group);
    return expandableList.groups.get(groupIndex).isExpand();
  }

  /**
   * @param flatPos the flattened position of an item in the list
   * @return true if {@code group} is expanded, false if it is collapsed
   */
  public boolean isGroupExpanded(int flatPos) {
    ExpandableListPosition listPosition = expandableList.getUnflattenedPosition(flatPos);
    return expandableList.groups.get(listPosition.groupPos).isExpand();
  }

  /**
   * @param flatPos The flat list position of the group
   * @return false if the group is expanded, *after* the toggle, true if the group is now collapsed
   */
  public boolean toggleGroup(int flatPos) {
    ExpandableListPosition listPos = expandableList.getUnflattenedPosition(flatPos);
    boolean expanded = expandableList.groups.get(listPos.groupPos).isExpand();
    if (expanded) {
      collapseGroup(listPos);
    } else {
      expandGroup(listPos);
    }
    return expanded;
  }

  public boolean toggleGroup(ExpandableGroup group) {
    if(expandableList==null||expandableList.groups==null||expandableList.groups.size()==0){
      return false;
    }
    ExpandableListPosition listPos =
        expandableList.getUnflattenedPosition(expandableList.getFlattenedGroupIndex(group));
    boolean expanded = expandableList.groups.get(listPos.groupPos).isExpand();
    if (expanded) {
      collapseGroup(listPos);
    } else {
      expandGroup(listPos);
    }
    return expanded;
  }

}