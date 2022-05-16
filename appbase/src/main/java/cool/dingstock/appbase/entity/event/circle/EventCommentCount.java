package cool.dingstock.appbase.entity.event.circle;

import cool.dingstock.appbase.entity.bean.circle.CircleDynamicBean;

public class EventCommentCount {

    private boolean add;
    private String dynamicId;
    private CircleDynamicBean entity;

    public EventCommentCount(boolean add, String dynamicId) {
        this.add = add;
        this.dynamicId = dynamicId;
    }
    public EventCommentCount(boolean add, String dynamicId,CircleDynamicBean dynamicBean) {
        this.add = add;
        this.dynamicId = dynamicId;
        this.entity = dynamicBean;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public String getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(String dynamicId) {
        this.dynamicId = dynamicId;
    }

    public CircleDynamicBean getEntity() {
        return entity;
    }

    public void setEntity(CircleDynamicBean entity) {
        this.entity = entity;
    }
}
