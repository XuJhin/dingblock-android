package cool.dingstock.appbase.net.api.account.callback;

public interface ILoginCallback {

    void onLoginSucceed();

    void onLoginFailed(String errorCode, String errorMsg);

}
