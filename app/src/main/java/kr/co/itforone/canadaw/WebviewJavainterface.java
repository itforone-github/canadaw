package kr.co.itforone.canadaw;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

class WebviewJavainterface {
    Activity activity;
    MainActivity mainActivity;


    WebviewJavainterface (MainActivity mainActivity){
        this.mainActivity=mainActivity;
    }
    WebviewJavainterface(Activity activity){
        this.activity=activity;
    }

    @JavascriptInterface
    public void login(String id) {

        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("lginfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id",id);
        editor.commit();
        //Toast.makeText(mainActivity.getApplicationContext(),id,Toast.LENGTH_LONG).show();

    }
    @JavascriptInterface
    public void logout() {

        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("lginfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        //Toast.makeText(mainActivity.getApplicationContext(),id,Toast.LENGTH_LONG).show();

    }
}
