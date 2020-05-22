package com.example.rubbishclassifywork.HelperClass;

public class NewsItem {
    private String newsname;

    private String newsfrom;

    private int newspictureurl;

    private String newsurl;

    public NewsItem(String newsname, String newsfrom, String newsurl) {
        this.newsname = newsname;
        this.newsfrom = newsfrom;
        this.newsurl = newsurl;
    }

    public NewsItem(String newsname, String newsfrom, int pictureurl, String newsurl) {
        this.newsname = newsname;
        this.newsfrom = newsfrom;
        this.newspictureurl = pictureurl;
        this.newsurl = newsurl;
    }

    public String getNewsname() {
        return newsname;
    }

    public void setNewsname(String newsname) {
        this.newsname = newsname;
    }

    public String getNewsfrom() {
        return newsfrom;
    }

    public void setNewsfrom(String newsfrom) {
        this.newsfrom = newsfrom;
    }

    public int getNewspictureurl() {
        return newspictureurl;
    }

    public void setPictureurl(int pictureurl) {
        this.newspictureurl = pictureurl;
    }

    public String getNewsurl() {
        return newsurl;
    }

    public void setNewsurl(String newsurl) {
        this.newsurl = newsurl;
    }
}
