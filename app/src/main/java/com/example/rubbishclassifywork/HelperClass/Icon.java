package com.example.rubbishclassifywork.HelperClass;

public class Icon {
    private String title;
    private String iName;

    public Icon() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getiName() {
        return iName;
    }

    public void setiName(String iName) {
        this.iName = iName;
    }

    public Icon(String iId, String iName) {
        this.title = iId;
        this.iName = iName;
    }


}