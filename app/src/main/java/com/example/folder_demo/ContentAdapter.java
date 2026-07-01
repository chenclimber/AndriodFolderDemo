package com.example.folder_demo;


import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

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
        int margin = 35;

        for (int i = 0; i < item.imagePathList.size(); i++) {
            String path = item.imagePathList.get(i);

            ImageView imageView = new ImageView(context);
            Glide.with(context).load(path).transform(new RoundedCorners(50)).encodeQuality(50).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setTag(path);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(320, 200);

            int row = i / 3;
            int col = i % 3;

            int left = col * (300 + margin) + margin;
            int top = row * (200 + margin) + margin;

            params.setMargins(left, top, 0, 0);
            params.gravity = Gravity.TOP | Gravity.START;

            imageView.setLayoutParams(params);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ContentImageActivity.class);
                    String path = (String) v.getTag();
                    intent.putExtra("path",path);
                    context.startActivity(intent);
                }
            });
            holder.content_frameLayout.addView(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return ContentItemList.size();
    }
}


