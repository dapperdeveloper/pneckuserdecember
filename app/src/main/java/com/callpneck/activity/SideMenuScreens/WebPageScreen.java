package com.callpneck.activity.SideMenuScreens;

import android.annotation.TargetApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;

public class WebPageScreen extends AppCompatActivity {

    private ImageView goBack;
    private TextView title;

    private boolean isPrivacy;
    private String LoadWebUrl;

    private WebView mWebview ;
    private ProgressBar progressBar;

    private void findViews() {
        goBack = (ImageView)findViewById( R.id.go_back );
        title = (TextView)findViewById( R.id.title );
        mWebview  = findViewById(R.id.load_web);
        progressBar=findViewById(R.id.loading_progress);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_web_page_screen);

        findViews();

        isPrivacy=getIntent().getBooleanExtra("is_privacy",false);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (isPrivacy){
            title.setText(getResources().getString(R.string.privacy_policy));
        }else {
            title.setText(getResources().getString(R.string.term_and_conditions));
        }

        LoadWebUrl=getIntent().getStringExtra("url");
        mWebview.getSettings().setJavaScriptEnabled(true);

        mWebview.setWebViewClient(new AppWebViewClients(progressBar) {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WebPageScreen.this, description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });

        mWebview.loadUrl(LoadWebUrl);


    }



    public class AppWebViewClients extends WebViewClient {
        private ProgressBar progressBar;

        public AppWebViewClients(ProgressBar progressBar) {
            this.progressBar=progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            Log.e("sdfjsdkfsjds","page is loaded successfully");
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}
