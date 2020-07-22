package com.zyx.minitiktok.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zyx.minitiktok.R;


/**
 * 登陆的界面
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private Button mBtnLogin;
    private View mInputLayout;
    private float mWidth, mHeight;
    private LinearLayout mName, mPsw;
    private EditText mEtUserName, mEtPassWord;
    private SharedPreferences login_sp;
    private SharedPreferences.Editor editor;
    private CheckBox mRememberCheck;
    private TextView sign_in;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_login);
        login_sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemenber = login_sp.getBoolean("remember_password", false);
        initView();
        if (isRemenber) {
            //将账号和密码都设置到文本中
            String account = login_sp.getString("account", "");
            String password = login_sp.getString("password", "");
            mEtUserName.setText(account);
            mEtPassWord.setText(password);
            mRememberCheck.setChecked(true);

        }
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mInputLayout = findViewById(R.id.login_input_layout);
        mName = (LinearLayout) findViewById(R.id.input_layout_name);
        mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);
        mEtUserName = (EditText) findViewById(R.id.input_login_name);
        mEtPassWord = (EditText) findViewById(R.id.input_login_pwd);
        mRememberCheck = (CheckBox) findViewById(R.id.login_remember);
        sign_in = (TextView) findViewById(R.id.tv_register);

        sign_in.setOnClickListener(this);

        mBtnLogin.setOnClickListener(new animationOnClickListener(this, mBtnLogin));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_register:
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
                break;
        }


    }

    /**
     * 简单的动画效果
     */
    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                MarginLayoutParams params = (MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                mInputLayout.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

    }

    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(1000);
        animator3.start();

    }

    /**
     * 带动画的点击动作
     */
    class animationOnClickListener implements OnClickListener {
        private Context context;
        private TextView btnLogin;


        public animationOnClickListener(Context context, Button btnLogin) {
            this.btnLogin = btnLogin;
            this.context = context;
        }

        @Override
        public void onClick(View view) {

            if (btnLogin.getVisibility() == View.VISIBLE) {

                btnLogin.setVisibility(View.GONE);

                // 计算出控件的高与宽
                mWidth = btnLogin.getMeasuredWidth();
                mHeight = btnLogin.getMeasuredHeight();
                // 隐藏输入框
                mName.setVisibility(View.INVISIBLE);
                mPsw.setVisibility(View.INVISIBLE);

                inputAnimator(mInputLayout, mWidth, mHeight);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final String username = mEtUserName.getText().toString();
                        final String password = mEtPassWord.getText().toString();


                        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                            Toast.makeText(LoginActivity.this, "请输入账号或密码", Toast.LENGTH_SHORT).show();
                            recovery();
                            return;
                        }
                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        //记住密码
                        editor = login_sp.edit();
                        if (mRememberCheck.isChecked()) {
                            editor.putBoolean("remember_password", true);
                            editor.putString("account", username);
                            editor.putString("password", password);
                        } else {
                            editor.clear();
                        }
                        editor.apply();
                        finish();

                    }
                }, 1000);

            }

        }
    }

    /**
     * 恢复初始状态
     */
    private void recovery() {

        mBtnLogin.setVisibility(View.VISIBLE);
        mInputLayout.setVisibility(View.VISIBLE);
        mName.setVisibility(View.VISIBLE);
        mPsw.setVisibility(View.VISIBLE);

        MarginLayoutParams params = (MarginLayoutParams) mInputLayout.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        mInputLayout.setLayoutParams(params);


        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 0.5f, 1f);
        animator2.setDuration(500);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }

    /**
     * 设置状态栏， 沉浸体验
     */
    private void setStatusBar() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
}
