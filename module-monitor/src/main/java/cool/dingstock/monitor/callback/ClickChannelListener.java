package cool.dingstock.monitor.callback;

import cool.dingstock.appbase.entity.bean.monitor.GroupChannelEntity;

public interface ClickChannelListener {
    void onClickChannel(GroupChannelEntity bean, int sectionKey, int sectionViewPosition);
}