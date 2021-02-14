package com.hajma.qalanews_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hajma.qalanews_android.PicassoCache;
import com.hajma.qalanews_android.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<String> imageList;

    public ImageAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.card_images_design, parent, false);

        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ImageViewHolder imgHolder = (ImageViewHolder) holder;

        PicassoCache.getPicassoInstance(context)
                .load(imageList.get(position)
                        .replace("http:","https:"))
                .into(imgHolder.imgCardImages);


    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    //ImageViewHolder class
    class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgCardImages;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCardImages = itemView.findViewById(R.id.imgCardImages);
        }


    }


}
