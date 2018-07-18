package com.example.kalyan.tutorial;

/**
 * Created by KALYAN on 27-01-2018.
 */

public class News {
    String title,desc,url,urlImg;

    public News(String title, String desc, String url, String urlImg) {
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.urlImg = urlImg;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlImg() {
        return urlImg;
    }
}
