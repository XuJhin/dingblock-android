package cool.dingstock.appbase.customerview.scroll

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.funnywolf.hollowkit.scenes.douban.view.*
import cool.dingstock.appbase.ext.dp
import cool.dingstock.lib_base.util.Logger
import kotlin.math.max

/**
 * 豆瓣详情页的框架视图
 *
 * @author https://github.com/funnywolfdadada
 * @since 2020/4/10
 */
@SuppressLint("ClickableViewAccessibility")
@RequiresApi(Build.VERSION_CODES.M)
class ScrollDetailView : FrameLayout {

    //    val toolBar: TextView
    val linkedScrollView: LinkedScrollView
    val bottomSheetLayout: BottomSheetLayout
    val topRecyclerView: RecyclerView

    val bottomLayout: FrameLayout
    var bottomScrollViewProvider: (() -> View?)? = null
        set(value) {
            linkedScrollView.setBottomView(bottomLayout, value)
            field = value
        }

    var toolbarHeight = 60.dp.toInt()
    var topMarginHeight = 100.dp.toInt()
    var minBottomShowingHeight = 60.dp.toInt()

    var isBottomViewFloating = false
        private set

    private var topScrolledY = 0F

    var bottomExpendListener: BottomExpendListener? = null
    var currentState = -1
    var enableTopScroll = true


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setBackgroundColor(Color.TRANSPARENT)
        linkedScrollView = LinkedScrollView(context)
        linkedScrollView.setBackgroundColor(Color.TRANSPARENT)
        addView(linkedScrollView)
        bottomSheetLayout = BottomSheetLayout(context)
        addView(
            bottomSheetLayout,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                topMargin = topMarginHeight
            })
//        toolBar = TextView(context)
//        addView(toolBar, LayoutParams(LayoutParams.MATCH_PARENT, toolbarHeight))
//
        linkedScrollView.topContainer.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        linkedScrollView.bottomContainer.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                bottomMargin = topMarginHeight
            }

        linkedScrollView.setOnScrollChangeListener { _, _, _, _, _ ->
            updateBottomView()
            updateToolbar()
        }
        bottomSheetLayout.onProcessChangedListener = { updateToolbar() }

        topRecyclerView = RecyclerView(context)
        topRecyclerView.setPaddingRelative(0, 0, 0, 0)
        topRecyclerView.clipToPadding = false
        topRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                topScrolledY += dy
                if (topScrolledY < 0) {
                    topScrolledY = 0F
                }
                updateToolbar()
            }
        })
        linkedScrollView.naverTouchListener = OnTouchListener { v, event ->
            if(event?.action == MotionEvent.ACTION_DOWN){
                if(!enableTopScroll){
                    close()
                }
            }
            true
        }
        linkedScrollView.setTopView(topRecyclerView) { topRecyclerView }

        bottomLayout = FrameLayout(context)
        linkedScrollView.setBottomView(bottomLayout, bottomScrollViewProvider)
        linkedScrollView.isNestedScrollingEnabled = true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        post { updateBottomView() }
    }

    private fun updateBottomView() {
        val bottomY = linkedScrollView.bottomContainer.y - linkedScrollView.scrollY
        val shouldBottomFloating = bottomY > height - minBottomShowingHeight
        if (shouldBottomFloating && !isBottomViewFloating) {
            isBottomViewFloating = true
            linkedScrollView.removeBottomView()
            bottomSheetLayout.setContentView(bottomLayout, minBottomShowingHeight)
            bottomExpendListener?.onBottomExpend(false, !isBottomViewFloating)
            Logger.e("updateBottomView isBottomViewFloating:${isBottomViewFloating}")
        } else if (!shouldBottomFloating && isBottomViewFloating) {
            isBottomViewFloating = false
            bottomSheetLayout.removeContentView()
            linkedScrollView.setBottomView(bottomLayout, bottomScrollViewProvider)
            Logger.e("updateBottomView isBottomViewFloating:${isBottomViewFloating}")
            bottomExpendListener?.onBottomExpend(false, !isBottomViewFloating)
        }
    }

    private fun updateToolbar() {
        if(bottomSheetLayout.state == BOTTOM_SHEET_STATE_SCROLLING){
            return
        }
        if (currentState == bottomSheetLayout.state) {
            return
        }
        Logger.e("updateToolbar isBottomViewFloating:${isBottomViewFloating}")
        currentState = bottomSheetLayout.state
        if (bottomSheetLayout.state == BOTTOM_SHEET_STATE_COLLAPSED) {
            bottomExpendListener?.onBottomExpend(false, !isBottomViewFloating)
        } else if (bottomSheetLayout.state == BOTTOM_SHEET_STATE_EXTENDED) {
            bottomExpendListener?.onBottomExpend(true, !isBottomViewFloating)
        }
    }


    fun open(){
        bottomSheetLayout.open()
    }

    fun close(){
        bottomSheetLayout.close()
    }

    fun enableTopScroll(boolean: Boolean){
        enableTopScroll = boolean
        linkedScrollView.isNestedScrollingEnabled = boolean
    }


    interface BottomExpendListener {
        fun onBottomExpend(isExpend: Boolean, isContactBottom: Boolean)
    }


}