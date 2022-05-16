package cool.dingstock.appbase.constant;

/**
 * @author WhenYoung
 * CreateAt Time 2021/1/6  15:35
 */
public interface SearchConstant {
    interface Path {
        String SEARCH_INDEX = "/search/index";
    }
    interface Uri {
        String SEARCH_INDEX = RouterConstant.getSchemeHost()+ Path.SEARCH_INDEX;
    }
}
