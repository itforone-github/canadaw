package kr.co.itforone.canadaw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

class ChoromeManager extends WebChromeClient {
    MainActivity mainActivity;
    Activity activity;

    ChoromeManager(MainActivity mainActivity, Activity activity){
        this.mainActivity = mainActivity;
        this.activity = activity;
    }

    ChoromeManager(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }
    ChoromeManager(){}

    //어럴트 창 처리
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return false;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                .create()
                .show();
        return true;
    }

    //경고창 띄우기
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        new AlertDialog.Builder(view.getContext())
                .setMessage("\n" + message + "\n")
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                result.confirm();
                            }
                        }).create().show();
        return true;
    }

}
