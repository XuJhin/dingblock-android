package cool.dingstock.appbase.util;

import android.os.Build;

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author: xujing
 * @Date: 2021/5/12 14:41
 * @Version: 1.1.0
 * @Description:
 */
public class PlatUtils {
	// 检测MIUI
	private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
	private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
	private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
	
	public static boolean isXiaomi() {
		return "Xiaomi".equalsIgnoreCase(Build.BRAND.toLowerCase()) || "Redmi".equalsIgnoreCase(Build.BRAND.toLowerCase());
	}

	public static boolean isHuawei() {
		return "Huawei".equalsIgnoreCase(Build.BRAND.toLowerCase()) || "Redmi".equalsIgnoreCase(Build.BRAND.toLowerCase());
	}

	public static boolean isVivo() {
		return "vivo".equalsIgnoreCase(Build.BRAND.toLowerCase()) || "Redmi".equalsIgnoreCase(Build.BRAND.toLowerCase());
	}

	public static boolean isOppo() {
		return "OPPO".equalsIgnoreCase(Build.BRAND.toLowerCase()) || "Redmi".equalsIgnoreCase(Build.BRAND.toLowerCase());
	}

	public static boolean isMeizu() {
		return "Meizu".equalsIgnoreCase(Build.BRAND.toLowerCase()) || "Redmi".equalsIgnoreCase(Build.BRAND.toLowerCase());
	}

	public static String getPlat(){
		if (isOppo()){
			return "oppo";
		}else if(isVivo()){
			return "vivo";
		}else if(isXiaomi()){
			return "xiaomi";
		}else if(isHuawei()){
			return "huawei";
		}else if(isMeizu()){
			return "meizu";
		}
		return "";
	}


	
}
