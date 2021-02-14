package com.hajma.qalanews_android.entity;

public class News {

    private int id;
    private String title;
    private String cover;
    private String sound;
    private String video_link;
    private int news_type_id = 2;
    private int category_id;
    private boolean comment_avialible;
    private int view_count;
    private String publish_date;
    private String news_type_name;
    private String category_name;
    private String content;

    public News(int id, String title, String cover, String sound, String video_link, int news_type_id, int category_id, boolean comment_avialible, int view_count, String publish_date, String news_type_name, String category_name, String content) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.sound = sound;
        this.video_link = video_link;
        this.news_type_id = news_type_id;
        this.category_id = category_id;
        this.comment_avialible = comment_avialible;
        this.view_count = view_count;
        this.publish_date = publish_date;
        this.news_type_name = news_type_name;
        this.category_name = category_name;
        this.content = content;
    }

    public News() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getVideo_link() {
        return video_link;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public int getNews_type_id() {
        return news_type_id;
    }

    public void setNews_type_id(int news_type_id) {
        this.news_type_id = news_type_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public boolean isComment_avialible() {
        return comment_avialible;
    }

    public void setComment_avialible(boolean comment_avialible) {
        this.comment_avialible = comment_avialible;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public String getNews_type_name() {
        return news_type_name;
    }

    public void setNews_type_name(String news_type_name) {
        this.news_type_name = news_type_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", sound='" + sound + '\'' +
                ", video_link='" + video_link + '\'' +
                ", news_type_id=" + news_type_id +
                ", category_id=" + category_id +
                ", comment_avialible=" + comment_avialible +
                ", view_count=" + view_count +
                ", publish_date='" + publish_date + '\'' +
                ", news_type_name='" + news_type_name + '\'' +
                ", category_name='" + category_name + '\'' +
                '}';
    }
}
