package kr.co.itforone.canadaw;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.ViewTreeObserver;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import kr.co.itforone.canadaw.databinding.ActivityMainBinding;
import util.Common;


public class MainActivity extends AppCompatActivity {

    private long backPrssedTime = 0;
    static final int PERMISSION_REQUEST_CODE = 1;
    ActivityMainBinding binding;
    String url = "";
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private boolean hasPermissions(String[] permissions) {
        // 퍼미션 확인
        int result = -1;
        for (int i = 0; i < permissions.length; i++) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]);
        }
        Log.d("per_result",String.valueOf(result));
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;

        }else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (!hasPermissions(PERMISSIONS)) {

                } else {
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setMainData(this);
        //스플레시 인텐트

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        Intent splash = new Intent(this,SplashActivity.class);
        startActivity(splash);
        //토큰 생성
        Common.setTOKEN(this);


        //웹뷰 세팅
        binding.webview.addJavascriptInterface(new WebviewJavainterface(this),"Android");
        binding.webview.setWebViewClient(new ClientManager(this));
        binding.webview.setWebChromeClient(new ChoromeManager(this, this));
        WebSettings settings = binding.webview.getSettings();
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);//캐쉬 사용여부
        settings.setDomStorageEnabled(true);//HTML5에서 DOM 사용여부
        //토큰 값 생성
        FirebaseApp.initializeApp(this);//firebase 등록함
        FirebaseMessaging.getInstance().subscribeToTopic("tdaeri");

        binding.webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {

                    //String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
                    //fileName = URLEncoder.encode(fileName, "EUC-KR").replace("+", "%20");
                    //fileName = URLDecoder.decode(fileName, "UTF-8");
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimetype);

                    //------------------------COOKIE!!------------------------
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie", cookies);
                    //------------------------COOKIE!!------------------------
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file...");
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                    request.allowScanningByMediaScanner();

                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));

                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "다운로드 시작..", Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), fileName, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        } else {
                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        }
                    }
                }
            }
        });

        //자동로그인
        SharedPreferences sf = getSharedPreferences("lginfo",MODE_PRIVATE);
        String id = sf.getString("id","");


        //Toast.makeText(getApplicationContext(),id+pwd,Toast.LENGTH_LONG).show();

        //새로고침
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.webview.clearCache(true);
                binding.webview.reload();
                binding.refreshLayout.setRefreshing(false);
            }
        });

        binding.refreshLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(binding.webview.getScrollY() == 0){
                    binding.refreshLayout.setEnabled(true);
                }
                else{
                    binding.refreshLayout.setEnabled(false);
                }
            }
        });

        //Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();
        Intent intent = getIntent();
        url=getString(R.string.index);
        if (!id.equals("")) {
            url=getString(R.string.chklogin) + id;
        } else {
            url=getString(R.string.index);
        }
        try{
            if(!intent.getExtras().getString("goUrl").equals("")){
                url =intent.getExtras().getString("goUrl");
            }
        }catch(Exception e){
            Log.d("error1",e.toString());
            e.printStackTrace();
        }
        binding.webview.loadUrl(url);




    }

    //뒤로가기이벤트
    @Override
    public void onBackPressed(){
        WebBackForwardList list = null;
        String backurl ="";

        if(binding.webview.getUrl().contains("intro.php")){



        }


        try {
            list = binding.webview.copyBackForwardList();
            if(list.getSize() >1 ){
                backurl = list.getItemAtIndex(list.getCurrentIndex() - 1).getUrl();
            }
        } catch (NullPointerException e) {
                e.printStackTrace();
        }

        if(backurl.contains("lotto.game.update.php")){
            binding.webview.clearCache(true);
            binding.webview.loadUrl(getString(R.string.index));
        }
        else if(binding.webview.getUrl().contains("mobile/intro.php") || binding.webview.getUrl().equals("http://canadaw2.itforone.co.kr/")){
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPrssedTime;
            if (0 <= intervalTime && 2000 >= intervalTime){
                finish();
            }
            else
            {
                backPrssedTime = tempTime;
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(binding.webview.canGoBack()){
            binding.webview.goBack();
        }else{
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPrssedTime;
            if (0 <= intervalTime && 2000 >= intervalTime){
                finish();
            }
            else
            {
                backPrssedTime = tempTime;
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
