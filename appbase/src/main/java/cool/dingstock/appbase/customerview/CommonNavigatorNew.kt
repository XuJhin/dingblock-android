package cool.dingstock.appbase.customerview

/**
 * @author WhenYoung
 *  CreateAt Time 2020/11/23  10:36
 */
import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import net.lucode.hackware.magicindicator.NavigatorHelper
import net.lucode.hackware.magicindicator.NavigatorHelper.OnNavigatorScrollListener
import net.lucode.hackware.magicindicator.R
import net.lucode.hackware.magicindicator.ScrollState
import net.lucode.hackware.magicindicator.abs.IPagerNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData
import java.util.*


/**
 * 通用的ViewPager指示器，包含PagerTitle和PagerIndicator
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
class CommonNavigatorNew(context: Context) : FrameLayout(context), IPagerNavigator, OnNavigatorScrollListener {
    private var mScrollView: HorizontalScrollView? = null
    var titleContainer: LinearLayout? = null
        private set
    private var mIndicatorContainer: LinearLayout? = null
    var pagerIndicator: IPagerIndicator? = null
        private set
    var adapter: CommonNavigatorAdapter? = null
        private set
    private val mNavigatorHelper: NavigatorHelper = NavigatorHelper()
    /**
     * 提供给外部的参数配置
     */
    /** */
    var isAdjustMode // 自适应模式，适用于数目固定的、少量的title
            = false
    var isEnablePivotScroll // 启动中心点滚动
            = false
    var scrollPivotX = 0.5f // 滚动中心点 0.0f - 1.0f
    var isSmoothScroll = true // 是否平滑滚动，适用于 !mAdjustMode && !mFollowTouch
    var isFollowTouch = true // 是否手指跟随滚动
    var rightPadding = 0
    var leftPadding = 0
    var isIndicatorOnTop // 指示器是否在title上层，默认为下层
            = false
    private var mSkimOver // 跨多页切换时，中间页是否显示 "掠过" 效果
            = false
    var isReselectWhenLayout = true // PositionData准备好时，是否重新选中当前页，为true可保证在极端情况下指示器状态正确

    /** */ // 保存每个title的位置信息，为扩展indicator提供保障
    private val mPositionDataList: MutableList<PositionData> = ArrayList()
    private val mObserver: DataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            mNavigatorHelper.totalCount = adapter!!.count // 如果使用helper，应始终保证helper中的totalCount为最新
            init()
        }

        override fun onInvalidated() {
            // 没什么用，暂不做处理
        }
    }

    override fun notifyDataSetChanged() {
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    fun setAdapter(adapter: CommonNavigatorAdapter) {
        if (this.adapter === adapter) {
            return
        }
        if (this.adapter != null) {
            adapter.unregisterDataSetObserver(mObserver)
        }
        this.adapter = adapter
        if (this.adapter != null) {
            adapter.registerDataSetObserver(mObserver)
            mNavigatorHelper.totalCount = adapter.count
            if (titleContainer != null) {  // adapter改变时，应该重新init，但是第一次设置adapter不用，onAttachToMagicIndicator中有init
                adapter.notifyDataSetChanged()
            }
        } else {
            mNavigatorHelper.totalCount = 0
            init()
        }
    }

    private fun init() {
        removeAllViews()
        val root: View
        root = if (isAdjustMode) {
            LayoutInflater.from(context).inflate(R.layout.pager_navigator_layout_no_scroll, this)
        } else {
            LayoutInflater.from(context).inflate(R.layout.pager_navigator_layout, this)
        }
        mScrollView =
            root.findViewById<View>(R.id.scroll_view) as HorizontalScrollView // mAdjustMode为true时，mScrollView为null
        titleContainer = root.findViewById<View>(R.id.title_container) as LinearLayout
        titleContainer!!.setPadding(leftPadding, 0, rightPadding, 0)
        mIndicatorContainer = root.findViewById<View>(R.id.indicator_container) as LinearLayout
        if (isIndicatorOnTop) {
            mIndicatorContainer!!.parent.bringChildToFront(mIndicatorContainer)
        }
        initTitlesAndIndicator()
    }

    /**
     * 初始化title和indicator
     */
    private fun initTitlesAndIndicator() {
        var i = 0
        val j = mNavigatorHelper.totalCount
        while (i < j) {
            val v = adapter!!.getTitleView(context, i)
            if (v is View) {
                val view = v as View
                var lp: LinearLayout.LayoutParams
                if (isAdjustMode) {
                    lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
                    lp.weight = adapter!!.getTitleWeight(context, i)
                } else {
                    lp = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
                }
                titleContainer!!.addView(view, lp)
            }
            i++
        }
        if (adapter != null) {
            pagerIndicator = adapter!!.getIndicator(context)
            if (pagerIndicator is View) {
                val lp = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                mIndicatorContainer!!.addView(pagerIndicator as View?, lp)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (adapter != null) {
            preparePositionData()
            if (pagerIndicator != null) {
                pagerIndicator!!.onPositionDataProvide(mPositionDataList)
            }
            if (isReselectWhenLayout && mNavigatorHelper.scrollState == ScrollState.SCROLL_STATE_IDLE) {
                onPageSelected(mNavigatorHelper.currentIndex)
                onPageScrolled(mNavigatorHelper.currentIndex, 0.0f, 0)
            }
        }
        super.onLayout(changed, left, top, right, bottom)
    }

    /**
     * 获取title的位置信息，为打造不同的指示器、各种效果提供可能
     */
    private fun preparePositionData() {
        mPositionDataList.clear()
        var i = 0
        val j = mNavigatorHelper.totalCount
        while (i < j) {
            val data = PositionData()
            val v = titleContainer!!.getChildAt(i)
            if (v != null) {
                data.mLeft = v.left
                data.mTop = v.top
                data.mRight = v.right
                data.mBottom = v.bottom
                if (v is IMeasurablePagerTitleView) {
                    val view = v as IMeasurablePagerTitleView
                    data.mContentLeft = view.contentLeft
                    data.mContentTop = view.contentTop
                    data.mContentRight = view.contentRight
                    data.mContentBottom = view.contentBottom
                } else {
                    data.mContentLeft = data.mLeft
                    data.mContentTop = data.mTop
                    data.mContentRight = data.mRight
                    data.mContentBottom = data.mBottom
                }
            }
            mPositionDataList.add(data)
            i++
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (adapter != null) {
            mNavigatorHelper.onPageScrolled(position, positionOffset, positionOffsetPixels)
            if (pagerIndicator != null) {
                pagerIndicator!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            // 手指跟随滚动
            if (mScrollView != null && mPositionDataList.size > 0 && position >= 0 && position < mPositionDataList.size) {
                if (isFollowTouch) {
                    val currentPosition = Math.min(mPositionDataList.size - 1, position)
                    val nextPosition = Math.min(mPositionDataList.size - 1, position + 1)
                    val current = mPositionDataList[currentPosition]
                    val next = mPositionDataList[nextPosition]
                    val scrollTo = current.horizontalCenter() - mScrollView!!.width * scrollPivotX
                    val nextScrollTo = next.horizontalCenter() - mScrollView!!.width * scrollPivotX
                    mScrollView!!.scrollTo((scrollTo + (nextScrollTo - scrollTo) * positionOffset).toInt(), 0)
                } else if (!isEnablePivotScroll) {
                    // TODO 实现待选中项完全显示出来
                }
            }
        }
    }

    override fun onPageSelected(position: Int) {
        if (adapter != null) {
            mNavigatorHelper.onPageSelected(position)
            if (pagerIndicator != null) {
                pagerIndicator!!.onPageSelected(position)
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (adapter != null) {
            mNavigatorHelper.onPageScrollStateChanged(state)
            if (pagerIndicator != null) {
                pagerIndicator!!.onPageScrollStateChanged(state)
            }
        }
    }

    override fun onAttachToMagicIndicator() {
        init() // 将初始化延迟到这里
    }

    override fun onDetachFromMagicIndicator() {}

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        if (titleContainer == null) {
            return
        }
        val v = titleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onEnter(index, totalCount, enterPercent, leftToRight)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        if (titleContainer == null) {
            return
        }
        val v = titleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onLeave(index, totalCount, leavePercent, leftToRight)
        }
    }

    var isSkimOver: Boolean
        get() = mSkimOver
        set(skimOver) {
            mSkimOver = skimOver
            mNavigatorHelper.setSkimOver(skimOver)
        }

    override fun onSelected(index: Int, totalCount: Int) {
        if (titleContainer == null) {
            return
        }
        val v = titleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onSelected(index, totalCount)
        }
        if (!isAdjustMode && !isFollowTouch && mScrollView != null && mPositionDataList.size > 0) {
            val currentIndex = Math.min(mPositionDataList.size - 1, index)
            val current = mPositionDataList[currentIndex]
            if (isEnablePivotScroll) {
                val scrollTo = current.horizontalCenter() - mScrollView!!.width * scrollPivotX
                if (isSmoothScroll) {
                    mScrollView!!.smoothScrollTo(scrollTo.toInt(), 0)
                } else {
                    mScrollView!!.scrollTo(scrollTo.toInt(), 0)
                }
            } else {
                // 如果当前项被部分遮挡，则滚动显示完全
                if (mScrollView!!.scrollX > current.mLeft) {
                    if (isSmoothScroll) {
                        mScrollView!!.smoothScrollTo(current.mLeft, 0)
                    } else {
                        mScrollView!!.scrollTo(current.mLeft, 0)
                    }
                } else if (mScrollView!!.scrollX + width < current.mRight) {
                    if (isSmoothScroll) {
                        mScrollView!!.smoothScrollTo(current.mRight - width, 0)
                    } else {
                        mScrollView!!.scrollTo(current.mRight - width, 0)
                    }
                }
            }
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (titleContainer == null) {
            return
        }
        val v = titleContainer!!.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onDeselected(index, totalCount)
        }
    }

    fun getPagerTitleView(index: Int): IPagerTitleView? {
        return if (titleContainer == null) {
            null
        } else titleContainer!!.getChildAt(index) as IPagerTitleView
    }

    init {
        mNavigatorHelper.setNavigatorScrollListener(this)
    }
}
