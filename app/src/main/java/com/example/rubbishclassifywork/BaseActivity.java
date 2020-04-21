package com.example.rubbishclassifywork;

import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rubbishclassifywork.HelperClass.StatusBarUtil;

import static android.content.ContentValues.TAG;

public class BaseActivity extends AppCompatActivity {


    @Override  //设置状态栏透明属性
    protected void onStart() {
        StatusBarUtil.transparencyBar(this);
        StatusBarUtil.BlackFontStatusBar(this.getWindow());
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onStart();
        Log.e(TAG, getClass().getSimpleName()+": onStart ");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, getClass().getSimpleName()+": onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, getClass().getSimpleName()+": onResume ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, getClass().getSimpleName()+": onDestroy ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, getClass().getSimpleName()+": onStop ");
    }
}
