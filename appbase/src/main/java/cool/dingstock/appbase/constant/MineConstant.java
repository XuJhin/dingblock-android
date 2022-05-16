package cool.dingstock.appbase.constant;

public interface MineConstant {
	
	String VIP_CENTER = "vipCenter";
	String DYNAMIC_PAGE = "common";
	String TRADING_PAGE = "deal";
	String SERIES_PAGE = "series";
	String LOTTERY_NOTE = "lotteryNote";
	String RAFFLE_PAGE = "raffleRecord";
	String SWITCH_PAGE_TYPE = "mineIndex";

	interface Path {
		String MALL = "/mine/mall";
		String DYNAMIC = "/mine/dynamic";
		String FOLLOW = "/mine/follow";
		String COLLECTION = "/mine/collection";
		String VIP_CENTER = "/vip/center";
		String Shield = "/setting/shield";
		String SCORE_INDEX = "/score/index";
		String EXCHANGE_RECORD = "/score/exchangeRecord";
		String EXCHANGE_DETAILS = "/score/exchangeDetails";
		String EXCHANGE_GOOD_DETAIL = "/exchange/details";
        String RECEIVE_INFORMATION = "/score/receiveInformation";
        String MEDAL_LIST = "/mine/medalList";
		String MEDAL_DETAIL = "/mine/medalDetail";
		String MODIFY_PENDANT = "/mine/modifyPendant";
    }
	
	interface Uri {
		String MALL = RouterConstant.getSchemeHost() + Path.MALL;
		String DYNAMIC = RouterConstant.getSchemeHost() + Path.DYNAMIC;
		String FOLLOW = RouterConstant.getSchemeHost() + Path.FOLLOW;
		String Shield = RouterConstant.getSchemeHost() + Path.Shield;
		String VIP_CENTER = RouterConstant.getSchemeHost() + Path.VIP_CENTER;
		String SCORE_INDEX = RouterConstant.getSchemeHost() + Path.SCORE_INDEX;
		String EXCHANGE_RECORD = RouterConstant.getSchemeHost() + Path.EXCHANGE_RECORD;
		String EXCHANGE_DETAILS = RouterConstant.getSchemeHost() + Path.EXCHANGE_DETAILS;
		String EXCHANGE_GOOD_DETAIL = RouterConstant.getSchemeHost() + Path.EXCHANGE_GOOD_DETAIL;
        String RECEIVE_INFORMATION = RouterConstant.getSchemeHost() + Path.RECEIVE_INFORMATION;
        String METAL_LIST = RouterConstant.getSchemeHost() + Path.MEDAL_LIST;
		String MEDAL_DETAIL = RouterConstant.getSchemeHost() + Path.MEDAL_DETAIL;
		String MODIFY_PENDANT = RouterConstant.getSchemeHost() + Path.MODIFY_PENDANT;
    }
	
	interface PARAM_KEY {
		String LONGITUDE = "longitude";
		String LATITUDE = "latitude";
		String SUCCESS_FINISH = "finish";
		String ID = "id";
		String USER_DYNAMIC_PAGE = "user_dynamic_page";
		String MEDAL_ID = "medalId";
	}
	
	interface ExtraParam {
		String FROM_WHERE = "from_where";
		String FOLLOW_TYPE = "follow_type";
		String EXCHANGE_GOOD = "exchange_good";
		String EXCHANGE_USER_INFO = "exchange_user_info";
		String FROM_LIST = "fromList";
	}
	
	interface ScoreTask {
		String hotToday = "hotToday";
		String watchAds = "watchAds";
	}
	
	interface VipLayerType {
		String activity1 = "newuser_discount";//1
		String activity2 = "timelimit_discount";//2
		String activity3 = "newuser_trial15";//3
		String activity4 = "backflow_discount_phase1";//4
		String activity5 = "backflow_discount_phase2";//5
		String activity6 = "backflow_discount_phase3";//6
	}
}
