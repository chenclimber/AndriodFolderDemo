package com.example.folder_demo;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button title_exit;
    private TextView title_select_all_none;
    private Button title_edit;
    private TextView title_cancel;
    private LinearLayout tool_image;
    private LinearLayout tool_video;
    private RecyclerView content_recyclerView;
    private LinearLayout status_bar;
    private TextView status_share;
    private TextView status_delete;

    private ContentAdapter adapter;

    private ActivityResultLauncher<Intent> resultLauncher;
    /**
     * 标题栏-编辑按钮触发时，修改显示模式标志位
     */
    private boolean edit_view_mode;
    /**
     * 用于控制“图片”“视频”图标颜色,false选中图片，ture选中视频
     *
     */
    private boolean content_view_mode;
    private boolean title_select_all_none_selected;

    //权限获取部分
    private final String[] PermissonMethods = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int RequestExternalStorage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_item();
        checkPermisson();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/hongshi/IMAGE";
        adapter = new ContentAdapter(path, this, resultLauncher);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        content_recyclerView.setLayoutManager(linearLayoutManager);
        content_recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestExternalStorage) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init_item() {
        title_exit = (Button) findViewById(R.id.title_exit);
        title_select_all_none = (TextView) findViewById(R.id.title_select_all_none);
        title_edit = (Button) findViewById(R.id.title_edit);
        title_cancel = (TextView) findViewById(R.id.title_cancel);
        tool_image = (LinearLayout) findViewById(R.id.tool_image);
        tool_video = (LinearLayout) findViewById(R.id.tool_video);
        content_recyclerView = (RecyclerView) findViewById(R.id.content_recyclerview);
        status_bar = (LinearLayout) findViewById(R.id.status_bar);
        status_share = (TextView) findViewById(R.id.status_share);
        status_delete = (TextView) findViewById(R.id.status_delete);

        bind_onclickListener();
        edit_view_mode = false;
        title_select_all_none_selected = true;
        content_view_mode = false;
        change_edit_view_mode();
        change_content_view_mode();
        create_resultLauncher();
    }

    private void create_resultLauncher() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == RESULT_OK) {
                            Intent intent = o.getData();
                            if (intent != null) {
                                int row = intent.getIntExtra("row", -1);
                                int col = intent.getIntExtra("col", -1);
                                if (row != -1 && col != -1) {
                                    adapter.removeItem(row,col);
                                }
                            }
                        }
                    }
                }
        );
    }

    private void change_edit_view_mode() {
        if (edit_view_mode) {
            title_exit.setVisibility(View.GONE);
            title_select_all_none.setVisibility(View.VISIBLE);
            title_edit.setVisibility(View.GONE);
            title_cancel.setVisibility(View.VISIBLE);
            status_bar.setVisibility(View.VISIBLE);
        } else {
            title_exit.setVisibility(View.VISIBLE);
            title_select_all_none.setVisibility(View.GONE);
            title_edit.setVisibility(View.VISIBLE);
            title_cancel.setVisibility(View.GONE);
            status_bar.setVisibility(View.GONE);
        }
    }

    private void change_content_view_mode() {
        if (content_view_mode) {
            //选中视频
            tool_video.setSelected(true);
            tool_image.setSelected(false);
        } else {
            //选中图片
            tool_image.setSelected(true);
            tool_video.setSelected(false);
        }
    }

    private void bind_onclickListener() {
        title_exit.setOnClickListener(this);
        title_select_all_none.setOnClickListener(this);
        title_edit.setOnClickListener(this);
        title_cancel.setOnClickListener(this);
        tool_image.setOnClickListener(this);
        tool_video.setOnClickListener(this);
        status_share.setOnClickListener(this);
        status_delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.title_exit) {
            finish();
        } else if (id == R.id.title_edit) {
            edit_view_mode = !edit_view_mode;
            adapter.edit_view_mode = true;
            change_edit_view_mode();
        } else if (id == R.id.title_cancel) {
            edit_view_mode = !edit_view_mode;
            adapter.edit_view_mode = false;
            change_edit_view_mode();
        } else if (id == R.id.title_select_all_none) {
            on_title_select_all_none_clicked();
        } else if (id == R.id.tool_image) {
            content_view_mode = false;
            change_content_view_mode();
        } else if (id == R.id.tool_video) {
            content_view_mode = true;
            change_content_view_mode();
        }
    }

    private void on_title_select_all_none_clicked() {
        title_select_all_none_selected = !title_select_all_none_selected;
        if (title_select_all_none_selected) {
            //从“全选”切换为“全不选”，当前执行逻辑为“全选”
            title_select_all_none.setText("全不选");
        } else {
            //从“全不选”切换为“全选”，当前执行逻辑为“全选”
            title_select_all_none.setText("全选");
        }
    }

    private void checkPermisson() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("请开启文件访问权限")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(MainActivity.this, PermissonMethods, RequestExternalStorage);
                        }
                    }).create();
            dialog.show();
        }
    }

}