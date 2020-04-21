package com.example.rubbishclassifywork.Adaper;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
    private  List<Fragment> fragments;

    public TabFragmentPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        fragment = fragments.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("id",""+position);
        if (fragment != null) {
            fragment.setArguments(bundle);
        }
//        return fragments.get(position);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}