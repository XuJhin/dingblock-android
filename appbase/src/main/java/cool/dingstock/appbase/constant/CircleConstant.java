package cool.dingstock.appbase.constant;

public interface CircleConstant {
    interface ServerLoader {
        String SELECT_GOODS_DIALOG = "selectGoodsDialog";
        String TRANSACTION_EDIT_DIALOG = "transactionEditDialog";
    }

    interface Path {

        String POST_LOCATION = "/circle/location";
        String CIRCLE_TOPIC = "/circle/topic";//好像被移除了

        String CIRCLE_TOPIC_DETAIL = "/community/topic/detail";
        String CIRCLE_DETAIL = "/community/post";

        String CIRCLE_DYNAMIC_EDIT = "/circle/edit";
        String CIRCLE_IDENTIFY_EDIT = "/circle/identify/edit";//被移除了

        String CIRCLE_DEAL_EDIT = "/circle/deal/edit";
        String CIRCLE_CAMERA_EDIT = "/circle/camera";

        String CIRCLE_SUB_COMMENTS = "/community/subComments";

        //v1.5.5新增
        String FIND_TOPIC_CREATE = "/find/create";
        //        String FIND_TOPIC_DETAIL = "/find/detail";
        String FIND_TOPIC_DETAIL = "/community/talk";
        String FIND_PARTY_DETAIL = "/community/partyDetails";
        String FIND_TOPIC_CREATED_SUCCESS = "/find/success";
        String FIND_TOPIC_ALL = "/find/all";
        String IM_USER_LIST = "/find/userList";

        String NOTIFICATION_SETTING = "/find/notification_setting";

        String SELECT_CITY = "/circle/select/city";
        String NEARBY_DETAILS = "/circle/nearbyDetails";
        String DEAL_DETAILS = "/circle/dealDetails";
        String HOT_RANK_DETAIL = "/community/hotRank";
    }

    interface Uri {
        String NOTIFICATION_SETTING = RouterConstant.getSchemeHost() + Path.NOTIFICATION_SETTING;
        String POST_LOCATION = RouterConstant.getSchemeHost() + Path.POST_LOCATION;
        String CIRCLE_TOPIC = RouterConstant.getSchemeHost() + Path.CIRCLE_TOPIC;
        String CIRCLE_TOPIC_DETAIL = RouterConstant.getSchemeHost() + Path.CIRCLE_TOPIC_DETAIL;
        String CIRCLE_DETAIL = RouterConstant.getSchemeHost() + Path.CIRCLE_DETAIL;

        String CIRCLE_DYNAMIC_EDIT = RouterConstant.getSchemeHost() + Path.CIRCLE_DYNAMIC_EDIT;
        String CIRCLE_IDENTIFY_EDIT = RouterConstant.getSchemeHost() + Path.CIRCLE_IDENTIFY_EDIT;
        String CIRCLE_DEAL_EDIT = RouterConstant.getSchemeHost() + Path.CIRCLE_DEAL_EDIT;
        String CIRCLE_CAMERA_EDIT = RouterConstant.getSchemeHost() + Path.CIRCLE_CAMERA_EDIT;

        String CIRCLE_SUB_COMMENTS = RouterConstant.getSchemeHost() + Path.CIRCLE_SUB_COMMENTS;

        //v1.5.5新增
        String FIND_TOPIC_CREATE = RouterConstant.getSchemeHost() + Path.FIND_TOPIC_CREATE;
        //        String FIND_TOPIC_DETAIL = RouterConstant.getSchemeHost() + Path./find/detail";
        String FIND_TOPIC_DETAIL = RouterConstant.getSchemeHost() + Path.FIND_TOPIC_DETAIL;
        String FIND_TOPIC_CREATED_SUCCESS = RouterConstant.getSchemeHost() + Path.FIND_TOPIC_CREATED_SUCCESS;
        String FIND_TOPIC_ALL = RouterConstant.getSchemeHost() + Path.FIND_TOPIC_ALL;
        String IM_USER_LIST = RouterConstant.getSchemeHost() + Path.IM_USER_LIST;
        String SELECT_CITY = RouterConstant.getSchemeHost() + Path.SELECT_CITY;
        String NEARBY_DETAILS = RouterConstant.getSchemeHost() + Path.NEARBY_DETAILS;
        String DEAL_DETAILS = RouterConstant.getSchemeHost() + Path.DEAL_DETAILS;
        String FIND_PARTY_DETAIL = RouterConstant.getSchemeHost() + Path.FIND_PARTY_DETAIL;
        String HOT_RANK_DETAIL = RouterConstant.getSchemeHost() + Path.HOT_RANK_DETAIL;
    }

    interface UriParams {
        String ID = "id";
        String ONE_POS = "one_pos";
        String isTask = "isTask";
        String TOPIC_DETAIL_ID = "topic_id";
        String PRODUCT_ID = "product_id";
        String KEYWORD = "keyword";
        String DETAIL_FROM_FASHION = "DETAIL_FROM_FASHION";
        String DETAIL_FROM_SHOES = "detailFromShoes";
        String DETAIL_FROM_FASHION_LIST = "DETAIL_FROM_FASHION_LIST";
        String IS_AUTO_COMMENT = "IS_AUTO_COMMENT";
        String ONE_COMMENT_ID = "ONE_COMMENT_ID";
        String IMAGE_URL = "imageUrl";
        String IMAGE_PATH = "imagePath";
        String IS_DEAL = "isDeal";
        String IS_SHOW_DEAL = "IS_SHOW_DEAL";
    }

    interface Extra {
        String DISCUSS_ID = "DISCUSS_ID";
        String IS_DISCUSS = "IS_DISCUSS";
        String KEY_COMMENT = "comment";
        String Model = "model";
        String TALK = "talk";
        String HOT = "hot";
        String CIRCLE_DYNAMIC = "circle_dynamic";
        String DEAL_GOODS = "deal_goods";
        String PARTY = "party";
        String AUTO_PLAY = "_autoPlay";
    }

    interface Constant {
        String Model_Product = "Model_Product";
        String Model_Shoes = "Model_Shoes";
    }

    interface ImNotificationFragmentType {
        String ALL = "";
        String REPLY = "interaction";
        String THINK_GOOD = "favor";
        String NEW_FOLLOWER = "follow";
    }

    interface UserListsType {
        String FOLLOW_EACH_OTHER = "FOLLOW_EACH_OTHER";
        String MY_STAR = "MY_STAR";
        String MY_FANS = "MY_FANS";
    }

    interface ImFragmentTypes {
        String followEachOther = "互相关注";
        String my_follow = "我的关注";
        String my_fans = "我的粉丝";
        String im_notification = "通知";
        String im_message = "私信";
    }
}
