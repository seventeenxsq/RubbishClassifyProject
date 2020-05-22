package com.example.rubbishclassifywork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private Button button_register;
    private EditText editText_phone_number,editText_password,editText_again_password;
    private String userName,passw,passwagain,md5_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();

    }

    private void initView(){
        button_register=findViewById(R.id.register_button);
        editText_phone_number=findViewById(R.id.ed_register_user);
        editText_password=findViewById(R.id.ed_register_password);
        editText_again_password=findViewById(R.id.ed_again_password);
        button_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_button:
                userName = editText_phone_number.getText().toString().trim();
                passw = editText_password.getText().toString().trim();
                passwagain = editText_again_password.getText().toString().trim();
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(RegisterActivity.this,"请输入手机号",Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(passw)){
                    Toast.makeText(RegisterActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(passwagain)){
                    Toast.makeText(RegisterActivity.this,"请再次输入密码",Toast.LENGTH_SHORT).show();
                    return;
                }else if(!passw.equals(passwagain)){
                    Toast.makeText(RegisterActivity.this,"输入两次的密码不一致",Toast.LENGTH_SHORT).show();
                    return;
                }else{

                }
                    break;
        }
    }
}
