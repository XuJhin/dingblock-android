package cool.dingstock.mobile;

public interface PayCallback {

    void onSucceed();

    void onFailed(String errorCode, String errorMsg);

    void onCancel();
}

