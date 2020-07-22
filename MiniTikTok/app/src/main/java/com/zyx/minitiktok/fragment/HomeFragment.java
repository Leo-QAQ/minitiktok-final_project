package com.zyx.minitiktok.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayerStandard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.zyx.minitiktok.model.GetVideosResponse;
import com.zyx.minitiktok.api.IMiniDouyinService;
import com.zyx.minitiktok.model.Video;
import com.zyx.minitiktok.model.MyVideoPage;

import com.zyx.minitiktok.R;

public class HomeFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView mrvVideo;

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private IMiniDouyinService miniDouyinService = retrofit.create(IMiniDouyinService.class);

    private List<Video> urlList = new ArrayList<>();
    private ListVideoAdapter videoAdapter;
    private SnapHelper snapHelper;
    private LinearLayoutManager layoutManager;
    private List<Boolean> likeList = new ArrayList<>();

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_home, container, false);
        initView(mView);
        fetchFeed();
        addListener();
        return mView;
    }

    private void initView(View view){

        mrvVideo = view.findViewById(R.id.rv_video);
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mrvVideo);
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mrvVideo.setLayoutManager(layoutManager);
        mrvVideo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mrvVideo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                videoAdapter = new ListVideoAdapter(getActivity(), mrvVideo.getWidth(), mrvVideo.getHeight());
                mrvVideo.setAdapter(videoAdapter);
            }
        });
    }

    public void fetchFeed() {
        miniDouyinService.getVideos().enqueue(new Callback<GetVideosResponse>() {
            @Override
            public void onResponse(Call<GetVideosResponse> call, Response<GetVideosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    urlList.clear();
                    likeList.clear();
                    GetVideosResponse getVideosResponse = response.body();
                    if (getVideosResponse.isSuccess()) {
                        urlList = getVideosResponse.getVideos();
                        //Collections.reverse(urlList);
                        for (int i = 0; i < urlList.size(); i++) {
                            likeList.add(false);
                        }
                        //Collections.reverse(likeList);
                        mrvVideo.getAdapter().notifyDataSetChanged();
                    } else
                        Toast.makeText(getContext(), "Refresh fail!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "Refresh fail!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<GetVideosResponse> call, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addListener() {

        mrvVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        View view = snapHelper.findSnapView(layoutManager);
                        RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                        if (viewHolder instanceof VideoViewHolder && !((VideoViewHolder) viewHolder).mp_video.isCurrentPlay()) {
                            ((VideoViewHolder) viewHolder).mp_video.seekToInAdvance = 0;
                            ((VideoViewHolder) viewHolder).mp_video.startVideo();
                        }
                        break;

                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;

                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                }

            }
        });
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        private MyVideoPage mp_video;
        public VideoViewHolder(View rootView) {
            super(rootView);
            this.mp_video = rootView.findViewById(R.id.lv_view);
        }
    }

    class ListVideoAdapter extends RecyclerView.Adapter <VideoViewHolder> {

        private Context mContext;
        private int mHeight;
        private int mWidth;


        public ListVideoAdapter(Context context, int width, int height) {
            mContext = context;
            mWidth = width;
            mHeight = height;
        }

        @Override
        public void onBindViewHolder(final VideoViewHolder holder, final int position) {

            ImageButton imageButton = holder.itemView.findViewById(R.id.iv_like);
            imageButton.setOnClickListener(v -> {
                ImageButton button = (ImageButton)v;
                boolean like = !likeList.get(position);
                likeList.set(position, like);
                button.setImageDrawable(getResources().getDrawable(like ? R.drawable.like : R.drawable.like_blank));
                if (like) {
                    ScaleAnimation animation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                            ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(200);
                    animation.setRepeatCount(1);
                    animation.setRepeatMode(Animation.REVERSE);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            final ImageView heart = holder.itemView.findViewById(R.id.iv_like_blnk);
                            heart.setVisibility(View.VISIBLE);
                            Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.like_anim);
                            heart.startAnimation(anim);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    button.startAnimation(animation);
                }
            });


            holder.mp_video.setUp(urlList.get(position).videoUrl, JZVideoPlayerStandard.CURRENT_STATE_NORMAL);
            if (position == 0){
                holder.mp_video.startVideo();
            }

            GlideBuilder builder = new GlideBuilder(getActivity());
            int diskSizeInBytes = 1024 * 1024 * 100;
            int memorySizeInBytes = 1024 * 1024 * 60;
            builder.setDiskCache(new InternalCacheDiskCacheFactory(getActivity(), diskSizeInBytes));
            builder.setMemoryCache(new LruResourceCache(memorySizeInBytes));
            Glide.with(getActivity())
                    .load(urlList.get(position).imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mp_video.thumbImageView);
            holder.mp_video.thumbImageView.setVisibility(View.VISIBLE);
        }

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.activity_click_video, null);

            MyVideoPage videoPlayer = view.findViewById(R.id.lv_view);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) videoPlayer.getLayoutParams();
            layoutParams.width = mWidth;
            layoutParams.height = mHeight;
            videoPlayer.setLayoutParams(layoutParams);

            VideoViewHolder holder = new VideoViewHolder(view);
            return holder;

        }

        @Override
        public int getItemCount() {
            return urlList.size();
        }

    }

}