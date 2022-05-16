package cool.dingstock.appbase.helper

import android.app.Activity
import android.content.Context
import android.os.Bundle
import cool.dingstock.appbase.router.DcUriRequest

/**
 * @author WhenYoung
 *  CreateAt Time 2020/12/11  16:19
 *
 *  用于集中管理所有首页的弹窗
 *
 */
object HomeDialogHelper {

    private val list = arrayListOf<DialogIntent>()
    private var showingDialogIntent: DialogIntent? = null

    /**
     * 最高优先级显示的弹窗集合
     * */
    private val highLevelList = arrayListOf<DialogIntent>()

    /**
     * 排完之后的顺序是降序
     * */
    fun addDialogIntent(context: Context, dialogIntent: DialogIntent) {
        for (i in list.indices) {
            val dialogIntent1 = list[i]
            if (dialogIntent.level > dialogIntent1.level) {
                list.add(i, dialogIntent)
                break
            }
            if (i == list.size - 1) {
                list.add(dialogIntent)
            }
        }
        if (list.size == 0) {
            //如果list size = 0
            list.add(dialogIntent)
        }
        showLowLevelNexDialog(context)
    }

    fun addDialogIntent(context: Context, list: ArrayList<DialogIntent>) {
        for (dialogIntent in list) {
            addDialogIntent(context, dialogIntent)
        }
    }

    fun showLowLevelNexDialog(context: Context) {
        //如果有最高优先级显示弹窗就不在继续 判断显示弹窗了
        if (highLevelList.size > 0) {
            return
        }
        //目前没有显示的
        if (showingDialogIntent != null) {
            return
        }
        if (list.size == 0) {
            return
        }
        val dialogIntent = list.removeAt(0)
        showingDialogIntent = dialogIntent
        val builder = DcUriRequest(context, dialogIntent.link)
        dialogIntent.bundle?.let {
            builder.putExtras(it)
        }
        dialogIntent.map?.let {
            for (key in it.keys) {
                it[key]?.let {
                    builder.putUriParameter(key, it)
                }
            }
        }
        when (dialogIntent.aniType) {
            DcUriRequest.CENTER_ANI -> {
                builder.dialogCenterAni()
            }
            DcUriRequest.BOTTOM_ANI ->{
                builder.dialogBottomAni()
            }
        }
        builder.start()
    }

    /**
     * 一般 弹窗关闭
     */
    fun dismissDialog(context: Context) {
        //如果当前没显示。直接显示列表里面优先级最高的
        if (showingDialogIntent == null) {
            showLowLevelNexDialog(context)
        } else {
            //如果有，就判断
            (context as? Activity)?.let {
                it.intent.data?.let {
                    if (it.toString().contains(showingDialogIntent!!.link)) {//当前关闭的是 当前显示的才显示下一个
                        showingDialogIntent = null
                        showLowLevelNexDialog(context)
                    }
                }
            }
        }

    }

    /**
     * 高等级弹窗 立即显示
     */
    fun showHighLevel(context: Context, dialogIntent: DialogIntent) {
        highLevelList.add(0, dialogIntent)
        val builder = DcUriRequest(context, dialogIntent.link)
        dialogIntent.bundle?.let {
            builder.putExtras(it)
        }
        dialogIntent.map?.let {
            for (key in it.keys) {
                it[key]?.let {
                    builder.putUriParameter(key, it)
                }
            }
        }
        builder.start()
    }

    /**
     * 高等级 弹窗关闭
     */
    fun dismissHigLevel(context: Context) {
        if (highLevelList.size > 0) {
            highLevelList.removeAt(0)
        }
        showLowLevelNexDialog(context)
    }


}

data class DialogIntent(val link: String, val level: Int, var bundle: Bundle? = null, var map: Map<String, String>? = null, var aniType: String = DcUriRequest.CENTER_ANI)
