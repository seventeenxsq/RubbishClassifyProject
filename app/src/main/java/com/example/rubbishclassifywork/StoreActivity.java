package com.example.rubbishclassifywork;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.rubbishclassifywork.Fragment.PlayFgment;
import com.example.rubbishclassifywork.Fragment.StoreFragment;
import com.example.rubbishclassifywork.HelperClass.AnalysisUtils;
import com.example.rubbishclassifywork.HelperClass.DBUtils;
import com.example.rubbishclassifywork.HelperClass.HttpUtil;
import com.example.rubbishclassifywork.HelperClass.PersionInfo;
import com.example.rubbishclassifywork.HelperClass.StatusBarUtil;
import com.example.rubbishclassifywork.HelperClass.StoreAdapter;
import com.example.rubbishclassifywork.HelperClass.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends FragmentActivity implements AdapterView.OnItemClickListener {
    List<PersionInfo> listinfoInfos=new ArrayList<PersionInfo>();
    private ListView listView;
    private StoreAdapter adapter;
    private StoreFragment myFragment;
    private TextView tv_jifen;
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

        tv_jifen=findViewById(R.id.tv_jifen);
        if(readLoginStatus()){
            //从服务器读取积分数据
            String url_1 = "http://106.13.235.119:8080/Server/MyJifenServlet?username="+AnalysisUtils.readLoginUserName(this);
                new StoreActivity.GetjifenTask().execute(url_1);
        }else {
            Toast.makeText(StoreActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
        }


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


    private boolean readLoginStatus(){
        SharedPreferences sp= (SharedPreferences) this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        boolean isLogin=sp.getBoolean("isLogin",false);
        return isLogin;
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

    class GetjifenTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String par = params[0];
            URL url = null;
            try {
                url = new URL(par);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String result = HttpUtil.doPost(url);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //保存数据
            if (!result.equals(-1)) {
                tv_jifen.setText(String.valueOf(result));

            } else {
                if (result.equals("-1")) {
                    Toast.makeText(StoreActivity.this, "积分获取失败", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        }
    }

}
