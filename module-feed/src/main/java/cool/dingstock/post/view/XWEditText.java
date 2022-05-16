package cool.dingstock.post.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

public class XWEditText extends AppCompatEditText {

	private XWEditText mthis;

	public XWEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mthis = this;
		// TODO Auto-generated constructor stub
	}
	public XWEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mthis = this;
		// TODO Auto-generated constructor stub
	}
	

	public XWEditText(Context context) {
		super(context);
		mthis = this;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if(e.getAction()==MotionEvent.ACTION_DOWN){
			
			//通知父控件不要干扰
			getParent().requestDisallowInterceptTouchEvent(true);
		}else if(e.getAction()==MotionEvent.ACTION_MOVE){
			
			//通知父控件不要干扰
			getParent().requestDisallowInterceptTouchEvent(true);
		}else if(e.getAction()==MotionEvent.ACTION_UP){
			
//			getParent().requestDisallowInterceptTouchEvent(true);
		}
		return super.onTouchEvent(e);
	}

}