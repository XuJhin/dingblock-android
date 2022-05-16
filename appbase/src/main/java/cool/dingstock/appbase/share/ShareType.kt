package cool.dingstock.appbase.share

import android.graphics.Bitmap
import cool.dingstock.appbase.R

class ShareParams {
    var title: String? = null
    var content: String? = null
    var link: String? = null
    var imageUrl: String? = null
    var imageUrlForDC: String? = null
    var imageBitmap: Bitmap? = null
    var imagePath: String? = null // qq分享图片必须的

    var platformStr: String? = null
        set(value) {
            if (null == value) {
                return
            }
            platforms = value?.split("_").map { SharePlatform.getSharePlatform(it)!! }.toList()
            field = value
        }
    var platforms: List<SharePlatform>? = null

    // 口令
    var pwdUrl: String? = null

    // 小程序专用
    var mpPath: String? = null

    // 小程序ID
    var mpId :String? = null
}

// 分享的数据类型
enum class ShareType {
    Link, Image, Mp, ImageText, CONFIG;

    var params: ShareParams? = null
}

enum class DcPlatform {
    QQ, Wechat, WechatMoments,DC_DYNAMIC,SAVE_PICTURE
}

// 分享平台
enum class SharePlatform(val cnCode: String, val iconRes: Int, val shareName: String, val platform: DcPlatform? = null) {
    QQ("qq", R.drawable.icon_share_qq, "QQ", DcPlatform.QQ),
    WeChat("wechat", R.drawable.icon_share_wechat, "微信好友", DcPlatform.Wechat),
    WeChatMini("wechat", R.drawable.icon_share_wechat, "微信好友", DcPlatform.Wechat),
    WeChatMoments("wechatMoment", R.drawable.icon_share_moment, "朋友圈", DcPlatform.WechatMoments),
    Copy("copy", R.drawable.icon_share_copy, "复制链接"),
    PWD("password", R.drawable.copy_word_icon, "复制口令"),
    DC_DYNAMIC("dcDynamic", R.drawable.common_sign_share_dynamic, "盯链动态"),
    SAVE_PICTURE("savePicture", R.drawable.common_sign_share_save, "保存相册");

    companion object {
        @JvmStatic
        fun getSharePlatform(cnCode: String): SharePlatform? {
            for (sp in values()) {
                if (sp.cnCode == cnCode) {
                    return sp
                }
            }
            return null
        }
    }
}