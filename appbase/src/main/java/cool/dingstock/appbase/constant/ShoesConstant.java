package cool.dingstock.appbase.constant;

/**
 * @author wangjiang CreateAt Time 2021/6/1  10:10
 */
public interface ShoesConstant {

    interface ServerLoader {
        String SERIES_LIST_FRAGMENT = "seriesListFragment";
        String AUTH_DIALOG = "authDialog";
    }

    interface Path {
        String SERIES_DETAILS = "/shoes/seriesDetails";
        String GOODS_SERIES = "/shoes/goodsSeries";
        String SHOES_COMMENT = "/shoes/comment";
        String SHOES_PICTURE = "/shoes/allPicture";
    }

    interface Uri {
        String SERIES_DETAILS = RouterConstant.getSchemeHost() + ShoesConstant.Path.SERIES_DETAILS;
        String GOODS_SERIES = RouterConstant.getSchemeHost() + ShoesConstant.Path.GOODS_SERIES;
        String SHOES_COMMENT = RouterConstant.getSchemeHost() + ShoesConstant.Path.SHOES_COMMENT;
        String SHOES_PICTURE = RouterConstant.getSchemeHost() + ShoesConstant.Path.SHOES_PICTURE;
    }

    interface UriParameter {
        String SEARCH_FILTER = "filter";
        String PRODUCT_ID = "product_id";
        String SHOW_KEYBOARD = "show_keyboard";
    }

    interface Parameter {
        String SERIES_ID = "seriesId";
        String SEL_SKU_LIST = "selSkuList";
        String SERIES_INFO_NAME = "seriesInfoName";
        String SERIES_INFO_SCORE = "seriesInfoScore";
    }

    interface Extra {
        String SERIES_ID = "SERIES_ID";
        String SERIES_NAME = "SERIES_NAME";
        String SERIES_SCORE = "SERIES_SCORE";
    }

    interface ResultCode {
        int SETTLE_ADDRESS = 0x0011;
    }

}
