package com.example.rubbishclassifywork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RewardActivity extends BaseActivity implements View.OnClickListener{

    private RadioGroup radioGroup;
    private EditText editText;
    private Button button_upload;
    private TextView textView_reward,textView_explain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        initView();

    }

    private void initView(){
        final AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                .setTitle("奖励关卡")
                .setMessage("恭喜您，触发了奖励关卡")
                .setIcon(R.mipmap.gift)
                .setPositiveButton("继续", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //alertDialog2.cancel();
                    }
                })

                .create();
        alertDialog2.show();
        radioGroup=findViewById(R.id.rw_rg_options);
        editText=findViewById(R.id.edt_name);
        button_upload=findViewById(R.id.rw_btn_upload);
        textView_explain=findViewById(R.id.rw_tv_explain);
        textView_reward=findViewById(R.id.rw_tv_istrue);
        button_upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rw_btn_upload:
                textView_reward.setText("感谢您的参与，积分+40");
                textView_explain.setText("");
                finish();
                break;
        }
    }
}
