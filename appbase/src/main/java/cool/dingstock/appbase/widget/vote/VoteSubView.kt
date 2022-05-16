package cool.dingstock.appbase.widget.vote

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.RelativeLayout.ALIGN_PARENT_LEFT
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import cool.dingstock.appbase.R
import cool.dingstock.appbase.entity.bean.circle.VoteOptionEntity
import cool.dingstock.appbase.ext.dp
import kotlin.math.roundToInt

class VoteSubView @JvmOverloads constructor(mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(mContext, attrs, defStyleAttr), VoteObserver {

    private var selectedColor = ContextCompat.getColor(context, R.color.post_vote_title_txt_color_sel)
    private val unSelectedColor = ContextCompat.getColor(context, R.color.color_text_deep_gray)
    private var contentMarginStart = 10.dp
    private var progressBar: ProgressBar? = null
    private var contentView: TextView? = null
    private var numberView: TextView? = null
    private var percentView: TextView? = null
    private var mTotalNumber = 1
    private var mCurrentNumber = 1
    private var cacheData: VoteOptionEntity? = null
    private var mCurrentPercent = 0f
    private var layoutParent: RelativeLayout? = null
    private var ivTagSelected: ImageView? = null
    private var contentInitAtStart = true

    /**
     * 是否显示投票详情
     */
    private var currentShowState: Boolean = false

    init {
        inflate(mContext, R.layout.vote_sub_view, this)
        initView()
    }

    private fun initView() {
        layoutParent = findViewById(R.id.layout_parent)
        progressBar = findViewById(R.id.progress_view)
        contentView = findViewById(R.id.name_text_view)
        numberView = findViewById(R.id.number_text_view)
        percentView = findViewById(R.id.percent_vote_view)
        ivTagSelected = findViewById(R.id.iv_selected_flag)
    }

    fun setNumber(number: Int) {
        mCurrentNumber = number
        numberView?.text = "${number}票"
    }

    fun setContent(content: String?) {
        contentView?.text = content
    }


    override fun update(view: View, status: Boolean) {
        changeChildrenViewStatus(view.tag as Int == getCurrentIndex())
        if (view.tag as Int == getCurrentIndex()) {
            Log.e("update", "当前被点选的是:" + getCurrentIndex())
            if (status) {
                mCurrentNumber += 1
                mTotalNumber += 1
                numberView?.text = "$mCurrentNumber"
            }
        }
        isSelected = status
    }

    override fun setTotalNumber(totalNumber: Int) {
        mTotalNumber = totalNumber
    }

    /**
     * 进度条动画
     */
    private fun progressBarAnimation(progressBar: ProgressBar,
                                     @FloatRange(from = 0.0, to = 1.0) start: Float,
                                     @FloatRange(from = 0.0, to = 1.0) end: Float) {
        try {
            val animator: ValueAnimator = ValueAnimator.ofInt(0, 100).setDuration(VoteView.mAnimationRate)
            val totalChange = end - start
            animator.addUpdateListener { valueAnimator ->
                val percent = valueAnimator.animatedValue as Int
                val value = start + totalChange.times(percent / 100.0)
                progressBar.progress = 100.times(value).toInt()
            }
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    mCurrentPercent = end
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            animator.start()
        } catch (e: Throwable) {
            Log.e("vote", e.message?:"")
        }
    }

    private fun getCurrentIndex(): Int {
        return tag as Int
    }

    private fun changeChildrenViewStatus(status: Boolean) {
        //选中文字颜色
        contentView?.setTextColor(if (status) selectedColor else unSelectedColor)
        //数字颜色
        numberView?.setTextColor(ContextCompat.getColor(context, R.color.post_vote_count_txt_color))
        //带勾选框
        if (status) {
            ivTagSelected?.visibility = View.VISIBLE
        } else {
            ivTagSelected?.visibility = View.GONE
        }
        //进度条颜色设置
        progressBar?.progressDrawable = ContextCompat.getDrawable(context,
                if (status) R.drawable.select_progress_view_bg
                else R.drawable.unselect_progress_view_bg)
        setBackgroundResource(if (status) R.drawable.select_bg else R.drawable.unselect_bg)
    }


    /**
     * 计算百分比
     */
    private fun calculatePercent(count: Int): Float {
        if (mTotalNumber == 0) {
            return 0f
        }
        return count / mTotalNumber.toFloat()
    }


    fun setProgressText(progress: Int) {
        percentView?.text = "${progress}%"
    }

    private fun setContentVisible(showOtherInfo: Boolean) {
        if (showOtherInfo) {
            numberView?.visibility = View.VISIBLE
            contentView?.setTextColor(selectedColor)
            percentView?.visibility = View.VISIBLE
            contentView?.post {
                val halfContent = contentView!!.width
                contentView?.x = contentMarginStart
                ivTagSelected?.x = 16.dp + halfContent
            }
        } else {
            progressBar?.progress = 0
            numberView?.visibility = View.GONE
            percentView?.visibility = View.GONE
            contentView?.setTextColor(unSelectedColor)
            ivTagSelected?.visibility = View.INVISIBLE
        }
    }

    fun clearState() {
        currentShowState = false
        voteDetailHideAnimator()
        translateProgress(0f)
        progressBar?.progressDrawable = ContextCompat.getDrawable(context, R.drawable.unselect_progress_view_bg)
        this.setBackgroundResource(R.drawable.unselect_bg)
        resetContent()
    }

    private fun voteDetailHideAnimator() {
        numberView?.animate()?.alpha(0f)?.setDuration(VoteView.mAnimationRate)?.start()
        percentView?.animate()?.alpha(0f)?.setDuration(VoteView.mAnimationRate)?.start()
        ivTagSelected?.animate()?.alpha(0f)?.setDuration(VoteView.mAnimationRate)?.start()
    }

    private fun voteDetailShowAnimator() {
        numberView?.animate()?.alpha(1f)?.setDuration(VoteView.mAnimationRate)?.start()
        percentView?.animate()?.alpha(1f)?.setDuration(VoteView.mAnimationRate)?.start()
        ivTagSelected?.animate()?.alpha(1f)?.setDuration(VoteView.mAnimationRate)?.start()
    }


    /**
     * 更新投票选项的数据
     * 主要是选中状态的颜色切换以及内容显示
     *
     */
    fun updateData(voteOptionEntity: VoteOptionEntity, totalNumber: Int, showDetail: Boolean) {
        mTotalNumber = totalNumber
        updateNumber(voteOptionEntity)
        voteSelected(voteOptionEntity.isVote)
        val newPercent = calculatePercent(voteOptionEntity.count)
        setProgressText((newPercent * 100).toInt())
        if (this.currentShowState) {
            if (showDetail) {
                translateProgress(newPercent)
                voteDetailShowAnimator()
            }
        } else {
            if (showDetail) {
                voteDetailShowAnimator()
                translateContentToStart()
                translateProgress(newPercent)
            }
        }
        this.currentShowState = showDetail
        setContentVisible(this.currentShowState)
    }

    private fun updateNumber(voteOptionEntity: VoteOptionEntity) {
        setNumber(voteOptionEntity.count)
    }

    private fun voteSelected(vote: Boolean) {
        changeChildrenViewStatus(vote)
    }


    private fun translateContentToStart() {
        if (contentInitAtStart) {
            contentView?.animate()?.translationX(0f)?.setDuration(VoteView.mAnimationRate)?.start()
            ivTagSelected?.animate()?.translationX(0f)?.setDuration(VoteView.mAnimationRate)?.start()
        } else {
            post {
                val halfContent = contentView!!.width / 2f
                val parentHalfWidth = (layoutParent?.width!! / 2f)
                val halfText = (parentHalfWidth - halfContent - contentMarginStart)
                contentView?.animate()?.translationX(-halfText)?.setDuration(VoteView.mAnimationRate)?.start()
                ivTagSelected?.animate()?.translationX(-halfText)?.setDuration(VoteView.mAnimationRate)?.start()
            }
        }
    }

    private fun resetContent() {
        if (contentInitAtStart) {
            post {
                val halfContent = contentView!!.width / 2f
                val parentHalfWidth = (layoutParent?.width!! / 2f)
                val halfText = (parentHalfWidth - halfContent - contentMarginStart)
                contentView?.animate()?.translationX(halfText)?.setDuration(VoteView.mAnimationRate)?.start()
                ivTagSelected?.animate()?.translationX(halfText)?.setDuration(VoteView.mAnimationRate)?.start()
            }
        } else {
            contentView?.animate()?.translationX(0f)?.setDuration(VoteView.mAnimationRate)?.start()
            ivTagSelected?.animate()?.translationX(0f)?.setDuration(VoteView.mAnimationRate)?.start()
        }

    }


    //调整投票数目
    private fun translateProgress(newPercent: Float) {
        progressBar?.let {
            post { progressBarAnimation(it, mCurrentPercent, newPercent) }
        }
    }

    /**
     * @param data          当前选项数据
     * @param totalVote     总共票数
     * @param showDetail    是否显示投票详情
     */
    fun initVoteData(data: VoteOptionEntity, totalVote: Int, showDetail: Boolean) {
        cacheData = data
        mTotalNumber = totalVote
        this.currentShowState = showDetail
        initVoteSubText(data)
        showVoteDetail(showDetail)
        initVoteSubState(data.isVote)
        contentInitAtStart = this.currentShowState
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initVoteSubState(hasSelected: Boolean) {
        //进度条颜色设置
        if (currentShowState) {
            progressBar?.progressDrawable = resources.getDrawable(
                    if (hasSelected) R.drawable.select_progress_view_bg
                    else R.drawable.unselect_progress_view_bg)
        } else {
            progressBar?.progressDrawable = resources.getDrawable(R.drawable.unselect_progress_view_bg)
        }
        if (hasSelected) {
            ivTagSelected?.visibility = View.VISIBLE
            contentView?.setTextColor(selectedColor)
        } else {
            ivTagSelected?.visibility = View.INVISIBLE
            contentView?.setTextColor(unSelectedColor)
        }
    }

    private fun initVoteSubText(data: VoteOptionEntity) {
        mCurrentNumber = data.count
        numberView?.text = "${mCurrentNumber}票"
        contentView?.text = data.title
        this.mCurrentPercent = calculatePercent(cacheData?.count ?: 0)
        val progress: Int = (mCurrentPercent * 100).roundToInt()
        if (currentShowState) {
            progressBar?.progress = progress
        } else {
            progressBar?.progress = 0
        }
        percentView?.text = "${progress}%"
    }


    private fun showVoteDetail(showDetail: Boolean) {
        if (showDetail) {
            numberView?.visibility = View.VISIBLE
            percentView?.visibility = View.VISIBLE
            //设置文字显示在左边
            val layoutParams = contentView?.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(ALIGN_PARENT_LEFT)
            layoutParams.marginStart = contentMarginStart.toInt()
            contentView?.layoutParams = layoutParams
        } else {
            numberView?.visibility = View.INVISIBLE
            percentView?.visibility = View.INVISIBLE
            //设置文字显示在中间
            val layoutParams = contentView?.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            layoutParams.marginStart = contentMarginStart.toInt()
            contentView?.layoutParams = layoutParams
        }

    }

}