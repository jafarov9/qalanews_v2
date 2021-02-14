package com.hajma.qalanews_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.qalanews_android.DetailedNewsActivity;
import com.hajma.qalanews_android.PicassoCache;
import com.hajma.qalanews_android.R;
import com.hajma.qalanews_android.SignActivity;
import com.hajma.qalanews_android.TimeAgo;
import com.hajma.qalanews_android.entity.Advertisement;
import com.hajma.qalanews_android.entity.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context c;
    private List<News> newsList;
    private List<Advertisement> advList;
    private final static int VIEW_TYPE_NORMAL = 3;
    private final static int VIEW_TYPE_LARGE = 1;
    private boolean haveIsSound;
    private boolean isLogin;
    private SharedPreferences sharedPreferences;

    public NewsAdapter(Context c, List<News> newsArrayList, boolean islogin, SharedPreferences sharedPreferences, List<Advertisement> list) {
        this.c = c;
        this.newsList = newsArrayList;
        this.isLogin = islogin;
        this.sharedPreferences = sharedPreferences;
        this.advList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


         if(viewType == VIEW_TYPE_LARGE) {
            View v = LayoutInflater.from(c).inflate(R.layout.card_large_design, parent, false);
            return new NewsLargeCardHolder(v);
        } else if(viewType == VIEW_TYPE_NORMAL) {
            View v = LayoutInflater.from(c).inflate(R.layout.card_normal_design, parent, false);
            return new NewsNormalCardHolder(v);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.e("recylertesti", "onbind");


        if(holder instanceof NewsNormalCardHolder) {
            NewsNormalCardHolder normalCardHolder = (NewsNormalCardHolder) holder;
            normalCardHolder.textViewCategoryName.setText(newsList.get(position).getCategory_name());
            normalCardHolder.textViewCategoryName
                    .setText(newsList.get(position)
                            .getCategory_name());

            if(isLogin) {
                normalCardHolder.frmLoginPanel.setVisibility(View.GONE);
            }else {
                if(position == 0) {
                    normalCardHolder.frmLoginPanel.setVisibility(View.VISIBLE);
                }else {
                    normalCardHolder.frmLoginPanel.setVisibility(View.GONE);
                }
            }

            if(advList != null) {
                if ((position + 1) % 10 == 0) {
                    normalCardHolder.relativeAdv.setVisibility(View.VISIBLE);
                    PicassoCache.getPicassoInstance(c)
                            .load(advList.get(0).getCover()
                                    .replace("http:", "https:"))
                            .into(normalCardHolder.imgAdvertisement);
                } else {
                    normalCardHolder.relativeAdv.setVisibility(View.GONE);
                }
            }

            Log.e("Postest", "Pos: "+position);



            if(newsList.get(position).getTitle().length() > 70) {
                normalCardHolder.textViewTitleNormal.setText(newsList.get(position).getTitle().substring(0, 65)+"...");
            } else {
                normalCardHolder.textViewTitleNormal.setText(newsList.get(position).getTitle());
            }



            normalCardHolder.txtCountNormal.setText(""+newsList.get(position).getView_count());

            PicassoCache.getPicassoInstance(c)
                    .load(newsList.get(position).getCover()
                            .replace("http:","https:"))
                    .resize(120, 90)
                    .into(normalCardHolder.imageViewNormalCard);
            PicassoCache.getPicassoInstance(c).setLoggingEnabled(true);

            String timeText = newsList.get(position).getPublish_date();
            long parsedTime = TimeAgo.parseDate(timeText);
            String timeAgo = TimeAgo.getTimeAgo(parsedTime, sharedPreferences);

            String soundUrl = newsList.get(position).getSound();
            haveIsSound = soundUrl.isEmpty();

            if(!haveIsSound) {
                normalCardHolder.imageViewAuidoIcon.setVisibility(View.VISIBLE);
            }else {
                normalCardHolder.imageViewAuidoIcon.setVisibility(View.INVISIBLE);
            }

            normalCardHolder.textViewDateTimeNormal.setText(timeAgo);

        }

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
        if((position + 1) % 4 == 0){
                return VIEW_TYPE_LARGE;
        }else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    //Normal cardView holder
    class NewsNormalCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageViewNormalCard;
        private TextView textViewTitleNormal;
        private TextView txtCountNormal;
        private TextView textViewDateTimeNormal;
        private TextView textViewCategoryName;
        private CardView card;
        private ImageView imageViewAuidoIcon;
        private FrameLayout frmLoginPanel;
        private Button btnSignUpPanel;
        private RelativeLayout relativeAdv;
        private ImageView imgAdvertisement;
        private SharedPreferences sharedPreferencesUser;
        private SharedPreferences.Editor editor;

        public NewsNormalCardHolder(@NonNull View itemView) {
            super(itemView);

            sharedPreferencesUser = c.getSharedPreferences("usercontrol", Context.MODE_PRIVATE);
            editor = sharedPreferencesUser.edit();
            imgAdvertisement = itemView.findViewById(R.id.imgAdvertisement);
            relativeAdv = itemView.findViewById(R.id.relative_adv);
            frmLoginPanel = itemView.findViewById(R.id.frmLoginPanel);
            card = itemView.findViewById(R.id.cardnormal);
            imageViewNormalCard = itemView.findViewById(R.id.imageViewNormalCardView);
            textViewDateTimeNormal = itemView.findViewById(R.id.textViewDateTime);
            txtCountNormal = itemView.findViewById(R.id.txtViewCountNormal);
            textViewTitleNormal = itemView.findViewById(R.id.textViewTitle);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            imageViewAuidoIcon = itemView.findViewById(R.id.imageViewAudioControl);

            btnSignUpPanel = frmLoginPanel.findViewById(R.id.btnSignPanel);
            btnSignUpPanel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, SignActivity.class);
                    c.startActivity(intent);
                }
            });

            Log.e("test4", ""+getAdapterPosition());

            card.setOnClickListener(this);


        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(c.getApplicationContext(), DetailedNewsActivity.class);
            int position = getAdapterPosition();
            int newsID = newsList.get(position).getId();

            intent.putExtra("newsID", newsID);
            c.startActivity(intent);
        }
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
            intent.putExtra("newsID", newsID);
            intent.putExtra("audio", audio);
            c.startActivity(intent);
        }
    }
}





