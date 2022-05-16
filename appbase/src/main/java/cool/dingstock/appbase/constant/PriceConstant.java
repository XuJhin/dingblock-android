package cool.dingstock.appbase.constant;

public interface PriceConstant {

    boolean ALWAYS_SHOW_GUIDE = false;

    interface Path {
        String SEARCH = "/price/search";
        String DETAIL = "/price/detail";
        String SEARCH_RESULT = "/price/searchResult";
        String REMIND = "/price/remind";
        String REMIND_INDEX = "/price/remindIndex";
        String PRICE_MAIN = "/price/main";
    }

    interface Uri {
        String SEARCH = RouterConstant.getSchemeHost()+Path.SEARCH;
        String DETAIL = RouterConstant.getSchemeHost()+Path.DETAIL;
        String SEARCH_RESULT = RouterConstant.getSchemeHost()+Path.SEARCH_RESULT;
        String REMIND = RouterConstant.getSchemeHost()+Path.REMIND;
        String REMIND_INDEX = RouterConstant.getSchemeHost()+Path.REMIND_INDEX;
        String PRICE_MAIN = RouterConstant.getSchemeHost()+Path.PRICE_MAIN;
    }

    interface UriParam {
        String ID = "productId";
        String SEARCH = "search";
    }

    interface CellType {
        String CATEGORY_COLLECTION = "categoryCollection";
        String ADS = "ads";
        String HOT_RECOMMEND = "hotRecommend";
        String RANK_LIST = "rankList";
    }


}
