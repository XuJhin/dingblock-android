package cool.mobile.account.share

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cool.dingstock.appbase.R
import cool.dingstock.appbase.net.mobile.MobileHelper
import cool.dingstock.appbase.share.DcPlatform
import cool.dingstock.appbase.share.SharePlatform
import cool.dingstock.appbase.share.ShareType
import cool.dingstock.lib_base.util.CollectionUtils


// Dialog只负责展示UI逻辑，没有任何实际的分享、请求逻辑
class ShareDialog(context: Context, style: Int) : Dialog(context, style) {

    var utEventId = ""

    constructor(context: Context, needBlackWindow: Boolean) : this(context, if (needBlackWindow) 0 else R.style.MyDialogStyle)

    constructor(context: Context) : this(context, true)

    private val shareList: MutableList<SharePlatform> = arrayListOf()
    private val shareView = LayoutInflater.from(context).inflate(R.layout.dialog_share, null, false)
    private val shareRv = shareView.findViewById<RecyclerView>(R.id.rv_share)
    private val shareAdapter = ShareAdapter()

    var shareHelper = ShareHelper()
    lateinit var shareType: ShareType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewAndEvent()
    }


    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setWindowAnimations(R.style.DC_bottom_dialog_animation)
        setContentView(shareView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))
    }

    override fun show() {
        super.show()
        val attributes = window?.attributes
        attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = attributes
        window?.setGravity(Gravity.BOTTOM)
    }

    @SuppressLint("ResourceType")
    private fun initViewAndEvent() {
        val shareConfigs = MobileHelper.getInstance().shareConfigs
        if (CollectionUtils.isEmpty(shareType.params?.platforms)) {
            when (shareType) {
                ShareType.Link, ShareType.Image -> {
                    shareList.addAll(
                        listOf(
                            SharePlatform.WeChat,
                            SharePlatform.WeChatMoments,
                            SharePlatform.QQ,
                            SharePlatform.Copy
                        )
                    )
                }
                ShareType.Mp -> {
                    shareList.add(SharePlatform.WeChat)
                    if (!TextUtils.isEmpty(shareType.params?.link)) {
                        shareList.addAll(listOf(SharePlatform.WeChatMoments, SharePlatform.QQ, SharePlatform.Copy))
                    }
                }
                ShareType.ImageText -> {
                    shareList.addAll(listOf(SharePlatform.DC_DYNAMIC, SharePlatform.WeChat, SharePlatform.WeChatMoments, SharePlatform.SAVE_PICTURE))
                }
                ShareType.CONFIG -> {
                    val noLink = !TextUtils.isEmpty(shareType.params?.link)
                    if (shareConfigs != null) {
                        shareConfigs.forEach {
                            when(it.plat) {
                                "wx" -> {
                                    if (it.shareType == "circle") {
                                        if (noLink) {
                                            shareList.add(SharePlatform.WeChatMoments)
                                        }
                                    } else if (it.shareType == "mini") {
                                        shareList.add(SharePlatform.WeChatMini)
                                    } else {
                                        shareList.add(SharePlatform.WeChat)
                                    }
                                }
                                "qq" -> {
                                    if (noLink) {
                                        shareList.add(SharePlatform.QQ)
                                    }
                                }
                                "copy" -> {
                                    if (noLink) {
                                        shareList.add(SharePlatform.Copy)
                                    }
                                }
                            }
                        }
                    } else {
                        shareList.add(SharePlatform.WeChat)
                        if (noLink) {
                            shareList.addAll(listOf(SharePlatform.WeChatMoments, SharePlatform.QQ, SharePlatform.Copy))
                        }
                    }
                }
            }
        } else {
            shareList.addAll(shareType.params!!.platforms!!)
        }

        // 口令优先级最高，只要提供pwdUrl，都显示复制口令
        if (null != shareType.params?.pwdUrl && !shareList.contains(SharePlatform.PWD)) {
            shareList.add(SharePlatform.PWD)
        }

        shareAdapter.shareList.addAll(shareList)
        shareRv.apply {
            layoutManager = GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false)
            adapter = shareAdapter
        }
        shareAdapter.shareListener = object : ShareAdapter.ShareListener {
            override fun onClickShare(sharePlatform: SharePlatform) {
                shareHelper.share(context, shareType, sharePlatform, utEventId)
                dismiss()
            }
        }
        shareAdapter.notifyDataSetChanged()
    }

}