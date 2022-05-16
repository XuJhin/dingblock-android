package cool.dingstock.appbase.entity.bean.post

/**
 * Copyright (C), 2015-2020, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2020/11/6 11:06
 * @Version:         1.7.2
 * @Description:     屏蔽用户以后返回信息
 */
data class ShieldUserEntity(
        val id: String? = "",
        val createdAt: Long = 0,
        val updatedAt: Long = 0,
        val blockedBy: String? = "",
        val userId: String? = "",
        val blockUserId: String? = "",
        val blocked: Boolean = false
)

data class CancelShieldResultEntity(
        val deletedCount: Int = 0
)