package com.example.rubbishclassifywork.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rubbishclassifywork.R;

public class PlayFgment extends Fragment implements View.OnClickListener {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        View view=inflater.inflate(R.layout.fragment_play,container,false);
        initView();

        return view;
    }


    private void initView() {
    }


    @Override
    public void onClick(View v) {

    }
}
