package com.example.rubbishclassifywork;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends BaseActivity {

    private Timer mTimer;      //计时器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Transfer();
        initView();
    }

    private  void  Transfer() {//这个方法帮我们去初始化一些类
            mTimer = new Timer();//时间管理对象
            mTimer.schedule(new TimerTask() {//时间任务
                @Override
                public void run() {
                    toMain();
                }
            },  1500);//第二参数数时延迟时间，，3秒
        }

    private void initView() {

    }


    //页面跳转函数
    private  void toMain(){
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
