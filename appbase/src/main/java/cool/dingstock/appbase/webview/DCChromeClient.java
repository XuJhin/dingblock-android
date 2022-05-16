package cool.dingstock.appbase.webview;




import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import cool.dingstock.appbase.delegate.DCWebViewControllerDelegate;
import cool.dingstock.lib_base.json.JSONHelper;
import cool.dingstock.lib_base.util.Logger;

public class DCChromeClient extends WebChromeClient {

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        Logger.d("onReceivedTitle ");
        if (!(view instanceof DCWebView)) {
            return;
        }
        DCWebView dcWebView = (DCWebView) view;
        DCWebViewControllerDelegate controllerDelegate = dcWebView.getControllerDelegate();
        if (null != controllerDelegate) {
            controllerDelegate.setTitleBarTitle(title);
        }
    }
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (null == consoleMessage) {
            super.onConsoleMessage(null);
        }
        Logger.d(JSONHelper.toJson(consoleMessage));
        return super.onConsoleMessage(consoleMessage);
    }
}
