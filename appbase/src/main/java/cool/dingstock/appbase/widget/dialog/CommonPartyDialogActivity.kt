package cool.dingstock.appbase.widget.dialog

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.sankuai.waimai.router.annotation.RouterUri
import cool.dingstock.appbase.R
import cool.dingstock.appbase.base.BaseObservablePopWindow
import cool.dingstock.appbase.imageload.GlideHelper
import cool.dingstock.appbase.mvvm.activity.BaseViewModel
import cool.dingstock.appbase.util.StatusBarUtil
import cool.dingstock.appbase.constant.CommonConstant
import cool.dingstock.appbase.constant.ModuleConstant
import cool.dingstock.appbase.constant.RouterConstant
import cool.dingstock.appbase.databinding.ActivityCommonPartyDialogBinding
import cool.dingstock.appbase.entity.bean.party.PartyDialogEntity

@RouterUri(scheme = RouterConstant.SCHEME, host = RouterConstant.HOST, path = [CommonConstant.Path.PARTY_DIALOG])
class CommonPartyDialogActivity : BaseObservablePopWindow<BaseViewModel, ActivityCommonPartyDialogBinding>() {

    lateinit var partyDialogEntity: PartyDialogEntity

    override fun initViewAndEvent(savedInstanceState: Bundle?) {
        val entity: PartyDialogEntity? = intent.getParcelableExtra(CommonConstant.Extra.PARTY_DIALOG_ENTITY)
        if (entity == null) {
            finish()
            return
        }
        partyDialogEntity = entity
        initView()
    }

    override fun setSystemStatusBar() {
        StatusBarUtil.setTranslucent(this)
    }

    private fun initView() {
        GlideHelper.loadCircle(partyDialogEntity.avatarUrl, viewBinding.avatarIv, this, R.drawable.default_avatar)
        val name = if ((partyDialogEntity.nickName ?: "").length > 3) {
            (partyDialogEntity.nickName?.substring(0, 3) ?: "") + "..."
        } else {
            partyDialogEntity.nickName ?: ""
        }

        val span = object : ClickableSpan() {
            override fun onClick(widget: View) {
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.parseColor("#3A639E")
            }
        }
        val title = name + partyDialogEntity.title
        val spanAll = SpannableString(title)
        spanAll.setSpan(span, 0, name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        viewBinding.contentTv.text = spanAll
        GlideHelper.loadRadiusImage(this, partyDialogEntity.imageUrl, viewBinding.contentIv, 0f)
        viewBinding.lookTv.text = partyDialogEntity.button
    }

    override fun initListeners() {
        viewBinding.apply {
            closeIv.setOnClickListener {
                finish()
            }
            lookTv.setOnClickListener {
                partyDialogEntity.link?.let {
                    finish()
                    DcRouter(it).start()
                }
            }
        }
    }

    override fun enablePartyVerify(): Boolean {
        return false
    }

    override fun moduleTag(): String {
        return ModuleConstant.DIALOG
    }
}