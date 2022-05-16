package cool.dingstock.appbase.entity.bean.common;

public class DateBean {
    private int month;
    private long timeStamp;

    public DateBean(int month, long timeStamp) {
        this.month = month;
        this.timeStamp = timeStamp;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
