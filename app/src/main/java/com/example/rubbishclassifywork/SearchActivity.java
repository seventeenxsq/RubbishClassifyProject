package com.example.rubbishclassifywork;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.rubbishclassifywork.Fragment.SearchFgment;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "kkkk";
    private SearchFgment fragment = new SearchFgment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //在启动这个界面的时候，就跳转到搜索Fragment
        // test this
        FragmentManager fragmentManager = SearchActivity.this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_search,fragment);
        transaction.commit();
    }
}
