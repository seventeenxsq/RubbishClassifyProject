package com.example.rubbishclassifywork.HelperClass;

public class ContentModel {
    private int imageView;
    private int imageView_next;
    private String text,text_info;
    private int id;

    public int getImageView_next() {
        return imageView_next;
    }

    public void setImageView_next(int imageView_next) {
        this.imageView_next = imageView_next;
    }

    public String getText_info() {
        return text_info;
    }

    public void setText_info(String text_info) {
        this.text_info = text_info;
    }

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
    public ContentModel(int imageView, String text, String text_info, int id){
        this.imageView=imageView;
        this.text=text;
        this.text_info=text_info;
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
