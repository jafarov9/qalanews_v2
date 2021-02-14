package com.hajma.qalanews_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.hajma.qalanews_android.adapters.ImageAdapter;
import com.hajma.qalanews_android.data.NewsProvider;
import com.hajma.qalanews_android.entity.News;
import com.hajma.qalanews_android.eventbus.DataEvent;
import com.hajma.qalanews_android.retrofit.ApiUtils;
import com.hajma.qalanews_android.retrofit.UserDAOInterface;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class DetailedNewsActivity extends AppCompatActivity implements Runnable, YouTubePlayer.OnInitializedListener {

    private final String API_KEY = "AIzaSyAmavuMV4iojhEKjxI7l1ORV79LFC8JY_g";
    private ImageView imgDetailedNews;
    private TextView txtDetailedNews;
    private TextView txtDetailedDate;
    private TextView txtDetailedViewCount;
    private int newsID;
    private String soundUrl;
    private LinearLayout audiopanel;
    private FloatingActionButton fab;
    MediaPlayer mediaPlayer = new MediaPlayer();
    private SeekBar seekBar;
    boolean wasPlaying = false;
    private Runnable runnable;
    private ImageButton imgButtonSaveNews;
    private UserDAOInterface userDIF;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private News news;
    private ArrayList<String> images;
    private RecyclerView rvImages;
    private ImageAdapter imgAdapter;
    private RelativeLayout relativeLayoutImages;
    private RelativeLayout relativeLayoutVideo;
    private YouTubePlayerView youTubePlayerView;
    private String video_link;
    private boolean logined;
    private ContentResolver contentResolver;
    private Uri CONTENT_URI = NewsProvider.CONTENT_URI_USERS;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_news);
        newsID = getIntent().getIntExtra("newsID", 0);

        contentResolver = getContentResolver();
        relativeLayoutImages = findViewById(R.id.relativeImagesLayout);

        relativeLayoutVideo = findViewById(R.id.relativeLayoutVideo);

        rvImages = findViewById(R.id.rv_other_images);
        rvImages.setLayoutManager(new LinearLayoutManager(this));
        rvImages.setHasFixedSize(true);
        images = new ArrayList<>();


        audiopanel = findViewById(R.id.audiopanel);
        imgDetailedNews = findViewById(R.id.imgNewsDetailed);
        txtDetailedNews = findViewById(R.id.txtNewsDetailed);
        txtDetailedDate = findViewById(R.id.txtDetailedDate);
        txtDetailedViewCount = findViewById(R.id.txtDetailedViewCount);
        userDIF = ApiUtils.getUserDAOInterface();
        sharedPreferences = getSharedPreferences("usercontrol", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        logined = sharedPreferences.getBoolean("logined", false);

        if(logined) {
            increaseCount(Constants.CODE_READ);
            EventBus.getDefault().postSticky(new DataEvent.CallSavedProfileFragmentUpdate(1, DataEvent.FRAGMENT_PROFILE_ID));
        }
        String token = sharedPreferences.getString("token", null);

        imgButtonSaveNews = findViewById(R.id.imageButtonSaveNews);
        imgButtonSaveNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(token != null) {
                    userDIF.savenews(newsID, "Bearer " + token).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                try {
                                    String body = response.body().string();
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.added_to_saved), Toast.LENGTH_LONG).show();
                                    increaseCount(Constants.CODE_SAVED);
                                    EventBus.getDefault().postSticky(new DataEvent.CallSavedProfileFragmentUpdate(1, 0));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    String body = response.errorBody().string();
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "You are not logged", Toast.LENGTH_LONG).show();
                }

            }
        });


        //soundUrl = getIntent().getStringExtra("audio");
        //video_link = getIntent().getStringExtra("video_link");



        //Log.e("ghgh", "NewsID: "+newsID);

        seekBar = findViewById(R.id.seekBar);

        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Variables.isNetworkConnected) {
                    playSong();
                }else {
                    Toast.makeText(DetailedNewsActivity.this, getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                int x = (int) Math.ceil(progress / 1000f);


                double percent = progress / (double) seekBar.getMax();
                int offset = seekBar.getThumbOffset();
                int seekWidth = seekBar.getWidth();
                int val = (int) Math.round(percent * (seekWidth - 2 * offset));


                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    clearMediaPlayer();
                    fab.setImageDrawable(ContextCompat.getDrawable(DetailedNewsActivity.this, android.R.drawable.ic_media_play));
                    DetailedNewsActivity.this.seekBar.setProgress(0);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        loadDetailedNews();
    }

    private void increaseCount(int code) {

        String username = sharedPreferences.getString("username", "");
        Cursor c;
        int count = 0;

        if(code == Constants.CODE_READ) {
            String projection[] = {"read"};
            String selection = " username = ?";
            String selectionArgs[] = {username};

            c = contentResolver.query(CONTENT_URI, projection, selection, selectionArgs, null);

            if(c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    count = c.getInt(c.getColumnIndex("read"));
                }
            }
            ContentValues values = new ContentValues();
            values.put("read", count+1);
            contentResolver.update(CONTENT_URI, values, selection, selectionArgs);
        }

        if(code == Constants.CODE_SAVED) {
            String projection[] = {"saved"};
            String selection = " username = ?";
            String selectionArgs[] = {username};

            c = contentResolver.query(CONTENT_URI, projection, selection, selectionArgs, null);

            if(c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    count = c.getInt(c.getColumnIndex("saved"));
                }
            }
            ContentValues values = new ContentValues();
            values.put("saved", count+1);
            contentResolver.update(CONTENT_URI, values, selection, selectionArgs);
        }
    }


    public void playSong() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                clearMediaPlayer();
                seekBar.setProgress(0);
                wasPlaying = true;
                fab.setImageDrawable(ContextCompat.getDrawable(DetailedNewsActivity.this, android.R.drawable.ic_media_play));
            }


            if (!wasPlaying) {

                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }

                fab.setImageDrawable(ContextCompat.getDrawable(DetailedNewsActivity.this, android.R.drawable.ic_media_pause));


                mediaPlayer.setDataSource(soundUrl);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    mediaPlayer.prepare();
                    mediaPlayer.setVolume(0.5f, 0.5f);
                    mediaPlayer.setLooping(false);
                    seekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {

                        }
                    });

                    new Thread(this).start();

            }

            wasPlaying = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();


        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }

            seekBar.setProgress(currentPosition);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mediaPlayer != null) {
            clearMediaPlayer();
        }
    }

    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }


    public void loadDetailedNews() {
        String url = String.format("https://qalanews.com/api/news-detailed?newsId=%1$s", String.valueOf(newsID));

        StringRequest request =
                new StringRequest(Request.Method.GET,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("qweee", "NewsID: "+newsID);


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray imageArray = jsonObject.getJSONObject("data").getJSONArray("images");

                            if(imageArray != null && imageArray.length() > 0) {
                                for(int i = 0;i < imageArray.length();i++) {
                                    String image = imageArray.getString(i);
                                    images.add(image);
                                    loadImages();
                                }
                            }

                            news = new News(
                                    jsonObject.getJSONObject("data").getJSONObject("news").getInt("id"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getString("title"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getString("cover"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getString("sound"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getString("video_link"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getInt("news_type_id"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getInt("category_id"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getBoolean("comment_available"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getInt("view_count"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getString("publish_date"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getString("news_type_name"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getString("category_name"),
                                    jsonObject.getJSONObject("data").getJSONObject("news").getString("content")
                            );

                            Log.e("vidd", jsonObject.getJSONObject("data").getJSONObject("news").getString("video_link"));

                            txtDetailedNews.setText(news.getContent());
                            txtDetailedViewCount.setText(""+news.getView_count());
                            String timeText = news.getPublish_date();
                            long parsedTime = TimeAgo.parseDate(timeText);
                            String timeAgo = TimeAgo.getTimeAgo(parsedTime, sharedPreferences);

                            txtDetailedDate.setText(timeAgo);
                            video_link = news.getVideo_link();

                            if(video_link != null && !video_link.equals("null")) {
                                relativeLayoutVideo.setVisibility(View.VISIBLE);
                                YouTubePlayerFragment frag =
                                        (YouTubePlayerFragment) DetailedNewsActivity.this.getFragmentManager().findFragmentById(R.id.youtube_fragment);
                                frag.initialize(API_KEY, DetailedNewsActivity.this);
                            }

                            soundUrl = news.getSound();

                            if(!soundUrl.equals("")) {

                                audiopanel.setVisibility(View.VISIBLE);
                            }

                            Picasso.get()
                                    .load(news.getCover()
                                            .replace("http:", "https:"))
                                    .into(imgDetailedNews);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void loadImages() {
        relativeLayoutImages.setVisibility(View.VISIBLE);
        imgAdapter = new ImageAdapter(this, images);
        rvImages.setAdapter(imgAdapter);
    }

    public void backToHome(View view) {
        onBackPressed();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b) {
            youTubePlayer.loadVideo(video_link);
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    //add news to database
    /*private void addNewsToDatabase(News news) {

        if(isNewsExist(news)) {

            //mAsyncTask = new DownloadTask().execute(stringToURL(news.getCover()));
            String urlImageDatabase = ImageDownloadTask.saveImageToPhone(news.getCover().replace("http:", "https:"), contextWrapper);
            ContentValues values = new ContentValues();
            values.put("id", news.getId());
            values.put("title", news.getTitle());
            values.put("cover", urlImageDatabase);
            values.put("sound", news.getSound());
            values.put("video_link", news.getVideo_link());
            values.put("news_type_id", news.getNews_type_id());
            if (news.isComment_avialible()) {
                values.put("comment_available", 1);

            } else {
                values.put("comment_available", 0);
            }

            values.put("view_count", news.getView_count());
            values.put("publish_date", news.getPublish_date());
            values.put("category_name", news.getCategory_name());
            values.put("content", news.getContent());

            getContentResolver().insert(CONTENT_URI, values);
            Toast.makeText(this, "Added to saved", Toast.LENGTH_LONG).show();
            EventBus.getDefault().postSticky(new DataEvent.CallDataUpdate(1));

        }else {
            Toast.makeText(this, "This news already exists", Toast.LENGTH_LONG).show();
        }
    }
    //News is exist control
    private boolean isNewsExist(News news) {

        Cursor c = null;

        String[] projection = {String.valueOf(news.getId())};
        String selection = "id = ?";
        String selectionArgs[] = {String.valueOf(news.getId())};
        c = getContentResolver().query(CONTENT_URI, projection, selection, selectionArgs, null, null);

        if(c != null && c.getCount() ==  0) {
            return true;
        } else {
            return false;
        }
    }*/

}
