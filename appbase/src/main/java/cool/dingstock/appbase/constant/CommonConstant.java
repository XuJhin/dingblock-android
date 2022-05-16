package cool.dingstock.appbase.constant;

public interface CommonConstant {

    enum ClockStyle {
        None, Default, Style1, Style2, Style3
    }

    interface CommonConfigType {
        String kolInvitations = "kolInvitations";
        String clockInstruction = "clockInstruction";

    }


    interface VipRemindType {

    }

    interface VipRemindUserType {
    }

    interface Path {
        String SELECT = "/common/select";
        String SELECT_PREVIEW = "/common/select_preview";
        String PREVIEW = "/common/preview";
        String VIDEO_VIEW = "/common/videoView";
        String CLIP = "/common/clip";
        String VIP_LAYERED_REMIND = "/vip/vipLayeredDialog";
        String IMG_DIALOG = "/common/commonImgDialog";
        String PARTY_DIALOG = "/common/commonPartyDialog";
        String UPDATE_VER_DIALOG = "/common/updateVerDialog";
        String PUSH_DIALOG = "/common/checkpush";
        String TIME_CLOCK_WINDOW = "/lab/clockService";
        String TIME_CLOCK_INDEX = "/lab/clock";
        String TIME_CLOCK_CHOSE_STYLE = "/lab/choseClockStyle";
        String CSJ_ADV = "/adv/csjJl";
    }

    interface Uri {
        String SELECT = RouterConstant.getSchemeHost() + Path.SELECT;
        String SELECT_PREVIEW = RouterConstant.getSchemeHost() + Path.SELECT_PREVIEW;
        String PREVIEW = RouterConstant.getSchemeHost() + Path.PREVIEW;
        String VIDEO_VIEW = RouterConstant.getSchemeHost() + Path.VIDEO_VIEW;
        String CLIP = RouterConstant.getSchemeHost() + Path.CLIP;
        String VIP_LAYERED_REMIND = RouterConstant.getSchemeHost() + Path.VIP_LAYERED_REMIND;
        String IMG_DIALOG = RouterConstant.getSchemeHost() + Path.IMG_DIALOG;
        String PARTY_DIALOG = RouterConstant.getSchemeHost() + Path.PARTY_DIALOG;
        String UPDATE_VER_DIALOG = RouterConstant.getSchemeHost() + Path.UPDATE_VER_DIALOG;
        String PUSH_DIALOG = RouterConstant.getSchemeHost() + Path.PUSH_DIALOG;
        String TIME_CLOCK_WINDOW = RouterConstant.getSchemeHost() + Path.TIME_CLOCK_WINDOW;
        String TIME_CLOCK_INDEX = RouterConstant.getSchemeHost() + Path.TIME_CLOCK_INDEX;
        String CSJ_ADV = RouterConstant.getSchemeHost() + Path.CSJ_ADV;
        String TIME_CLOCK_CHOSE_STYLE = RouterConstant.getSchemeHost() + Path.TIME_CLOCK_CHOSE_STYLE;
    }


    interface UriParams {
        String URL = "url";
        String CIRCLE_DYNAMIC_ID = "circle_dynamic_id";
        String CIRCLE_VIDEO_TITLE = "circle_video_title";
        String CIRCLE_VIDEO_COVER = "circle_video_cover";
        String LOCAL_VIDEO_PATH = "local_video_path";

        String VIP_REMIND_TYPE = "vipRemindType";
        String VIP_REMIND_URL = "vipRemindUrl";
        String VIP_USER_TYPE = "vipRemindUserType";

        String COMMON_IMG_ENTITY = "commonImgEntity";
        String COMMON_IMG_URL = "commonImgUrl";
        String COMMON_IMG_CLICK_LINK = "commonImgClickLink";
        String COMMON_IMG_WIDTH = "commonImgWidth";
        String COMMON_IMG_HIGH = "commonImgHigh";
        String IMG_URL = "imgUrl";
        String TYPE = "type";
        String advTaskId = "advTaskId";

        String SOURCE = "source";
        String UT_EVENT_ID = "utEventId";
        String CLOCK_STYLE_INDEX = "clockStyleIndex";

        String BACK_TAG_URL = "backTagUrl";

    }

    interface Extra {
        String PARTY_DIALOG_ENTITY = "PartyDialogEntity";
        String UPDATE_VER_ENTITY = "updateVerEntity";
        String CLOCK_STYLE_INDEX = "clockStyleIndex";
    }

    interface Sp {
        String CLOCK_STYLE_KEY = "clockStyleKey";
        String IS_SHOW_SECOND_LAST_TIME = "IS_SHOW_SECOND_LAST_TIME";
    }
}
