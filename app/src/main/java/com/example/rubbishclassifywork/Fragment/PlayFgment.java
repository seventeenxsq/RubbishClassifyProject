package com.example.rubbishclassifywork.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rubbishclassifywork.AnswerPageActivity;
import com.example.rubbishclassifywork.HelperClass.HttpUtil;
import com.example.rubbishclassifywork.HelperClass.MyDatabaseHelper;
import com.example.rubbishclassifywork.HelperClass.Question;
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


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        View view=inflater.inflate(R.layout.fragment_play,container,false);
        dbHelper=new MyDatabaseHelper(getContext(),"Dati.db",null,1);
        db=dbHelper.getWritableDatabase();
        initView(view);
        start();

        return view;
    }


    private void initView(View view){
        //slidingSquareLoaderView=view.findViewById(R.id.view);
        relativeLayout=view.findViewById(R.id.rl_load);
        btn_start_dati=view.findViewById(R.id.btn_start_dati);
        rotateimg=view.findViewById(R.id.image_rotate);
        btn_start_dati.setOnClickListener(this);
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

    public void addDatas(String titlenum,String question,String choose1,String choose2,String choose3,String choose4,String answer,String explain){
        //获取读写权限 是用SQLite数据库对象来操作
        ContentValues values=new ContentValues();
        Log.e("addData",answer);
        //组装数据
        values.put("question",question);
        values.put("titlenum",titlenum);
        values.put("answer1",choose1);
        values.put("answer2",choose2) ;
        values.put("answer3",choose3);
        values.put("answer4",choose4);
        values.put("answer",answer);
        values.put("explains",explain);
        db.insert("Dati",null,values);
        Log.v("addData","succeed");
        values.clear();
    }

    class QuestionTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //relativeLayout.setVisibility(View.VISIBLE);
            btn_start_dati.setVisibility(View.GONE);
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
                        ,questions.get(i).getOptionC(),questions.get(i).getOptionD(),questions.get(i).getAnswer(),questions.get(i).getExplain());
            }
            Log.v("title",questions.get(1).getTitle().toString());
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            relativeLayout.setVisibility(View.GONE);
            Intent intent=new Intent(getContext(), AnswerPageActivity.class);
            startActivity(intent);
            btn_start_dati.setVisibility(View.VISIBLE);


        }
    }

}
