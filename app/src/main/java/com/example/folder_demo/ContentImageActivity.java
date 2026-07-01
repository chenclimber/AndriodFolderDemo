package com.example.folder_demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class ContentImageActivity extends AppCompatActivity implements View.OnClickListener {

    private String imagePath;
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
        int id= v.getId();
        if(id == R.id.content_image_exit){
            finish();
        }
    }

    private void item_init(){
        imagePath = getIntent().getStringExtra("path");
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

    private String parseName(String path){
//        /storage/emulated/0/DCIM/hongshi/IMAGE/20260104112412192_104600782_chn0_screenshot.jpg
        String[] raw = path.split("/");
        return raw[raw.length -1];
    }
}