package cool.dingstock.appbase.helper;

import android.text.TextUtils;

import cool.dingstock.appbase.entity.bean.lab.RegConfigData;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.stroage.ConfigSPHelper;

public class LabHelper {

    private volatile static LabHelper instance;

    private static final String DC_LAB_REG = "DC_LAB_REG";

    public static LabHelper getInstance() {
        if (null == instance) {
            synchronized (LabHelper.class) {
                if (null == instance) {
                    instance = new LabHelper();
                }
            }
        }
        return instance;

    }

    private LabHelper() {
        saveRegInfo();
    }


    public void saveRegInfo() {
        String test = "{\n" +
                "    \"rangeReg\": \"例如：.*\\n\",\n" +
                "    \"replaceArray\": [\n" +
                "        \"例如：\"\n" +
                "    ],\n" +
                "    \"infoRegs\": [\n" +
                "        {\n" +
                "            \"reg\": \"[0-9]{11}\",\n" +
                "            \"key\": \"PHONE\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"reg\": \"[A-Z]{1}\",\n" +
                "            \"key\": \"SIZE\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"reg\": \"[0-9X]{18}\",\n" +
                "            \"key\": \"IDCARD\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        ConfigSPHelper.getInstance().save(DC_LAB_REG, test);
    }



    public RegConfigData getRegInfo() {
        String regStr= ConfigSPHelper.getInstance().getString(DC_LAB_REG);
        if (TextUtils.isEmpty(regStr)){
            return null;
        }
        return JSONHelper.fromJson(regStr, RegConfigData.class);
    }



}
