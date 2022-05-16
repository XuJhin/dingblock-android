package cool.dingstock.appbase.constant;

/**
 * @author WhenYoung
 * CreateAt Time 2021/1/14  14:45
 */
public interface TideConstant {
    interface ServerLoader{
        String TIDE_FRAGMENT ="TideFragment";
    }

    interface UrlParam{
        String KEY_HEAVY_ID = "key_heavy_id";

    }

    interface Path{
        String SMS_PERSON_EDIT = "/calendar/smsPersonEdit";

    }

    interface  Uri{
        String SMS_PERSON_EDIT = RouterConstant.getSchemeHost() + TideConstant.Path.SMS_PERSON_EDIT;


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
