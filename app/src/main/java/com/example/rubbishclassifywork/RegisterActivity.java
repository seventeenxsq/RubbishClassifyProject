package com.example.rubbishclassifywork;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rubbishclassifywork.HelperClass.HttpUtil;
import com.example.rubbishclassifywork.HelperClass.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

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
                    User user = new User();
                    user.setUserName(userName);
                    user.setPassword(passwagain);
                    user.setNickName("见圾行事");
                    user.setJifen("0");
                    Gson gson = new Gson();
                    Type type = new TypeToken<User>(){}.getType();
                    String jsonstr = gson.toJson(user,type);
                    String url = "http://106.13.235.119:8080/Server/RegisterServlet";
                    new RegisterActivity.RegisterTask().execute(url,jsonstr);
                }
                    break;
        }
    }

    //注册异步
    class  RegisterTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            String par  = params[0];
            String jsonstr = params[1];
            java.net.URL url = null;
            try {
                url = new URL(par);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String result = HttpUtil.doJsonPost(url,jsonstr);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int i = Integer.parseInt(result);
            if(i==1){
                Toast.makeText(getApplicationContext(),"注册成功", Toast.LENGTH_SHORT).show();
                RegisterActivity.this.finish();
                return;

            }else if (i == -1) {
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                    return;
            }else if(i==2){
                Toast.makeText(getApplicationContext(), "该账号已存在", Toast.LENGTH_SHORT).show();
                return;
            }

        }
    }

    //利用正则表达式判断手机号码是否规则
    public static boolean isPhoneNumber(String input) {// 判断手机号码是否规则
        String regex = "^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$";
        Pattern p = Pattern.compile(regex);
        return p.matches(regex, input);//如果不是号码，则返回false，是号码则返回true
    }

}
