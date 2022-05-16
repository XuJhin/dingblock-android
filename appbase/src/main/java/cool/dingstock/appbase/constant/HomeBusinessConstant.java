package cool.dingstock.appbase.constant;

public interface HomeBusinessConstant {
	
	String KEY_CATEGORY = "category";
	String KEY_HOMEITEMS = "homeItems";
	
	interface HomeType {
		String BANNER = "banner";
		String FEATURED = "featured";
		String HEADER = "header";
		String ARTICLE = "article";
		String RAFFLE = "raffle";
		String LAB = "lab";
	}
	
	interface CategoryType {
		String HOME = "home";
		String SNEAKERS = "sneakers";
		String STREET_WEAR = "streetwear";
		String H5 = "h5";
		String OVERSEA = "oversea";
		String TIDE_PLAY = "tidePlay";
		String SHOES = "seriesShoes";
	}
	
	interface PostType {
		String Recommend = "recommend"; // 推荐
		String OFFICIAl = "official"; // 订阅号
		String Webpage = "webpage"; // 网页
		String Latest = "latest"; // 关注
		String Followed = "followed";
		String FASHION = "fashion";
	}
}
