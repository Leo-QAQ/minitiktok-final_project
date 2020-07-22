package com.zyx.minitiktok.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.zyx.minitiktok.R;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_TIME = 1000; //开机动画持续时间
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hideExtra();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        },SPLASH_TIME);
    }

}