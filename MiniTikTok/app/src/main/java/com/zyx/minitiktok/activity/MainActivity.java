package com.zyx.minitiktok.activity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zyx.minitiktok.R;
import com.zyx.minitiktok.base.MyContants;
import com.zyx.minitiktok.fragment.HomeFragment;
import com.zyx.minitiktok.model.PostVideoResponse;
import com.zyx.minitiktok.util.HttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    private String imagePath;
    private String videoPath;
    private static final String TAG = "MainActivity";
    private RadioGroup gp;
    private String userID = "123456789";
    private List<Fragment> fragments = new ArrayList<>();
    private String userName = "user";
    private static final int PICK_IMAGE = 1;
    private static final int LOGIN = 2;
    private RadioButton rbHome;
    private static final int RECORD = 3;
    private long lastTime;
    private boolean login = false;
    private final int EXIT_TIME = 2000;
    private ImageView ivLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideExtra();
        permissionRequest(1);
        loadFragments();
        initBottom();
    }

    private void loadFragments(){
        Intent intent = getIntent();
        fragments.add(HomeFragment.newInstance("a","0"));
    }

    private void initBottom(){
        gp = findViewById(R.id.radio_group_buttons);
        rbHome = findViewById(R.id.rb_home);
        ivLogin = findViewById(R.id.btn_login);

        ((RadioButton)findViewById(R.id.rb_home)).setTextColor(getResources().getColor(R.color.light_background));
        gp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            Fragment fragment = null;
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioID) {
                switch (radioID){
                    case R.id.radio_button_upload:{
                        ((RadioButton)findViewById(R.id.radio_button_upload)).setChecked(false);
                        Intent intent = new Intent(MainActivity.this, TakeVideoActivity.class);
                        startActivityForResult(intent,RECORD);
                        return;
                    }
                }
                if (null!=fragment){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container,fragment)
                            .commit();
                }
                updateRadioColor(radioID);
            }
        });
        rbHome.setChecked(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragments.get(0))
                .commit();

        //  去到登陆页面
        ivLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateRadioColor(int radioID) {
        ((RadioButton)findViewById(R.id.rb_home)).setTextColor(getResources().getColor(R.color.dark_background));
        ((RadioButton)findViewById(radioID)).setTextColor(getResources().getColor(R.color.light_background));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode  = "+requestCode+" resultCode = "+resultCode);
        if(null == data || RESULT_OK != resultCode) return;
        switch (requestCode){
            case RECORD:{
                videoPath = data.getStringExtra(getString(R.string.video_path));
                Toast.makeText(MainActivity.this,"请拍摄封面图",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                startActivityForResult(intent,PICK_IMAGE);
                break;
            }
            case PICK_IMAGE:{
                imagePath = data.getStringExtra(getString(R.string.photo_path));
                Log.d(TAG, "selectedImage = " + imagePath);
                Log.d(TAG, "selectedVideo = " + videoPath);

                if (videoPath != null && imagePath != null) {
                    uploadVideo();
                } else {
                    throw new IllegalArgumentException("error data uri, mSelectedVideo = "
                            + videoPath
                            + ", mSelectedImage = "
                            + imagePath);
                }
                break;
            }
            case LOGIN:{
                userID = data.getStringExtra(("userId"));
                userName = data.getStringExtra("username");

                fragments.remove(fragments.size()-1);
                fragments.remove(fragments.size()-1);
                Log.d(TAG, "onActivityResult: Set login = true");
                login = true;
                break;
            }
        }
    }

    /**
     * 上传视频
     */
    private void uploadVideo() {
        MultipartBody.Part videoPart = getUrl(MyContants.VIEDO, videoPath);

        MultipartBody.Part coverImagePart = getUrl(MyContants.COVER_IMAGE, imagePath);

        Toast.makeText(MainActivity.this,"视频正在上传中，请耐心等待……",Toast.LENGTH_SHORT).show();


        HttpUtils.postVideo(userID, userName, coverImagePart, videoPart).enqueue(
                new Callback<PostVideoResponse>() {
                    @Override
                    public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                        if (response.body() != null) {
                            Toast.makeText(MainActivity.this, "视频上传成功", Toast.LENGTH_SHORT).show();
                            refreshVideo();
                        }
                    }
                    @Override
                    public void onFailure(Call<PostVideoResponse> call, Throwable throwable) {
                        Toast.makeText(MainActivity.this,"视频上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //刷新一下视频
    private void refreshVideo() {
        HomeFragment fragment = (HomeFragment) fragments.get(0);
        fragment.fetchFeed();
    }

    private MultipartBody.Part getUrl(String name, String path) {
        File f = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }


    //双击退出
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastTime > EXIT_TIME) {
            if (gp.getCheckedRadioButtonId() != R.id.rb_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, fragments.get(0))
                        .commit();
                gp.check(R.id.rb_home);
                updateRadioColor(R.id.rb_home);
            }else{
                Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                lastTime = System.currentTimeMillis();
            }
        } else {
            super.onBackPressed();
        }
    }

}