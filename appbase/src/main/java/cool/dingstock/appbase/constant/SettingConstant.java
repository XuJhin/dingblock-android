package cool.dingstock.appbase.constant;

public interface SettingConstant {
    interface Function {
        String USER_DETAIL = "updateUserInfo";
    }

    interface Path {
        String INDEX = "/setting/index";
        String ABOUT_DC = "/setting/aboutDc";
        String USER_EDIT = "/setting/userEdit";
        String VERIFY = "/setting/verify";
        String REGISTER_OUT = "/setting/registerOut";
        String ACCOUNT_SETTING = "/setting/accountSetting";
        String CHECK_PHONE = "/setting/checkPhone";
        String SET_PHONE = "/setting/setPhone";
        String SET_VIDEO = "/setting/setVideo";
        String SET_DARK_MODEL = "/setting/darkMode";
    }

    interface Uri {
        String INDEX = RouterConstant.getSchemeHost() + Path.INDEX;
        String USER_EDIT = RouterConstant.getSchemeHost() + Path.USER_EDIT;
        String VERIFY = RouterConstant.getSchemeHost() + Path.VERIFY;
        String ABOUT_DC = RouterConstant.getSchemeHost() + Path.ABOUT_DC;
        String REGISTER_OUT = RouterConstant.getSchemeHost() + Path.REGISTER_OUT;
        String ACCOUNT_SETTING = RouterConstant.getSchemeHost() + Path.ACCOUNT_SETTING;
        String CHECK_PHONE = RouterConstant.getSchemeHost() + Path.CHECK_PHONE;
        String SET_PHONE = RouterConstant.getSchemeHost() + Path.SET_PHONE;
        String SET_VIDEO = RouterConstant.getSchemeHost() + Path.SET_VIDEO;
        String SET_DARK_MODEL = RouterConstant.getSchemeHost() + Path.SET_DARK_MODEL;
    }

    interface PARAM_KEY {
        String PHONE_NUMBER = "phone_number";
        String PHONE_ZONE = "phone_zone";
        String OLD_PHONE = "old_phone";
    }
}