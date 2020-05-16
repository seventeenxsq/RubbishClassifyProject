package com.example.rubbishclassifywork.HelperClass;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rubbishclassifywork.R;

import java.util.List;

public class StoreAdapter extends BaseAdapter {

    private Context context;
    private List<PersionInfo> listinfos;

    public StoreAdapter(Context context, List<PersionInfo> listinfos){
        this.context =context;
        this.listinfos = listinfos;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listinfos.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listinfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
        TextView tv = (TextView) convertView.findViewById(R.id.tv);
        PersionInfo persionInfo = listinfos.get(position);
        tv.setText(persionInfo.getNameString());
        if (persionInfo.isChick()) {
            convertView.setBackgroundColor(Color.parseColor("#D4D4D4"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#f4f4f4"));
        }
        return convertView;
    }
}