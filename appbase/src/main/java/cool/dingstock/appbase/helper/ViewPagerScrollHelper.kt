package cool.dingstock.appbase.helper

import android.util.SparseArray
import android.util.SparseBooleanArray
import net.lucode.hackware.magicindicator.ScrollState

class ViewPagerScrollHelper{


    private val mDeselectedItems = SparseBooleanArray()
    private val mLeavedPercents = SparseArray<Float>()

    private var mTotalCount = 0
    private var mCurrentIndex = 0
    private var mLastIndex = 0
    private var mLastPositionOffsetSum = 0f
    private var mScrollState = 0

    private var mSkimOver = false
    private var mViewPagerChangeListener: OnViewPagerChangeListener? = null

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val currentPositionOffsetSum = position + positionOffset
        var leftToRight = false
        if (mLastPositionOffsetSum <= currentPositionOffsetSum) {
            leftToRight = true
        }
        if (mScrollState != ScrollState.SCROLL_STATE_IDLE) {
            if (currentPositionOffsetSum == mLastPositionOffsetSum) {
                return
            }
            var nextPosition = position + 1
            var normalDispatch = true
            if (positionOffset == 0.0f) {
                if (leftToRight) {
                    nextPosition = position - 1
                    normalDispatch = false
                }
            }
            for (i in 0 until mTotalCount) {
                if (i == position || i == nextPosition) {
                    continue
                }
                val leavedPercent = mLeavedPercents[i, 0.0f]
                if (leavedPercent != 1.0f) {
                    dispatchOnLeave(i, 1.0f, leftToRight, true)
                }
            }
            if (normalDispatch) {
                if (leftToRight) {
                    dispatchOnLeave(position, positionOffset, true, false)
                    dispatchOnEnter(nextPosition, positionOffset, true, false)
                } else {
                    dispatchOnLeave(nextPosition, 1.0f - positionOffset, false, false)
                    dispatchOnEnter(position, 1.0f - positionOffset, false, false)
                }
            } else {
                dispatchOnLeave(nextPosition, 1.0f - positionOffset, true, false)
                dispatchOnEnter(position, 1.0f - positionOffset, true, false)
            }
        } else {
            for (i in 0 until mTotalCount) {
                if (i == mCurrentIndex) {
                    continue
                }
                val deselected = mDeselectedItems[i]
                if (!deselected) {
                    dispatchOnDeselected(i)
                }
                val leavedPercent = mLeavedPercents[i, 0.0f]
                if (leavedPercent != 1.0f) {
                    dispatchOnLeave(i, 1.0f, false, true)
                }
            }
            dispatchOnEnter(mCurrentIndex, 1.0f, false, true)
            dispatchOnSelected(mCurrentIndex)
        }
        mLastPositionOffsetSum = currentPositionOffsetSum
    }

    private fun dispatchOnEnter(index: Int, enterPercent: Float, leftToRight: Boolean, force: Boolean) {
        if (mSkimOver || index == mCurrentIndex || mScrollState == ScrollState.SCROLL_STATE_DRAGGING || force) {
            if (mViewPagerChangeListener != null) {
                mViewPagerChangeListener!!.onEnter(index, mTotalCount, enterPercent, leftToRight)
            }
            mLeavedPercents.put(index, 1.0f - enterPercent)
        }
    }

    private fun dispatchOnLeave(index: Int, leavePercent: Float, leftToRight: Boolean, force: Boolean) {
        if (mSkimOver || index == mLastIndex || mScrollState == ScrollState.SCROLL_STATE_DRAGGING || (index == mCurrentIndex - 1 || index == mCurrentIndex + 1) && mLeavedPercents[index, 0.0f] != 1.0f || force) {
            if (mViewPagerChangeListener != null) {
                mViewPagerChangeListener!!.onLeave(index, mTotalCount, leavePercent, leftToRight)
            }
            mLeavedPercents.put(index, leavePercent)
        }
    }

    private fun dispatchOnSelected(index: Int) {
        if (mViewPagerChangeListener != null) {
            mViewPagerChangeListener!!.onSelected(index, mTotalCount)
        }
        mDeselectedItems.put(index, false)
    }

    private fun dispatchOnDeselected(index: Int) {
        if (mViewPagerChangeListener != null) {
            mViewPagerChangeListener!!.onDeselected(index, mTotalCount)
        }
        mDeselectedItems.put(index, true)
    }

    fun onPageSelected(position: Int) {
        mLastIndex = mCurrentIndex
        mCurrentIndex = position
        dispatchOnSelected(mCurrentIndex)
        for (i in 0 until mTotalCount) {
            if (i == mCurrentIndex) {
                continue
            }
            val deselected = mDeselectedItems[i]
            if (!deselected) {
                dispatchOnDeselected(i)
            }
        }
    }

    fun onPageScrollStateChanged(state: Int) {
        mScrollState = state
    }

    fun setViewPagerChangeListener(viewPagerChangeListener: OnViewPagerChangeListener?) {
        mViewPagerChangeListener = viewPagerChangeListener
    }

    fun setSkimOver(skimOver: Boolean) {
        mSkimOver = skimOver
    }

    fun getTotalCount(): Int {
        return mTotalCount
    }

    fun setTotalCount(totalCount: Int) {
        mTotalCount = totalCount
        mDeselectedItems.clear()
        mLeavedPercents.clear()
    }

    fun getCurrentIndex(): Int {
        return mCurrentIndex
    }

    fun getScrollState(): Int {
        return mScrollState
    }

    interface OnViewPagerChangeListener {
        fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean)
        fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean)
        fun onSelected(index: Int, totalCount: Int)
        fun onDeselected(index: Int, totalCount: Int)
    }

}