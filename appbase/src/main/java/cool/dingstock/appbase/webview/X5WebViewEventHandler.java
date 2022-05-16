package cool.dingstock.appbase.webview;

//import android.os.Bundle;
//import android.view.MotionEvent;
//import android.view.View;
//
//import com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension;
//import com.tencent.smtt.sdk.WebViewCallbackClient;
//
//import cool.dingstock.lib_base.util.Logger;
//
//
//public class X5WebViewEventHandler extends ProxyWebViewClientExtension  {
//
//	/**
//	 * 这个类用于实现由于X5webview适配架构导致的部分client回调不会发生，或者回调中传入的值不正确
//	 * 这个方法中所有的interface均是直接从内核中获取值并传入内核，请谨慎修改 使用时只需要在对应的方法中加入你自己的逻辑就可以
//	 * 同时注意：具有返回值的方法在正常情况下保持其返回效果 一般而言：返回true表示消费事件，由用户端直接处理事件
//	 * 返回false表示需要内核使用默认机制处理事件
//	 */
//	private DCWebView webView;
//
//	public X5WebViewEventHandler(DCWebView webView) {
//		this.webView = webView;
//		this.webView.setWebViewCallbackClient(callbackClient);
//	}
//
//
//
//	/**
//	 * 这里使用内核的事件回调链接到对应webview的事件回调
//	 */
//	private WebViewCallbackClient callbackClient = new WebViewCallbackClient() {
//
//		@Override
//		public boolean onTouchEvent(MotionEvent event, View view) {
//			return webView.tbs_onTouchEvent(event, view);
//		}
//
//		@Override
//		public boolean overScrollBy(int deltaX, int deltaY, int scrollX,
//				int scrollY, int scrollRangeX, int scrollRangeY,
//				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent,
//				View view) {
//			Logger.d("overScrollBy","deltaY="+deltaY,"scrollY="+scrollY,"scrollRangeY="+scrollRangeY,"maxOverScrollY="+maxOverScrollY);
//			return webView.tbs_overScrollBy(deltaX, deltaY, scrollX, scrollY,
//					scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
//					isTouchEvent, view);
//		}
//
//		@Override
//		public void computeScroll(View view) {
//			Logger.d("computeScroll",view.getScrollY()+","+view.getY());
//			webView.tbs_computeScroll(view);
//		}
//
//		@Override
//		public void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
//								   boolean clampedY, View view) {
//			Logger.d("onOverScrolled","scrollY="+scrollY,",webViewDy="+webView.getScrollY());
//			webView.tbs_onOverScrolled(scrollX, scrollY, clampedX, clampedY,
//					view);
//		}
//
//		@Override
//		public void onScrollChanged(int l, int t, int oldl, int oldt, View view) {
//			Logger.d("onScrollChanged","l="+l,",t="+t,"oldl="+oldl,",oldt="+oldt,",view="+view.getScrollY());
//			webView.tbs_onScrollChanged(l, t, oldl, oldt, view);
//		}
//
//		@Override
//		public void invalidate() {
//			Logger.d("invalidate","invalidate");
//		}
//
//		@Override
//		public boolean dispatchTouchEvent(MotionEvent ev, View view) {
//			return webView.tbs_dispatchTouchEvent(ev, view);
//		}
//
//		@Override
//		public boolean onInterceptTouchEvent(MotionEvent ev, View view) {
//			return webView.tbs_onInterceptTouchEvent(ev, view);
//		}
//	};
//
//	/**
//	 * 这里是内核代理的事件处理方法
//	 */
//	@Override
//	public Object onMiscCallBack(String method, Bundle bundle) {
//		return null;
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event, View view) {
//		return callbackClient.onTouchEvent(event, view);
//	}
//
//	public boolean onInterceptTouchEvent(MotionEvent ev, View view) {
//		return callbackClient.onInterceptTouchEvent(ev, view);
//	}
//
//	public boolean dispatchTouchEvent(MotionEvent ev, View view) {
//		return callbackClient.dispatchTouchEvent(ev, view);
//	}
//
//	public boolean overScrollBy(int deltaX, int deltaY, int scrollX,
//								int scrollY, int scrollRangeX, int scrollRangeY,
//								int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent,
//								View view) {
//		return callbackClient.overScrollBy(deltaX, deltaY, scrollX, scrollY,
//				scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
//				isTouchEvent, view);
//	}
//
//	public void onScrollChanged(int l, int t, int oldl, int oldt, View view) {
//		Logger.d("onScrollChanged1","l="+l,",t="+t,"oldl="+oldl,",oldt="+oldt,",view="+view.getScrollY());
//		callbackClient.onScrollChanged(l, t, oldl, oldt, view);
//	}
//
//
//	public void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
//							   boolean clampedY, View view) {
//		callbackClient.onOverScrolled(scrollX, scrollY, clampedX, clampedY,
//				view);
//	}
//
//	public void computeScroll(View view) {
//		callbackClient.computeScroll(view);
//	}
//
//}