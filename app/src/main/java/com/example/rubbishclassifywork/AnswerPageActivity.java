package com.example.rubbishclassifywork;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;

import com.example.rubbishclassifywork.HelperClass.AnalysisUtils;
import com.example.rubbishclassifywork.HelperClass.DBUtils;
import com.example.rubbishclassifywork.HelperClass.MyDatabaseHelper;

public class AnswerPageActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_question_type,tv_question,tv_istrue,tv_explain,tv_question_count,tv_time_count;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private RadioButton optionA,optionB,optionC,optionD;
    private RadioGroup radioGroup;
    private Button btn_upload,btn_next;
    private RelativeLayout rl_explain;
    private CountDownTimer timer,next_timer;
    private ImageView iv_img;
    private int count=1,right_count=0,jifen=0;
    private String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_page);
        dbHelper=new MyDatabaseHelper(this,"Dati.db",null,1);
        db=dbHelper.getWritableDatabase();
        cursor = db.query("Dati", null, null, null, null, null, null, null);
        cursor.moveToFirst();
        init();
        initView();
        showData();


    }

    private void init(){
//
        timer=new CountDownTimer(15*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_time_count.setText(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                tv_time_count.setText("00:00");
            }
        };
    }

    private void initView(){
        phone_number= AnalysisUtils.readLoginUserName(this);
        tv_question_type=findViewById(R.id.tv_question_type);
        tv_question_count=findViewById(R.id.tv_question_conut);
        tv_question=findViewById(R.id.tv_question);
        radioGroup=findViewById(R.id.rg_options);
        optionA=findViewById(R.id.optionA);
        optionB=findViewById(R.id.optionB);
        optionC=findViewById(R.id.optionC);
        optionD=findViewById(R.id.optionD);
        tv_istrue=findViewById(R.id.tv_istrue);
        tv_explain=findViewById(R.id.tv_explain);
        rl_explain=findViewById(R.id.rl_explain);
        btn_upload=findViewById(R.id.btn_upload);
        btn_next=findViewById(R.id.btn_next);
        tv_question_count=findViewById(R.id.tv_question_conut);
        tv_time_count=findViewById(R.id.tv_time_count);
        iv_img=findViewById(R.id.iv_img);
        optionA.setOnClickListener(this);
        optionB.setOnClickListener(this);
        optionC.setOnClickListener(this);
        optionD.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        btn_next.setOnClickListener(this);
    }

    private void showRight(){
        tv_istrue.setVisibility(View.VISIBLE);
        tv_istrue.setText("恭喜你，回答正确，积分+20");
        tv_explain.setText(cursor.getString(cursor.getColumnIndex("explains")));
    }

    private void showWrong(){
        tv_istrue.setVisibility(View.VISIBLE);
        tv_istrue.setText("很遗憾，回答错误，积分+0");
        tv_istrue.setTextColor(this.getResources().getColor(R.color.colorAccent));
        tv_explain.setText(cursor.getString(cursor.getColumnIndex("explains")));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(AnswerPageActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.optionA:
                if(optionA.getText().equals(cursor.getString(cursor.getColumnIndex("answer")))){
                    rightOption();
                }else {
                    wrongOption();
                }
                break;
            case R.id.optionB:
                if(optionB.getText().equals(cursor.getString(cursor.getColumnIndex("answer")))){
                    rightOption();
                }else {
                    wrongOption();
                }
                break;
            case R.id.optionC:
                if(optionC.getText().equals(cursor.getString(cursor.getColumnIndex("answer")))){
                    rightOption();
                }else {
                    wrongOption();
                }
                break;
            case R.id.optionD:
                if(optionD.getText().equals(cursor.getString(cursor.getColumnIndex("answer")))){
                    rightOption();
                }else {
                    wrongOption();
                }
                break;
        }
    }

    private void optionIsClick(){
        optionA.setClickable(true);
        optionB.setClickable(true);
        optionC.setClickable(true);
        optionD.setClickable(true);
    }

    private void optionNoClick(){
        optionA.setClickable(false);
        optionB.setClickable(false);
        optionC.setClickable(false);
        optionD.setClickable(false);
    }

    private void wrongOption(){
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionNoClick();
                btn_next.setVisibility(View.VISIBLE);
                btn_upload.setVisibility(View.GONE);
                showWrong();
                timerCancel();
                count++;
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionIsClick();
                iv_img.setImageResource(R.mipmap.xiangpini);
               if (count>10){
                    if (right_count>=8){
                        //Toast.makeText(AnswerPageActivity.this,right_count,Toast.LENGTH_SHORT).show();
                        Intent intent2=new Intent(AnswerPageActivity.this,RewardActivity.class);
                        intent2.putExtra("datijifen",jifen);
                        startActivity(intent2);
                        finish();
                        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
                   }else {
                        DBUtils.getInstance(AnswerPageActivity.this).updateUserInfo("jifen",String.valueOf(jifen),phone_number);
                        startActivity(new Intent(AnswerPageActivity.this,MainActivity.class));
                        finish();
                   }

                }
                cursor.moveToNext();
                showData();
            }
        });

    }

    private void rightOption(){
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionNoClick();
                btn_next.setVisibility(View.VISIBLE);
                btn_upload.setVisibility(View.GONE);
                right_count++;
                jifen=jifen+20;
                showRight();
                timerCancel();
                count++;
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionIsClick();
                iv_img.setImageResource(R.mipmap.xiangpini);
                if (count>10){
                    if (right_count>=8){
                        //Toast.makeText(AnswerPageActivity.this,right_count,Toast.LENGTH_SHORT).show();
                        Intent intent1=new Intent(AnswerPageActivity.this,RewardActivity.class);
                        intent1.putExtra("datijifen",jifen);
                        startActivity(intent1);
                        finish();
                        overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
                    }else {
                        DBUtils.getInstance(AnswerPageActivity.this).updateUserInfo("jifen",String.valueOf(jifen),phone_number);
                        startActivity(new Intent(AnswerPageActivity.this,MainActivity.class));
                        finish();
                    }

               }
                cursor.moveToNext();
                showData();
            }
        });

    }

    private void showData(){
        radioGroup.clearCheck();
        timerStart();
        btn_next.setVisibility(View.GONE);
        btn_upload.setVisibility(View.VISIBLE);
        tv_explain.setText("这题有点难哦！");
        tv_istrue.setText("温馨小提示：");
        tv_istrue.setTextColor(this.getResources().getColor(R.color.green));
        tv_question.setText(cursor.getString(cursor.getColumnIndex("question")));
        optionA.setText(cursor.getString(cursor.getColumnIndex("answer1")));
        optionB.setText(cursor.getString(cursor.getColumnIndex("answer2")));
        optionC.setText(cursor.getString(cursor.getColumnIndex("answer3")));
        optionD.setText(cursor.getString(cursor.getColumnIndex("answer4")));
        tv_question_count.setText(String.valueOf(count)+"/10");
    }

    public String formatTime(long millisecond) {
        int minute;//分钟
        int second;//秒数
        minute = (int) ((millisecond / 1000) / 60);
        second = (int) ((millisecond / 1000) % 60);
        if (minute < 10) {
            if (second < 10) {
                return "0" + minute + ":" + "0" + second;
            } else {
                return "0" + minute + ":" + second;
            }
        }else {
            if (second < 10) {
                return minute + ":" + "0" + second;
            } else {
                return minute + ":" + second;
            }
        }
    }

    /**
     * 取消倒计时
     */
    public void timerCancel() {
        timer.cancel();
    }

    /**
     * 开始倒计时
     */
    public void timerStart() {
        timer.start();
    }
}
