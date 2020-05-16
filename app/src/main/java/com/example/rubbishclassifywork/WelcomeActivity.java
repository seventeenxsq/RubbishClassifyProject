package com.example.rubbishclassifywork;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends BaseActivity {

    private Timer mTimer;      //计时器对象
    ImageView eye1,eye2,xingshi,car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        animationEye();
                    }
                },300
        );

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        animationCar();
                    }
                },1300
        );

        Transfer();
    }

    private  void  Transfer() {//这个方法帮我们去初始化一些类
            mTimer = new Timer();//时间管理对象
            mTimer.schedule(new TimerTask() {//时间任务
                @Override
                public void run() {
                    toMain();
                }
            },  2600);//第二参数数时延迟时间，，3秒
        }

    private void initView() {
        eye1=findViewById(R.id.eye1);
        eye2=findViewById(R.id.eye2);
        xingshi=findViewById(R.id.xingshi);
        car=findViewById(R.id.iv_car);
    }


    //页面跳转函数
    private  void toMain(){
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        //结束welcome页面
        finish();
    }

    //眼睛跳
    private void animationEye() {

        //Handler().postDelayed消息延时
        new Handler().postDelayed(new Runnable(){
            public void run() {
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(eye2, "translationY", 0f, -50f,-5f);
                animator2.setDuration(1000);
                animator2.start();
                //execute the task
            }

        }, 300);

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(eye1, "translationY", 0f, -40f,0f);
        animator1.setDuration(800);
        animator1.start();
            }
    //车子进来
    private void animationCar() {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(xingshi, "translationX", 500f,0f);
        animator1.setDuration(300);
        animator1.start();
        xingshi.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable(){
            public void run() {
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(car, "translationX", -500f,0f);
                animator2.setDuration(800);
                animator2.start();
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(car, "translationY", 0,-10f,0);
                animator3.setDuration(50);
                animator3.setRepeatCount(3);
                animator3.start();
                //execute the task
                AnimatorSet animatorcar = new AnimatorSet();
                animatorcar.setDuration(800);//时间
                animatorcar.play(animator2).with(animator3);//两个动画同时开始
                animatorcar.start();//开始
                car.setVisibility(View.VISIBLE);
            }

        }, 500);

    }


}


