package com.example.rubbishclassifywork;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.rubbishclassifywork.Fragment.StoreFragment;
import com.example.rubbishclassifywork.HelperClass.PersionInfo;
import com.example.rubbishclassifywork.HelperClass.StatusBarUtil;
import com.example.rubbishclassifywork.HelperClass.StoreAdapter;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends FragmentActivity implements AdapterView.OnItemClickListener {
    List<PersionInfo> listinfoInfos=new ArrayList<PersionInfo>();
    private ListView listView;
    private StoreAdapter adapter;
    private StoreFragment myFragment;
    private GridView grid_photo;
    private BaseAdapter mAdapter = null;
    String[] listname={"话费","会员","数码","图书","百货","洗护","优惠卷"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        StatusBarUtil.transparencyBar(this);
        StatusBarUtil.BlackFontStatusBar(this.getWindow());
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onStart();
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        // TODO Auto-generated method stub
        for (int i = 0; i < 7; i++) {
            PersionInfo info=new PersionInfo(listname[i]);
            listinfoInfos.add(info);
        }

        //
        //listView = (ListView) findViewById(R.id.listview);
        //默认
        listinfoInfos.get(0).setChick(true);
        adapter = new StoreAdapter(this, listinfoInfos);
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(this);

        //创建MyFragment对象
        myFragment = new StoreFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, myFragment);

        Bundle mBundle=new Bundle();
        mBundle.putSerializable("info", listinfoInfos.get(0));
        myFragment.setArguments(mBundle);
        fragmentTransaction.commit();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        PersionInfo info = listinfoInfos.get(position);
        for (int i = 0; i < listinfoInfos.size(); i++) {
            if(listinfoInfos.get(i).getNameString().equals(info.getNameString())){
                listinfoInfos.get(i).setChick(true);
            }else {
                listinfoInfos.get(i).setChick(false);
            }
        }

        adapter.notifyDataSetChanged();


        //
        //创建MyFragment对象
        myFragment = new StoreFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, myFragment);

        Bundle mBundle=new Bundle();
        mBundle.putSerializable("info", listinfoInfos.get(position));
        myFragment.setArguments(mBundle);
        fragmentTransaction.commit();

    }
}
