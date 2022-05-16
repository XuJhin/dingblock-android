package cool.dingstock.appbase.nestview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.ViewCompat

class NestedScrollingParent2LayoutImpl1 @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : NestedScrollingParent2Layout(context, attrs, defStyleAttr), NestedScrollingParent2 {
    private var mTopView: View? = null
    private var mTabLayout: View? = null
    private var mViewPager: View? = null
    private var mTopViewHeight = 0
    fun getStickyOffsetListener(): StickyOffsetListener? {
        return stickyOffsetListener
    }

    fun setStickyOffsetListener(stickyOffsetListener: StickyOffsetListener?) {
        this.stickyOffsetListener = stickyOffsetListener
    }

    private var stickyOffsetListener: StickyOffsetListener? = null

    interface StickyOffsetListener {
        fun stickAtTop(b: Boolean)
        fun scrollY(distance: Int)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    /**
     * 在嵌套滑动的子View未滑动之前，判断父view是否优先与子view处理(也就是父view可以先消耗，然后给子view消耗）
     *
     * @param target   具体嵌套滑动的那个子类
     * @param dx       水平方向嵌套滑动的子View想要变化的距离
     * @param dy       垂直方向嵌套滑动的子View想要变化的距离 dy<0向下滑动 dy>0 向上滑动
     * @param consumed 这个参数要我们在实现这个函数的时候指定，回头告诉子View当前父View消耗的距离
     * consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离 好让子view做出相应的调整
     * @param type     滑动类型，ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        //这里不管手势滚动还是fling都处理
        val hideTop = dy > 0 && scrollY < mTopViewHeight
        val showTop = dy < 0 && scrollY >= 0 && !target.canScrollVertically(-1)
        Log.e("scrollY", "scrollY +${scrollY}")
        stickyOffsetListener?.scrollY(scrollY)
        if (scrollY == 0) {
            stickyOffsetListener?.stickAtTop(true)
        } else {
            stickyOffsetListener?.stickAtTop(false)
        }
        if (hideTop || showTop) {
            scrollBy(0, dy)
            consumed[1] = dy
        }

    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        //当子控件处理完后，交给父控件进行处理。
        if (dyUnconsumed < 0) {
            //表示已经向下滑动到头
            scrollBy(0, dyUnconsumed)
        }
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //ViewPager修改后的高度= 总高度-导航栏高度
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val layoutParams = mViewPager?.layoutParams
        layoutParams?.height = measuredHeight - mTabLayout!!.measuredHeight
        mViewPager?.layoutParams = layoutParams
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mTopViewHeight = mTopView!!.measuredHeight
    }

    override fun scrollTo(x: Int, y: Int) {
        var y = y
        if (y < 0) {
            y = 0
        }
        if (y > mTopViewHeight) {
            y = mTopViewHeight
        }
        super.scrollTo(x, y)
    }

    fun setTopView(mTopView: View?) {
        this.mTopView = mTopView
    }

    fun setTabLayout(mTabLayout: View?) {
        this.mTabLayout = mTabLayout
    }

    fun setViewPager(mViewPager: View?) {
        this.mViewPager = mViewPager
    }

    init {
        orientation = VERTICAL
    }
}