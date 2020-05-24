package com.example.rubbishclassifywork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rubbishclassifywork.HelperClass.DBUtils;
import com.example.rubbishclassifywork.HelperClass.HttpUtil;
import com.example.rubbishclassifywork.HelperClass.User;

import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_register,tv_modify_pass;
    private String string_phone_number,string_password,spPsw;
    private EditText editText_phone_number,editText_password;
    private Button button_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }
    private void initView(){
        tv_register=findViewById(R.id.tv_register);
        tv_modify_pass=findViewById(R.id.tv_forget_password);
        editText_password=findViewById(R.id.ed_login_password);
        editText_phone_number=findViewById(R.id.ed_login_phone_number);
        button_login=findViewById(R.id.login_button);

        tv_register.setOnClickListener(this);
        tv_modify_pass.setOnClickListener(this);
        button_login.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_register:
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.login_button:
                string_password=editText_password.getText().toString().trim();
                string_phone_number=editText_phone_number.getText().toString().trim();
                if(string_phone_number.equals("")){
                    Toast.makeText(LoginActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                }else if(string_password.equals("")){
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else {
                    //保存登录状态和登录的用户名
                    saveLoginStatus(true,string_phone_number);
                    //把登录成功的状态传递到MainActivity中

                    Intent phone = new Intent();
                    phone.putExtra("textData",string_phone_number);
                    setResult(RESULT_OK,phone);
//                    Intent data = new Intent();
//                    data.putExtra("isLogin",true);
//                    setResult(RESULT_OK,data);
                    initUserData(string_phone_number);
                    LoginActivity.this.finish();
                    /*
                        上传服务器代码
                     */
                }
        }
    }

    private void initUserData(String spphonenumber){
        User bean=null;
        bean= DBUtils.getInstance(this).getUserInfo(spphonenumber);
        if (bean==null){
            bean =new User();
            bean.userName=spphonenumber;
            bean.nickName="环保小卫士";
            bean.sex="男";
            bean.signature="创意生活 文明分类";
            bean.jifen=0;
            DBUtils.getInstance(this).saveUserInfo(bean);
        }
    }

    /**
     * 从SharedPreferences中根据用户名获取密码
     */
    private String readPsw(String userName){
        SharedPreferences sp = getSharedPreferences("loginInfo",MODE_PRIVATE);
        return sp.getString(userName,"");
    }
    /**
     * 保存登录状态和登录用户名到SharedPreferences中
     */
    private void saveLoginStatus(boolean status,String userName){
        //loginInfo表示文件名
        SharedPreferences sp = getSharedPreferences("loginInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();//获取编辑器
        editor.putBoolean("isLogin",status);
        editor.putString("loginUserName",userName);
        editor.commit();//提交修改
    }


    //登录信息与服务器信息匹配异步类
    class LoginTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String par = params[0];
            URL url = null;
            try {
                url = new URL(par);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String result = HttpUtil.doPost(url);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //保存数据
            if (result.equals("1")) {
                Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                //保存登录状态和登录的用户名
                saveLoginStatus(true,string_phone_number);
                //把登录成功的状态传递到MainActivity中
                Intent data = new Intent();
                data.putExtra("isLogin",true);
                setResult(RESULT_OK,data);
                LoginActivity.this.finish();
                return;

            } else {
                if (result.equals("-1")) {
                    Toast.makeText(getApplicationContext(), "密码错误或账号不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        }
    }

}
