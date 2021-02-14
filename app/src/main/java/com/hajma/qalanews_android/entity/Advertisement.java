package com.hajma.qalanews_android.entity;

public class Advertisement {

    private int id;
    private String cover;
    private String title;
    private String site;
    private String mobile;

    public Advertisement() {
    }

    public Advertisement(int id, String cover, String title, String site, String mobile) {
        this.id = id;
        this.cover = cover;
        this.title = title;
        this.site = site;
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
