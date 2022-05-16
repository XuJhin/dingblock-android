package cool.dingstock.appbase.constant;

/**
 * @author wangjiang CreateAt Time 2021/6/1  10:10
 */
public interface ShopConstant {
	String DC_SHOP = "dcShop";
	
	interface ServerLoader {
		String OVERSEA_HOME_FRAGMENT = "oversea_h_f";
	}
	
	interface Path {
		String MY_ADDRESS = "/shop/myAddress";
		String ADD_ADDRESS = "/shop/addAddress";
		String COUPON_LIST = "/shop/coupon_list";
		String GOODS_DETAILS = "/oversea/goods";
		String ORDER_LIST = "/shop/order_list";
		String ORDER_DETAIL = "/shop/order_detail";
		String PAY_SHOP = "/shop/pay_shop";
		String CATEGORY_INDEX = "/oversea/category";
		String PAY_SUCCESS = "/shop/pay_success";
		
		String LOGISTICS_TRACKING = "/shop/logisticsTracking";
		String GOODS_SHOP_CAR = "/shop/car";
		String GOODS_ORDERS = "/shop/orders";
		//搜索展示
		String SEARCH_FILTER_ALL = "/shop/filter";
		String SEARCH_HISTORY = "/shop/search";
		String SEARCH_RESULT = "/oversea/searchResult";
	}
	
	interface Uri {
		String COUPON_LIST = RouterConstant.getSchemeHost() + ShopConstant.Path.COUPON_LIST;
		String MY_ADDRESS = RouterConstant.getSchemeHost() + ShopConstant.Path.MY_ADDRESS;
		String ADD_ADDRESS = RouterConstant.getSchemeHost() + ShopConstant.Path.ADD_ADDRESS;
		String GOODS_DETAILS = RouterConstant.getSchemeHost() + ShopConstant.Path.GOODS_DETAILS;
		String GOODS_SHOP_CAR = RouterConstant.getSchemeHost() + ShopConstant.Path.GOODS_SHOP_CAR;
		String CATEGORY = RouterConstant.getSchemeHost() + ShopConstant.Path.CATEGORY_INDEX;
		String SHOP_SEARCH = RouterConstant.getSchemeHost() + ShopConstant.Path.SEARCH_HISTORY;
		String LOGISTICS_TRACKING = RouterConstant.getSchemeHost() + ShopConstant.Path.LOGISTICS_TRACKING;
		String GOODS_ORDERS = RouterConstant.getSchemeHost() + ShopConstant.Path.GOODS_ORDERS;
		String ORDER_LIST = RouterConstant.getSchemeHost() + ShopConstant.Path.ORDER_LIST;
		String ORDER_DETAIL = RouterConstant.getSchemeHost() + ShopConstant.Path.ORDER_DETAIL;
		String PAY_SHOP = RouterConstant.getSchemeHost() + ShopConstant.Path.PAY_SHOP;
		String SEARCH_FILTER_ALL = RouterConstant.getSchemeHost() + ShopConstant.Path.SEARCH_FILTER_ALL;
		String SEARCH_RESULT = RouterConstant.getSchemeHost() + ShopConstant.Path.SEARCH_RESULT;
		String PAY_SUCCESS = RouterConstant.getSchemeHost() + ShopConstant.Path.PAY_SUCCESS;
	}
	
	interface UriParameter {
		String GOODS_ID = "sku";
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
		String SKU_ID = "skuId";
		String SEARCH_FILTER = "filter";
	}
	
	interface Parameter {
		String SEL_SKU_LIST = "selSkuList";
	}
	
	interface ResultCode {
		int SETTLE_ADDRESS = 0x0011;
	}
	
}
