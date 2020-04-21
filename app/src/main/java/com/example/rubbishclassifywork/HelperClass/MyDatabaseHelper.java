package com.example.rubbishclassifywork.HelperClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    //建表语句  注意CREAT_DATI是表名，不是数据库的名字
    public static final String CREAT_DATI="create table Dati ("
            +"id integer primary key autoincrement, "
            +"titlenum text,"
            +"question text, "
            +"answer1 text, "
            +"answer2 text, "
            +"answer3 text, "
            +"answer4 text,"
            +"answer text,"
            +"explains text)";

    //传输上下文参数
    private Context mContext;

    //数据库帮助类构造函数  用来接收上下文
    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }


    @Override//执行建表语句
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_DATI);
        Log.e("DB","CreatDB succeed");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Dati");
        Log.e("upgradetable", "onUpgrade: shengjichenggong" );
    }
}
