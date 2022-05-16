package cool.dingstock.appbase.constant;

public interface WebviewConstant {

    interface UriParam {
        String TITLE = "title";
        String URL = "url";
        String IS_FULL_SCREEN = "fullscreen";
        String NO_PULL_REFRESH = "noPullRefresh";
        String TITLE_BG_COLOR = "titleBg";
        String TXT_BG_COLOR = "txtBg";
        String BACK_ICON_COLO = "backIconColor";
    }



    interface Path {
        String INDEX = "/browser/internal";
        String INDEX1 = "/browser/hybird";
        String INDEX2 = "/browser/shandw";
        String DEFAULT = "/browser/default";

        String HELPER_CENTER = "browser/helper";
    }

    interface Uri {
        String INDEX = RouterConstant.getSchemeHost() + Path.INDEX;
        String INDEX1 = RouterConstant.getSchemeHost() + Path.INDEX1;
        String INDEX2 = RouterConstant.getSchemeHost() + Path.INDEX2;
        String HELPER_CENTER = RouterConstant.getSchemeHost() + Path.HELPER_CENTER;
    }
}
