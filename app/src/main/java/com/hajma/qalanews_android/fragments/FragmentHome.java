package com.hajma.qalanews_android.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hajma.qalanews_android.Constants;
import com.hajma.qalanews_android.LocaleHelper;
import com.hajma.qalanews_android.MySingleton;
import com.hajma.qalanews_android.R;
import com.hajma.qalanews_android.Variables;
import com.hajma.qalanews_android.adapters.NewsAdapter;
import com.hajma.qalanews_android.entity.Advertisement;
import com.hajma.qalanews_android.entity.News;
import com.hajma.qalanews_android.eventbus.DataEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {

    private TextView txtCardtoLogin;
    private RecyclerView rvNews;
    private NewsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<News> newsList;
    private List<Advertisement> advertisements;
    private LayoutInflater layoutInflater;
    private static final String URL_NEWS = "https://qalanews.com/api/news-search-by-title?page=1&deviceId=asdfsdf&languageId=1&search=";
    private int pagenumber;
    private int itemcount = 20;
    private int visibleitemcount, totalitemcount, pastvisibleitems;
    private boolean loading= true;
    private String searchText = "";
    private boolean mIsRefreshing = false;
    private boolean isLogin;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int languageID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        String language = LocaleHelper.getPersistedData(getActivity(), "az");

        if(language.equals("az")) {
            languageID = Constants.LANGUAGE_AZ;
        } else if(language.equals("ru")) {
            languageID = Constants.LANGUAGE_RU;
        }


        //Shared Preferences options
        sharedPreferences = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isLogin = sharedPreferences.getBoolean("logined", false);

        txtCardtoLogin = view.findViewById(R.id.txtFrameToLogin);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        //Pagination options
        pagenumber = 1;
        newsList = new ArrayList<>();
        advertisements = new ArrayList<>();
        rvNews = view.findViewById(R.id.rv_news);
        adapter = new NewsAdapter(getContext(), newsList, isLogin, getActivity().getSharedPreferences("lang", Context.MODE_PRIVATE), advertisements);
        final  RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvNews.setLayoutManager(layoutManager);

        rvNews.setAdapter(adapter);
        rvNews.setHasFixedSize(true);
        //reset the value of pagenumber and loading to default to get the data from start after refresing
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsList.clear();
                pagenumber = 1;
                loading = true;
                adapter.notifyDataSetChanged();
                if(isInternetAvailable()) {
                    loadAdvertisement();
                    loadRecyclerViewData(languageID, pagenumber, 1, searchText);
                }else {
                    Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });



        if(isInternetAvailable()) {
            loadAdvertisement();
            loadRecyclerViewData(languageID, pagenumber, 1, searchText);
        }else {
            Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
        }

        //add the scrollListener to recycler view and after reaching the end of the list we update the
        // pagenumber inorder to get other set to data ie 11 to 20
        rvNews.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(oldScrollY < 0) {

                    Log.e("ehhh", ""+ Variables.isNetworkConnected);



                    visibleitemcount = layoutManager.getChildCount();
                    totalitemcount = layoutManager.getItemCount();
                    pastvisibleitems = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                    //if loading is true which means there is data to be fetched from the database
                    if(loading) {
                        if((visibleitemcount + pastvisibleitems) >= totalitemcount) {
                            swipeRefreshLayout.setRefreshing(true);
                            loading = false;
                            pagenumber += 1;
                            Log.e("pagetest","Page: "+pagenumber);
                            if(isInternetAvailable()) {
                                loadAdvertisement();
                                loadRecyclerViewData(languageID, pagenumber, 1, searchText);
                            }else {
                                Toast.makeText(getContext(), getResources().getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

        return view;
    }

    public void loadRecyclerViewData(final int lang_id, final int page, final int deviceID, final String searchWord) {

        String url =
                String.format("https://qalanews.com/api/news-search-by-title?page=%1$s&deviceId=%2$s&languageId=%3$s&search=%4$s",
                        page, String.valueOf(deviceID), String.valueOf(lang_id), searchWord);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        swipeRefreshLayout.setRefreshing(false);
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
                                swipeRefreshLayout.setRefreshing(false);
                            } else {
                                Toast.makeText(getContext(), "No load more data", Toast.LENGTH_LONG).show();
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
        };
        RequestQueue requestQueue = MySingleton.getInstance(getActivity().getApplicationContext()).getmRequestQueue();
        requestQueue.add(stringRequest);
    }

    public void loadAdvertisement() {
        String url = "https://qalanews.com/api/advertisement";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Advertisement advertisement = new Advertisement();
                    advertisement.setId(jsonObject.getJSONObject("advertisement").getInt("id"));
                    advertisement.setCover(jsonObject.getJSONObject("advertisement").getString("cover"));
                    advertisement.setTitle(jsonObject.getJSONObject("advertisement").getString("title"));
                    advertisement.setSite(jsonObject.getJSONObject("advertisement").getString("site"));
                    advertisement.setMobile(jsonObject.getJSONObject("advertisement").getString("mobile"));

                    advertisements.add(advertisement);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


    public boolean isInternetAvailable() {
        return Variables.isNetworkConnected;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void onSearchQuery(DataEvent.SearchQuery event) {
        searchText = event.getSearchquery();
    }

    //Check internet connection
    public void checkConnection() {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getActivity()
                            .getApplicationContext()
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            connectivityManager.registerNetworkCallback(builder.build(),new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(Network network) {
                            Variables.isNetworkConnected = true; // Global Static Variable
                        }
                        @Override
                        public void onLost(Network network) {
                            Variables.isNetworkConnected = false; // Global Static Variable
                        }
                    }

            );
            Variables.isNetworkConnected = false;
        }catch (Exception e){
            Variables.isNetworkConnected = false;
        }

    }

}
