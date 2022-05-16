package cool.dingstock.appbase.constant;

/**
 * @author WhenYoung CreateAt Time 2021/1/6  15:32
 */
public interface PostConstant {
	String POST = "post";
	
	interface Result {
		int LOCATION = 0x0101;
		int CITY = 0x0202;
	}
	
	interface RequestCode {
		int LOCATION_PERMISSION = 0x0011;
	}
	
	interface Path {
		String MORE_VIEW = "/post/moreView";
	}
	
	interface Uri {
		String MORE_VIEW = RouterConstant.getSchemeHost() + Path.MORE_VIEW;
	}
	
	interface UriParams {
		String ID = "id";
	}
	
	interface Extra {
		String LOCATION_RESULT = "location_result";
		String CITY_RESULT = "city_result";
		String SELECTED_LOCATION = "current_location";
		
	}
}
