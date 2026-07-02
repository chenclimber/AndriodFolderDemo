package com.example.folder_demo;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;

import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private List<ContentItem> ContentItemList;
    private final Context context;

    public boolean edit_view_mode;

    private List<ImagePositionInfo> image_selected_List;

    private ArrayList<Uri> image_UriList;

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

    static class ImagePositionInfo {
        String imagePath;
        int row;
        int col;
        boolean selected = false;
        CheckBox checkBox;
    }

    public ContentAdapter(String directory, Context context, ActivityResultLauncher<Intent> resultLauncher) {
        ContentList contentList = new ContentList(directory);
        ContentItemList = contentList.getContentItemList();
        this.context = context;
        this.resultLauncher = resultLauncher;
        image_selected_List = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contentitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void
    onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ContentItem item = ContentItemList.get(position);
        holder.content_date.setText(item.getDate());
        int margin = 35;
        //必须清空旧的view，不然会显示错乱
        holder.content_frameLayout.removeAllViews();


        for (int i = 0; i < item.imagePathList.size(); i++) {
            String path = item.imagePathList.get(i);

            ImagePositionInfo imagePositionInfo = new ImagePositionInfo();

            ImageView imageView = new ImageView(context);
            CheckBox checkBox = new CheckBox(context);
            Glide.with(context).load(path).transform(new RoundedCorners(50)).encodeQuality(50).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


            int row = i / 3;
            int col = i % 3;

            int left = col * (300 + margin) + margin;
            int top = row * (200 + margin) + margin;

            imagePositionInfo.imagePath = path;
            imagePositionInfo.row = position;
            imagePositionInfo.col = i;
            imagePositionInfo.checkBox = checkBox;
            imageView.setTag(imagePositionInfo);

            FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(320, 200);
            imageParams.setMargins(left, top, 0, 0);
            imageParams.gravity = Gravity.TOP | Gravity.START;
            imageView.setLayoutParams(imageParams);

            FrameLayout.LayoutParams checkBoxParams = new FrameLayout.LayoutParams(80, 80);
            checkBoxParams.setMargins(left + 240, top, 0, 0);
            checkBoxParams.gravity = Gravity.TOP | Gravity.START;
            checkBox.setLayoutParams(checkBoxParams);
            checkBox.setButtonDrawable(R.drawable.selector_checkbox);
            checkBox.setChecked(check_checkBox_status(imagePositionInfo));
            checkBox.setVisibility(edit_view_mode ? View.VISIBLE : View.GONE);
            checkBox.setClickable(false);


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagePositionInfo imagePositionInfo = (ImagePositionInfo) v.getTag();
                    if (edit_view_mode) {
                        on_image_checkBox_triggered(imagePositionInfo);

                    } else {
                        Intent intent = new Intent(context, ContentImageActivity.class);
                        intent.putExtra("path", imagePositionInfo.imagePath);
                        intent.putExtra("row", imagePositionInfo.row);
                        intent.putExtra("col", imagePositionInfo.col);
                        resultLauncher.launch(intent);

                    }
                }
            });
            holder.content_frameLayout.addView(imageView);
            holder.content_frameLayout.addView(checkBox);
        }
    }

    @Override
    public int getItemCount() {
        return ContentItemList.size();
    }

    public void clear_Image_selected_List() {
        image_selected_List.clear();
    }

    /**
     * 适用于从Adapter中移除单个元素
     *
     */
    public void removeSingleItem(int row, int col) {
        int total_col = ContentItemList.get(row).imagePathList.size();
        if (total_col == 1) {
            //当前行只剩下最后一个元素，删除整行
            ContentItemList.remove(row);
            notifyItemRemoved(row);
        } else {
            ContentItemList.get(row).imagePathList.remove(col);
            notifyItemChanged(row);
        }
    }

    private void on_image_checkBox_triggered(ImagePositionInfo imagePositionInfo) {
        imagePositionInfo.selected = !imagePositionInfo.selected;
        imagePositionInfo.checkBox.setChecked(imagePositionInfo.selected);
        if (imagePositionInfo.selected) {
            image_selected_List.add(imagePositionInfo);
        } else {
            //尝试取消删除
            for (int i = 0; i < image_selected_List.size(); i++) {
                ImagePositionInfo info = image_selected_List.get(i);
                if (info.imagePath.equals(imagePositionInfo.imagePath)) {
                    image_selected_List.remove(i);
                    return;
                }
            }
        }
    }

    private boolean check_checkBox_status(ImagePositionInfo imagePositionInfo) {
        for (ImagePositionInfo info : image_selected_List) {
            if (info.imagePath.equals(imagePositionInfo.imagePath)) {
                return true;
            }
        }
        return false;
    }

    public void send_delete_images_request() {
        if (image_selected_List.isEmpty()) {
            Toast.makeText(context, "请选择图片", Toast.LENGTH_SHORT).show();
            return;
        }

        IntentSender intentSender = null;
        ContentResolver resolver = context.getContentResolver();
        get_image_UriList(resolver);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            intentSender = MediaStore.createWriteRequest(
                    resolver,
                    image_UriList
            ).getIntentSender();
        }
        try {
            ((Activity) context).startIntentSenderForResult(
                    intentSender,
                    1001,
                    null,
                    0,
                    0,
                    0,
                    null
            );
        } catch (IntentSender.SendIntentException e) {
            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
        }


    }

    private void get_image_UriList(ContentResolver resolver) {
        image_UriList = new ArrayList<>();

        String[] projection = {MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DATA +
                " IN (" + create_sql_placeholder(image_selected_List.size()) + ")";
        String[] selectionArgs = extract_paths().toArray(new String[0]);
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );
        if (cursor != null) {
            try {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    image_UriList.add(uri);
                }

            } finally {
                cursor.close();
            }
        }
    }


    private String create_sql_placeholder(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append("?");
            if (i < length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    private List<String> extract_paths() {
        List<String> paths = new ArrayList<>();
        for (ImagePositionInfo info : image_selected_List) {
            paths.add(info.imagePath);
        }
        return paths;
    }

    /**
     * 用于从adpter中删除数据，调用该方法前请先发送删除请求获取权限
     */
    public void delete_images() {
        image_selected_List.sort(
                Comparator.<ImagePositionInfo>comparingInt(info -> info.row)
                        .reversed()
                        .thenComparing(
                                Comparator.<ImagePositionInfo>comparingInt(info -> info.col).reversed()
                        )
        );

        for (ImagePositionInfo info : image_selected_List) {
            removeSingleItem(info.row, info.col);
        }
        image_selected_List.clear();
        ContentResolver resolver = context.getContentResolver();
        for (Uri uri : image_UriList) {
            resolver.delete(uri, null, null);
        }
    }

    public void share_images() {
        ContentResolver resolver = context.getContentResolver();
        image_UriList = new ArrayList<>();
        for (ImagePositionInfo info : image_selected_List) {
            File imageFile = new File(info.imagePath);
            if (imageFile.exists()) {
                Uri uri = FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".FileProvider",
                        imageFile
                );
                image_UriList.add(uri);
            }
        }
        if (image_UriList.isEmpty()) {
            Toast.makeText(context, "请选择图片", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("images/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, image_UriList);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(intent, "分享图片"));

    }

    public void select_all() {
        image_selected_List.clear();
        int row;
        int col;
        for (ContentItem item : ContentItemList) {
            for (int i = 0; i < item.imagePathList.size(); i++) {
                row = i / 3;
                col = i % 3;
                ImagePositionInfo info = new ImagePositionInfo();
                info.imagePath = item.imagePathList.get(i);
                info.row = row;
                info.col = col;
                info.selected = true;
                image_selected_List.add(info);
            }
        }
        notifyDataSetChanged();
    }
}


