package com.zyx.minitiktok.util;

import com.zyx.minitiktok.api.IMiniDouyinService;
import com.zyx.minitiktok.model.PostVideoResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpUtils {

    public static Call<PostVideoResponse> postVideo(String userID, String userName, MultipartBody.Part coverImagePart, MultipartBody.Part videoPart) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IMiniDouyinService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IMiniDouyinService miniDouyinService = retrofit.create(IMiniDouyinService.class);
        return miniDouyinService.postVideo(userID, userName, coverImagePart, videoPart);

    }
}
