package cool.dingstock.appbase.constant;

import org.jetbrains.annotations.Nullable;

public interface ServerConstant {
    String SERVER_PRODUCT = "https://api.dingstock.net";
    String SERVER_PRODUCT_NEW = "https://apiv2.dingstock.net";
    String SERVER_PRERELEASE = "http://47.102.153.228:7290";
    String SERVER_PRERELEASE_NEW = "https://stage.dingstock.net";
    String SERVER_DEBUG = "https://9ff0f7477c056646c6028db121642b2f.dingstock.net";
    String SERVER_DEBUG_NEW = "https://dc-oa-dev-0703.dingstock.net/";
    String SERVER_DEBUG_SHOP = "https://oversea-dev.dingstock.net";
    String KEY_VERSION = "version";
    String VERSION = "2.12.4";

    interface ErrorCode {
        int INVALID_SESSION = 401;
        int INVALID_SESSION1 = 209;
        int NOT_LOGIN_IN = 141;
    }

    interface Function {
        String TAOBAO_TIMESTAMP = "https://api.m.taobao.com/rest/api3.do?api=mtop.common.getTimestamp";
        String JD_TIMESTAMP = "https://api.m.jd.com/client.action?functionId=queryMaterialProducts&client=wh5";
        //config
        String CONFIG = "appConfig";
        String MONITOR_CONFIG = "monitorConfig";
        //home
        String HOME = "home";
        String RECOMMEND_POSTS = "recommendPosts";
        String RECENT_POSTS = "communityPost";
        String FOLLOWED_POSTS = "followedUserPost";
        String PRODUCT_ACTION = "productAction";
        String PRODUCT_GROUP = "productGroup";
        String DETAIL = "productDetail";
        String FOLLOWED = "followed";
        String APPLY_RAFFLE = "applyRaffle";
        String UPDATE_ACTIVE = "report";
        String GOTEM = "gotem";
        String SNEAKER_LAB = "sneakerLab";
        String STREET_WEAR = "streetwear";
        String STREET_WEAR_RECOMMEND = "streetwearRecommend";
        String STREET_WEAR_BRAND = "streetwearBrand";
        String STREET_WEAR_GROUP = "streetwearGroup";
        String RAFFLE_ACTION = "raffleAction";
        String PRODUCT_COMMENTS = "productComments";
        String PRODUCT_COMMENT_SUBMIT = "productCommentSubmit ";
        String SNEAKER_CALENDAR = "sneakerCalendar";
        String SNEAKER_MONTH_CALENDAR = "sneakerMonthCalendar";
        //bp
        String BP_LIST = "bpList";
        String BP_RECOMMEND = "bpRecommends";
        String PARSE_LINK = "bpResolver";
        //could url function
        String ARREEMENT = "agreement";
        String PRIVACY = "privacyUrl";
        String APP_PERMISSIONS = "appPermissionsUrl";
        String PERSONAL_INFORMATION = "personalInformationUrl";
        String EXTERNAL_INFORMATION = "externalInformationUrl";
        String QUESTIONS = "questions";
        String VIP_INSTRUCTION = "vipInstruction";


        String appPermissionsUrl = "appPermissionsUrl";//应用权限
        String privacyUrl = "privacyUrl";//	隐私政策
        String personalInformationUrl = "personalInformationUrl";//个人信息收集清单
        String externalInformationUrl = "externalInformationUrl";//		第三方信息共享清单


        //客服是否可用
        String SUPPORT = "support ";

        //mine
        String FOLLOWED_LIST = "userFollowList";
        String FANS = "userFansList";
        String REGION = "region";
        String MALL = "mall";
        String REDEEM = "redeem";
        String CHECKIN = "checkIn";
        String USER_REGION = "userRegion";
        String REGION_SHOPLIST = "regionShopList";
        //Chat
        String SUPPORT_ID = "support";
        String CHAT_GROUPS = "chatGroups";
        String JOIN_GROUP = "joinGroup";
        //setting
        String ACTIVATE = "activate";
        //monitor
        String SWITCH_MONITOR_STATE = "monitorSubscribe";
        String SHIELD = "blockedFeeds";
        String FEED = "feed";
        String SUBSCRIBED_FEED = "subscribedFeeds";
        String CHANNEL_MENU = "subscribeMenu";
        String CHANNELS = "channels";
        String CHANNEL_GROUP = "groupConfig";
        String CHANNEL_REGION = "regionConfig";
        String MONITOR_BLOCK_ITEM = "monitorBlockItem";
        String MONITOR_SUBSCRIBE = "monitorSubscribe";
        String REGION_SUBSCRIBE = "regionSubscribe";
        String USER_SUBSCRIBED = "userSubscribed";
        String MONITOR_CENTER_REGIONS = "subscribedRegions";
        String CHANNEL_DETAIL = "channelDetail";
        String MONITOR_STOCK = "monitorStock";
        //circle
        String SWITCH_MUTED = "muteChannel";
        String COMMUNITY_TOPIC_LIST = "communityTopicList";
        String COMMUNITY_TOPIC = "communityTopic";
        String RESOLVE_COMMUNITY_LINK = "resolveCommunityLink";
        String COMMUNITY_POST_SUBMIT = "communityPostSubmit";
        String COMMUNITY_POST = "communityPost";
        String COMMUNITY_SUBSCRIPTION = "communitySubscription";
        String COMMUNITY_TOPIC_DETAIL = "communityTopicDetail";
        String COMMUNITY_TOPIC_SUBSCRIBE = "communityTopicSubscribe";
        String COMMUNITY_USER_POST = "communityUserPost";
        String COMMUNITY_POST_DELETE = "communityPostDelete";
        String COMMUNITY_POST_FAVORED = "communityPostFavored";
        String COMMUNITY_POST_DETAIL = "communityPostDetail";
        String COMMUNITY_POST_COMMENTS = "communityPostComments";
        String COMMUNITY_POST_COMMENT_SUBMIT = "communityPostCommentSubmit";
        String COMMUNITY_IDENTIFY_SUBMIT = "communityIdentifySubmit";
        String COMMUNITY_TRADE_SUBMIT = "communityTradeSubmit";
        String COMMUNITY_POST_COMMENT_FAVORED = "communityPostCommentFavored";
        String PRODUCT_COMMENT_FAVORED = "productCommentFavored";
        String COMMUNITY_POST_COMMENT_REPORT = "communityPostCommentReport";
        String COMMUNITY_POST_COMMENT_DELETE = "communityPostCommentDelete";
        String COMMUNITY_IDENTIFY_CONFIG = "communityIdentifyConfig";
        String COMMUNITY_COMMENT_DETAIL = "communityCommentDetail";
        String COMMUNITY_USER_DETAIL = "userDetail";
        String COMMUNITY_USER_POSTS = "communityUserPost";
        String VIDEO_INFO = "mediaMeta";
        String PRODUCT_COMMENTS_PAGE = "productCommentsPage";
        String PRODUCT_COMMENT_DETAIL = "productCommentDetail";

        //price
        String PRICE_HOME = "priceHome";
        String PRICE_BRAND_CATEGORY = "priceBrandCategory";
        String PRICE_RANK_LIST = "priceRankList";
        String PRICE_DETAIL = "priceDetail";
        String PRICE_DETAIL_GRAPH = "priceDetailGraph";
        String PRICE_BRANDS = "priceBrands";
        String SEARCH_PRODUCT = "searchProduct";
        String PRICE_NOTIFY_SUBMIT = "priceNotifySubmit";
        String PRICE_NOTIFICATION = "priceNotification";
        String PRICE_NOTIFICATION_CANCEL = "priceNotificationCancel";
        //downLoad
        String DOWNLOAD_URL = "downloadUrl";
        //pay
        String PAY = "pay";
        String ACTIVITY_PRICES = "activityPrices";
        String ACTIVITY_REDEEM = "activityRedeem";
        String SWITCH_FOLLOW = "userRelationAction";
        String POST_BLOCK = "communityPostBlock";
        String USER_BLOCK = "communityUserBlock";
        String POST_REPORT = "communityPostReport";
        String POST_DELETE = "communityPostDelete";
        String NOTICE_LIST = "userMessageList";
        String FAVOR = "postFavorList";
        String VOTE = "communityPostVote";
        //话题
        String TALK_LIST_FOLLOWED = "talkFollowed";
        String TALK_LIST_RECOMMEND = "talkRecommend";
        String TALK_DETAIL = "talkDetail";
        String TALK_DISCOVER = "talkDiscover";
        String TALK_LIST = "talkList";
        String TALK_FOLLOW = "talkFollow";
        String TALK_CREATE = "talkCreate";
        String ALL_SELECTABLE_TOPIC = "selectableTopicList";
        //潮牌清单
        String FASHION_BRAND = "fashionBrands";
        String FASHION_POSTS = "fashionPosts";
        String FASHION_BRAND_TOP = "fashionBrandsRecommend";
        //搜索
        String SEARCH = "search";

    }

    interface ParamKEY {
        String ACTION = "action";
        String CATEGORY = "category";
        String GROUP = "group";
        String PAGE = "page";
        String PRODUCT = "product";
        String CODE = "code";
        String CHANNEL = "channel";
        String LINK = "link";
        String CONTENT = "content";
        String TALK_ID = "talkId";
        String TOPIC = "topic";
        String COMMUNITY_TOPIC = "communityTopic";
        String ATTACHMENT = "attachement";
        String SUBSCRIBE = "subscribe";
        String COMMUNITY_POST = "communityPost";
        String FAVORED = "favored";
        String COMMUNITY_POST_COMMENT = "communityPostComment";
        String COMMENT_ID = "commentId";
        String MENTIONED = "mentioned";
        String BIZ_ID = "bizId";
        String BLOCK = "block";
        String BRAND = "brand";
        String RANK = "rank";
        String OBJECT_ID = "objectId";
        String SIZE_VALUE = "sizeValue";
        String SIZE = "size";
        String INTERVAL = "interval";
        String SEARCH_STRING = "searchString";
        String PRICE = "price";
        String BRANDS = "brands";
        String ID = "id";
        String VALUE = "value";
        String SIZE_OPTION = "sizeOption";
        String TYPE = "type";
        String NOTIFICATION = "notification";
        String ROLE = "role";
        String REGION = "region";
        String NEXT_KEY = "nextKey";
        String IS_SUBSCRIBED = "isSubscribed";
        String CHANNEL_ID = "channelId";
        String REGION_ID = "regionId";
        String DATE = "date";
        String FILTERED = "filtered";
        String MUTED = "muted";
        String TARGET = "target";
        String USER = "user";
        String USER_ID = "userId";
        @Nullable
        String NEXT_STR = "nextStr";
    }

    interface ATTACHMENT {
        String IMAGE = "image";
        String LINK = "link";
    }

    interface Action {
        String SUBSCRIBE = "subscribe";
        String RETRIEVE_SUBSCRIBE = "retrieveSubscribe";
        String LIKE = "like";
        String RETRIEVE_LIKE = "retrieveLike";
        String DISLIKE = "dislike";
        String RETRIEVE_DISLIKE = "retrieveDislike";
    }

}
