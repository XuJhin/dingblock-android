package cool.dingstock.appbase.constant;

public interface PushConstant {

    interface XiaoMi {
        String APP_ID = "2882303761517972943";
        String APP_KEY = "5661797263943";
    }

    interface MeiZu{
        String APP_ID = "122382";
        String APP_KEY = "3879b37a6828413887ded42bbed6b96f";
    }

    interface Oppo{
        String APP_SECRET = "b0e3c038fe1241cea79e970d7650982e";
        String APP_KEY = "167c9d9790c44b49bcb75ec08874a052";
    }


    interface Key{
        String KEY_ROUTE = "route";
        String KEY_URL = "url";
        String KYE_NOTICE="notice";
        String KEY_ACTION_VIEW="actionView";
        String KEY_IGNORE_AD="adIgnore";
    }


    interface Event{
        String UPDATE_USER_INFO="updateUserInfo";
        String USER_PAY_SUCCESS="userPaySuccess";
        String USER_LOGGEDIN="userLoggedIn";
        String ON_BACK_CLICK= "onBackClick";
        String SCORE_STATUS_CHANGE = "scoreStatusChange";
        String COMMUNITY_FOLLOW = "communityFollow";
        String COMMUNITY_REPLY = "communityReply";
        String COMMUNITY_COMMENT = "communityComment";
        String COMMUNITY_FAVOR = "communityFavor";
        String PAY_SUCCESS="paySuccess";



    }
}
