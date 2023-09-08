package kr.co.itforone.canadaw;

import android.app.Activity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import util.Common;

class ClientManager extends WebViewClient {
    Activity activity;
    MainActivity mainActivity;
    ClientManager(Activity activity){
        this.activity = activity;
    }
    ClientManager(Activity activity, MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Common.setTOKEN(activity);
        view.loadUrl("javascript:fcmKey('"+ Common.TOKEN +"')");
    }
}

