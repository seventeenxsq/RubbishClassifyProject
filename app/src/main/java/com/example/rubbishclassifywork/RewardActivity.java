package com.example.rubbishclassifywork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rubbishclassifywork.HelperClass.AnalysisUtils;
import com.example.rubbishclassifywork.HelperClass.DBUtils;
import com.example.rubbishclassifywork.HelperClass.HttpUtil;
import com.sdsmdg.tastytoast.TastyToast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class RewardActivity extends BaseActivity implements View.OnClickListener{

    private RadioGroup radioGroup;
    private EditText editText;
    private Button button_upload,button_exit;
    private TextView textView_reward,textView_explain;
    private String phone_number;
    private int jifen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        initView();


    }

    private void initView(){
        jifen=getIntent().getIntExtra("datijifen",0);
        //jifen=Integer.valueOf(text);
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
        button_exit=findViewById(R.id.rw_btn_exit);
        editText=findViewById(R.id.edt_name);
        button_upload=findViewById(R.id.rw_btn_upload);
        textView_explain=findViewById(R.id.rw_tv_explain);
        textView_reward=findViewById(R.id.rw_tv_istrue);
        button_upload.setOnClickListener(this);
        button_exit.setOnClickListener(this);
        phone_number= AnalysisUtils.readLoginUserName(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rw_btn_upload:
                textView_reward.setText("感谢您的参与，积分+40");
                textView_explain.setText("");
                button_upload.setVisibility(View.GONE);
                button_exit.setVisibility(View.VISIBLE);
//                Toast.makeText(this,phone_number,Toast.LENGTH_SHORT).show();
                int i=jifen+40;
                String spUserName=AnalysisUtils.readLoginUserName(getApplicationContext());
                String url="http://106.13.235.119:8080/Server/ChangeJifenServlet?username="+ spUserName+ "&jifen=" + String.valueOf(i);
                new RewardActivity.ChangejifenTask().execute(url);
                DBUtils.getInstance(this).updateUserInfo("jifen",String.valueOf(i),phone_number);
                TastyToast.makeText(getApplicationContext(), "Thank You!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                break;
            case R.id.rw_btn_exit:
                Intent intent=new Intent(RewardActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    class ChangejifenTask extends AsyncTask<String, Integer, String> {
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
            if (!result.equals("1")) {
                Toast.makeText(getApplicationContext(), "积分上传失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "积分上传成功", Toast.LENGTH_SHORT).show();

            }

        }
    }
}
