package cool.dingstock.appbase.serviceloader

import cool.dingstock.appbase.mvp.BaseFragment

/**
 * Copyright (C), 2015-2021, 盯链成都科技有限公司
 *
 * @Author:          xujing
 * @Date:            2021/6/25 15:01
 * @Version:         1.1.0
 * @Description:
 */
interface IOverseaServer {
	fun getOverseaFragment(): BaseFragment
}