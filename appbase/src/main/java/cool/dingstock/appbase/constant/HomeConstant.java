package cool.dingstock.appbase.constant;

public interface HomeConstant {
    String USER_TAB_ID = "usercenter";
    String HOME_TAB_ID = "home";

    interface ServerLoader {
        String NEW_SALE_ITEM = "new_sale_item";
    }

    interface SP {
        //联盟广告延迟多少展示，只存一次，默认为-1
        String AD_UNIT_DELAY_FIRST = "ad_unit_delay";
        String FIRST_INSTALL_TIME = "first_install";
        String MONITOR_EMPTY_VIEW_SHOW = "monitor_empty_view_show";
        String BP_LINK_COPY_NOT_SHOW = "link_copy_link";
        String LAUNCH_VIDEO_VERSION = "LAUNCH_VIDEO_VERSION";
        String LAUNCH_VIDEO_VERSION_NAME = "V2.0.0";
        String BP_RESULT_HINT_TAG = "bpResultHintTag";
    }

    interface Path {
        String HOME_MESSAGE = "/home/message";
        String TAB = "/home/tab";
        String DETAIL = "/product/raffle";
        String HEAVY_RELEASE = "/calendar/heavy/release";
        String HEAVY = "/calendar/featured";
        String GOTEM_INDEX = "/gotem/index";
        String GOTEM_CONTENT = "/gotem/content";
        String SMS_EDIT = "/product/raffle/sms";
        String SMS_REGISTRATION = "/home/smsRegistration";
        String LAB = "/lab";
        String CLUE = "/bp/clue";
        String MINE_REWARD = "/bp/mineReward";
        String MINE_WITHDRAWAL = "/bp/withdrawal";
        String MINE_WITHDRAWAL_RECORD = "/bp/withdrawalRecord";
        String REMIND_LIST = "/bp/remindlist";
        String SIGN_GOODS = "/bp/signGoods";
        String REGION_RAFFLE = "/region/raffle";
        String REGION_COMMENT = "/region/comment";
        String BP_JUMP = "/home/bp/route";
        String DOU_YIN = "/home/share/douyinCallback";
        String BP_GOODS_DETAIL = "/home/sneaker/good";
        String BP_LINK_PARSE = "/bp/parse";
        String RECOMMEND_FOLLOW = "/recommend/follow";
        String LAUNCH_VIDEO = "/recommend/launchVideo";
        String COUPON_LIST = "/bp/couponList";
        String THEME_AREA = "/bp/themeArea";
        String MOUTAI_AREA = "/bp/moutaiArea";
        String BASIC = "/home/basic";
        String BUY_STRATEGY = "/bp/strategy";
    }

    interface Uri {
        String TAB = RouterConstant.getSchemeHost() + Path.TAB;
        String DETAIL = RouterConstant.getSchemeHost() + Path.DETAIL;
        String HEAVY_RELEASE = RouterConstant.getSchemeHost() + Path.HEAVY_RELEASE;
        String GOTEM_CONTENT = RouterConstant.getSchemeHost() + Path.GOTEM_CONTENT;
        String SMS_EDIT = RouterConstant.getSchemeHost() + Path.SMS_EDIT;
        String REMIND_LIST = RouterConstant.getSchemeHost() + Path.REMIND_LIST;
        String MINE_WITHDRAWAL = RouterConstant.getSchemeHost() + Path.MINE_WITHDRAWAL;
        String MINE_WITHDRAWAL_RECORD = RouterConstant.getSchemeHost() + Path.MINE_WITHDRAWAL_RECORD;
        String REGION_COMMENT = RouterConstant.getSchemeHost() + Path.REGION_COMMENT;
        String BP_JUMP = RouterConstant.getSchemeHost() + Path.BP_JUMP;
        String BP_GOODS_DETAIL = RouterConstant.getSchemeHost() + Path.BP_GOODS_DETAIL;
        String BP_LINK_PARSE = RouterConstant.getSchemeHost() + Path.BP_LINK_PARSE;
        String RECOMMEND_FOLLOW = RouterConstant.getSchemeHost() + Path.RECOMMEND_FOLLOW;
        String HEAVY = RouterConstant.getSchemeHost() + Path.HEAVY;
        String BASIC = RouterConstant.getSchemeHost() + Path.BASIC;
    }

    interface UriParam {
        //强制显示广告
        String IS_BACK2FONT = "isBack2Font";
        String KEY_PRODUCT_ID = "productId";
        String KEY_PRODUCT_RAFFLE_COUNT = "raffleCount";
        String KEY_CATEGORY_ID = "categoryId";
        String HOME_POST_TYPE = "homePostType";
        String KEY_GROUP_ID = "groupId";
        String KEY_BRAND_ID = "brandId";
        String KEY_ID = "id";
        String REFRESH_TAB = "refreshTab";
        String KEY_GROUP_NAME = "groupName";
        String KEY_SUBTITLE = "subtitle";
        String KEY_IMAGE_URL = "imageUrl";
        String KEY_TYPE = "type";
        String KEY_GOOD_DETAIL = "GOOD_DETAIL";
        String KEY_BP_ROUTE_URL = "route_uri";
        String KEY_BP_PRESALE = "bp+presale";
        String KEY_BP_ROUTE_TIME = "route_time";
        String KEY_BP_GOODS_ROUTE_TIME = "goods_route_time";
        String KEY_BP_PLAT_OFFSET_TIME = "platOffsetTime";
        String KEY_BP_OFFSET_TIME = "offsetTime";
        String KEY_BP_NEED_TB_AUTH = "needTbAuth";
        String KEY_IS_TB_AUTH = "isTbAuth";
        String KEY_IS_JUMP = "isJump";
        String KEY_TB_AUTH_URL = "tbAutbUrl";
        String KEY_SHOP_URL = "bpShopUrl";
        String KEY_BP_PLAT = "plat";
        String KEY_BP_PLAT_TIME_TYPE = "plat_time_type";
        String KEY_GOODS_ID = "goodsId";
        String KEY_AUTO_SUBMIT = "auto_submit";
        String KEY_GOODS_TITLE = "goodsTitle";
        String KEY_GOODS_IMAGE_URL = "goodsImageUrl";
        String KEY_GOODS_PRICE = "goodsImagePrice";
        String KEY_SELECT_COUNT = "goodsSelectCount";
        String KEY_MAX_AMOUNT = "maxAmount";
        String PLAT_TYPE = "platType";
    }

    interface StreetWearType {
        String BRANDS = "brands";
        String GROUPS = "groups";
        String RECOMMEND = "recommend";
    }

    interface RecommendChildType {
        String PRODUCT = "product";
        String GROUP = "group";
    }

    interface GotMeType {
        String SHARE = "share";
        String VIP_RECEIVE = "vipReceive";
    }

    interface Constant {
        String POST_TYPE_RECOMMEND = "recommend";
    }

}
