package com.example.rubbishclassifywork.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rubbishclassifywork.AnswerPageActivity;
import com.example.rubbishclassifywork.HelperClass.AnalysisUtils;
import com.example.rubbishclassifywork.HelperClass.DBUtils;
import com.example.rubbishclassifywork.HelperClass.HttpUtil;
import com.example.rubbishclassifywork.HelperClass.MyDatabaseHelper;
import com.example.rubbishclassifywork.HelperClass.Question;
import com.example.rubbishclassifywork.HelperClass.User;
import com.example.rubbishclassifywork.MainActivity;
import com.example.rubbishclassifywork.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.steelkiwi.library.SlidingSquareLoaderView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import id.arieridwan.lib.PageLoader;

public class PlayFgment extends Fragment implements View.OnClickListener {

    private Button btn_start_dati;
    String url="http://106.13.235.119:8080/Server/MyAnswerServlet";
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private RelativeLayout relativeLayout;
    private ImageView rotateimg;
    private TextView textView_jifen;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        View view=inflater.inflate(R.layout.fragment_play,container,false);
        dbHelper=new MyDatabaseHelper(getContext(),"Dati.db",null,1);
        db=dbHelper.getWritableDatabase();
        initView(view);
        if(readLoginStatus()){
            //从服务器读取积分数据
            User user= DBUtils.getInstance(getContext()).getUserInfo(AnalysisUtils.readLoginUserName(getContext()));
            String url_1 = "http://106.13.235.119:8080/Server/MyJifenServlet?username="+user.userName;
            if(user==null){
                Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
            }else {
                new PlayFgment.GetjifenTask().execute(url_1);
            }
        }else {
            Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
        }
        start();

        return view;
    }


    private void initView(View view){
        //slidingSquareLoaderView=view.findViewById(R.id.view);
        relativeLayout=view.findViewById(R.id.rl_load);
        btn_start_dati=view.findViewById(R.id.btn_start_dati);
        rotateimg=view.findViewById(R.id.image_rotate);
        textView_jifen=view.findViewById(R.id.play_tv_coin);
        btn_start_dati.setOnClickListener(this);
//        Intent intent=new Intent();
//          //用getXxxExtra()取出对应类型的数据。取出String只需要指定key
//        String jifen=intent.getStringExtra("datijifen");
//        if (!TextUtils.isEmpty(jifen)){
//            textView_jifen.setText(jifen);
//        }
    }

    private void start(){

        Animation animation= AnimationUtils.loadAnimation(getContext(),R.anim.imgrotate);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        rotateimg.startAnimation(animation);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_dati:
                new PlayFgment.QuestionTask().execute(url);
                break;
        }
    }

    public void addDatas(String titlenum,String question,String choose1,String choose2,String choose3,String choose4,String answer,String explain,String url){
        //获取读写权限 是用SQLite数据库对象来操作
        ContentValues values=new ContentValues();
        Log.v("addData",answer);
        Log.v("addData",url);
        //组装数据
        values.put("question",question);
        values.put("titlenum",titlenum);
        values.put("answer1",choose1);
        values.put("answer2",choose2) ;
        values.put("answer3",choose3);
        values.put("answer4",choose4);
        values.put("answer",answer);
        values.put("explains",explain);
        values.put("url",url);
        db.insert("Dati",null,values);
        Log.v("addData","succeed");
        values.clear();
    }

//    //接收答题获得的积分并显示
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==2){
//            String textjifen = data.getStringExtra("datijifen");
//            //Toast.makeText(getContext(),textData,Toast.LENGTH_SHORT).show();
//            textView_jifen.setText(textjifen);
//
//        }
//
//    }


    private boolean readLoginStatus(){
        SharedPreferences sp= (SharedPreferences) getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        boolean isLogin=sp.getBoolean("isLogin",false);
        return isLogin;
    }

    class QuestionTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //relativeLayout.setVisibility(View.VISIBLE);
            //btn_start_dati.setVisibility(View.GONE);
        }

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
            Gson gson=new Gson();
            ArrayList<Question> questions=gson.fromJson(result,new TypeToken<ArrayList<Question>>(){}.getType());
            //tv_explain.setText(questions.get(1).getAnswer());
            Log.e("url","success");
            for (int i=0;i<questions.size();i++){
//                try {
//                    //Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                addDatas("第"+String.valueOf(questions.get(i).getId())+"题",questions.get(i).getTitle(),questions.get(i).getOptionA(),questions.get(i).getOptionB()
                        ,questions.get(i).getOptionC(),questions.get(i).getOptionD(),questions.get(i).getAnswer(),questions.get(i).getExplain(),questions.get(i).getUrl());
            }
            Log.v("title",questions.get(1).getTitle().toString());
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            relativeLayout.setVisibility(View.GONE);
            Intent intent=new Intent(getContext(), AnswerPageActivity.class);
            startActivityForResult(intent,3);
            //btn_start_dati.setVisibility(View.VISIBLE);

            getActivity().finish();
        }
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
                textView_jifen.setText(String.valueOf(result));

            } else {
                if (result.equals("-1")) {
                    Toast.makeText(getContext(), "积分获取失败", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        }
    }

}
