package com.example.rubbishclassifywork.Fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.rubbishclassifywork.Adaper.MoveScrollView;
import com.example.rubbishclassifywork.ImageActivity;
import com.example.rubbishclassifywork.R;

import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

import static android.content.ContentValues.TAG;

public class CameraFgment extends Fragment implements View.OnClickListener {

    private MoveScrollView moveScrollView;
    private Toolbar toolbar;
    private FrameLayout fmlayout_takephoto;
    private ImageView iv_banyuan,ivgreenbullon,ivsky,btnkehuishou,btnqitalaji,btnshilaji,btnyouhailaji;
    private GifImageView gifbird;
    private Button btn_local;
    private TextView tv_local;


    @Override//将fragment与布局文件联系起来
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        final View view=inflater.inflate(R.layout.entirefragment,container,false);
        //fragment中找到布局必须用将父view传入进去
        initFindView(view);

        Log.e(TAG, "状态栏高度 "+getStatusBarHeight());
        //让toobar显示在最上面
        toolbar.bringToFront();

        //控制toolbar的显示与关闭 回调接口监听scroll位置
        moveScrollView.setScrollViewListener(new MoveScrollView.getTopListener() {
            @Override
            public void showTitle(View scrollView) {
                int scaleY=getToolbarHeight(toolbar);
                if (scrollView.getTop()<=scaleY) {
                    //显示toolbar
                    //设置背景透明度要先获取背景
                    toolbar.setVisibility(View.VISIBLE);
                    //onCreat方法中可以显示getheight;
                    Log.e(TAG, "到了到了");
                }
                else {
                    toolbar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public View stopView() {
                return ivsky;
            }
        });
        //属性动画
        Animatation();

        return view;
    }

    private void initFindView(final View view) {
        moveScrollView=view.findViewById(R.id.scroll_camerafgment);
        toolbar=view.findViewById(R.id.toolbar_camerafgmnet);
        fmlayout_takephoto=view.findViewById(R.id.fmlayout_takephoto);
        iv_banyuan=view.findViewById(R.id.iv_banyuan);
        btn_local=view.findViewById(R.id.btn_local);
        tv_local=view.findViewById(R.id.tv_local);
        ivgreenbullon=view.findViewById(R.id.tv_greeybullon);
        gifbird=view.findViewById(R.id.gif_bird);
        ivsky=view.findViewById(R.id.iv_sky);
        btnkehuishou=view.findViewById(R.id.iv_kehuishou);
        btnqitalaji=view.findViewById(R.id.iv_ganlaji);
        btnshilaji=view.findViewById(R.id.iv_shilaji);
        btnyouhailaji=view.findViewById(R.id.iv_youhailaji);

        //设置为可点击
        fmlayout_takephoto.setOnClickListener(this);
        btn_local.setOnClickListener(this);
        btnkehuishou.setOnClickListener(this);
        btnqitalaji.setOnClickListener(this);
        btnshilaji.setOnClickListener(this);
        btnyouhailaji.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //震动对象
        Vibrator vibrator = (Vibrator) Objects.requireNonNull(getActivity()).getSystemService(Service.VIBRATOR_SERVICE);
        switch (v.getId()){
            case R.id.fmlayout_takephoto:
                vibrator.vibrate(30);
                Intent intent1 = new Intent(v.getContext(), ImageActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_local:
                vibrator.vibrate(30);
                AlertDialog.Builder builder=new AlertDialog.Builder(v.getContext());
                builder.setTitle("选 择 城 市");
                //指定下拉列表的显示数据
                final String[] cities = {"上海","广州","北京","深圳","太原","芜湖","南京","青岛","大连","唐山"};
                //设置下拉列表选择项
                builder.setItems(cities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_local.setText(cities[which]);
                    }
                });
                //最后我们要显示alterdialog
                builder.show();
                break;
        }
    }

    //动画实现
    private void Animatation() {
        //缩放动画
        // 组合动画对象
        AnimatorSet animatorSetsuofang = new AnimatorSet();
        @SuppressLint("ObjectAnimatorBinding") ObjectAnimator scaleX = ObjectAnimator.ofFloat(fmlayout_takephoto, "scaleX", 1, 0.9f,1);//后几个参数是放大的倍数
        @SuppressLint("ObjectAnimatorBinding") ObjectAnimator scaleY = ObjectAnimator.ofFloat(fmlayout_takephoto, "scaleY", 1, 0.9f,1);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);//永久循环
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        animatorSetsuofang.setDuration(2500);//时间
        animatorSetsuofang.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSetsuofang.start();//开始

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(iv_banyuan, "translationY", 0f, 40f,0f);
        animator1.setDuration(3500);
        animator1.setRepeatCount(-1);//设置一直重复
        animator1.start();

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(ivgreenbullon, "translationY", 0f, -1500f);
        animator2.setDuration(7000);
        animator2.setRepeatCount(-1);//设置一直重复
        animator2.start();

        AnimatorSet birdAnimatorSet = new AnimatorSet();
        @SuppressLint("ObjectAnimatorBinding") ObjectAnimator animator3_x = ObjectAnimator.ofFloat(gifbird, "translationX",0,1500);
        @SuppressLint("ObjectAnimatorBinding") ObjectAnimator animator3_y = ObjectAnimator.ofFloat(gifbird, "translationY",0,-300);
        animator3_x.setRepeatCount(ValueAnimator.INFINITE);//永久循环
        animator3_y.setRepeatCount(ValueAnimator.INFINITE);
        birdAnimatorSet.setDuration(4000);//时间
        birdAnimatorSet.play(animator3_x).with(animator3_y);//两个动画同时开始

        birdAnimatorSet.start();//开始
    }

    private void showCoordinate(View view) {
        float coordx=view.getX();
        float coordy=view.getY();
        Toast.makeText(view.getContext(),"坐标"+String.valueOf(coordx)+" "+String.valueOf(coordy),Toast.LENGTH_SHORT).show();

        Log.e(TAG, "showCoordinate: onclick");
    }

    //获取状态栏高度
    private int getStatusBarHeight(){
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    //获取toolbar高度
    private int getToolbarHeight(View toolbar){
        return (int) toolbar.getHeight();
    }

//    //透明渐变的标题
//    private int alphaTitle(View view){
//        float alpha= (float) view.getTop() /(float)getToolbarHeight(view);
//        return (int)alpha*255;
//    }
}
