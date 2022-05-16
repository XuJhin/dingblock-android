package net.dingblock.mobile

import android.content.res.Configuration
import cool.dingstock.appbase.app.AppBaseSDKInitor
import cool.dingstock.appbase.app.BaseDcApplication
import cool.dingstock.appbase.app.SDKInitHelper
import cool.dingstock.appbase.helper.front.AppFrontBackHelper
import cool.dingstock.appbase.helper.front.OnAppStatusListener
import cool.mobile.account.initor.AccountInitor.init
class DCApplication : BaseDcApplication() {

    init {
        SDKInitHelper.registerInitor(AppBaseSDKInitor)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    /**
     * 切换至后台的时间
     * 用于判断广告时间
     */
    override fun initModuleInitor() {
        init(this)
        observerAppFront()
    }

    private fun observerAppFront() {
        val appFrontBackHelper = AppFrontBackHelper()
        appFrontBackHelper.register(this, object : OnAppStatusListener {
            override fun onFront() {
            }

            override fun onBack() {
            }
        })
    }
}