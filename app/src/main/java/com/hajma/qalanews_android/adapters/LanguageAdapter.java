package com.hajma.qalanews_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.qalanews_android.HomeActivity;
import com.hajma.qalanews_android.LocaleHelper;
import com.hajma.qalanews_android.R;
import com.hajma.qalanews_android.entity.Language;

import java.util.ArrayList;

public class LanguageAdapter extends RecyclerView.Adapter {

    private Context c;
    private ArrayList<Language> languageList;

    public LanguageAdapter(Context c, ArrayList<Language> list) {
        this.c = c;
        this.languageList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(c).inflate(R.layout.language_card_design, parent, false);
        return new LanguageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LanguageViewHolder langHolder = (LanguageViewHolder) holder;
        langHolder.txtLanguageName.setText(languageList.get(position).getName().toUpperCase());
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    //LanguageViewHolder
    class LanguageViewHolder extends RecyclerView.ViewHolder {

        private TextView txtLanguageName;
        private CardView cardLanguage;


        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLanguageName = itemView.findViewById(R.id.txtLanguageName);
            cardLanguage = itemView.findViewById(R.id.cardlanguage);

            cardLanguage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int positon = getAdapterPosition();
                    LocaleHelper.setLocale(c,languageList.get(positon).getName());
                    restartHome();
                }
            });
        }
    }

    private void restartHome() {
        Intent intent = new Intent(c, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        c.startActivity(intent);
        ((HomeActivity)c).finish();
    }
}
