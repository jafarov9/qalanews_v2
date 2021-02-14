package com.hajma.qalanews_android.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hajma.qalanews_android.MySingleton;
import com.hajma.qalanews_android.R;
import com.hajma.qalanews_android.SavedNewsRecyclerView;
import com.hajma.qalanews_android.adapters.NewsAdapterSaved;
import com.hajma.qalanews_android.entity.News;
import com.hajma.qalanews_android.eventbus.DataEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentSaved extends Fragment {

    private View emptyList;
    private SavedNewsRecyclerView savedRecyclerViewNews;
    private NewsAdapterSaved adapter;
    private ArrayList<News> newsList;
    private static final String URL_NEWS_SAVED = "https://qalanews.com/api/saved-news?";
    private int pagenumber;
    private int visibleitemcount, totalitemcount, pastvisibleitems;
    private boolean loading= true;
    private boolean mIsRefreshing = false;
    private SwipeRefreshLayout swpRefreshLt;
    private String token;
    private SharedPreferences sharedPreferences;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fargment_saved, container, false);

        sharedPreferences = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        swpRefreshLt = v.findViewById(R.id.swp_refresh_lt);
        swpRefreshLt.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        //Pagination options
        pagenumber = 1;
        newsList = new ArrayList<>();
        savedRecyclerViewNews = (SavedNewsRecyclerView)v.findViewById(R.id.rv_saved_news);
        adapter = new NewsAdapterSaved(getContext(), newsList, getActivity().getSharedPreferences("lang", Context.MODE_PRIVATE));
        final  SavedNewsRecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        savedRecyclerViewNews.setLayoutManager(layoutManager);

        savedRecyclerViewNews.setAdapter(adapter);
        savedRecyclerViewNews.setHasFixedSize(true);
        emptyList = v.findViewById(R.id.emptyList);
        savedRecyclerViewNews.setEmptyView(emptyList);

        //reset the value of pagenumber and loading to default to get the data from start after refresing
        swpRefreshLt.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsList.clear();
                pagenumber = 1;
                loading = true;
                adapter.notifyDataSetChanged();
                if(isInternetAvailable()) {
                    loadRecyclerViewData(pagenumber, 1, token);
                }
            }
        });

        if(isInternetAvailable()) {
            loadRecyclerViewData(pagenumber, 1, token);
        }

        //add the scrollListener to recycler view and after reaching the end of the list we update the
        // pagenumber inorder to get other set to data ie 11 to 20
        savedRecyclerViewNews.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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
                            swpRefreshLt.setRefreshing(true);
                            loading = false;
                            pagenumber += 1;
                            Log.e("pagetest","Page: "+pagenumber);
                            if(isInternetAvailable()) {
                                loadRecyclerViewData(pagenumber, 1, token);
                            }
                        }
                    }
                }
            }
        });

        return v;
    }


    public void loadRecyclerViewData(final int page, final int deviceID, String token) {

        Log.e("pgtest", "Page: "+page);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URL_NEWS_SAVED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        swpRefreshLt.setRefreshing(false);
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



                                newsList.add(news);
                            }

                            if(!newsList.isEmpty()) {
                                loading = true;
                                swpRefreshLt.setRefreshing(false);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("page", String.valueOf(page));
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        RequestQueue requestQueue = MySingleton.getInstance(getActivity().getApplicationContext()).getmRequestQueue();
        requestQueue.add(stringRequest);
    }

    public boolean isInternetAvailable() {
        /*try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }*/
        return true;
    }

    /*private ArrayList<News> getAllSavedNews() {
        ArrayList<News> savedNews = new ArrayList<>();
        String[] projection = {"id", "title", "cover", "sound",
                "video_link", "news_type_id", "category_id", "comment_available",
                "view_count", "publish_date", "news_type_name", "category_name", "content"};


        Cursor cursor = contentResolver.query(CONTENT_URI, projection, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                News news = new News();
                news.setId(cursor.getInt(cursor.getColumnIndex("id")));
                news.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                news.setCover(cursor.getString(cursor.getColumnIndex("cover")));
                news.setSound(cursor.getString(cursor.getColumnIndex("sound")));
                news.setVideo_link(cursor.getString(cursor.getColumnIndex("video_link")));
                news.setNews_type_id(cursor.getInt(cursor.getColumnIndex("news_type_id")));
                news.setCategory_id(cursor.getInt(cursor.getColumnIndex("category_id")));

                if(cursor.getInt(cursor.getColumnIndex("comment_available")) == 1) {
                    news.setComment_avialible(true);
                }else {
                    news.setComment_avialible(false);
                }
                news.setView_count(cursor.getInt(cursor.getColumnIndex("view_count")));
                news.setPublish_date(cursor.getString(cursor.getColumnIndex("publish_date")));
                news.setNews_type_name(cursor.getString(cursor.getColumnIndex("news_type_name")));
                news.setCategory_name(cursor.getString(cursor.getColumnIndex("category_name")));
                news.setContent(cursor.getString(cursor.getColumnIndex("content")));
                newsList.add(news);
            }
        }
        return newsList;
    }

    private void dataUpdate() {
        newsList.clear();
        newsList = getAllSavedNews();
        adapter.update(newsList);
    }

    */

}
