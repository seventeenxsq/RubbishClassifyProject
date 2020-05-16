package com.example.rubbishclassifywork.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rubbishclassifywork.HelperClass.Icon;
import com.example.rubbishclassifywork.HelperClass.MyAdapter;
import com.example.rubbishclassifywork.HelperClass.PersionInfo;
import com.example.rubbishclassifywork.R;

import java.util.ArrayList;


public class StoreFragment extends Fragment {

    private GridView grid_photo;
    private BaseAdapter mAdapter = null;
    private ArrayList<Icon> mData = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.storefragment, null);

        initview(view);
        mData = new ArrayList<Icon>();
        mData.add(new Icon("5元话费", "500积分"));
        mData.add(new Icon("10元话费", "1000积分"));
        mData.add(new Icon("15元话费", "1500积分"));
        mData.add(new Icon("20元话费", "2000积分"));
        mData.add(new Icon("30元话费", "2900积分"));
        mData.add(new Icon("50元话费", "4800积分"));

        mAdapter = new MyAdapter<Icon>(mData, R.layout.item_grid_icon) {
            @Override
            public void bindView(ViewHolder holder, Icon obj) {
                holder.setText(R.id.txt_label, obj.getTitle());
                holder.setText(R.id.txt_cost, obj.getiName());
            }

        };

        grid_photo.setAdapter(mAdapter);
        grid_photo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:

                }
            }
        });

       //TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        //得到数据
        PersionInfo info = (PersionInfo) getArguments().getSerializable("info");
        //tv_title.setText(info.getNameString());
        return view;
    }
    private void  initview(View view){
        grid_photo=view.findViewById(R.id.grid_photo);
    }

}
