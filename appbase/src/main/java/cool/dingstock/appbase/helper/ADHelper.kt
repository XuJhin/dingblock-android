package cool.dingstock.appbase.helper

import cool.dingstock.appbase.entity.bean.config.ConfigData
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.net.retrofit.manager.RxRetrofitServiceManager
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.lib_base.BaseLibrary
import cool.dingstock.lib_base.json.JSONHelper
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.FileUtils
import cool.dingstock.lib_base.util.FileUtilsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception


/**
 * 类名：ADHelper
 * 包名：cool.dingstock.appbase.helper
 * 创建时间：2021/11/22 5:52 下午
 * 创建人： WhenYoung
 * 描述：
 **/
object ADHelper {

    const val LAST_SHOW_AD_TIME = "LastShowAdTime"

    fun updateCache(configData: ConfigData, lastImaUrl: String, lastDownLoadUrl: String) {
        if (configData.bizAD?.mediaUrl.isNullOrEmpty()) {
            ConfigSPHelper.getInstance()
                .save(MobileHelper.APP_CONFIG_KEY, JSONHelper.toJson(configData))
            return
        }
        if (lastImaUrl == configData.bizAD?.mediaUrl) {
            configData.bizAD?.localPath = lastDownLoadUrl
            ConfigSPHelper.getInstance()
                .save(MobileHelper.APP_CONFIG_KEY, JSONHelper.toJson(configData))
            return
        }
        val request = Request.Builder().get().url(configData.bizAD?.mediaUrl!!).build()
        MainScope().launch(Dispatchers.IO) {
            RxRetrofitServiceManager.getInstance().okHttpClient.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            return
                        }
                        FileUtilsCompat
                        val fileUrl =
                            FileUtils.getDCCacheDirCompat() + "/"
                        val fileName =
                            "${System.currentTimeMillis()}-adImg.${configData.bizAD?.mediaType ?: "jpeg"}"
                        val copySuccess = copyAdData(response.body, fileUrl, fileName)
                        if (copySuccess) {
                            configData.bizAD?.localPath = "$fileUrl$fileName"
                            ConfigSPHelper.getInstance()
                                .save(MobileHelper.APP_CONFIG_KEY, JSONHelper.toJson(configData))
                        }
                    }
                })

        }

    }

    /**
     * 存储文件
     *
     * @param body          请求响应body
     * @param savedFiledDir 存储dir
     * @param fileName      文件名
     */
    private fun copyAdData(body: ResponseBody?, savedFiledDir: String, fileName: String): Boolean {
        if (null == body) {
            return false
        }
        var `is`: InputStream? = null
        var fos: FileOutputStream? = null
        var copySuccess: Boolean
        try {
            `is` = body.byteStream()
            val buf = ByteArray(2048)
            var len: Int
            val dir = File(savedFiledDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, fileName)
            fos = FileOutputStream(file)
            while (`is`.read(buf).also { len = it } != -1) {
                fos.write(buf, 0, len)
            }
            fos.flush()
            copySuccess = true
        } catch (e: Exception) {
            e.printStackTrace()
            copySuccess = false
        } finally {
            try {
                body.close()
                if (null != `is`) {
                    `is`.close()
                }
                if (null != fos) {
                    fos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return copySuccess
    }


    fun isInBizAd(): Boolean {
        val currentMillis = System.currentTimeMillis()
        val configData = MobileHelper.getInstance().configData
        if (currentMillis >= (configData.bizAD?.startTime ?: 0)
            && currentMillis <= (configData.bizAD?.endTime ?: 0)
        ) {
            return true
        }
        return false
    }

    fun checkBizAd(): Boolean {
        val bizAdEntity = MobileHelper.getInstance().configData.bizAD ?: return false
        if (!isInBizAd()) {
            return false
        }
        val lastShowTime = getLastShowTime()
        val interval = (if (LoginUtils.getCurrentUser()?.isVip() == true) {
            bizAdEntity.vipInterval
        } else {
            bizAdEntity.showInterval
        } ?: return false) * 1000L
        //小于间隔时间
        if (System.currentTimeMillis() - lastShowTime < interval) {
            return false
        }
        bizAdEntity.localPath ?: return false
        return true
    }

    fun getLastShowTime(): Long {
        return ConfigSPHelper.getInstance().getLong(LAST_SHOW_AD_TIME, 0)
    }

    fun onBizAdShow() {
        ConfigSPHelper.getInstance().save(LAST_SHOW_AD_TIME, System.currentTimeMillis())
    }


}