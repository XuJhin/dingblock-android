package cool.dingstock.appbase.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextPaint
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cool.dingstock.appbase.R
import cool.dingstock.appbase.SpanUtils
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.ut.UTHelper
class PrivacyPolicyDialog(context: Context, var activity: AppCompatActivity) : AlertDialog(context, R.style.AppCommonDialog) {

    var root: View? = View.inflate(context, R.layout.dialog_privacy_policy, null)
    var orderId: String = ""
    lateinit var privacyListener: PrivacyListener

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setView(root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.decorView?.setPadding(0, 0, 0, 0)
        val lp = window?.attributes
        lp?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER
        }
        window?.setWindowAnimations(cool.dingstock.appbase.R.style.ScaleDialogAni)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        initViewAndEvent()
    }

    private fun initViewAndEvent() {
        root?.findViewById<TextView>(R.id.tv_agree)?.setOnClickListener {
            if (::privacyListener.isInitialized) {
                UTHelper.commonEvent(UTConstant.Login.LOGIN_PRIVACY_DIALOG, "点击同意")
                privacyListener.onAgree()
            }
            this.dismiss()
        }
        root?.findViewById<TextView>(R.id.tv_quit)?.setOnClickListener {
            if (::privacyListener.isInitialized) {
                UTHelper.commonEvent(UTConstant.Login.LOGIN_PRIVACY_DIALOG, "点击不同意并退出")
                privacyListener.onQuit()
            }
            this.dismiss()
        }
        val tvContent = root?.findViewById<TextView>(R.id.tv_content)
        tvContent?.highlightColor = Color.TRANSPARENT
        SpanUtils.with(tvContent)
                .apply {
                    append("欢迎您使用盯链App!为了更好的保护您的权益，请您在使用我们的产品或服务之前，仔细阅读我们的")
                    append("《用户协议》")
                    setClickSpan(object : SpanUtils.NoRefCopySpan() {
                        override fun onClick(widget: View) {
                            clickProtocol()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.parseColor("#438FFF")
                        }

                    })
                    append("及")
                    append("《隐私政策》")
                    setClickSpan(object : SpanUtils.NoRefCopySpan() {
                        override fun onClick(widget: View) {
                            clickPrivacy()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.parseColor("#438FFF")
                        }

                    })
                    setForegroundColor(Color.parseColor("#FE6C6C"))
                    setUnderline()
                    append("，以帮助您更好的了解我们对于您的个人信息的保护情况，请您充分阅读并接受全部内容后点击同意\n")
                    append("1.当您注册、登录盯淘账号时，我们将收集您手机号码、验证码信息，您可查阅")
                    append("《个人信息收集清单》")
                    setClickSpan(object : SpanUtils.NoRefCopySpan() {
                        override fun onClick(widget: View) {
                            if (::privacyListener.isInitialized) {
                                clickUserCollectList()
                            }
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.parseColor("#438FFF")
                        }

                    })
                    append("快速了解我们收集和使用您个人信息的情况。\n")
                    append("2.当您浏览社区时，我们可能会收集您的个人设备信息、网络信息、日志信息\n")
                    append("3.当您发布动态时，我们可能会收集您的发布内容，展示您的昵称、头像、发布内容\n")
                    append("4.当您在社区互动时，我们可能会收集您的账号信息、网络信息\n")
                    append("5.当您使用搜索功能时，我们可能会收集并存储您的搜索关键字信息\n")
                    append("6.当您需要支付时，我们可能会收集您的第三方支付渠道的user ID（如支付宝user ID、微信open ID）\n")
                    append("7.当您设置或变更您的头像时，我们将调用相机权限/相册权限/用以上传头像\n")
                    append("如您对本《隐私政策》的内容或使用我们的服务时遇到的与隐私保护相关的事宜有任何疑问或进行咨询时，您均可以通过以下方式与我们取得联系：盯链App中“我的”-“侧边栏”-“点击客服”进行咨询或反馈意见\n")
                    append("如您同意")
                    append("《用户协议》")
                    setClickSpan(object : SpanUtils.NoRefCopySpan() {
                        override fun onClick(widget: View) {
                            if (::privacyListener.isInitialized) {
                                clickProtocol()
                            }
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.parseColor("#438FFF")
                        }

                    })
                    append("、")
                    append("《隐私政策》")
                    setClickSpan(object : SpanUtils.NoRefCopySpan() {
                        override fun onClick(widget: View) {
                            clickPrivacy()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.parseColor("#438FFF")
                        }

                    })
                    append("、")
                    append("《个人信息收集清单》")
                    setClickSpan(object : SpanUtils.NoRefCopySpan() {
                        override fun onClick(widget: View) {
                            clickUserCollectList()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.parseColor("#438FFF")
                        }

                    })
                    append("、")
                    append("《第三方信息共享清单》")
                    setClickSpan(object : SpanUtils.NoRefCopySpan() {
                        override fun onClick(widget: View) {
                            clickShareList()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.parseColor("#438FFF")
                        }

                    })
                    append("，请点击“同意”，开始使用我们的产品和服务！")
                }
                .create()
    }


    /**
     * 点击用户协议
     */
    private fun clickProtocol() {
        if (::privacyListener.isInitialized) {
            UTHelper.commonEvent(UTConstant.Login.LOGIN_PRIVACY_DIALOG, "点击《用户服务协议》")
            privacyListener.readProtocol()
        }
    }

    /**
     * 点击隐私政策
     */
    private fun clickPrivacy() {
        if (::privacyListener.isInitialized) {
            UTHelper.commonEvent(UTConstant.Login.LOGIN_PRIVACY_DIALOG, "点击《隐私政策》")
            privacyListener.readPrivacy()
        }
    }

    /**
     * 点击个人信息收集清单
     */
    private fun clickUserCollectList() {
        if (::privacyListener.isInitialized) {
            UTHelper.commonEvent(UTConstant.Login.LOGIN_PRIVACY_DIALOG, "点击《个人信息收集清单》")
            privacyListener.readUserCollectList()
        }
    }

    /**
     * 点击第三方信息共享清单
     */
    private fun clickShareList() {
        if (::privacyListener.isInitialized) {
            UTHelper.commonEvent(UTConstant.Login.LOGIN_PRIVACY_DIALOG, "点击《第三方信息共享清单》")
            privacyListener.thirdShareList()
        }
    }


    interface PrivacyListener {
        fun onAgree()
        fun onQuit()
        fun readProtocol()
        fun readPrivacy()
        fun readUserCollectList()
        fun thirdShareList()
    }
}