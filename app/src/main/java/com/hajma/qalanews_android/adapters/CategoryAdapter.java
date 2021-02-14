package com.hajma.qalanews_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.qalanews_android.NewsForCategory;
import com.hajma.qalanews_android.R;
import com.hajma.qalanews_android.entity.Category;

import java.util.List;
import java.util.Random;

public class CategoryAdapter extends RecyclerView.Adapter {

    private Context c;
    private List<Category> categoryList;
    private boolean colorAttached = false;
    private int randomImage;
    private int images[] = {
            R.drawable.categoryimage0,
            R.drawable.categoryimage1,
            R.drawable.categoryimage2,
            R.drawable.categoryimage3,
    };
    private int languageID;

    public CategoryAdapter(Context c, List<Category> categoryList, int langugageID) {
        this.c = c;
        this.categoryList = categoryList;
        this.languageID = langugageID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.card_category_design, parent, false);
        return new CardCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CardCategoryViewHolder cardHolder = (CardCategoryViewHolder) holder;

            int high = 4;
            int low = 0;
            randomImage = new Random().nextInt(high - low) + low;


        cardHolder.txtCategoryName.setText(categoryList.get(position).getCategoryName());
        cardHolder.imgCategory.setBackgroundResource(images[randomImage]);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CardCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgCategory;
        TextView txtCategoryName;
        TextView txtCountNewsForCategory;

        public CardCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = new Intent(c, NewsForCategory.class);
            intent.putExtra("categoryID", categoryList.get(position).getCategoryID());
            intent.putExtra("categoryName", categoryList.get(position).getCategoryName());
            intent.putExtra("langID", languageID);
            c.startActivity(intent);
        }
    }
}

