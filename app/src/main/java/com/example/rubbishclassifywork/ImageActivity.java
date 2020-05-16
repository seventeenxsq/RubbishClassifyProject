package com.example.rubbishclassifywork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ImageActivity extends BaseActivity {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

    }
}
