package cool.dingstock.appbase.net.parse;

public interface ParseCallback<T> {
    void onSucceed(T data);

    void onFailed(String errorCode, String errorMsg);
}

