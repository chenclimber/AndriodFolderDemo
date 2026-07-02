package com.example.folder_demo;

import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.io.File;

public class ContentImageActivity extends AppCompatActivity implements View.OnClickListener {

    private String imagePath;
    private int row;
    private int col;
    private Button content_image_exit;
    private TextView content_image_title;
    private ImageView content_image_body;
    private TextView status_share;
    private TextView status_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_image);
        item_init();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.content_image_exit) {
            finish();
        } else if (id == R.id.status_share) {
            on_status_share_clicked();
        } else if (id == R.id.status_delete) {
            on_status_delete_clicked();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001){
            if(resultCode == RESULT_OK){
                on_status_delete_clicked();
            }
        }
    }

    private void item_init() {
        Intent intent = getIntent();
        imagePath = intent.getStringExtra("path");
        row = intent.getIntExtra("row",-1);
        col = intent.getIntExtra("col",-1);


        content_image_exit = findViewById(R.id.content_image_exit);
        content_image_title = findViewById(R.id.content_image_title);
        content_image_body = findViewById(R.id.content_image_body);
        status_share = findViewById(R.id.status_share);
        status_delete = findViewById(R.id.status_delete);

        content_image_title.setText(parseName(imagePath));
        Glide.with(this).load(imagePath).into(content_image_body);

        content_image_exit.setOnClickListener(this);
        status_share.setOnClickListener(this);
        status_delete.setOnClickListener(this);
    }

    private String parseName(String path) {
//        /storage/emulated/0/DCIM/hongshi/IMAGE/20260104112412192_104600782_chn0_screenshot.jpg
        String[] raw = path.split("/");
        return raw[raw.length - 1];
    }

    private void on_status_share_clicked() {
        File imageFile = new File(imagePath);
        Uri ImageUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".FileProvider",
                imageFile
        );
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("images/*");
        intent.putExtra(Intent.EXTRA_STREAM, ImageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "分享图片"));
    }

    private void on_status_delete_clicked() {
        try {
            delete_image();
        }
        catch (IntentSender.SendIntentException e){
            Toast.makeText(ContentImageActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
        }

    }

    private void delete_image() throws IntentSender.SendIntentException {
        ContentResolver resolver = getContentResolver();
        Uri imageUri = null;
        Log.d("测试", "delete_image: "+MediaStore.Images.Media.DATA + "=?");
        String[] projection = {MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DATA + "=?";
        String[] selectionArgs = {imagePath};
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            cursor.close();
        }
        if (imageUri != null) {
            try {
                resolver.delete(imageUri, null, null);
                Intent intent = new Intent();
                intent.putExtra("row",row);
                intent.putExtra("col",col);
                setResult(RESULT_OK,intent);
                finish();
            } catch (SecurityException e) {
                if (e instanceof RecoverableSecurityException) {
                    startIntentSenderForResult(
                            ((RecoverableSecurityException) e).getUserAction().getActionIntent().getIntentSender(),
                            1001,
                            null,
                            0,
                            0,
                            0,
                            null
                    );
                }
            }
        }
    }
}