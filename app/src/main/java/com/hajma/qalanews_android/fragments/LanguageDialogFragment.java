package com.hajma.qalanews_android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hajma.qalanews_android.HomeActivity;
import com.hajma.qalanews_android.R;
import com.hajma.qalanews_android.adapters.LanguageAdapter;
import com.hajma.qalanews_android.entity.Language;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LanguageDialogFragment extends DialogFragment {

    private RecyclerView rvLanguage;
    private LanguageAdapter adapter;
    private ArrayList<Language> languageList;
    private ProgressBar languageProgressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v  = inflater.inflate(R.layout.language_dialog_fragment, container, false);

        languageProgressBar = v.findViewById(R.id.progressBarLanguage);
        rvLanguage = v.findViewById(R.id.rv_language);
        rvLanguage.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLanguage.setHasFixedSize(true);

        languageList = new ArrayList<>();

        loadLanguageData();

        /*btnLanguageRu = v.findViewById(R.id.btnRuLanguage);
        btnLanguageRu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocaleHelper.setLocale(getActivity(),"ru");
                restartHome();
                dismiss();
            }
        });
*/
        return v;
    }

    private void loadLanguageData() {

        languageProgressBar.setIndeterminate(true);

        String url = "https://qalanews.com/api/get-languages";

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    languageProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for(int i = 0;i < jsonArray.length();i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Language language =
                                new Language(object.getInt("id"), object.getString("name"));
                        languageList.add(language);
                    }

                    adapter = new LanguageAdapter(getContext(), languageList);
                    rvLanguage.setAdapter(adapter);

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

    private void restartHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
