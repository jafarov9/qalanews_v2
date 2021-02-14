package com.hajma.qalanews_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.qalanews_android.DetailedNewsActivity;
import com.hajma.qalanews_android.PicassoCache;
import com.hajma.qalanews_android.R;
import com.hajma.qalanews_android.TimeAgo;
import com.hajma.qalanews_android.entity.News;

import java.util.List;

public class NewsAdapterSaved extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context c;
    private List<News> newsList;
    private final static int VIEW_TYPE_LARGE = 1;
    private boolean haveIsSound;
    private SharedPreferences sharedPreferences;


    public NewsAdapterSaved(Context c, List<News> newsArrayList, SharedPreferences sharedPreferences) {
        this.c = c;
        this.newsList = newsArrayList;
        this.sharedPreferences = sharedPreferences;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


         if(viewType == VIEW_TYPE_LARGE) {
            View v = LayoutInflater.from(c).inflate(R.layout.card_large_design, parent, false);
            return new NewsLargeCardHolder(v);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.e("recylertest", "onbind");

        if(holder instanceof NewsLargeCardHolder) {
            NewsLargeCardHolder largeCardHolder = (NewsLargeCardHolder) holder;

            if(newsList.get(position).getTitle().length() > 90) {
                largeCardHolder.txtLargeCardTitle.setText(newsList.get(position).getTitle().substring(0, 90));
            } else {
                largeCardHolder.txtLargeCardTitle.setText(newsList.get(position).getTitle());
            }

            String timeText = newsList.get(position).getPublish_date();
            long parsedTime = TimeAgo.parseDate(timeText);
            String timeAgo = TimeAgo.getTimeAgo(parsedTime, sharedPreferences);

            largeCardHolder.txtLargeCardDate.setText(timeAgo);
            largeCardHolder.txtLargeCardViewCount.setText(""+newsList.get(position).getView_count());

            PicassoCache.getPicassoInstance(c)
                    .load(newsList.get(position).getCover()
                            .replace("http:","https:"))
                    .into(largeCardHolder.imgLargeCardNews);

        }

    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_LARGE;
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    //Card large view holder
    class NewsLargeCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgLargeCardNews;
        private TextView txtLargeCardTitle;
        private TextView txtLargeCardDate;
        private CardView cardlarge;
        private TextView txtLargeCardViewCount;


        public NewsLargeCardHolder(@NonNull View itemView) {
            super(itemView);

            imgLargeCardNews = itemView.findViewById(R.id.imageViewCardLarge);
            txtLargeCardTitle = itemView.findViewById(R.id.textViewTitleLarge);
            txtLargeCardDate = itemView.findViewById(R.id.txtLargeDesignTime);
            txtLargeCardViewCount = itemView.findViewById(R.id.txtLargeDesignViewCount);
            cardlarge = itemView.findViewById(R.id.cardlarge);
            cardlarge.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(c.getApplicationContext(), DetailedNewsActivity.class);
            int position = getAdapterPosition();
            int newsID = newsList.get(position).getId();
            String audio = newsList.get(position).getSound();
            String video_link = newsList.get(position).getVideo_link();
            intent.putExtra("newsID", newsID);
            intent.putExtra("audio", audio);
            intent.putExtra("video_link", video_link);

            c.startActivity(intent);
        }
    }
}





