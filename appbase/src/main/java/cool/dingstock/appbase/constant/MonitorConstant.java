package cool.dingstock.appbase.constant;

public interface MonitorConstant {
	
	interface Path {
		String MONITOR_DIALOG_VIP = "/monitor/vip";
		String MONITOR_SETTING = "/monitor/setting";
		String MONITOR_DETAIL = "/monitor/detail";
		String MONITOR_REGIONS = "/region/list";
		String MONITOR_SHIELD = "/monitor/shield";
		String MONITOR_SHARE = "/monitor/share";
		String MONITOR_TOPIC = "/monitor/topic";
		String MONITOR_MENU_OFFLINE = "/monitor/manage?source=region";
		String MONITOR_MENU_ON_LINE = "/monitor/manage?source=feed";
		String MONITOR_MANAGE = "/monitor/manage";
		String MONITOR_MANAGE1 = "/region/manage";
		String MONITOR_SELECT_CHANNEL = "/monitor/select_channel";
		String MONITOR_SETTING_RULE = "/monitor/setting_rule";

		String MONITOR_LOG = "/monitor/log";
		String MONITOR_REMIND_SETTING = "/monitor/remind_setting";
		String MONITOR_CITY = "/monitor/monitor_city";
	}

	interface Uri {
		String MONITOR_DIALOG_VIP = RouterConstant.getSchemeHost()+Path.MONITOR_DIALOG_VIP;
		String MONITOR_SETTING = RouterConstant.getSchemeHost()+Path.MONITOR_SETTING;
		String MONITOR_DETAIL = RouterConstant.getSchemeHost()+Path.MONITOR_DETAIL;
		String MONITOR_REGIONS = RouterConstant.getSchemeHost()+Path.MONITOR_REGIONS;
		String MONITOR_SHIELD = RouterConstant.getSchemeHost()+Path.MONITOR_SHIELD;
		String MONITOR_SHARE = RouterConstant.getSchemeHost()+Path.MONITOR_SHARE;
		String MONITOR_TOPIC = RouterConstant.getSchemeHost()+Path.MONITOR_TOPIC;
		String MONITOR_MENU_OFFLINE = RouterConstant.getSchemeHost()+Path.MONITOR_MENU_OFFLINE;
		String MONITOR_MENU_ON_LINE = RouterConstant.getSchemeHost()+Path.MONITOR_MENU_ON_LINE;
		String MONITOR_MANAGE = RouterConstant.getSchemeHost()+Path.MONITOR_MANAGE;
		String MONITOR_SELECT_CHANNEL = RouterConstant.getSchemeHost()+Path.MONITOR_SELECT_CHANNEL;
		String MONITOR_SETTING_RULE = RouterConstant.getSchemeHost()+Path.MONITOR_SETTING_RULE;

		String MONITOR_LOG = RouterConstant.getSchemeHost()+Path.MONITOR_LOG;
		String MONITOR_REMIND_SETTING = RouterConstant.getSchemeHost()+Path.MONITOR_REMIND_SETTING;
		String MONITOR_CITY = RouterConstant.getSchemeHost()+Path.MONITOR_CITY;
	}
	
	interface UriParam {
		String KEY_PRODUCT_ID = "productId";
		String CHANNEL_ID = "channelId";
		String FILTER_ID = "filterId";
	}
	
	interface DataParam {
		String STOCK_DATA = "share_data";
		String MONITOR_HINT_POINT = "monitor_hint_point";
		String MONITOR_SHIELD_HINT_ENABLE = "monitor_is_can_show_shield";
		String OFFLINE_MONITOR_SHIELD_HINT_ENABLE = "offline_monitor_is_can_show_shield";
	}
	
}
