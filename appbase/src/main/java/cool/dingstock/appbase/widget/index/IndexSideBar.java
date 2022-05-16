package cool.dingstock.appbase.widget.index;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import cool.dingstock.appbase.R;
import cool.dingstock.lib_base.util.SizeUtils;

public class IndexSideBar extends View {
	public static final int POSITION_RIGHT = 0;
	public static final int POSITION_LEFT = 1;
	public static final int TEXT_ALIGN_CENTER = 0;
	public static final int TEXT_ALIGN_LEFT = 1;
	public static final int TEXT_ALIGN_RIGHT = 2;
	private final static int DEFAULT_TEXT_SIZE = 14; // sp
	private final static int DEFAULT_CENTER_TIPS_SIZE = 28; // dp
	private final static String[] DEFAULT_INDEX_ITEMS = {};
	private final static int DEFAULT_FOCUS_BG_COLOR = Color.parseColor("#179FED");
	private final static int DEFAULT_CENTER_TIPS_BG_COLOR = Color.parseColor("#30000000");
	/**
	 * 指示器背景颜色
	 */
	private final int mFocusBgColor;
	/**
	 * 中间提示文字颜色
	 */
	private final int mCenterTipTextColor;
	/**
	 * 中间提示背景颜色
	 */
	private final int mCenterTipBgColor;
	/**
	 * 提示语大小
	 */
	private final float mCenterTipsSize;
	private final RectF mStartTouchingArea = new RectF();
	private final DisplayMetrics mDisplayMetrics;
	private String[] mIndexItems;
	/**
	 * 当前索引
	 */
	private int mCurrentIndex = 0;
	/**
	 * 手指触摸的点的Y坐标
	 */
	private float mCurrentY = -1;
	private Paint mPaint;
	/**
	 * 指示器文字颜色
	 */
	private int mTextColor;
	/**
	 * 文字大小
	 */
	private float mTextSize;
	/**
	 * 索引item的高度
	 */
	private float mIndexItemHeight;
	/**
	 * 宽、高
	 */
	private float mBarHeight;
	private float mBarWidth;
	private boolean mStartTouching = false;
	/**
	 * true：ACTION_UP 后列表跳转
	 */
	private boolean mLazyRespond = false;
	/**
	 * 是否触摸中
	 */
	private boolean mIsMoved = false;
	/**
	 * 是否显示中间提示
	 */
	private boolean mIsShowCenterTips = false;
	/**
	 * 侧栏的位置
	 */
	private int mSideBarPosition;
	/**
	 * 文字位置, default is {@link #TEXT_ALIGN_CENTER}.
	 */
	private int mTextAlignment;
	/**
	 * 选择项监听
	 */
	private OnSelectIndexItemListener onSelectIndexItemListener;
	private float mFirstItemBaseLineY;
	
	public IndexSideBar(Context context) {
		this(context, null);
	}
	
	public IndexSideBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public IndexSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mDisplayMetrics = context.getResources().getDisplayMetrics();
		
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusSideBar);
		mLazyRespond = typedArray.getBoolean(R.styleable.FocusSideBar_sidebar_lazy_respond, false);
		mIsShowCenterTips = typedArray.getBoolean(R.styleable.FocusSideBar_sidebar_show_center_tips, false);
		mTextColor = typedArray.getColor(R.styleable.FocusSideBar_sidebar_text_color, Color.GRAY);
		mFocusBgColor = typedArray.getColor(R.styleable.FocusSideBar_sidebar_focus_bg_color, DEFAULT_FOCUS_BG_COLOR);
		mCenterTipTextColor = typedArray.getColor(R.styleable.FocusSideBar_sidebar_center_tips_text_color, Color.GRAY);
		mCenterTipBgColor = typedArray.getColor(R.styleable.FocusSideBar_sidebar_center_tips_bg_color, DEFAULT_CENTER_TIPS_BG_COLOR);
		mCenterTipsSize = typedArray.getDimension(R.styleable.FocusSideBar_sidebar_center_tips_size, dp2px(DEFAULT_CENTER_TIPS_SIZE));
		mTextSize = typedArray.getDimension(R.styleable.FocusSideBar_sidebar_text_size, sp2px(DEFAULT_TEXT_SIZE));
		mSideBarPosition = typedArray.getInt(R.styleable.FocusSideBar_sidebar_position, POSITION_RIGHT);
		mTextAlignment = typedArray.getInt(R.styleable.FocusSideBar_sidebar_text_alignment, TEXT_ALIGN_CENTER);
		typedArray.recycle();
		
		mIndexItems = DEFAULT_INDEX_ITEMS;
		
		initPaint();
	}
	
	private void initPaint() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(mTextColor);
		mPaint.setTextSize(mTextSize);
		switch (mTextAlignment) {
			case TEXT_ALIGN_CENTER:
				mPaint.setTextAlign(Paint.Align.CENTER);
				break;
			case TEXT_ALIGN_LEFT:
				mPaint.setTextAlign(Paint.Align.LEFT);
				break;
			case TEXT_ALIGN_RIGHT:
				mPaint.setTextAlign(Paint.Align.RIGHT);
				break;
		}
	}
	
	private float dp2px(int dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.mDisplayMetrics);
	}
	
	private float sp2px(int sp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, this.mDisplayMetrics);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mIndexItems.length == 0) {
			return super.onTouchEvent(event);
		}
		
		float eventY = event.getY();
		float eventX = event.getX();
		mCurrentIndex = getSelectedIndex(eventY);
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mStartTouchingArea.contains(eventX, eventY)) {
					mStartTouching = true;
					if (!mLazyRespond && onSelectIndexItemListener != null) {
						onSelectIndexItemListener.onTouch(true);
						onSelectIndexItemListener.onSelectIndexItem(mIndexItems[mCurrentIndex]);
					}
					mIsMoved = true;
					invalidate();
					return true;
				} else {
					mIsMoved = false;
					return false;
				}
			
			case MotionEvent.ACTION_MOVE:
				if (mStartTouching && !mLazyRespond && onSelectIndexItemListener != null) {
					onSelectIndexItemListener.onTouch(true);
					onSelectIndexItemListener.onSelectIndexItem(mIndexItems[mCurrentIndex]);
				}
				mIsMoved = true;
				invalidate();
				return true;
			case MotionEvent.ACTION_UP:
				if (onSelectIndexItemListener != null) {
					onSelectIndexItemListener.onTouch(false);
				}
				return true;
			
			case MotionEvent.ACTION_CANCEL:
				if (mLazyRespond && onSelectIndexItemListener != null) {
					onSelectIndexItemListener.onTouch(false);
					onSelectIndexItemListener.onSelectIndexItem(mIndexItems[mCurrentIndex]);
				}
				mStartTouching = false;
				mIsMoved = false;
				invalidate();
				return true;
		}
		
		return super.onTouchEvent(event);
	}
	
	@SuppressLint("ResourceAsColor")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mIndexItems.length == 0) {
			return;
		}
		for (int i = 0, mIndexItemsLength = mIndexItems.length; i < mIndexItemsLength; i++) {
			float baseLineY = mFirstItemBaseLineY + mIndexItemHeight * i;
			
			float baseLineX = 0f;
			if (mSideBarPosition == POSITION_LEFT) {
				switch (mTextAlignment) {
					case TEXT_ALIGN_CENTER:
						baseLineX = getPaddingLeft() + mBarWidth / 2;
						break;
					case TEXT_ALIGN_LEFT:
						baseLineX = getPaddingLeft();
						break;
					case TEXT_ALIGN_RIGHT:
						baseLineX = getPaddingLeft() + mBarWidth;
						break;
				}
			} else {
				switch (mTextAlignment) {
					case TEXT_ALIGN_CENTER:
						baseLineX = getWidth() - getPaddingRight() - mBarWidth / 2;
						break;
					case TEXT_ALIGN_RIGHT:
						baseLineX = getWidth() - getPaddingRight();
						break;
					case TEXT_ALIGN_LEFT:
						baseLineX = getWidth() - getPaddingRight() - mBarWidth;
						break;
				}
			}
			
			if (i == mCurrentIndex) {
				mPaint.setColor(mFocusBgColor);
				canvas.drawCircle(baseLineX, baseLineY - mTextSize / 3, (float) (mTextSize * 0.7), mPaint);
				mPaint.setColor(Color.WHITE);
			} else {
				mPaint.setColor(Color.GRAY);
			}
			canvas.drawText(mIndexItems[i], baseLineX, baseLineY, mPaint);
		}
		
		if (mIsMoved && mIsShowCenterTips) {
			mPaint.setColor(mCenterTipTextColor);
			float x = getWidth() / 2;
			float y = getHeight() / 2;
			mPaint.setTextSize(mCenterTipsSize);
			canvas.drawText(mIndexItems[mCurrentIndex], x, y, mPaint);
			mPaint.setColor(mCenterTipBgColor);
			canvas.drawCircle(x, (float) (y - mCenterTipsSize * 0.3), mCenterTipsSize, mPaint);
		}
		
		// reset paint
		mPaint.setAlpha(255);
		mPaint.setTextSize(mTextSize);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		
		Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
		mIndexItemHeight = fontMetrics.bottom - fontMetrics.top + SizeUtils.dp2px(4);
		mBarHeight = mIndexItems.length * mIndexItemHeight;
		
		// 计算最长文本的宽度作为边栏的宽度
		if (mIndexItems.length == 0) {
			return;
		}
		for (String indexItem : mIndexItems) {
			mBarWidth = Math.max(mBarWidth, mPaint.measureText(indexItem));
		}
		
		float areaLeft = (mSideBarPosition == POSITION_LEFT) ? 0 : (width - mBarWidth - getPaddingRight());
		float areaRight = (mSideBarPosition == POSITION_LEFT) ? (getPaddingLeft() + areaLeft + mBarWidth) : width;
		float areaTop = height / 2 - mBarHeight / 2;
		float areaBottom = areaTop + mBarHeight;
		mStartTouchingArea.set(
				areaLeft,
				areaTop,
				areaRight,
				areaBottom);
		
		// 要绘制的第一个文本的Y
		mFirstItemBaseLineY = (height / 2 - mIndexItems.length * mIndexItemHeight / 2)
				+ (mIndexItemHeight / 2 - (fontMetrics.descent - fontMetrics.ascent) / 2)
				- fontMetrics.ascent;
	}
	
	private int getSelectedIndex(float eventY) {
		mCurrentY = eventY - (getHeight() / 2 - mBarHeight / 2);
		if (mCurrentY <= 0) {
			return 0;
		}
		
		int index = (int) (mCurrentY / this.mIndexItemHeight);
		if (index >= this.mIndexItems.length) {
			index = this.mIndexItems.length - 1;
		}
		if (index < 0) {
			index = 0;
		}
		return index;
	}
	
	/**
	 * 设置指示器显示字符
	 *
	 * @param indexItems default is {@link #DEFAULT_INDEX_ITEMS}.
	 */
	public void setIndexItems(String[] indexItems) {
		mIndexItems = indexItems;
		requestLayout();
	}
	
	public void setTextColor(int color) {
		mTextColor = color;
		mPaint.setColor(color);
		postInvalidate();
	}
	
	public void setPosition(int position) {
		if (position != POSITION_RIGHT && position != POSITION_LEFT) {
			throw new IllegalArgumentException("the position must be POSITION_RIGHT or POSITION_LEFT");
		}
		
		mSideBarPosition = position;
		requestLayout();
	}
	
	public void setSelectedLetter(String letter) {
		if (mIndexItems.length == 0) {
			return;
		}
		int targetPosition = 0;
		for (int i = 0; i < mIndexItems.length; i++) {
			if (mIndexItems[i].equals(letter)) {
				targetPosition = i;
				break;
			}
		}
		if (targetPosition == mCurrentIndex) {
			return;
		}
		setCurrentIndex(targetPosition);
	}
	
	public void setCurrentIndex(int currentIndex) {
		this.mCurrentIndex = currentIndex;
		requestLayout();
	}
	
	public void setLazyRespond(boolean lazyRespond) {
		mLazyRespond = lazyRespond;
	}
	
	/**
	 * 设置文字对齐方向
	 *
	 * @param align 方向
	 */
	public void setTextAlign(int align) {
		if (mTextAlignment == align) {
			return;
		}
		switch (align) {
			case TEXT_ALIGN_CENTER:
				mPaint.setTextAlign(Paint.Align.CENTER);
				break;
			case TEXT_ALIGN_LEFT:
				mPaint.setTextAlign(Paint.Align.LEFT);
				break;
			case TEXT_ALIGN_RIGHT:
				mPaint.setTextAlign(Paint.Align.RIGHT);
				break;
			default:
				throw new IllegalArgumentException(
						"the alignment must be TEXT_ALIGN_CENTER, TEXT_ALIGN_LEFT or TEXT_ALIGN_RIGHT");
		}
		mTextAlignment = align;
		invalidate();
	}
	
	public void setTextSize(float size) {
		if (mTextSize == size) {
			return;
		}
		mTextSize = size;
		mPaint.setTextSize(size);
		invalidate();
	}
	
	public void setOnSelectIndexItemListener(OnSelectIndexItemListener onSelectIndexItemListener) {
		this.onSelectIndexItemListener = onSelectIndexItemListener;
	}
	
	public interface OnSelectIndexItemListener {
		void onSelectIndexItem(String index);
		
		void onTouch(boolean isTouch);
	}
}