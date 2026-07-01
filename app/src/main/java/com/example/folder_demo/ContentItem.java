package com.example.folder_demo;

import java.io.File;
import java.util.ArrayList;

import java.util.List;


public class ContentItem {
    private String date;
    public List<String> imagePathList;

    public ContentItem() {
        imagePathList = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}

class ContentList {
    private List<ContentItem> ContentItemList;
    private String directory;


    public ContentList(String directory) {

        this.directory = directory;
    }

    public List<ContentItem> getContentItemList() {
        GetContent();
        return ContentItemList;
    }

    private void GetContent() {
        ContentItemList = new ArrayList<>();
        File dir = new File(directory);
        File[] files = dir.listFiles();
        if (files != null) {
            String oldDate = parseDate(files[0].getName());
            ContentItem item = new ContentItem();
            for (File file : files) {
                String Date = parseDate(file.getName());
                String path = file.getPath();
                if (Date.equals(oldDate)) {
                    item.setDate(Date);
                    item.imagePathList.add(path);
                } else {
                    ContentItemList.add(item);
                    item = new ContentItem();
                    oldDate = Date;
                    item.setDate(Date);
                    item.imagePathList.add(path);
                }
                if (file == files[files.length - 1]) {
                    ContentItemList.add(item);
                }
            }
        }
    }

    private String parseDate(String fileName) {
        //20260319142218801_104600976_screenshot.jpg
        // 时间部分: 2026 03 19 14 22 18 801 (年月日时分秒毫秒)
        String year = fileName.substring(0, 4);      // 2026
        String month = fileName.substring(4, 6);     // 03
        String day = fileName.substring(6, 8);       // 19
        return year + "-" + month + "-" + day;
    }


}