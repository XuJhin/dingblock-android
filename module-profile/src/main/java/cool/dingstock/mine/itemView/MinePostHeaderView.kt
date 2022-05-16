package cool.dingstock.mine.itemView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cool.dingstock.appbase.adapter.dc.DcBaseBinderAdapter
import cool.dingstock.appbase.constant.UTConstant
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicDetailUserBean
import cool.dingstock.appbase.entity.bean.mine.VipPrivilegeEntity
import cool.dingstock.appbase.entity.bean.score.MineScoreInfoEntity
import cool.dingstock.appbase.ext.azDp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.ext.load
import cool.dingstock.appbase.ext.visual
import cool.dingstock.appbase.router.DcUriRequest
import cool.dingstock.appbase.ut.UTHelper
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.imagepicker.views.AvatarView
import cool.dingstock.imagepre.ImagePreview
import cool.dingstock.mine.R
import cool.dingstock.uicommon.helper.VipBubHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import java.text.SimpleDateFormat
import java.util.*

class MinePostHeaderView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {
    constructor(context: Context, isMineHub: Boolean) : this(context) {
        this.isMineHub = isMineHub
        if (!isMineHub) {
            vipBubIv.visibility = View.GONE
            userDesc.visibility = View.GONE
            mineScoreLayer.visibility = View.GONE
            mineOtherUserDesView.visibility = View.VISIBLE

            val applyConstraintSet = ConstraintSet()
            applyConstraintSet.clone(mineFollowAndFansView)
            applyConstraintSet.connect(
                R.id.layout_think_good,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            applyConstraintSet.connect(
                R.id.layout_followed,
                ConstraintSet.START,
                R.id.layout_focus,
                ConstraintSet.END
            )
            applyConstraintSet.connect(
                R.id.layout_followed,
                ConstraintSet.END,
                R.id.layout_think_good,
                ConstraintSet.START
            )
            applyConstraintSet.connect(
                R.id.layout_focus,
                ConstraintSet.START,
                R.id.tv_other_user_desc,
                ConstraintSet.END
            )
            applyConstraintSet.applyTo(mineFollowAndFansView)

            layoutFollow.gravity = Gravity.END
            layoutFans.gravity = Gravity.END
            layoutClickUp.gravity = Gravity.END
        }
        initBub(isMineHub)
    }

    private var isMineHub = false
    private val userIvAvatar: AvatarView
    private val userNickname: TextView
    private val userDesc: TextView
    private val layoutFollow: LinearLayout
    private val layoutFans: LinearLayout
    private val layoutClickUp: LinearLayout
    private val useFollowCount: TextView
    private val userFansCount: TextView
    private val clickUpCount: TextView
    private val layoutVip: ConstraintLayout
    private val layoutBadge: FrameLayout
    private val tvBadge: TextView
    private val vipDate: TextView
    private val vipAction: TextView
    private val bgHeader: ImageView
    private val mineScoreLayer: View
    private val mineScoreTv: TextView
    private val mineScoreHintV: View
    private val mineFollowAndFansView: ConstraintLayout
    private val mineOtherUserDesView: TextView
    private val mineShowScoreIv: ImageView
    private val userScoreCount: TextView
    private val vipBubIv: ImageView
    private val vipHintRv: RecyclerView
    private val llVip: ConstraintLayout
    private val llMedal: LinearLayout
    private val ivMedal: ImageView
    private val tvMedal: TextView
    private val viewMedalHint: View
    private lateinit var vipBubHelper: VipBubHelper
    private val mAdapter = DcBaseBinderAdapter(arrayListOf())

    init {
        LayoutInflater.from(context).inflate(R.layout.mine_post_header, this, true)
        vipBubIv = findViewById(R.id.iv_vip_bub)
        userIvAvatar = findViewById(R.id.mine_head_user_info_iv)
        userNickname = findViewById(R.id.mine_header_user_info_nickname)
        userDesc = findViewById(R.id.tv_user_desc)
        layoutFollow = findViewById(R.id.layout_focus)
        layoutFans = findViewById(R.id.layout_followed)
        layoutClickUp = findViewById(R.id.layout_think_good)
        useFollowCount = findViewById(R.id.tv_fans_count)
        userFansCount = findViewById(R.id.tv_followed_count)
        clickUpCount = findViewById(R.id.tv_click_good_count)
        layoutVip = findViewById(R.id.mine_vip_layer)
        layoutBadge = findViewById(R.id.layout_badge)
        tvBadge = findViewById(R.id.tv_badge)
        findViewById<TextView>(R.id.mine_head_user_info_vip_txt).also { vipDate = it }
        vipAction = findViewById(R.id.mine_vip_info_action_txt)
        bgHeader = findViewById(R.id.mine_header_iv)
        mineScoreLayer = findViewById(R.id.score_layer)
        mineScoreTv = findViewById(R.id.score_tv)
        mineScoreHintV = findViewById(R.id.mine_score_hint_v)
        mineFollowAndFansView = findViewById(R.id.layout_fans_followed)
        mineOtherUserDesView = findViewById(R.id.tv_other_user_desc)
        mineShowScoreIv = findViewById(R.id.mine_score_show_more_iv)
        userScoreCount = findViewById(R.id.tv_score_count)
        vipHintRv = findViewById(R.id.vip_hint_rv)
        llVip = findViewById(R.id.ll_vip)
        llMedal = findViewById(R.id.layout_medal_container)
        ivMedal = findViewById(R.id.iv_medal)
        tvMedal = findViewById(R.id.tv_medal)
        viewMedalHint = findViewById(R.id.mine_medal_hint_v)
        layoutFollow.gravity = Gravity.START
        layoutFans.gravity = Gravity.START
        layoutClickUp.gravity = Gravity.START
    }

    fun initBub(isMineHub: Boolean) {
        if (!isMineHub) {
            return
        }
        vipBubHelper = VipBubHelper()
        if (vipBubHelper.checkEnableShowBub()) {
            vipBubIv.visibility = View.VISIBLE
        } else {
            vipBubIv.visibility = View.GONE
        }
    }

    private var listener: MineHeaderClickListener? = null
    fun initListener(param: MineHeaderClickListener) {
        listener = param
    }

    var item: CircleDynamicDetailUserBean? = null
        set(value) {
            field = value
            value?.let { setData(it) }
        }

    private fun setData(item: CircleDynamicDetailUserBean) {
        userIvAvatar.setAvatarUrl(item.avatarUrl)
        Glide.with(this)
            .load(item.bigAvatarUrl)
            .error(R.drawable.default_avatar)
            .placeholder(R.drawable.default_avatar)
            .into(bgHeader)
        userIvAvatar.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Mine.MyP_click_MyAvatar)
            ImagePreview.getInstance()
                .setContext(context)
                .setEnableDragClose(true)
                .setFolderName("DingChao")
                .setLoadStrategy(ImagePreview.LoadStrategy.NetworkAuto)
                .setShowCloseButton(true)
                .setUserId(item.objectId)
                .setErrorPlaceHolder(R.drawable.default_avatar)
                .setImage(item.avatarUrl)
                .setAvatarPendantName(item.avatarPendantName)
                .setPendantUrl(item.avatarPendantUrl)
                .startAvatar()
        }
        userNickname.text = item.nickName
        userNickname.setTextColor(Color.parseColor(if (item.isVip) "#FFE3C3" else "#FFFFFF"))
        if (isMineHub) {
            userDesc.text = item.desc
        } else {
            mineOtherUserDesView.text = item.desc
        }
        useFollowCount.text = "${item.followedCount}"
        userFansCount.text = "${item.fansCount}"
        clickUpCount.text = "${item.favorCount}"
        layoutFollow.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Mine.MyP_click_Attention)
            listener?.onClickFollow(item.objectId)
        }
        layoutFans.setOnClickListener {
            UTHelper.commonEvent(UTConstant.Mine.MyP_click_Followed)
            listener?.onClickFans(item.objectId)
        }
        layoutClickUp.setOnClickListener {
            listener?.onClickThinkUp(""""${item.nickName}"""", item.favorCount)
        }
        mineScoreLayer.setOnClickListener {
            if (vipBubHelper.checkEnableShowBub()) {
                vipBubIv.visibility = View.GONE
                vipBubHelper.showEndBubDraw()
            }
            listener?.clickReceivePoints()
        }

        val isSuitMedal = !item.achievement?.iconUrl.isNullOrEmpty()
        if (isSuitMedal) {
            ivMedal.load(item.achievement?.iconUrl)
            tvMedal.text = item.achievement?.name
        } else {
            ivMedal.load(R.drawable.mine_medal)
            tvMedal.text = "查看勋章"
        }

        llMedal.setOnShakeClickListener {
            viewMedalHint.hide(true)
            listener?.onClickMedal(item.objectId)
        }

        if (item.isVerified) {
            if (item.badges.isNotEmpty()) {
                tvBadge.text = item.badges[0].title
                layoutBadge.visual()
            } else {
                layoutBadge.hide()
            }
        } else {
            layoutBadge.hide()
        }
        userIvAvatar.setPendantUrl(item.avatarPendantUrl)
        userIvAvatar.setBorderColor(ContextCompat.getColor(context, R.color.color_line))
        userIvAvatar.setBorderWidth(
            if (item.isVip) {
                0
            } else {
                1.azDp.toInt()
            }
        )
        onVipChange()
    }

    fun onVipHintChange(list: ArrayList<VipPrivilegeEntity>) {
        vipHintRv.apply {
            adapter = mAdapter
            layoutManager =
                object : GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
        }

        val vipItemView = VipMineItemBinder()
        vipItemView.setVipClick { path ->
            if (path.isEmpty()) {
                goVipPage()
            } else {
                try {
                    if (path.contains("musicMember")) {
                        UTHelper.commonEvent(
                            UTConstant.Mine.EnterSource_Musicvip,
                            "type",
                            if (LoginUtils.getCurrentUser()?.isVip() == true) "会员" else "非会员",
                            "source",
                            "个人中心icon"
                        )
                    }
                    DcUriRequest(context, path).start()
                } catch (e: Exception) {
                    DcUriRequest(context, path).start()
                }
            }
        }
        mAdapter.addItemBinder(VipPrivilegeEntity::class.java, vipItemView)
        mAdapter.setList(list)

        if (list.isNotEmpty()) {
            if (list.size > 8) {
                //限定rv高度2行
                val lp: ViewGroup.LayoutParams = vipHintRv.layoutParams
                lp.height = UIUtil.dip2px(context, 145.0)
                vipHintRv.layoutParams = lp
            }
        }
    }

    fun onScoreChange(mineScoreInfoEntity: MineScoreInfoEntity) {
        mineScoreLayer.isSelected = mineScoreInfoEntity.unReceive
        mineScoreHintV.isSelected = mineScoreInfoEntity.unReceive
        mineScoreTv.isSelected = mineScoreInfoEntity.unReceive
        if (mineScoreInfoEntity.unReceive) {
            mineScoreTv.text = "积分待领取"
            userScoreCount.visibility = View.GONE
        } else {
            userScoreCount.visibility = View.VISIBLE
            userScoreCount.text = mineScoreInfoEntity.getNiceScore().toString()
            mineScoreTv.text = "积分"
        }
    }

    fun onMedalHintChange(isHaveNewMedal: Boolean) {
        viewMedalHint.hide(!isHaveNewMedal)
    }


    @SuppressLint("SetTextI18n")
    fun onVipChange() {
        val currentUser = LoginUtils.getCurrentUser()
        when {
            null == currentUser -> {
                llVip.visibility = View.GONE
                return
            }
            currentUser.id == item?.objectId -> {
                llVip.visibility = View.VISIBLE
                if (currentUser.vipValidity == null) {
                    vipAction.text = "立即开通"
                    vipDate.text = "开通会员享更多惊喜特权"
                } else {
                    if ((currentUser.vipValidity ?: 0) < System.currentTimeMillis()) {
                        vipAction.text = "立即开通"
                        vipDate.text = "开通会员享更多惊喜特权"
                    } else {
                        vipAction.text = "续费"
                        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                        val formatString = dateFormat.format(currentUser.vipValidity)
                        vipDate.text = "会员有效期至$formatString"
                    }
                }
            }
            else -> {
                llVip.visibility = View.GONE
            }
        }

        llVip.setOnShakeClickListener {
            goVipPage()
        }
    }

    private fun goVipPage() {
        UTHelper.commonEvent(UTConstant.Mine.MyP_click_VipEnt, "status", vipAction.text.toString())
        UTHelper.commonEvent(UTConstant.Mine.VipP_ent, "source", "我的页入口")
        if (LoginUtils.getCurrentUser() == null) {
            listener?.clickLogin()
        } else {
            listener?.clickVip()
        }
    }
}

interface MineHeaderClickListener {
    fun onClickFollow(objectId: String)
    fun onClickFans(objectId: String)
    fun onClickThinkUp(name: String, num: Int)
    fun clickLogin()
    fun clickVip()
    fun clickReceivePoints()
    fun onClickMedal(objectId: String)
}