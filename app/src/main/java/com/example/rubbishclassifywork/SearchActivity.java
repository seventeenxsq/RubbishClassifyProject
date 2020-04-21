package com.example.rubbishclassifywork;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.rubbishclassifywork.Fragment.SearchFragment;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //在启动这个界面的时候，就跳转到搜索Fragment
        SearchFragment fragment = new SearchFragment();
        FragmentManager fragmentManager = SearchActivity.this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_search,fragment);
        transaction.commit();
    }
}
