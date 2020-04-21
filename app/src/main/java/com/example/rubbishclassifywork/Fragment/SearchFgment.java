package com.example.rubbishclassifywork.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rubbishclassifywork.Activity.SearchActivity;
import com.example.rubbishclassifywork.R;

public class SearchFgment extends Fragment implements View.OnClickListener {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        View view=inflater.inflate(R.layout.fragment_search,container,false);

        return view;
    }


    @Override
    public void onClick(View v) {

    }
}