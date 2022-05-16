package cool.dingstock.monitor.callback;

public interface UserInfoSaveCallback {

    void onSucceed();

    void onFailed(String errorMsg);
}
