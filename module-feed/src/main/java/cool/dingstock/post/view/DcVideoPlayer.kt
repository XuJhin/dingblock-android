package cool.dingstock.post.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.flexbox.FlexboxLayout
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotListener
import com.shuyu.gsyvideoplayer.listener.GSYVideoShotSaveListener
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.NetworkUtils
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import cool.dingstock.appbase.config.AppConfigManager
import cool.dingstock.appbase.constant.CircleConstant
import cool.dingstock.appbase.entity.bean.circle.CircleDynamicVideoDetailBean
import cool.dingstock.appbase.ext.dp
import cool.dingstock.appbase.ext.hide
import cool.dingstock.appbase.helper.MuteHelper
import cool.dingstock.appbase.helper.SettingHelper
import cool.dingstock.appbase.mvp.DCActivityManager
import cool.dingstock.appbase.net.api.circle.CircleHelper
import cool.dingstock.appbase.net.parse.ParseCallback
import cool.dingstock.appbase.util.LoginUtils
import cool.dingstock.appbase.util.cellularConnected
import cool.dingstock.appbase.util.setOnShakeClickListener
import cool.dingstock.appbase.util.wifiConnected
import cool.dingstock.lib_base.stroage.ConfigSPHelper
import cool.dingstock.lib_base.util.Logger
import cool.dingstock.lib_base.util.ToastUtil
import cool.dingstock.post.R
import cool.dingstock.post.helper.getViewVisiblePercent
import cool.dingstock.post.helper.preDynamicId
import cool.dingstock.post.helper.preVideoPosition
import cool.dingstock.post.helper.preVideoUrl
import io.reactivex.rxjava3.disposables.Disposable
import moe.codeest.enviews.ENDownloadView
import moe.codeest.enviews.ENPlayView
import java.io.File

open class DcVideoPlayer : GSYVideoPlayer, LifecycleEventObserver {
    //亮度dialog
    protected var mBrightnessDialog: Dialog? = null

    //音量dialog
    protected var mVolumeDialog: Dialog? = null

    //触摸进度dialog
    protected var mProgressDialog: Dialog? = null

    //触摸进度条的progress
    protected var mDialogProgressBar: ProgressBar? = null

    //音量进度条的progress
    protected var mDialogVolumeProgressBar: ProgressBar? = null

    //亮度文本
    protected var mBrightnessDialogTv: TextView? = null

    //触摸移动显示文本
    protected var mDialogSeekTime: TextView? = null

    //触摸移动显示全部时间
    protected var mDialogTotalTime: TextView? = null
    protected var mMuteIv: ImageView? = null
    protected var mActionLayer: FlexboxLayout? = null
    protected var mShare: ConstraintLayout? = null
    protected var mCollection: ConstraintLayout? = null
    protected var mComment: ConstraintLayout? = null
    protected var mRaise: ConstraintLayout? = null
    protected var mVideoDuration: TextView? = null

    //触摸移动方向icon
    protected var mDialogIcon: ImageView? = null
    protected var mBottomProgressDrawable: Drawable? = null
    protected var mBottomShowProgressDrawable: Drawable? = null
    protected var mBottomShowProgressThumbDrawable: Drawable? = null
    protected var mVolumeProgressDrawable: Drawable? = null
    protected var mDialogProgressBarDrawable: Drawable? = null
    protected var mDialogProgressHighLightColor = -11
    protected var mDialogProgressNormalColor = -11

    private var muteHelper: MuteHelper? = null
    private var actionListener: ActionListener? = null

    private var fullVideoPlayer: DcVideoPlayer? = null

    private var clickToPause = false

    private var lifecycle: Lifecycle? = null

    var dynamicId: String? = null
    var videoDuration: Int = 0
        set(value) {
            field = value
            mVideoDuration?.isVisible = field > 0
            if (field > 0) {
                mVideoDuration?.text = CommonUtil.stringForTime(field * 1000)
            }
        }

    var isCollected = false
        set(value) {
            field = value
            mCollection?.apply {
                children.forEach {
                    it.isSelected = value
                }
            }
            fullVideoPlayer?.isCollected = value
        }
    var favorCount = 0
        set(value) {
            field = value
            mRaise?.apply {
                (getChildAt(1) as? TextView)?.apply {
                    text = if (value <= 0) {
                        "点赞"
                    } else {
                        "$value"
                    }
                }
            }
            fullVideoPlayer?.favorCount = value
        }
    var isFavored = false
        set(value) {
            field = value
            mRaise?.apply {
                children.forEach {
                    it.isSelected = value
                }
            }
            fullVideoPlayer?.isFavored = value
        }
    var commentCount = 0
        set(value) {
            field = value
            mComment?.apply {
                (getChildAt(1) as? TextView)?.apply {
                    text = if (value <= 0) {
                        "评论"
                    } else {
                        "$value"
                    }
                }
                fullVideoPlayer?.commentCount = value
            }
        }

    var shareHide = false
        set(value) {
            field = value
            mShare?.hide(value)
        }

    var collectHide = false
        set(value) {
            field = value
            mCollection?.hide(value)
        }

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == View.VISIBLE) {
            updateMuteIcon()
            muteHelper?.registerMuteListener(object : MuteHelper.MuteListener {
                override fun onMuteChange(mute: Boolean) {
                    AppConfigManager.userSelfSetting = true
                    if (mute) {
                        AppConfigManager.userTurnOnVoice = false
                        GSYVideoManager.instance().isNeedMute = true
                        mMuteIv?.setImageResource(R.drawable.ic_icon_mute)
                    } else {
                        AppConfigManager.userTurnOnVoice = true
                        GSYVideoManager.instance().isNeedMute = false
                        mMuteIv?.setImageResource(R.drawable.ic_icon_unmute)
                    }
                }
            })
        } else {
            muteHelper?.unregisterMuteListener()
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                lifecycle = source.lifecycle
            }
            Lifecycle.Event.ON_PAUSE -> {
                when (source) {
                    is Fragment -> {
                        if (retryCount >= 0) {
                            release()
                        } else {
                            if (isCurrentMediaListener) {
                                onVideoPause()
                            }
                        }
                    }
                    is AppCompatActivity -> {
                        if (source.isFinishing) {
                            release()
                        } else {
                            if (retryCount >= 0) {
                                if (isCurrentMediaListener) {
                                    release()
                                }
                            } else {
                                if (isCurrentMediaListener) {
                                    onVideoPause()
                                }
                            }
                        }
                    }
                }
            }
            Lifecycle.Event.ON_RESUME -> {
                if (!clickToPause) {
                    if (isCurrentMediaListener) {
                        onVideoResume()
                    } else {
                        if (context.wifiConnected()) {
                            if (SettingHelper.getInstance().isWifiAutoPlay) {
                                startPlay()
                            }
                        } else if (context.cellularConnected()) {
                            if (SettingHelper.getInstance().isCellularAutoPlay) {
                                startPlay()
                            }
                        }
                    }
                }
            }
            Lifecycle.Event.ON_DESTROY -> {
                if (source is Fragment) {
                    release()
                }
            }
            else -> Unit
        }
    }

    private fun startPlay() {
        post {
            if (playTag != GSYVideoManager.instance().playTag &&
                getViewVisiblePercent(this) >= 0.6f
            ) {
                if (dynamicId?.endsWith(CircleConstant.Extra.AUTO_PLAY) == true) {
                    startButton?.performClick()
                }
            }
        }
    }

    override fun init(context: Context) {
        super.init(context)
        muteHelper = MuteHelper(context)
        mMuteIv = findViewById(R.id.mute)
        mActionLayer = findViewById(R.id.video_action_layer)
        mShare = findViewById(R.id.post_video_share)
        mCollection = findViewById(R.id.post_video_collection)
        mComment = findViewById(R.id.post_video_comment)
        mRaise = findViewById(R.id.post_video_raise)
        mVideoDuration = findViewById(R.id.video_duration)
        updateMuteIcon()
        mMuteIv?.setOnClickListener {
            startDismissControlViewTimer()
            AppConfigManager.userSelfSetting = true
            GSYVideoManager.instance().isNeedMute = !GSYVideoManager.instance().isNeedMute
            AppConfigManager.userTurnOnVoice = !GSYVideoManager.instance().isNeedMute
            updateMuteIcon()
        }
        //增加自定义ui
        if (mBottomProgressDrawable != null) {
            mBottomProgressBar.progressDrawable = mBottomProgressDrawable
        }
        if (mBottomShowProgressDrawable != null) {
            mProgressBar.progressDrawable = mBottomProgressDrawable
        }
        if (mBottomShowProgressThumbDrawable != null) {
            mProgressBar.thumb = mBottomShowProgressThumbDrawable
        }
    }

    fun setVideoCallBack() {
        setVideoAllCallBack(object : GSYSampleCallBack() {
            override fun onPrepared(url: String?, vararg objects: Any?) {
                if (AppConfigManager.userSelfSetting) {
                    Logger.d("用户控制声音 ${AppConfigManager.userTurnOnVoice}")
                    GSYVideoManager.instance().isNeedMute = !AppConfigManager.userTurnOnVoice
                    updateMuteIcon()
                } else {
                    GSYVideoManager.instance().isNeedMute =
                        !SettingHelper.getInstance().isDefaultOpenSound
                    //静音
                    updateMuteIcon()
                }
            }
        })
    }

    fun updateMuteIcon() {
        if (GSYVideoManager.instance().isNeedMute) {
            mMuteIv?.setImageResource(R.drawable.ic_icon_mute)
        } else {
            mMuteIv?.setImageResource(R.drawable.ic_icon_unmute)
        }
    }

    /**
     * 继承后重写可替换为你需要的布局
     *
     * @return
     */
    override fun getLayoutId(): Int {
        return R.layout.video_layout_dc
    }

    /**
     * 开始播放
     */
    override fun startPlayLogic() {
        if (mVideoAllCallBack != null) {
            Debuger.printfLog("onClickStartThumb")
            mVideoAllCallBack.onClickStartThumb(mOriginUrl, mTitle, this@DcVideoPlayer)
        }
        prepareVideo()
        startDismissControlViewTimer()
    }

    override fun onVideoPause() {
        super.onVideoPause()
        preVideoPosition = mCurrentPosition
        preVideoUrl = mOriginUrl
        preDynamicId = dynamicId
    }

    override fun onAutoCompletion() {
        super.onAutoCompletion()
        releasePreVideoInfo()
    }

    override fun release() {
        if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE) {
            gsyVideoManager?.player?.let {
                if (mOriginUrl != null && it.currentPosition > 0) {
                    preVideoPosition = it.currentPosition
                    preVideoUrl = mOriginUrl
                    preDynamicId = dynamicId
                }
            }
        }
        super.release()
    }

    override fun startAfterPrepared() {
        super.startAfterPrepared()
        if (dynamicId == preDynamicId && mOriginUrl == preVideoUrl) {
            gsyVideoManager.seekTo(preVideoPosition)
        } else {
            releasePreVideoInfo()
        }
    }

    private fun releasePreVideoInfo() {
        preVideoPosition = 0L
        preVideoUrl = ""
        preDynamicId = null
    }

    private var retryCount = -1

    override fun clickStartIcon() {
        if (mUrl.isNullOrEmpty() || mOriginUrl.isNullOrEmpty()) {
            setStateAndUi(CURRENT_STATE_PREPAREING)
            if (gsyVideoManager.listener() != null) {
                gsyVideoManager.listener().onCompletion()
            }
            if (mVideoAllCallBack != null) {
                Debuger.printfLog("onStartPrepared")
                mVideoAllCallBack.onStartPrepared(mOriginUrl, mTitle, this)
            }
            gsyVideoManager.apply {
                playPosition = mPlayPosition
                playTag = mPlayTag
                setListener(this@DcVideoPlayer)
            }
            getPlayUrl()
        } else {
            hasUrlClickStart()
        }
    }

    private var subscribe: Disposable? = null

    private fun getPlayUrl() {
        retryCount++
        subscribe = CircleHelper.getInstance()
            .videoInfo(dynamicId ?: "", object : ParseCallback<CircleDynamicVideoDetailBean> {
                override fun onSucceed(data: CircleDynamicVideoDetailBean) {
                    if (isCurrentMediaListener) {
                        mapHeadData = hashMapOf("allowCrossProtocolRedirects" to "true")
                        setUp(data.playUrl, true, mTitle)
                        retryCount = -1
                        hasUrlClickStart()
                    } else {
                        setStateAndUi(CURRENT_STATE_NORMAL)
                        gsyVideoManager.apply {
                            playPosition = -22
                            playTag = ""
                            setListener(null)
                        }
                    }
                }

                override fun onFailed(errorCode: String?, errorMsg: String?) {
                    if (retryCount < 3) {
                        if (isCurrentMediaListener) {
                            getPlayUrl()
                        }
                    } else {
                        retryCount = -1
                        if (isCurrentMediaListener) {
                            setStateAndUi(CURRENT_STATE_NORMAL)
                            gsyVideoManager.apply {
                                playPosition = -22
                                playTag = ""
                                setListener(null)
                            }
                        }
                    }
                }
            })
    }

    override fun onCompletion() {
        subscribe?.let {
            it.dispose()
            subscribe = null
        }
        super.onCompletion()
    }

    private fun hasUrlClickStart() {
        if (mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR) {
            if (!context.wifiConnected() && context.cellularConnected() &&
                !SettingHelper.getInstance().isCellularAutoPlay &&
                ConfigSPHelper.getInstance().getBoolean("videoNoCellularShowToast", true)
            ) {
                ToastUtil.getInstance()._short(context, "非WIFI播放，请注意流量消耗")
                ConfigSPHelper.getInstance().save("videoNoCellularShowToast", false)
            }
            /*if (isShowNetConfirm) {
                showWifiDialog()
                return
            }*/
            startButtonLogic()
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            clickToPause = true
            try {
                onVideoPause()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            setStateAndUi(CURRENT_STATE_PAUSE)
            if (mVideoAllCallBack != null && isCurrentMediaListener) {
                if (mIfCurrentIsFullscreen) {
                    Debuger.printfLog("onClickStopFullscreen")
                    mVideoAllCallBack.onClickStopFullscreen(mOriginUrl, mTitle, this)
                } else {
                    Debuger.printfLog("onClickStop")
                    mVideoAllCallBack.onClickStop(mOriginUrl, mTitle, this)
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mVideoAllCallBack != null && isCurrentMediaListener) {
                if (mIfCurrentIsFullscreen) {
                    Debuger.printfLog("onClickResumeFullscreen")
                    mVideoAllCallBack.onClickResumeFullscreen(mOriginUrl, mTitle, this)
                } else {
                    Debuger.printfLog("onClickResume")
                    mVideoAllCallBack.onClickResume(mOriginUrl, mTitle, this)
                }
            }
            if (!mHadPlay && !mStartAfterPrepared) {
                startAfterPrepared()
            }
            try {
                gsyVideoManager.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            setStateAndUi(CURRENT_STATE_PLAYING)
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            startButtonLogic()
        }
    }

    /**
     * 显示wifi确定框，如需要自定义继承重写即可
     */
    override fun showWifiDialog() {
        if (!NetworkUtils.isAvailable(mContext)) {
            //Toast.makeText(mContext, getResources().getString(R.string.no_net), Toast.LENGTH_LONG).show();
            startPlayLogic()
            return
        }
        val builder = AlertDialog.Builder(activityContext)
        builder.setMessage(resources.getString(R.string.tips_not_wifi))
        builder.setPositiveButton(resources.getString(R.string.tips_not_wifi_confirm)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            startPlayLogic()
        }
        builder.setNegativeButton(resources.getString(R.string.tips_not_wifi_cancel)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.create().show()
    }

    /**
     * 触摸显示滑动进度dialog，如需要自定义继承重写即可，记得重写dismissProgressDialog
     */
    override fun showProgressDialog(
        deltaX: Float,
        seekTime: String,
        seekTimePosition: Int,
        totalTime: String,
        totalTimeDuration: Int
    ) {
        if (mProgressDialog == null) {
            val localView = LayoutInflater.from(activityContext).inflate(
                progressDialogLayoutId, null
            )
            if (localView.findViewById<View>(progressDialogProgressId) is ProgressBar) {
                mDialogProgressBar = localView.findViewById(
                    progressDialogProgressId
                )
                if (mDialogProgressBarDrawable != null) {
                    mDialogProgressBar!!.progressDrawable = mDialogProgressBarDrawable
                }
            }
            if (localView.findViewById<View>(progressDialogCurrentDurationTextId) is TextView) {
                mDialogSeekTime = localView.findViewById(
                    progressDialogCurrentDurationTextId
                )
            }
            if (localView.findViewById<View>(progressDialogAllDurationTextId) is TextView) {
                mDialogTotalTime = localView.findViewById(progressDialogAllDurationTextId)
            }
            if (localView.findViewById<View>(progressDialogImageId) is ImageView) {
                mDialogIcon = localView.findViewById(progressDialogImageId)
            }
            mProgressDialog = Dialog(activityContext, R.style.video_style_dialog_progress).apply {
                setContentView(localView)
                window?.apply {
                    addFlags(Window.FEATURE_ACTION_BAR)
                    addFlags(32)
                    addFlags(16)
                    setLayout(width, height)
                }
            }
            if (mDialogProgressNormalColor != -11 && mDialogTotalTime != null) {
                mDialogTotalTime?.setTextColor(mDialogProgressNormalColor)
            }
            if (mDialogProgressHighLightColor != -11 && mDialogSeekTime != null) {
                mDialogSeekTime?.setTextColor(mDialogProgressHighLightColor)
            }
            val localLayoutParams = mProgressDialog!!.window!!.attributes
            localLayoutParams.gravity = Gravity.TOP
            localLayoutParams.width = width
            localLayoutParams.height = height
            val location = IntArray(2)
            getLocationOnScreen(location)
            localLayoutParams.x = location[0]
            localLayoutParams.y = location[1]
            mProgressDialog!!.window!!.attributes = localLayoutParams
        }
        if (!mProgressDialog!!.isShowing) {
            mProgressDialog!!.show()
        }
        if (mDialogSeekTime != null) {
            mDialogSeekTime!!.text = seekTime
        }
        if (mDialogTotalTime != null) {
            mDialogTotalTime!!.text = " / $totalTime"
        }
        if (totalTimeDuration > 0) if (mDialogProgressBar != null) {
            mDialogProgressBar!!.progress = seekTimePosition * 100 / totalTimeDuration
        }
        if (deltaX > 0) {
            if (mDialogIcon != null) {
                mDialogIcon!!.setBackgroundResource(R.drawable.video_forward_icon)
            }
        } else {
            if (mDialogIcon != null) {
                mDialogIcon!!.setBackgroundResource(R.drawable.video_backward_icon)
            }
        }
    }

    override fun dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
    }

    /**
     * 触摸音量dialog，如需要自定义继承重写即可，记得重写dismissVolumeDialog
     */
    override fun showVolumeDialog(deltaY: Float, volumePercent: Int) {
        if (mVolumeDialog == null) {
            val localView = LayoutInflater.from(activityContext).inflate(
                volumeLayoutId, null
            )
            if (localView.findViewById<View>(volumeProgressId) is ProgressBar) {
                mDialogVolumeProgressBar = localView.findViewById<View>(
                    volumeProgressId
                ) as ProgressBar
                if (mVolumeProgressDrawable != null && mDialogVolumeProgressBar != null) {
                    mDialogVolumeProgressBar!!.progressDrawable = mVolumeProgressDrawable
                }
            }
            mVolumeDialog = Dialog(activityContext, R.style.video_style_dialog_progress)
            mVolumeDialog!!.setContentView(localView)
            mVolumeDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            mVolumeDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            mVolumeDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            mVolumeDialog!!.window!!
                .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val localLayoutParams = mVolumeDialog!!.window!!
                .attributes
            localLayoutParams.gravity = Gravity.TOP or Gravity.START
            localLayoutParams.width = width
            localLayoutParams.height = height
            val location = IntArray(2)
            getLocationOnScreen(location)
            localLayoutParams.x = location[0]
            localLayoutParams.y = location[1]
            mVolumeDialog!!.window!!.attributes = localLayoutParams
        }
        if (!mVolumeDialog!!.isShowing) {
            mVolumeDialog!!.show()
        }
        if (mDialogVolumeProgressBar != null) {
            mDialogVolumeProgressBar!!.progress = volumePercent
        }
    }

    override fun dismissVolumeDialog() {
        if (mVolumeDialog != null) {
            mVolumeDialog!!.dismiss()
            mVolumeDialog = null
        }
    }

    /**
     * 触摸亮度dialog，如需要自定义继承重写即可，记得重写dismissBrightnessDialog
     */
    override fun showBrightnessDialog(percent: Float) {
        if (mBrightnessDialog == null) {
            val localView = LayoutInflater.from(activityContext).inflate(
                brightnessLayoutId, null
            )
            if (localView.findViewById<View>(brightnessTextId) is TextView) {
                mBrightnessDialogTv = localView.findViewById<View>(brightnessTextId) as TextView
            }
            mBrightnessDialog = Dialog(activityContext, R.style.video_style_dialog_progress)
            mBrightnessDialog!!.setContentView(localView)
            mBrightnessDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            mBrightnessDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            mBrightnessDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            mBrightnessDialog!!.window!!.decorView.systemUiVisibility =
                SYSTEM_UI_FLAG_HIDE_NAVIGATION
            mBrightnessDialog!!.window!!
                .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val localLayoutParams = mBrightnessDialog!!.window!!
                .attributes
            localLayoutParams.gravity = Gravity.TOP or Gravity.END
            localLayoutParams.width = width
            localLayoutParams.height = height
            val location = IntArray(2)
            getLocationOnScreen(location)
            localLayoutParams.x = location[0]
            localLayoutParams.y = location[1]
            mBrightnessDialog!!.window!!.attributes = localLayoutParams
        }
        if (!mBrightnessDialog!!.isShowing) {
            mBrightnessDialog!!.show()
        }
        if (mBrightnessDialogTv != null) mBrightnessDialogTv!!.text = "${(percent * 100).toInt()}%"
    }

    override fun dismissBrightnessDialog() {
        if (mBrightnessDialog != null) {
            mBrightnessDialog!!.dismiss()
            mBrightnessDialog = null
        }
    }

    override fun cloneParams(from: GSYBaseVideoPlayer, to: GSYBaseVideoPlayer) {
        super.cloneParams(from, to)
        val sf = from as DcVideoPlayer
        val st = to as DcVideoPlayer
        if (st.mProgressBar != null && sf.mProgressBar != null) {
            st.mProgressBar.progress = sf.mProgressBar.progress
            st.mProgressBar.secondaryProgress = sf.mProgressBar.secondaryProgress
        }
        if (st.mTotalTimeTextView != null && sf.mTotalTimeTextView != null) {
            st.mTotalTimeTextView.text = sf.mTotalTimeTextView.text
        }
        if (st.mCurrentTimeTextView != null && sf.mCurrentTimeTextView != null) {
            st.mCurrentTimeTextView.text = sf.mCurrentTimeTextView.text
        }
    }

    /**
     * 将自定义的效果也设置到全屏
     *
     * @param context
     * @param actionBar 是否有actionBar，有的话需要隐藏
     * @param statusBar 是否有状态bar，有的话需要隐藏
     * @return
     */
    override fun startWindowFullscreen(
        context: Context,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        val gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar)
        if (gsyBaseVideoPlayer != null) {
            val gsyVideoPlayer = gsyBaseVideoPlayer as DcVideoPlayer
            gsyVideoPlayer.setLockClickListener(mLockClickListener)
            gsyVideoPlayer.isNeedLockFull = isNeedLockFull
            initFullUI(gsyVideoPlayer)
            //比如你自定义了返回案件，但是因为返回按键底层已经设置了返回事件，所以你需要在这里重新增加的逻辑
        }
        return gsyBaseVideoPlayer
    }
    /********************************各类UI的状态显示 */
    /**
     * 点击触摸显示和隐藏逻辑
     */
    override fun onClickUiToggle(e: MotionEvent) {
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            setViewShowState(mLockScreen, VISIBLE)
            return
        }
        if (mIfCurrentIsFullscreen && mCurrentState == CURRENT_STATE_ERROR) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToPlayingClear()
                } else {
                    changeUiToPlayingShow()
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PREPAREING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToPrepareingClear()
                } else {
                    changeUiToPreparingShow()
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToPlayingClear()
                } else {
                    changeUiToPlayingShow()
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToPauseClear()
                } else {
                    changeUiToPauseShow()
                }
            }
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToCompleteClear()
                } else {
                    changeUiToCompleteShow()
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            if (mBottomContainer != null) {
                if (mBottomContainer.visibility == VISIBLE) {
                    changeUiToPlayingBufferingClear()
                } else {
                    changeUiToPlayingBufferingShow()
                }
            }
        }
    }

    override fun hideAllWidget() {
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mBottomProgressBar, VISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mVideoDuration, INVISIBLE)
    }

    override fun changeUiToNormal() {
        Debuger.printfLog("changeUiToNormal")
        setViewShowState(mTopContainer, VISIBLE)
        if (duration > 0) {
            setViewShowState(mVideoDuration, VISIBLE)
        }
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, VISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        updateStartImage()
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
    }

    override fun changeUiToPreparingShow() {
        Debuger.printfLog("changeUiToPreparingShow")
        setViewShowState(mTopContainer, VISIBLE)
        setViewShowState(mVideoDuration, INVISIBLE)
        setViewShowState(mBottomContainer, VISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mLoadingProgressBar, VISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(mLockScreen, GONE)
        if (mLoadingProgressBar is ENDownloadView) {
            val enDownloadView = mLoadingProgressBar as ENDownloadView
            if (enDownloadView.currentState == ENDownloadView.STATE_PRE) {
                (mLoadingProgressBar as ENDownloadView).start()
            }
        }
    }

    override fun changeUiToPlayingShow() {
        Debuger.printfLog("changeUiToPlayingShow")
        clickToPause = false
        setViewShowState(mTopContainer, VISIBLE)
        setViewShowState(mVideoDuration, INVISIBLE)
        setViewShowState(mBottomContainer, VISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
        updateStartImage()
    }

    override fun changeUiToPauseShow() {
        Debuger.printfLog("changeUiToPauseShow")
        setViewShowState(mTopContainer, VISIBLE)
        setViewShowState(mVideoDuration, INVISIBLE)
        setViewShowState(mBottomContainer, VISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
        updateStartImage()
        updatePauseCover()
    }

    override fun changeUiToPlayingBufferingShow() {
        Debuger.printfLog("changeUiToPlayingBufferingShow")
        setViewShowState(mTopContainer, VISIBLE)
        setViewShowState(mVideoDuration, INVISIBLE)
        setViewShowState(mBottomContainer, VISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mLoadingProgressBar, VISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(mLockScreen, GONE)
        if (mLoadingProgressBar is ENDownloadView) {
            val enDownloadView = mLoadingProgressBar as ENDownloadView
            if (enDownloadView.currentState == ENDownloadView.STATE_PRE) {
                (mLoadingProgressBar as ENDownloadView).start()
            }
        }
    }

    override fun changeUiToCompleteShow() {
        Debuger.printfLog("changeUiToCompleteShow")
        setViewShowState(mTopContainer, VISIBLE)
        setViewShowState(mVideoDuration, INVISIBLE)
        setViewShowState(mBottomContainer, VISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, VISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
        updateStartImage()
    }

    override fun changeUiToError() {
        Debuger.printfLog("changeUiToError")
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mVideoDuration, INVISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
        updateStartImage()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dismissVolumeDialog()
        dismissBrightnessDialog()
    }

    /**
     * 触摸进度dialog的layoutId
     * 继承后重写可返回自定义
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected val progressDialogLayoutId: Int
        get() = R.layout.video_progress_dialog

    /**
     * 触摸进度dialog的进度条id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected val progressDialogProgressId: Int
        get() = R.id.duration_progressbar

    /**
     * 触摸进度dialog的当前时间文本
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected val progressDialogCurrentDurationTextId: Int
        get() = R.id.tv_current

    /**
     * 触摸进度dialog全部时间文本
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected val progressDialogAllDurationTextId: Int
        get() = R.id.tv_duration

    /**
     * 触摸进度dialog的图片id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showProgressDialog方法
     */
    protected val progressDialogImageId: Int
        get() = R.id.duration_image_tip

    /**
     * 音量dialog的layoutId
     * 继承后重写可返回自定义
     * 有自定义的实现逻辑可重载showVolumeDialog方法
     */
    protected val volumeLayoutId: Int
        get() = R.layout.video_volume_dialog

    /**
     * 音量dialog的百分比进度条 id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showVolumeDialog方法
     */
    protected val volumeProgressId: Int
        get() = R.id.volume_progressbar

    /**
     * 亮度dialog的layoutId
     * 继承后重写可返回自定义
     * 有自定义的实现逻辑可重载showBrightnessDialog方法
     */
    protected val brightnessLayoutId: Int
        get() = R.layout.video_brightness

    /**
     * 亮度dialog的百分比text id
     * 继承后重写可返回自定义，如果没有可返回空
     * 有自定义的实现逻辑可重载showBrightnessDialog方法
     */
    protected val brightnessTextId: Int
        get() = R.id.app_video_brightness

    protected fun changeUiToPrepareingClear() {
        Debuger.printfLog("changeUiToPrepareingClear")
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mVideoDuration, INVISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(mLockScreen, GONE)
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
    }

    protected fun changeUiToPlayingClear() {
        Debuger.printfLog("changeUiToPlayingClear")
        changeUiToClear()
        setViewShowState(mBottomProgressBar, VISIBLE)
    }

    protected fun changeUiToPauseClear() {
        Debuger.printfLog("changeUiToPauseClear")
        changeUiToClear()
        setViewShowState(mBottomProgressBar, VISIBLE)
        updatePauseCover()
    }

    protected fun changeUiToPlayingBufferingClear() {
        Debuger.printfLog("changeUiToPlayingBufferingClear")
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mLoadingProgressBar, VISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, VISIBLE)
        setViewShowState(mLockScreen, GONE)
        if (mLoadingProgressBar is ENDownloadView) {
            val enDownloadView = mLoadingProgressBar as ENDownloadView
            if (enDownloadView.currentState == ENDownloadView.STATE_PRE) {
                (mLoadingProgressBar as ENDownloadView).start()
            }
        }
        updateStartImage()
    }

    protected fun changeUiToClear() {
        Debuger.printfLog("changeUiToClear")
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mVideoDuration, INVISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, INVISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, INVISIBLE)
        setViewShowState(mBottomProgressBar, INVISIBLE)
        setViewShowState(mLockScreen, GONE)
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
    }

    protected fun changeUiToCompleteClear() {
        Debuger.printfLog("changeUiToCompleteClear")
        setViewShowState(mTopContainer, INVISIBLE)
        setViewShowState(mVideoDuration, INVISIBLE)
        setViewShowState(mBottomContainer, INVISIBLE)
        setViewShowState(mStartButton, VISIBLE)
        setViewShowState(mLoadingProgressBar, INVISIBLE)
        setViewShowState(mThumbImageViewLayout, VISIBLE)
        setViewShowState(mBottomProgressBar, VISIBLE)
        setViewShowState(
            mLockScreen,
            if (mIfCurrentIsFullscreen && mNeedLockFull) VISIBLE else GONE
        )
        if (mLoadingProgressBar is ENDownloadView) {
            (mLoadingProgressBar as ENDownloadView).reset()
        }
        updateStartImage()
    }

    /**
     * 定义开始按键显示
     */
    protected fun updateStartImage() {
        if (mStartButton is ENPlayView) {
            val enPlayView = mStartButton as ENPlayView
            enPlayView.setDuration(500)
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                enPlayView.play()
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                enPlayView.pause()
            } else {
                enPlayView.pause()
            }
        } else if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                CURRENT_STATE_PLAYING -> {
                    imageView.setImageResource(R.drawable.ic_icon_pause)
                }
                CURRENT_STATE_ERROR -> {
                    imageView.setImageResource(R.drawable.video_click_error_selector)
                }
                else -> {
                    imageView.setImageResource(R.drawable.ic_icon_play)
                }
            }
        }
    }

    /**
     * 全屏的UI逻辑
     */
    private fun initFullUI(dcVideoPlayer: DcVideoPlayer) {
        var delay = 500L
        fullVideoPlayer = dcVideoPlayer.apply {
            postDelayed({ mOrientationUtils.releaseListener() }, 500)
            isCollected = this@DcVideoPlayer.isCollected
            isFavored = this@DcVideoPlayer.isFavored
            favorCount = this@DcVideoPlayer.favorCount
            commentCount = this@DcVideoPlayer.commentCount
            shareHide = this@DcVideoPlayer.shareHide
            collectHide = this@DcVideoPlayer.collectHide
            mActionLayer?.isVisible = true
            setActionListener(this@DcVideoPlayer.actionListener)
            mShare?.setOnShakeClickListener {
                startDismissControlViewTimer()
                if (dcVideoPlayer.mOrientationUtils.isLand != 0) {
                    setOrientation()
                    postDelayed({
                        actionListener?.clickShare(it)
                    }, 500)
                } else {
                    actionListener?.clickShare(it)
                }
            }
            mCollection?.setOnShakeClickListener {
                startDismissControlViewTimer()
                if (DCActivityManager.getInstance()
                        .isTopActivity("cool.dingstock.mobile.activity.index.HomeIndexActivity")
                    && !LoginUtils.isLoginAndRequestLogin(context)
                ) {
                    this@DcVideoPlayer.clearFullscreenLayout()
                }
                actionListener?.clickCollect(it)
            }
            mComment?.setOnShakeClickListener {
                startDismissControlViewTimer()
                this@DcVideoPlayer.clearFullscreenLayout()
                Log.i("videoPlayer", "initFullUI: $delay")
                it.postDelayed({
                    actionListener?.clickComment(it)
                }, delay)
            }
            mRaise?.setOnClickListener {
                startDismissControlViewTimer()
                actionListener?.clickRaise(it)
            }
            actionListener?.longClickRaise(mRaise, isFavored)
            mMuteIv?.apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    bottomMargin = 20.dp.toInt()
                }
            }
            titleTextView?.apply {
                text = mTitle
                setTextColor(Color.WHITE)
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
            }
            val orientation = findViewById<ImageView>(R.id.orientation)
            if (orientation != null) {
                orientation.visibility = VISIBLE
                orientation.setOnClickListener {
                    startDismissControlViewTimer()
                    setOrientation()
                    delay = if (dcVideoPlayer.mOrientationUtils.isLand == 0) {
                        500L
                    } else {
                        1000L
                    }
                }
            }
            if (mBottomProgressDrawable != null) {
                setBottomProgressBarDrawable(mBottomProgressDrawable)
            }
            if (mBottomShowProgressDrawable != null && mBottomShowProgressThumbDrawable != null) {
                setBottomShowProgressBarDrawable(
                    mBottomShowProgressDrawable,
                    mBottomShowProgressThumbDrawable
                )
            }
            if (mVolumeProgressDrawable != null) {
                setDialogVolumeProgressBar(mVolumeProgressDrawable)
            }
            if (mDialogProgressBarDrawable != null) {
                setDialogProgressBar(mDialogProgressBarDrawable)
            }
            if (mDialogProgressHighLightColor != -11 && mDialogProgressNormalColor != -11) {
                setDialogProgressColor(
                    mDialogProgressHighLightColor,
                    mDialogProgressNormalColor
                )
            }
            this@DcVideoPlayer.lifecycle?.addObserver(this)
        }
    }

    private fun DcVideoPlayer.setOrientation() {
        mOrientationUtils.resolveByClick()
        setOrientationUI(this)
    }

    override fun resolveNormalVideoShow(
        oldF: View?,
        vp: ViewGroup?,
        gsyVideoPlayer: GSYVideoPlayer?
    ) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)
        fullVideoPlayer = null
    }

    private fun setOrientationUI(dcVideoPlayer: DcVideoPlayer) {
        if (dcVideoPlayer.mOrientationUtils.isLand == 0) {
            dcVideoPlayer.mMuteIv?.apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    bottomMargin = 20.dp.toInt()
                }
            }
            dcVideoPlayer.mProgressBar?.apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToEnd = R.id.mute
                    startToStart = ConstraintLayout.LayoutParams.UNSET
                    topToTop = R.id.mute
                    bottomToTop = ConstraintLayout.LayoutParams.UNSET
                }
            }
            dcVideoPlayer.mActionLayer?.apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    bottomToTop = R.id.progress
                    topToTop = ConstraintLayout.LayoutParams.UNSET
                    bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                }
            }
            dcVideoPlayer.mShare?.apply {
                setPadding(0)
                (getChildAt(0) as? ImageView)?.apply {
                    setImageResource(R.drawable.post_share_light_gray)
                }
                (getChildAt(1) as? TextView)?.apply {
                    setTextColor(Color.parseColor("#B2B2B2"))
                }
            }
            dcVideoPlayer.mCollection?.apply {
                setPadding(0)
                (getChildAt(0) as? ImageView)?.apply {
                    setImageResource(R.drawable.post_light_collection_selector)
                }
                (getChildAt(1) as? TextView)?.apply {
                    setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.selector_light_post_collect_action
                        )
                    )
                }
            }
            dcVideoPlayer.mComment?.apply {
                setPadding(0)
                (getChildAt(0) as? ImageView)?.apply {
                    setImageResource(R.drawable.post_comment_light_gray)
                }
                (getChildAt(1) as? TextView)?.apply {
                    setTextColor(Color.parseColor("#B2B2B2"))
                }
            }
            dcVideoPlayer.mRaise?.apply {
                setPadding(0)
                (getChildAt(0) as? ImageView)?.apply {
                    setImageResource(R.drawable.post_light_raise_state)
                    isSelected = dcVideoPlayer.isFavored
                }
                (getChildAt(1) as? TextView)?.apply {
                    setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.selector_light_post_action
                        )
                    )
                }
            }
        } else {
            dcVideoPlayer.mMuteIv?.apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    bottomMargin = 5.dp.toInt()
                }
            }
            dcVideoPlayer.mProgressBar?.apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    startToEnd = ConstraintLayout.LayoutParams.UNSET
                    topToTop = ConstraintLayout.LayoutParams.UNSET
                    bottomToTop = R.id.mute
                }
            }
            dcVideoPlayer.mActionLayer?.apply {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    bottomToTop = ConstraintLayout.LayoutParams.UNSET
                    topToTop = R.id.mute
                    bottomToBottom = R.id.mute
                    startToStart = ConstraintLayout.LayoutParams.UNSET
                }
            }
            dcVideoPlayer.mShare?.apply {
                setPadding(30.dp.toInt(), 0, 0, 0)
                (getChildAt(0) as? ImageView)?.apply {
                    setImageResource(R.drawable.post_share_white)
                }
                (getChildAt(1) as? TextView)?.apply {
                    setTextColor(Color.WHITE)
                }
            }
            dcVideoPlayer.mCollection?.apply {
                setPadding(30.dp.toInt(), 0, 0, 0)
                (getChildAt(0) as? ImageView)?.apply {
                    setImageResource(R.drawable.post_white_collection_selector)
                }
                (getChildAt(1) as? TextView)?.apply {
                    setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.selector_white_post_collect_action
                        )
                    )
                }
            }
            dcVideoPlayer.mComment?.apply {
                setPadding(30.dp.toInt(), 0, 0, 0)
                (getChildAt(0) as? ImageView)?.apply {
                    setImageResource(R.drawable.post_comment_white)
                }
                (getChildAt(1) as? TextView)?.apply {
                    setTextColor(Color.WHITE)
                }
            }
            dcVideoPlayer.mRaise?.apply {
                setPadding(30.dp.toInt(), 0, 0, 0)
                (getChildAt(0) as? ImageView)?.apply {
                    setImageResource(R.drawable.post_white_raise_state)
                }
                (getChildAt(1) as? TextView)?.apply {
                    setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.selector_white_post_action
                        )
                    )
                }
            }
        }
    }

    /**
     * 底部进度条-弹出的
     */
    fun setBottomShowProgressBarDrawable(drawable: Drawable?, thumb: Drawable?) {
        mBottomShowProgressDrawable = drawable
        mBottomShowProgressThumbDrawable = thumb
        if (mProgressBar != null) {
            mProgressBar.progressDrawable = drawable
            mProgressBar.thumb = thumb
        }
    }

    /**
     * 底部进度条-非弹出
     */
    fun setBottomProgressBarDrawable(drawable: Drawable?) {
        mBottomProgressDrawable = drawable
        if (mBottomProgressBar != null) {
            mBottomProgressBar.progressDrawable = drawable
        }
    }

    /**
     * 声音进度条
     */
    fun setDialogVolumeProgressBar(drawable: Drawable?) {
        mVolumeProgressDrawable = drawable
    }

    /**
     * 中间进度条
     */
    fun setDialogProgressBar(drawable: Drawable?) {
        mDialogProgressBarDrawable = drawable
    }

    /**
     * 中间进度条字体颜色
     */
    fun setDialogProgressColor(highLightColor: Int, normalColor: Int) {
        mDialogProgressHighLightColor = highLightColor
        mDialogProgressNormalColor = normalColor
    }
    /**
     * 获取截图
     *
     * @param high 是否需要高清的
     */
    /************************************* 关于截图的  */
    /**
     * 获取截图
     */
    @JvmOverloads
    fun taskShotPic(gsyVideoShotListener: GSYVideoShotListener?, high: Boolean = false) {
        if (currentPlayer.renderProxy != null) {
            currentPlayer.renderProxy.taskShotPic(gsyVideoShotListener, high)
        }
    }

    /**
     * 保存截图
     */
    fun saveFrame(file: File?, gsyVideoShotSaveListener: GSYVideoShotSaveListener?) {
        saveFrame(file, false, gsyVideoShotSaveListener)
    }

    /**
     * 保存截图
     *
     * @param high 是否需要高清的
     */
    fun saveFrame(file: File?, high: Boolean, gsyVideoShotSaveListener: GSYVideoShotSaveListener?) {
        if (currentPlayer.renderProxy != null) {
            currentPlayer.renderProxy.saveFrame(file, high, gsyVideoShotSaveListener)
        }
    }

    /**
     * 重新开启进度查询以及控制view消失的定时任务
     * 用于解决GSYVideoHelper中通过removeview方式做全屏切换导致的定时任务停止的问题
     * GSYVideoControlView   onDetachedFromWindow（）
     */
    fun restartTimerTask() {
        startProgressTimer()
        startDismissControlViewTimer()
    }

    fun setActionListener(actionListener: ActionListener?) {
        this.actionListener = actionListener
    }

    interface ActionListener {
        fun clickShare(view: View)
        fun clickCollect(view: View)
        fun clickComment(view: View)
        fun clickRaise(view: View)
        fun longClickRaise(view: View?, favored: Boolean)
    }
}