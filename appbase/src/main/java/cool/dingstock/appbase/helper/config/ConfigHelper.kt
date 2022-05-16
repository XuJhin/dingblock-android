package cool.dingstock.appbase.helper.config

import cool.dingstock.lib_base.stroage.ConfigSPHelper

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/5/19 17:45
 * @Version:         1.1.0
 * @Description:
 */
object ConfigHelper {
    private const val USER_AGREE_POLICY = "userAgreePolicy"

    private var isAgree: Boolean? = null

    /**
     * 用户是否同意协议
     */
    fun isAgreePolicy(): Boolean {
        if(isAgree == null){
            isAgree = ConfigSPHelper.getInstance().getBoolean(USER_AGREE_POLICY)
        }
        return isAgree == true
    }

    fun setUserAgreePolicy(agreePolicy: Boolean) {
        isAgree = agreePolicy
        ConfigSPHelper.getInstance().save(USER_AGREE_POLICY, agreePolicy)
    }
}