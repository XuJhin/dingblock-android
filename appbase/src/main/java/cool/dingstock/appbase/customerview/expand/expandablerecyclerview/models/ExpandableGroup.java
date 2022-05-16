package cool.dingstock.appbase.customerview.expand.expandablerecyclerview.models;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;
import java.util.List;

/**
 * The backing data object for an {@link ExpandableGroup}
 */
public class ExpandableGroup<D,T> implements Parcelable {
  private String title;
  private List<T> items;
  private int lastVisibleCount=0;

  D data;
  private boolean isExpand= false;




  public ExpandableGroup(String title, List<T> items,D d) {
    this.title = title;
    this.items = items;
    this.data=d;
  }

  public boolean isExpand() {
    return isExpand;
  }

  public void setExpand(boolean expand) {
    isExpand = expand;
  }

  //融合两个列表
  public void fuseList(){
    lastVisibleCount=0;
  }



  public String getTitle() {
    return title;
  }

  public List<T> getItems() {
    return items;
  }

  public int getItemCount() {
    return items == null ? 0 : items.size()-lastVisibleCount;
  }

  public void addItem(int index,T t){
    items.add(index,t);
  }

  //会自动判断是否是移除lastVisibleItem
  public T removeItem(int index){
    if(index>0&&index<getItemCount()){
      return items.remove(index);
    } else {
      T remove = items.remove(index);
      if(lastVisibleCount>0){
        lastVisibleCount--;
      }
      return remove;
    }
  }

  public T removeItem(Object o){
    for(int i=0;i<items.size();i++){
      if(items.get(i)==o){
        return removeItem(i);
      }
    }
    return null;
  }


  public void addListVisibleItem(int index,T t){
    items.add(index,t);
    lastVisibleCount++;
  }

  public int getLastVisibleCount() {
    return lastVisibleCount;
  }

  public D getData() {
    return data;
  }

  public void setData(D data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "ExpandableGroup{" +
        "title='" + title + '\'' +
        ", items=" + items +
        '}';
  }

  protected ExpandableGroup(Parcel in) {
    title = in.readString();
    byte hasItems = in.readByte();
    int size = in.readInt();
    if (hasItems == 0x01) {
      items = new ArrayList<T>(size);
      Class<?> type = (Class<?>) in.readSerializable();
      in.readList(items, type.getClassLoader());
    } else {
      items = null;
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(title);
    if (items == null) {
      dest.writeByte((byte) (0x00));
      dest.writeInt(0);
    } else {
      dest.writeByte((byte) (0x01));
      dest.writeInt(items.size());
      final Class<?> objectsType = items.get(0).getClass();
      dest.writeSerializable(objectsType);
      dest.writeList(items);
    }
  }

  @SuppressWarnings("unused")
  public static final Creator<ExpandableGroup> CREATOR =
      new Creator<ExpandableGroup>() {
        @Override
        public ExpandableGroup createFromParcel(Parcel in) {
          return new ExpandableGroup(in);
        }

        @Override
        public ExpandableGroup[] newArray(int size) {
          return new ExpandableGroup[size];
        }
      };
}
