package cool.dingstock.appbase.entity.bean.setting;

import android.net.Uri;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

import cool.dingstock.lib_base.R;

public enum PushSoundEnum {
    SYSTEM(0, "系统声音", "dingstock", null),
    DEFAULT(1, "叮咚", "notification_ding", R.raw.ding),
    MARIO(2, "吃金币", "notification_mario", R.raw.mario),
    NOISE(3, "狂暴模式", "notification_noise", R.raw.noise),
    OPEN_BOX(4, "开盲盒", "dingstock", R.raw.open_box);

    private int pushType;
    private String name;
    private String channelName;
    private Integer resId;

    public static PushSoundEnum getPushSoundWithType(int type) {
        switch (type) {
            case 1:
                return DEFAULT;
            case 2:
                return MARIO;
            case 3:
                return NOISE;
            case 4:
                return OPEN_BOX;
            default:
                return SYSTEM;
        }
    }

    public static Uri getSoundUri(PushSoundEnum pushSound) {
        switch (pushSound.getPushType()) {
            case 1:
                return Uri.parse("android.resource://cool.dingstock.mobile/" + R.raw.ding);
            case 2:
                return Uri.parse("android.resource://cool.dingstock.mobile/" + R.raw.mario);
            case 3:
                return Uri.parse("android.resource://cool.dingstock.mobile/" + R.raw.noise);
            case 4:
                return Uri.parse("android.resource://cool.dingstock.mobile/" + R.raw.open_box);
            default:
                return null;
        }
    }

    static public List<String> getSoundNameList() {
        return Arrays.asList(SYSTEM.getName(), DEFAULT.getName(), MARIO.getName(), NOISE.getName());
    }

    PushSoundEnum(int pushType, String name, String channelName, @Nullable Integer resId) {
        this.pushType = pushType;
        this.name = name;
        this.channelName = channelName;
        this.resId = resId;
    }

    public int getPushType() {
        return pushType;
    }

    public Integer getResId() {
        return resId;
    }

    public String getName() {
        return name;
    }

    public String getChannelName() {
        return channelName;
    }

}
