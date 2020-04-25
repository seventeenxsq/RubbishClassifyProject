package com.example.rubbishclassifywork;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_page);
        dbHelper=new MyDatabaseHelper(this,"Dati.db",null,1);
        db=dbHelper.getWritableDatabase();
        cursor = db.query("Dati", null, null, null, null, null, null, null);
        cursor.moveToFirst();
        initView();
        showData();


    }
    private void initView(){
        tv_question_type=findViewById(R.id.tv_question_type);
        tv_question=findViewById(R.id.tv_question);
        radioGroup=findViewById(R.id.rg_options);
        optionA=findViewById(R.id.optionA);
        optionB=findViewById(R.id.optionB);
        optionC=findViewById(R.id.optionC);
        optionD=findViewById(R.id.optionD);
        tv_istrue=findViewById(R.id.tv_istrue);
        tv_explain=findViewById(R.id.tv_explain);
        rl_explain=findViewById(R.id.rl_explain);
        btn_next=findViewById(R.id.btn_next);
        btn_upload=findViewById(R.id.btn_upload);
        tv_question_count=findViewById(R.id.tv_question_conut);
        tv_time_count=findViewById(R.id.tv_time_count);
    }

    @Override
    public void onClick(View v) {

    }

    private void showData(){
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
        timerStart();
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rl_explain.setVisibility(View.VISIBLE);
                tv_istrue.setVisibility(View.VISIBLE);
                tv_istrue.setText("恭喜你，回答正确");
                tv_explain.setText(cursor.getString(cursor.getColumnIndex("explains")));
            }
        });
        tv_explain.setText("这题有点难哦！");
        tv_istrue.setText("温馨小提示：");
        tv_question.setText(cursor.getString(cursor.getColumnIndex("question")));
        optionA.setText(cursor.getString(cursor.getColumnIndex("answer1")));
        optionB.setText(cursor.getString(cursor.getColumnIndex("answer2")));
        optionC.setText(cursor.getString(cursor.getColumnIndex("answer3")));
        optionD.setText(cursor.getString(cursor.getColumnIndex("answer4")));
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
