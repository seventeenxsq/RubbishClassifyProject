package com.example.rubbishclassifywork.HelperClass;

public class ContentModel {
    private int imageView;
    private int imageView_next;
    private String text;
    private int id;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public ContentModel(){
    }
    public ContentModel(int imageView, String text, int imageView_next, int id){
        this.imageView=imageView;
        this.text=text;
        this.imageView_next=imageView_next;
        this.id=id;
    }
    public int getImageView(){
        return imageView;
    }
    public void setImageView(int imageView){this.imageView=imageView;}
    public int getImageViewnext(){
        return imageView_next;
    }
    public void setImageViewnext(int imageView_next){this.imageView_next=imageView_next;}
    public String getText(){return text;}
    public void setText(String text){this.text=text;}
}
