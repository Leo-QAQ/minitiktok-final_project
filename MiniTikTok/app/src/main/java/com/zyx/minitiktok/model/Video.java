package com.zyx.minitiktok.model;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("id") public String Id;
    @SerializedName("user_name") public String userName;
    @SerializedName("image_url") public String imageUrl;
    @SerializedName("video_url") public String videoUrl;
    @SerializedName("image_w") public int imageWidth;
    @SerializedName("image_h") public int imageHeight;
    @SerializedName("createdAt") private String time;
}
