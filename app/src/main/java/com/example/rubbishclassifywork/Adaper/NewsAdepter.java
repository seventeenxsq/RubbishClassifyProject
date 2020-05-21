package com.example.rubbishclassifywork.Adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rubbishclassifywork.HelperClass.NewsItem;
import com.example.rubbishclassifywork.R;

import java.util.List;

public class NewsAdepter extends ArrayAdapter<NewsItem> {

    private int resourceId;


    public NewsAdepter(@NonNull Context context, int resource, @NonNull List<NewsItem> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @NonNull
    @Override//适配布局改写getView方法
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NewsItem newsItem=getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView newsname=view.findViewById(R.id.news_name);
        TextView newfrom=view.findViewById(R.id.news_from);
        ImageView newpic=view.findViewById(R.id.news_pic);

        assert newsItem != null;
        newsname.setText(newsItem.getNewsname());
        newfrom.setText(newsItem.getNewsfrom());
        newpic.setImageResource(newsItem.getNewspictureurl());
        return view;
    }
}
