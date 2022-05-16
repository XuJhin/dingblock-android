package cool.mobile.account.share;

public interface IAuthorizeCallback {

    void onAuthorizeSuccess(String userId,String token);

    void onAuthorizeFailed(String errorCode,String errorMsg);
}
