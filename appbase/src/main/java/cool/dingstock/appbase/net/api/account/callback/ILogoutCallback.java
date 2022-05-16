package cool.dingstock.appbase.net.api.account.callback;

public interface ILogoutCallback {
    void onLogoutSucceed();
    void onLogoutFailed(String errorCode, String errorMsg);
}
