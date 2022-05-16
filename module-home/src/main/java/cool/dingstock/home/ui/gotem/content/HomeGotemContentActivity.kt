package cool.dingstock.home.ui.gotem.content

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.constant.HomeConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.mvvm.activity.viewbinding.VMBindingActivity
import cool.dingstock.appbase.widget.IconTextView
import cool.dingstock.home.R
import cool.dingstock.home.databinding.HomeActivityGotemContentLayoutBinding
import cool.dingstock.lib_base.thread.ThreadPoolHelper
import cool.dingstock.lib_base.util.FileUtils
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.ScreenUtils

@RouterUri(
    scheme = RouterConstant.SCHEME,
    host = RouterConstant.HOST,
    path = [HomeConstant.Path.GOTEM_CONTENT]
)
class HomeGotemContentActivity : VMBindingActivity<BaseViewModel, HomeActivityGotemContentLayoutBinding>() {
    private lateinit var iv: ImageView
    private lateinit var subtitleTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var shareTxt: TextView
    private lateinit var delIcon: IconTextView
    private var mShareUri: Uri? = null
    private var mRequestCode = 100
    var type: String? = HomeConstant.GotMeType.SHARE

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        iv = viewBinding.homeActivityGotemContentIv
        subtitleTxt = viewBinding.homeActivityGotemContentSubtitle
        titleTxt = viewBinding.homeActivityGotemContentTitle
        shareTxt = viewBinding.homeActivityGotemContentShareTxt
        delIcon = viewBinding.homeActivityGotemContentDelIcon

        val title = uri.getQueryParameter(HomeConstant.UriParam.KEY_GROUP_NAME)
        val subTitle = uri.getQueryParameter(HomeConstant.UriParam.KEY_SUBTITLE)
        val imageUrl = uri.getQueryParameter(HomeConstant.UriParam.KEY_IMAGE_URL)
        type = uri.getQueryParameter(HomeConstant.UriParam.KEY_TYPE)
        if (type == null || type!!.isEmpty()) {
            type = HomeConstant.GotMeType.SHARE
        }
        titleTxt.text = title
        subtitleTxt.text = subTitle
        if (HomeConstant.GotMeType.SHARE != type) {
            shareTxt.text = "我知道了"
            iv.setImageResource(R.drawable.vip_card_icon)
        } else {
            iv.load(imageUrl)
            shareTxt.text = "分享"
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, ++mRequestCode)
        } else {
            share()
        }
        hideLoadingDialog()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == mRequestCode) {
            var resultFlag = true
            for (i in permissions.indices) {
                Logger.d("申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i])
                if (grantResults[i] != 0) {
                    resultFlag = false
                }
            }
            if (resultFlag) {
                share()
            } else {
                showToastLong("分享失败")
            }
        }
    }

    override fun initListeners() {
        shareTxt.setOnClickListener {
            if (HomeConstant.GotMeType.SHARE != type) {
                finish()
                return@setOnClickListener
            }
            showLoadingDialog()
            ThreadPoolHelper.getInstance().threadExecute {
                if (null == mShareUri) {
                    val bitmap = ScreenUtils.snapShotWithoutStatusBar(this@HomeGotemContentActivity)
                    mShareUri = FileUtils.saveBitmap(bitmap)
                }
                requestPermission()
            }
        }
        delIcon.setOnClickListener { finish() }
    }

    override fun moduleTag(): String {
        return ModuleConstant.HOME_MODULE
    }

    private fun share() {
        if (null == mShareUri) {
            showToastLong("分享失败")
            return
        }

        //创建分享的Dialog
        val shareIntent = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND //设置分享行为
            type = "image/*" //设置分享内容的类型
            putExtra(Intent.EXTRA_STREAM, mShareUri)
        }, "分享")
        startActivity(shareIntent)
    }

    companion object {
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}