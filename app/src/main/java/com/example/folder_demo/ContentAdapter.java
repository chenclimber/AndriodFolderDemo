package com.example.folder_demo;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private List<ContentItem> ContentItemList;
    private final Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView content_date;
        FrameLayout content_frameLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content_date = (TextView) itemView.findViewById(R.id.content_date);
            content_frameLayout = (FrameLayout) itemView.findViewById(R.id.content_frameLayout);
        }
    }

    public ContentAdapter(String directory, Context context) {
        ContentList contentList = new ContentList(directory);
        ContentItemList = contentList.getContentItemList();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contentitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContentItem item = ContentItemList.get(position);
        holder.content_date.setText(item.getDate());
        int margin = 20;

        for (int i = 0; i < item.bitMapList.size(); i++) {
            Bitmap bitmap = item.bitMapList.get(i);

            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(300, 200);

            int row = i / 3;
            int col = i % 3;

            int left = col * (300 + margin) + margin;
            int top = row * (200 + margin) + margin;

            params.setMargins(left, top, 0, 0);
            params.gravity = Gravity.TOP | Gravity.START;

            imageView.setLayoutParams(params);
            holder.content_frameLayout.addView(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return ContentItemList.size();
    }
}


