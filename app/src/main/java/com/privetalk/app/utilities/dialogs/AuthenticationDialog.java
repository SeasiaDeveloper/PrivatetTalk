package com.privetalk.app.utilities.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.privetalk.app.R;


public class AuthenticationDialog extends Dialog {
    private String request_url;
    private String redirect_url;
    private AuthenticationListener listener;
    private String code;
    private Context mContext;

    public AuthenticationDialog(@NonNull Context context, AuthenticationListener listener) {
        super(context);
        this.listener = listener;
        mContext = context;
       // this.redirect_url = "https://www.instagram.com";
        this.redirect_url = context.getResources().getString(R.string.instagram_redirect_url);
      /*  this.request_url = "https://api.instagram.com/oauth/authorize" +
                "?client_id=" + mContext.getString(R.string.instagram_client_id) +
                "&redirect_uri=" + redirect_url +
                "&scope=user_profile" +
                "&response_type=code";*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.auth_dialog);
        initializeWebView();
    }

    private void initializeWebView() {
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl(request_url);
        webView.loadUrl("https://api.instagram.com/oauth/authorize" +
                "?client_id="+mContext.getResources().getString(R.string.instagram_client_id)+
                "&redirect_uri=https://socialsizzle.herokuapp.com/auth/" +
                "&scope=user_profile,user_media" +
                "&response_type=code");
        webView.setWebViewClient(webViewClient);
    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(redirect_url)) {
               AuthenticationDialog.this.dismiss();
               return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            redirect_url=url;
            if (url.contains("code=")) {
               // if (url.contains("code=")) {
                int lastIndex, firstIndex;
                lastIndex = url.lastIndexOf("#");
                firstIndex = url.indexOf("=");
                code = url.substring(firstIndex + 1, lastIndex);
                Log.e("access_token", code);
                listener.onCodeReceived(code);
                AuthenticationDialog.this.dismiss();
               // dismiss();
            } else if (url.contains("?error")) {
                Log.e("access_token", "getting error fetching access token");
                dismiss();
            }
        }
    };
}
