package com.hajma.qalanews_android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hajma.qalanews_android.Constants;
import com.hajma.qalanews_android.LocaleHelper;
import com.hajma.qalanews_android.R;
import com.hajma.qalanews_android.adapters.CategoryAdapter;
import com.hajma.qalanews_android.entity.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentCategory extends Fragment {

    private RecyclerView rvCategory;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private ProgressBar progressBarCategoryLoading;
    private static String URL_CATEGORIES = "https://qalanews.com/api/get-categories?languageId=1";
    private int languageID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_category, container, false);

        String language = LocaleHelper.getPersistedData(getActivity(), "az");

        if(language.equals("az")) {
            languageID = Constants.LANGUAGE_AZ;
        } else if(language.equals("ru")) {
            languageID = Constants.LANGUAGE_RU;
        }

        progressBarCategoryLoading = v.findViewById(R.id.progressBarCategryLoading);

        rvCategory = v.findViewById(R.id.rv_category);
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(new LinearLayoutManager(getContext()));

        categoryList = new ArrayList<>();

        loadRecyclerViewData();
        return v;
    }

    private void loadRecyclerViewData() {

        progressBarCategoryLoading.setIndeterminate(true);

        String url = String.format("https://qalanews.com/api/get-categories?languageId=%1$s",
                languageID);


        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            progressBarCategoryLoading.setVisibility(ProgressBar.INVISIBLE);

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            for(int i = 0;i < jsonArray.length();i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Category category =
                                        new Category(object.getInt("id"), object.getString("name"));
                                categoryList.add(category);
                            }

                            adapter = new CategoryAdapter(getContext(), categoryList, languageID);
                            rvCategory.setAdapter(adapter);
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


}
