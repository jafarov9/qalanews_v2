package com.hajma.qalanews_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hajma.qalanews_android.adapters.NewsAdapter;
import com.hajma.qalanews_android.entity.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsForCategory extends AppCompatActivity {

    private RecyclerView rv_news_for_category;
    private NewsAdapter adapterForCategory;
    private SwipeRefreshLayout swipeRefreshLayoutForCategory;
    private List<News> newsListForCategory;
    private LayoutInflater layoutInflater;
    private static final String URL_NEWS_FOR_CATEGORY = "https://qalanews.com/api/news-search-by-category-id?page=1&deviceId=asdfsdf&languageId=1&categoryId=1";
    private int pagenumber;
    private int itemcount = 20;
    private int visibleitemcount, totalitemcount, pastvisibleitems;
    private boolean loading= true;
    private int categoryID;
    private String categoryName;
    private TextView txtCategoryTitle;
    private ImageButton imgButtonBackToMain;
    private int languageID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_for_category);

        categoryName = getIntent().getStringExtra("categoryName");
        categoryID = getIntent().getIntExtra("categoryID", 0);
        languageID = getIntent().getIntExtra("langID", 1);
        txtCategoryTitle = findViewById(R.id.textViewForCategoryTitle);
        imgButtonBackToMain = findViewById(R.id.image_category_back_to_activity);
        imgButtonBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtCategoryTitle.setText(categoryName);

        swipeRefreshLayoutForCategory = findViewById(R.id.swipe_refresh_layout_for_category);
        swipeRefreshLayoutForCategory.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        //Pagination options
        pagenumber = 1;
        newsListForCategory = new ArrayList<>();
        rv_news_for_category = findViewById(R.id.rv_news_for_category);
        adapterForCategory = new NewsAdapter(this, newsListForCategory, true, getSharedPreferences("lang", MODE_PRIVATE), null);
        final  RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_news_for_category.setLayoutManager(layoutManager);

        rv_news_for_category.setAdapter(adapterForCategory);
        rv_news_for_category.setHasFixedSize(true);

        //reset the value of pagenumber and loading to default to get the data from start after refresing
        swipeRefreshLayoutForCategory.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsListForCategory.clear();
                pagenumber = 1;
                loading = true;
                adapterForCategory.notifyDataSetChanged();
                if(isInternetAvailable()) {
                    loadRecyclerViewData(languageID, pagenumber, 1, String.valueOf(categoryID));
                }else {
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        if(isInternetAvailable()) {
            loadRecyclerViewData(languageID, pagenumber, 1,   String.valueOf(categoryID));
        }else {
            Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
        }



        //add the scrollListener to recycler view and after reaching the end of the list we update the
        // pagenumber inorder to get other set to data ie 11 to 20
        rv_news_for_category.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(oldScrollY < 0) {

                    Log.e("ehhh", "Scrool changed");

                    visibleitemcount = layoutManager.getChildCount();
                    totalitemcount = layoutManager.getItemCount();
                    pastvisibleitems = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                    //if loading is true which means there is data to be fetched from the database
                    if(loading) {
                        if((visibleitemcount + pastvisibleitems) >= totalitemcount) {
                            swipeRefreshLayoutForCategory.setRefreshing(true);
                            loading = false;
                            pagenumber += 1;
                            Log.e("pagetest","Page: "+pagenumber);
                            if(isInternetAvailable()) {
                                loadRecyclerViewData(languageID, pagenumber, 1,  String.valueOf(categoryID));
                            }else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });
    }

    public void loadRecyclerViewData(final int lang_id, final int page, final int deviceID, String categoryID) {

        Log.e("iiii", "ID: "+languageID);
        String url =
                String.format("https://qalanews.com/api/news-search-by-category-id?page=%1$s&deviceId=%2$s&languageId=%3$s&categoryId=%4$s",
                        page, String.valueOf(deviceID), lang_id, categoryID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        swipeRefreshLayoutForCategory.setRefreshing(false);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for(int i = 0;i < jsonArray.length();i++) {
                                JSONObject j = jsonArray.getJSONObject(i);
                                News news = new News(
                                        j.getInt("id"),
                                        j.getString("title"),
                                        j.getString("cover"),
                                        j.getString("sound"),
                                        j.getString("video_link"),
                                        j.getInt("news_type_id"),
                                        j.getInt("category_id"),
                                        j.getBoolean("comment_available"),
                                        j.getInt("view_count"),
                                        j.getString("publish_date"),
                                        j.getString("news_type_name"),
                                        j.getString("category_name"),
                                        ""
                                );


                                Log.e("uxuxu", news.getTitle());
                                newsListForCategory.add(news);
                            }

                            if(!newsListForCategory.isEmpty()) {
                                loading = true;
                                swipeRefreshLayoutForCategory.setRefreshing(false);
                            } else {
                                Toast.makeText(NewsForCategory.this, getResources().getString(R.string.no_load_more_news), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapterForCategory.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public boolean isInternetAvailable() {
        /*try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }*/
        return Variables.isNetworkConnected;
    }
}
