package com.zyx.minitiktok.model;

import android.content.Context;
import android.util.AttributeSet;

import cn.jzvd.JZVideoPlayerStandard;

public class MyVideoPage extends JZVideoPlayerStandard {

    public MyVideoPage(Context context) {
        super(context);
    }

    public MyVideoPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        startVideo();
    }
}
