package cool.dingstock.appbase.entity.event.account;

public class EventSessionInvalid {

    public EventSessionInvalid(boolean overdue) {
        this.overdue = overdue;
    }

    /**
     * 是否过期
     */
    private boolean overdue;

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }
}
