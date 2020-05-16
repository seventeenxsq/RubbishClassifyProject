package com.example.rubbishclassifywork.HelperClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rubbishclassifywork.R;

import java.util.List;

public class ListInfoAdapter extends BaseAdapter {
    private Context context;
    private List<ContentModel> list;
    public ListInfoAdapter(Context context, List<ContentModel> list){
        super();
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        if (list!=null){
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (list!=null){
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold holder;
        if (convertView==null){
            holder=new ViewHold() ;
            convertView= LayoutInflater.from(context).inflate(R.layout.me_list_item,null);
            convertView.setTag(holder);
        }else {
            holder=(ViewHold) convertView.getTag();
        }
        holder.textView=convertView.findViewById(R.id.item_text);
        holder.textView.setText(list.get(position).getText());
        holder.imageView=convertView.findViewById(R.id.item_imageview);
        holder.imageView.setImageResource(list.get(position).getImageView());
        holder.textView_info=convertView.findViewById(R.id.item_text_info);
        holder.textView_info.setText(list.get(position).getText_info());
        return convertView;
    }
    class ViewHold{
        public ImageView imageView;
        public TextView textView;
        public TextView textView_info;
    }
}
