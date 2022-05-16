package cool.dingstock.appbase.constant;

/**
 * @author WhenYoung
 * CreateAt Time 2021/1/14  14:45
 */
public interface CalendarConstant {
    interface CommentType{
        String CALENDAR = "calendar";
        String TIDE = "tide";
        String DIGITAL = "digital";
    }
    interface ServerLoader{
        String SNEAKERS_FRAGMENT ="SneakersFragmentServer";
    }

    interface UrlParam{
        String KEY_HEAVY_ID = "key_heavy_id";
        String COMMENT_TYPE = "commentType";

    }

    interface Path{
        String SMS_PERSON_EDIT = "/calendar/smsPersonEdit";

    }

    interface  Uri{
        String SMS_PERSON_EDIT = RouterConstant.getSchemeHost() + CalendarConstant.Path.SMS_PERSON_EDIT;


    }


    interface DataParam{
        String KEY_PRODUCT = "productBean";
        String KEY_SMS = "sms";
        String KEY_SMS_PERSON = "keySmsPerson";

        String KEY_BASIC_INPUT_LIST = "keyBasicInputList";
        String KEY_OTHER_INPUT_LIST = "keyOtherInputList";

        String HEAVY_SNEAKER_LIST = "heavySneakerList";
        String HEAVY_SNEAKER_LIST_POSITION = "position";
    }
}
