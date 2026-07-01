package com.example.folder_demo;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private List<ContentItem> ContentItemList;
    private final Context context;

    public boolean edit_view_mode;

    private ActivityResultLauncher<Intent> resultLauncher;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView content_date;
        FrameLayout content_frameLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content_date = (TextView) itemView.findViewById(R.id.content_date);
            content_frameLayout = (FrameLayout) itemView.findViewById(R.id.content_frameLayout);
        }
    }

    static class ImagePositionInfo{
        String imagePath;
        int row;
        int col;
    }

    public ContentAdapter(String directory, Context context,ActivityResultLauncher<Intent> resultLauncher) {
        ContentList contentList = new ContentList(directory);
        ContentItemList = contentList.getContentItemList();
        this.context = context;
        this.resultLauncher = resultLauncher;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contentitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ContentItem item = ContentItemList.get(position);
        holder.content_date.setText(item.getDate());
        int margin = 35;

        for (int i = 0; i < item.imagePathList.size(); i++) {
            String path = item.imagePathList.get(i);
            ImagePositionInfo imagePositionInfo = new ImagePositionInfo();

            ImageView imageView = new ImageView(context);
            Glide.with(context).load(path).transform(new RoundedCorners(50)).encodeQuality(50).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(320, 200);

            int row = i / 3;
            int col = i % 3;

            int left = col * (300 + margin) + margin;
            int top = row * (200 + margin) + margin;

            params.setMargins(left, top, 0, 0);
            params.gravity = Gravity.TOP | Gravity.START;

            imageView.setLayoutParams(params);

            imagePositionInfo.imagePath = path;
            imagePositionInfo.row = position;
            imagePositionInfo.col = i;
            imageView.setTag(imagePositionInfo);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(edit_view_mode){

                    }
                    else {
                        Intent intent = new Intent(context, ContentImageActivity.class);
                        ImagePositionInfo imagePositionInfo = (ImagePositionInfo)v.getTag();
                        intent.putExtra("path",imagePositionInfo.imagePath);
                        intent.putExtra("row",imagePositionInfo.row);
                        intent.putExtra("col",imagePositionInfo.col);
//                        context.startActivity(intent);
                        resultLauncher.launch(intent);

                    }
                }
            });
            holder.content_frameLayout.addView(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return ContentItemList.size();
    }

    public void removeItem(int row,int col){
        int total_row = ContentItemList.size();
        int total_col = ContentItemList.get(row).imagePathList.size();
        if(total_col == 1){
            //当前行只剩下最后一个元素，删除整行
            ContentItemList.remove(row);
            notifyItemRemoved(row);
        }
        else{
            ContentItemList.get(row).imagePathList.remove(col);
            notifyItemChanged(row);
        }
    }
}


