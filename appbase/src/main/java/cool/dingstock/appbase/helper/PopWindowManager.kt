package cool.dingstock.appbase.helper

import android.app.Activity
import android.content.Context
import android.os.Bundle
import cool.dingstock.appbase.constant.CommonConstant
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.dagger.AppBaseApiHelper
import cool.dingstock.appbase.entity.bean.config.ConfigData
import cool.dingstock.appbase.entity.bean.home.CommonImgDialogEntity
import cool.dingstock.appbase.entity.bean.home.FollowCountEntity
import cool.dingstock.appbase.entity.bean.home.UpdateType
import cool.dingstock.appbase.entity.bean.home.UpdateVerEntity
import cool.dingstock.appbase.entity.bean.home.fashion.ActivityEntity
import cool.dingstock.appbase.helper.TimeTagHelper.checkTimeTag
import cool.dingstock.appbase.helper.TimeTagHelper.updateTimeTag
import cool.dingstock.appbase.net.api.home.HomeApi
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.util.ClipboardHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.StringUtils
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableOnSubscribe
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*
import javax.inject.Inject

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/16  16:04
 */
object
PopWindowManager : PopWindowDismissListener {

    val net: Net by lazy {
        Net()
    }


    /// 启动app后检查弹窗，队列中可能有5个弹窗
    /// 1. 更新
    /// 2. 口令
    /// 3. 开屏
    /// 4. 用户分层
    /// 排序后展示
    fun checkAppLaunchedPopWindow(context: Context) {
        Logger.e("checkAppLaunchedPopWindow")
        //这里异步调用接口
        Flowable.merge(
            requestHomeConfig(context),
            requestPwd(context),
            requestUpdateDialog(context)
        )
            //开始收集
            .collectInto(ArrayList<DialogIntent>(), { list, dialogIntent ->
                dialogIntent?.let {
                    if (it.level >= 0) {
                        list.add(it)
                    }
                }
            })
            .toFlowable()
            .subscribe({ list ->
                list.sortByDescending {
                    it.level
                }
                for (dialogIntent in list) {
                    HomeDialogHelper.addDialogIntent(context, dialogIntent)
                }
            }, {

            })
    }

    /// 在其他页面登录成功后回到首页、在首页登陆成功、首页刷新，队列中可能有3个弹窗
    /// 1. 用户分层
    /// 2. 开屏弹窗
    /// 排序后展示
    fun checkReturnHomeOrRefreshPopWindow(context: Context, isLoginReturn: Boolean) {
        Logger.e("checkReturnHomeOrRefreshPopWindow，", "" + isLoginReturn)
        //这里异步调用接口
        //开始收集
        requestHomeConfig(context)
            .collectInto(ArrayList<DialogIntent>(), { list, dialogIntent ->
                dialogIntent?.let {
                    if (it.level >= 0) {
                        list.add(it)
                    }
                }
            })
            .toFlowable()
            .subscribe({ list ->
                list.sortByDescending {
                    it.level
                }
                for (dialogIntent in list) {
                    HomeDialogHelper.addDialogIntent(context, dialogIntent)
                }
            }, {

            })
    }

    /// 后台进入前台，检测分享口令解析，队列中只有口令弹窗
    fun checkPassword(context: Context) {


    }

    /// 指定路径显示，检测是否需要显示通知开关提醒，队列中只有通知开关弹窗
    fun checkNotification(context: Context) {

    }


    fun checkCommonImgDialog(context: Context, entity: CommonImgDialogEntity?): DialogIntent? {
        if (entity == null || StringUtils.isEmpty(entity.linkUrl) || StringUtils.isEmpty(entity.bgImgUrl)) {
            return null
        }
        //一天只弹一次
        val currentTimeMillis = System.currentTimeMillis()
        if (!(currentTimeMillis > entity.startAt && currentTimeMillis < entity.endAt)) { //不是在这中间就不显示
            return null
        }
        val timeKey = "_ShowCommonPopup"
        if (checkTimeTag(timeKey, TimeTagHelper.TimeTag.ONCE_DAY)) {
            updateTimeTag(timeKey, System.currentTimeMillis())
            if (context != null) {
                val map = HashMap<String, String>()
                map[CommonConstant.UriParams.COMMON_IMG_URL] = entity.bgImgUrl
                map[CommonConstant.UriParams.COMMON_IMG_CLICK_LINK] = entity.linkUrl
                val dialogIntent =
                    DialogIntent(CommonConstant.Uri.IMG_DIALOG, entity.weight, null, map)
                return dialogIntent
            }
        }
        return null
    }

    //判断是否需要显示升级弹窗
    private fun checkUpdateDialog(context: Context, entity: UpdateVerEntity?): DialogIntent? {
        //App启动的时候只检测一次，
        Logger.d("checkUpdateDialog")
        if (entity == null) {
            return null
        }
        if (entity.updateType == null) {
            return null
        }
        if (entity.weight == null) {
            return null
        }
        when (entity.updateType) {
            UpdateType.force -> {
            }
            UpdateType.strong -> if (checkTimeTag(
                    "strongUpdateVerDialog",
                    TimeTagHelper.TimeTag.ONCE_DAY
                )
            ) {
                updateTimeTag("strongUpdateVerDialog", System.currentTimeMillis())
            } else {
                return null
            }
            UpdateType.weak -> {
                var weakUpdateVer = ConfigSPHelper.getInstance().getString("weakUpdateVerDialog")
                var currentVersion = entity.weakMinVersion
                if (weakUpdateVer == null) {
                    weakUpdateVer = ""
                }
                if (currentVersion == null) {
                    currentVersion = ""
                }
                if (weakUpdateVer.equals(
                        entity.weakMinVersion,
                        ignoreCase = true
                    )
                ) { //如果这个版本显示过了就不显示了
                    return null
                }
                ConfigSPHelper.getInstance().save("weakUpdateVerDialog", currentVersion)
            }
        }
        val bundle = Bundle()
        bundle.putParcelable(CommonConstant.Extra.UPDATE_VER_ENTITY, entity)
        val dialogIntent = DialogIntent(
            CommonConstant.Uri.UPDATE_VER_DIALOG, entity.weight
                ?: 0, bundle, null
        )
//        addDialogIntent(context, dialogIntent)
        return dialogIntent
    }

    //判断是否需要显示活动弹窗
    private fun checkVipLearyDialog(context: Context, entity: ActivityEntity?): DialogIntent? {
        if (entity == null || StringUtils.isEmpty(entity.link)) {
            return null
        }
        val dialogIntent = DialogIntent(entity.link ?: "", entity.weight, null, null)
        return dialogIntent
    }


    /**
     * 请求 开屏 用户分层 接口
     * */
    private fun requestHomeConfig(context: Context): Flowable<DialogIntent> {
        return Flowable.create<DialogIntent>(FlowableOnSubscribe { emitter ->
            // 开屏 用户分层
            val subscribe: Disposable = net.homeApi.homeConfig().subscribe({ res ->
                val homeConfigEntity = res.res
                if (homeConfigEntity != null) {
                    checkCommonImgDialog(context, homeConfigEntity.popConfig)?.let {
                        emitter.onNext(it)
                    }
                    checkVipLearyDialog(context, homeConfigEntity.activity)?.let {
                        emitter.onNext(it)
                    }
                }
                emitter.onComplete()
            }, {
                emitter.onComplete()
            })

        }, BackpressureStrategy.BUFFER)
    }


    /**
     * 获取口令
     * */
    private fun requestPwd(context: Context): Flowable<DialogIntent> {
        return Flowable.create<DialogIntent>(FlowableOnSubscribe { emitter ->
            val copyMsg = ClipboardHelper.getCopyMsg(context)
            Logger.e("verifyCopyMsg", copyMsg)
            //判断不是刚刚本机生成的
            if (!StringUtils.isEmpty(copyMsg) && !ClipboardHelper.DingStockLabel.equals(
                    ClipboardHelper.getCopyMsgLabel(context),
                    true
                )
            ) {
                if (copyMsg!!.contains("#DS")) { //满足活动的口令
                    ClipboardHelper.copyMsg(context, "") {}
                    PartyVerifyHelper.net.commonApi.resolveParityWord(copyMsg)
                        .subscribe({
                            if (!it.err) {
                                //显示dialog
                                val bundle = Bundle()
                                bundle.putParcelable(
                                    CommonConstant.Extra.PARTY_DIALOG_ENTITY,
                                    it.res
                                )
                                val dialogIntent =
                                    DialogIntent(CommonConstant.Uri.PARTY_DIALOG, 10000, bundle)
                                emitter.onNext(dialogIntent)
                            }
                            emitter.onComplete()
                        }, {
                            emitter.onComplete()
                        })
                } else {
                    emitter.onComplete()
                }
            } else {
                emitter.onComplete()
            }

        }, BackpressureStrategy.BUFFER)
    }


    /**
     * 判断升级弹窗
     * */
    private fun requestUpdateDialog(context: Context): Flowable<DialogIntent> {
        return Flowable.create<DialogIntent>(FlowableOnSubscribe { emitter ->
            val configData: ConfigData? = MobileHelper.getInstance().configData
            Logger.d("requestUpdateDialog:$configData")
            checkUpdateDialog(context, configData?.updateConfig)?.let {
                emitter.onNext(it)
            }
            emitter.onComplete()
        }, BackpressureStrategy.BUFFER)
    }

    /**
     * 请求 用户关注
     * */
    private fun requestTest(context: Context): Flowable<DialogIntent> {
        return Flowable.create<DialogIntent>(FlowableOnSubscribe { emitter ->
            emitter.onNext(DialogIntent(CommonConstant.Uri.IMG_DIALOG, 101))
            emitter.onNext(DialogIntent(CommonConstant.Uri.VIP_LAYERED_REMIND, 102))
            emitter.onNext(DialogIntent(CommonConstant.Uri.IMG_DIALOG, 103))
            emitter.onComplete()
        }, BackpressureStrategy.BUFFER)
    }

    /**
     * 请求 用户关注
     * */
    private fun requestTest2(context: Context): Flowable<DialogIntent> {
        return Flowable.create<DialogIntent>(FlowableOnSubscribe { emitter ->
            emitter.onComplete()
        }, BackpressureStrategy.BUFFER)
    }

    class Net {
        @Inject
        lateinit var homeApi: HomeApi

        init {
            AppBaseApiHelper.appBaseComponent.inject(this)
        }
    }

    override fun onWindowDismiss(activity: Activity) {
        HomeDialogHelper.dismissHigLevel(activity)
        HomeDialogHelper.dismissDialog(activity)
    }


}

interface PopWindowDismissListener {

    fun onWindowDismiss(activity: Activity)

}

