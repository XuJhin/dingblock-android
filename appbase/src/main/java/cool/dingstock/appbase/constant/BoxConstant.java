package cool.dingstock.appbase.constant;

/**
 * @author wangjiang CreateAt Time 2021/6/1  10:10
 */
public interface BoxConstant {
    enum BuyType {
        purchase, exchange, compose, card
    }

    String Box = "Box";
    String BOX_BUY_AGREE = "boxBuyAgreeVersion2_6_5";
    String BOX_SEARCH_LIST = "BOX_SEARCH_LIST";
    String BOX_LAST_TIME_PAY_WAY = "BOX_LAST_TIME_PAY_WAY";

    interface ServerLoader {
    }

    interface Path {
        String GOODS_DETAILS = "/box/goods";
        String BOX_STORE_INDEX = "/box/storeIndex";
        String RECEIVING_RECORD = "/box/receivingRecord";
        String GOLD_COIN_RECORD = "/box/goldCoinRecord";
        String SEARCH_FILTER_ALL = "/box/filter";
        String SEARCH_RESULT = "/box/searchResult";
        String SEARCH_HISTORY = "/box/search";
        String MY_BAG = "/box/myBag";
        String OPEN_BOX_RESULT = "/box/openBoxResult";
        String BOX_DETAIL = "/box/boxDetail";
        String BUY_SUCCESS = "/box/buySuccess";
        String BUY_GOODS = "/box/buyGoods";
        String BOX_CASHIER = "/box/buyBox";
        String CATEGORY_INDEX = "/box/category";
        String BOX_HOME_INDEX = "/box/homeIndex";
    }

    interface Uri {
        String BOX_DETAIL = RouterConstant.getSchemeHost() + Path.BOX_DETAIL;
        String OPEN_BOX_RESULT = RouterConstant.getSchemeHost() + Path.OPEN_BOX_RESULT;
        String GOODS_DETAILS = RouterConstant.getSchemeHost() + Path.GOODS_DETAILS;
        String BOX_STORE_INDEX = RouterConstant.getSchemeHost() + Path.BOX_STORE_INDEX;
        String RECEIVING_RECORD = RouterConstant.getSchemeHost() + BoxConstant.Path.RECEIVING_RECORD;
        String GOLD_COIN_RECORD = RouterConstant.getSchemeHost() + BoxConstant.Path.GOLD_COIN_RECORD;
        String MY_BAG = RouterConstant.getSchemeHost() + BoxConstant.Path.MY_BAG;
        String SEARCH_FILTER_ALL = RouterConstant.getSchemeHost() + BoxConstant.Path.SEARCH_FILTER_ALL;
        String SEARCH_RESULT = RouterConstant.getSchemeHost() + BoxConstant.Path.SEARCH_RESULT;
        String SEARCH_HISTORY = RouterConstant.getSchemeHost() + BoxConstant.Path.SEARCH_HISTORY;
        String BUY_SUCCESS = RouterConstant.getSchemeHost() + BoxConstant.Path.BUY_SUCCESS;
        String BUY_GOODS = RouterConstant.getSchemeHost() + BoxConstant.Path.BUY_GOODS;
        String BOX_CASHIER = RouterConstant.getSchemeHost() + BoxConstant.Path.BOX_CASHIER;
        String BOX_HOME_INDEX = RouterConstant.getSchemeHost() + BoxConstant.Path.BOX_HOME_INDEX;
    }

    interface UriParameter {
        String SKU_ID = "sku";

        String ORDER_ID = "orderId";
        String GOODS_NAME = "goodsName";
        String GOODS_IMA_URl = "goodsImaUrl";
        String GOODS_FOREIGN_PRICE = "goodsForeignPrice";
        String GOODS_RMB_PRICE = "goodsRmbPrice";
        String GOODS_DETAIL_COUNT = "goodCount";
        String FILTER_NAME = "filter_name";
        String FILTER_ID = "filter_id";
        String FILTER_LIST = "filter_list";
        String SETTLE_ADDRESS = "settle_address";
        String SEARCH_KEYWORD = "keyword";
        String PRICE_STR = "priceStr";
        String SEARCH_FILTER = "filter";
        String BOX_ID = "BoxId";
        String ACTION = "action";
    }

    interface Parameter {
        String CASH_PARAM = "cash_param";
        String CASH_RESULT = "cash_result";
        String CASH_PAY_MSG = "cash_pay_msg";
        String GOOD_ID = "GoodId";
        String BUY_BOX_ENTITY = "buyBoxEntity";
        String BUY_BOX_ID = "buyBoxId";
        String BUY_TYPE = "buyType";
        String BUY_THINGS = "buyThings";
        String BOX_ID = "openBoxId";
        String BOX_COUNT = "openBoxCount";
        String BOX_COUPON_ID = "openBoxCouponId";
        String PRICE = "Price";
        String BOX_TRY = "openBoxTry";
        String OPEN_BOX_ENTITY = "openBoxEntity";
        String FROM_WHERE = "fromWhere";
        String BOX_NAME = "box_name";
        String BOX_BIG_IMAGE = "box_big_image";
        String FROM_BOX_DETAIL = "from_box_detail";
        String FROM_BOX_HOME = "from_box_home";
        String FROM_BOX_MY_BAG = "from_box_my_bag";
        String IS_SHOW_REOPEN_BTN = "is_show_reopen_btn";

        String SHARE_WE_CHAT_TITLE = "share_we_chat_title";
        String SHARE_WE_CHAT_IMG = "share_we_chat_img";
        String SHARE_WE_CHAT_TYPE = "share_we_chat_type";
        String BOX_IS_FIT_COUPON = "box_is_fit_coupon";
    }

    interface ResultCode {
    }

    interface action {
        String FORMAL_CHALLENGES = "formalChallenges";
        String NO_BOX = "noBox";
    }

    interface ButThings {
        String GOODS = "goods";
        String BOX = "box";
    }
}