package cool.dingstock.appbase.constant;

public interface AccountConstant {
    String LABEL = "ACCOUNT";
    int INVALID_SESSION_CODE = 209;
    String LOGIN_ACTION = "loginAction";
    int LOGIN_FINISH = 0x10313;
    int LOGIN_2MINE = 0x10242;
    int LOGIN_2IM = 0x10243;

    interface Uri {
        String INDEX = RouterConstant.getSchemeHost() + Path.INDEX;
        String INDEX1 = RouterConstant.getSchemeHost() + Path.INDEX1;
        String LOGIN = RouterConstant.getSchemeHost() + Path.LOGIN;
        String COUNTRYCODE = RouterConstant.getSchemeHost() + Path.COUNTRYCODE;

        String MY_ADDRESS = RouterConstant.getSchemeHost() + Path.MY_ADDRESS;
        String ADD_ADDRESS = RouterConstant.getSchemeHost() + Path.ADD_ADDRESS;
        String CONFIRM_ADDRESS = RouterConstant.getSchemeHost() + Path.CONFIRM_ADDRESS;
    }

    interface Path {
        String CONFIRM_ADDRESS = "/account/confirmAddress";
        String MY_ADDRESS = "/account/myAddress";
        String ADD_ADDRESS = "/account/addAddress";
        String INDEX = "/account/index";
        String INDEX1 = "/user/login";
        String LOGIN = "/login/phone";
        String COUNTRYCODE = "/account/countrycode";
    }

    interface ExtraParam {
        String COUNT = "count";
        String ICON = "icon";
        String TITLE = "title";
        String MSG = "msg";
        String CHOOSE_ADDRESS = "chooseAddress";
        String LOCATION = "location";
        String PHONE = "phone";
        String UserAddressEntity = "UserAddressEntity";
        String ID = "id";
        String NAME = "name";
        String MOBILE = "mobile";
        String MOBILE_ZONE = "mobileZone";
        String PROVINCE = "province";
        String CITY = "city";
        String DISTRICT = "district";
        String ADDRESS = "address";
        String IS_DEFAULT = "isDefault";
        String ADD_ADDRESS = "AddAddress";
        String MY_ADDRESS = "MyAddress";
        String FROM_CONFIRM = "FromConFirm";
        String ADDRESS_COUNT = "ADDRESS_COUNT";
        String AUTH_TYPE = "authType";
        String USER_ID = "userId";
        String TOKEN = "token";
        String LOGON_ACTION = "logon_action";
        String LOGIN_BACK = "logon_back";
    }

    interface AuthType {
        String SMS = "sms";
        String WECHAT = "wechat";
    }

    interface Function {
        String SMS = "sms";
        String CAPTCHA = "captcha";
        String CHECK_WE_CHAT = "wechatLogin";
        String BIND_WE_CHAT = "bindWechat";
    }


}
