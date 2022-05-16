package cool.dingstock.appbase.widget.vote

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import cool.dingstock.appbase.R
import cool.dingstock.appbase.entity.bean.circle.VoteOptionEntity
import cool.dingstock.lib_base.util.SizeUtils

class VoteView @JvmOverloads constructor(mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(mContext, attrs, defStyleAttr), View.OnClickListener {

    init {
        orientation = VERTICAL
    }

    private var animatorDuration: Long = mAnimationRate
    private var mVoteListener: VoteListener? = null
    private val currentNumbers: MutableList<Int> = ArrayList()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.VoteView)
        animatorDuration = typedArray.getFloat(R.styleable.VoteView_animate_duration, 0f).toLong()
        typedArray.recycle()
    }

    /**
     * 在回收的时候clear
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        voteObservers.clear()
    }

    /**
     * 设置数据
     *
     * @param isMine 是不是自己发起的
     */
    fun initVoteData(voteData: MutableList<VoteOptionEntity>, isMine: Boolean) {
        val showDetail = checkShowDetail(voteData) || isMine
        val totalVote = getTotalVote(voteData)
        removeAllViews()
        val layoutParams: LayoutParams = this.layoutParams as LayoutParams
        val verticalMargin = SizeUtils.dp2px(4f)
        layoutParams.setMargins(0, verticalMargin, 0, verticalMargin)
        voteData.forEachIndexed { index, it ->
            val voteSubView = VoteSubView(context)
            addView(voteSubView, layoutParams)
            voteSubView.initVoteData(it, totalVote, showDetail)
            voteSubView.tag = index
            voteSubView.setOnClickListener(this)
            register(voteSubView)
        }
        notifyTotalNumbers(getTotalVote(voteData))
    }

    /**
     * 是否显示投票详情数据
     *<p>
     * 需要用户自己参与投票才会显示详情
     * </p>
     */
    private fun checkShowDetail(voteData: MutableList<VoteOptionEntity>): Boolean {
        if (voteData.isNullOrEmpty()) {
            return false
        }
        val filterList = voteData.filter { it.isVote }
        return !filterList.isNullOrEmpty()
    }

    /**
     * 获取总投票数目
     */
    private fun getTotalVote(voteData: MutableList<VoteOptionEntity>): Int {
        var totalVote = 0
        if (voteData.isNullOrEmpty()) {
            return 0
        }
        voteData.map { totalVote += it.count }
        return totalVote
    }

    /**
     * 投票器动效速率设置
     *
     * @param speed 取值范围 100毫秒 - 5000毫秒
     */
    fun setAnimationRate(speed: Long) {
        if (speed in 101..5000) {
            mAnimationRate = speed
        }
    }

    /**
     * 恢复初始各条目投票数目设置
     */
    fun resetNumbers() {
        if (voteObservers.size === currentNumbers.size) {
            for (i in 0 until voteObservers.size) {
                val subView = voteObservers[i] as VoteSubView
                subView.setNumber(currentNumbers[i])
            }
        }
    }

    /**
     * 刷新每个子 view 的状态
     *
     * @param view   VoteSubView
     * @param status 投票状态 or 未投票状态
     */
    fun notifyUpdateChildren(view: View?, status: Boolean) {
        for (voteObserver in voteObservers) {
            voteObserver.update(view!!, status)
        }
    }

    /**
     * 投票器监听
     */
    fun setVoteListener(voteListener: VoteListener) {
        mVoteListener = voteListener
    }

    override fun onClick(view: View) {
        if (mVoteListener != null) {
            mVoteListener?.onItemClick(view, view.tag as Int, !view.isSelected)
        }
    }

    private fun notifyTotalNumbers(total: Int) {
        if (voteObservers.size == 0) {
            return
        }
        for (voteObserver in voteObservers) {
            voteObserver.setTotalNumber(total)
        }
    }

    private fun register(observer: VoteObserver) {
        if (voteObservers.size == 0) {
            voteObservers.add(observer)
        } else if (voteObservers.contains(observer)) {
            return
        }
    }

    fun updateVote(list: MutableList<VoteOptionEntity>,isMine: Boolean) {
        val showDetail = checkShowDetail(list) || isMine
        val totalNumber = getTotalVote(list)
        for (i in list.indices) {
            val voteSubView = this.getChildAt(i) as VoteSubView
            if (!showDetail) {
                voteSubView.clearState()
            } else {
                voteSubView.updateData(list[i], totalNumber, showDetail)
            }
        }
    }

    companion object {
        var mAnimationRate = 185L
        val voteObservers: MutableList<VoteObserver> = ArrayList()

    }
}