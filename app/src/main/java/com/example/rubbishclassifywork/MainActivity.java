package com.example.rubbishclassifywork;

import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.rubbishclassifywork.Adaper.TabFragmentPagerAdapter;
import com.example.rubbishclassifywork.Fragment.CameraFgment;
import com.example.rubbishclassifywork.Fragment.MeFgment;
import com.example.rubbishclassifywork.Fragment.PlayFgment;
import com.example.rubbishclassifywork.Fragment.SearchFgment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout button1,button2,button3,button4;
    ImageView imageView_btn1,imageView_btn2,imageView_btn3,imageView_btn4;

    private List<Fragment> list;
    private ViewPager viewPager;
    private TabFragmentPagerAdapter adapter;   //将fragment与viewpager绑定

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        button1=findViewById(R.id.tab_camera);
        button2=findViewById(R.id.tab_search);
        button3=findViewById(R.id.tab_dati);
        button4=findViewById(R.id.tab_me);
        imageView_btn1=findViewById(R.id.btn_1);
        imageView_btn2=findViewById(R.id.btn_2);
        imageView_btn3=findViewById(R.id.btn_3);
        imageView_btn4=findViewById(R.id.btn_4);
        viewPager = findViewById(R.id.viewpager);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);

        //把Fragment添加到List集合里面
        list = new ArrayList<>();
        list.add(new CameraFgment());
        list.add(new SearchFgment());
        list.add(new PlayFgment());
        list.add(new MeFgment());
        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyPagerChangeListener());
    }

    /**
     * 设置一个ViewPager的侦听事件，当左右滑动ViewPager时菜单栏被选中状态跟着改变
     */
    public class MyPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            resetImg(); //将图片全部默认为不选中
            int currentItem = viewPager.getCurrentItem();
            switch (currentItem) {
                case 0:
                    imageView_btn1.setImageResource(R.mipmap.icon_eye_green);
                    break;
                case 1:
                    imageView_btn2.setImageResource(R.mipmap.icon_search_green);
                    break;
                case 2:
                    imageView_btn3.setImageResource(R.mipmap.icon_dati_green);
                    break;
                case 3:
                    imageView_btn4.setImageResource(R.mipmap.icon_me_green);
                    break;
                default:
                    break;
            }

        }

    }

    //重置图片资源
    private void resetImg() {
        imageView_btn1.setImageResource(R.mipmap.icon_eye);
        imageView_btn2.setImageResource(R.mipmap.icon_search);
        imageView_btn3.setImageResource(R.mipmap.icon_dati);
        imageView_btn4.setImageResource(R.mipmap.icon_me);
    }

    @Override
    public void onClick(View v) {
        Vibrator vibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(10);
        resetImg();
        switch (v.getId()) {
            case R.id.tab_camera:
                viewPager.setCurrentItem(0);// 跳到第一个页面
                imageView_btn1.setImageResource(R.mipmap.icon_eye_green); // 图片变为选中
                break;
            case R.id.tab_search:
                viewPager.setCurrentItem(1);
                imageView_btn2.setImageResource(R.mipmap.icon_search_green);
                break;
            case R.id.tab_dati:
                viewPager.setCurrentItem(2);
                imageView_btn3.setImageResource(R.mipmap.icon_dati_green);
                break;
            case R.id.tab_me:
                viewPager.setCurrentItem(3);
                imageView_btn4.setImageResource(R.mipmap.icon_me_green);
                break;
            default:
                break;
        }
    }

}

