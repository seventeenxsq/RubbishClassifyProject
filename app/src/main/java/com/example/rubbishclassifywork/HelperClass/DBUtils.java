package com.example.rubbishclassifywork.HelperClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtils {
    private static DBUtils instance=null;
    private static SQLiteHelper helper;
    private static SQLiteDatabase db;
    public DBUtils(Context context){
        helper=new SQLiteHelper(context);
        db=helper.getWritableDatabase();
    }
    public static DBUtils getInstance(Context context){
        if (instance==null){
            instance=new DBUtils(context);
        }
        return instance;
    }
    /**
     * 保存个人信息
     */
    public void saveUserInfo(User bean){
        ContentValues cv=new ContentValues();
        cv.put("userName",bean.userName);
        cv.put("nickName",bean.nickName);
        cv.put("sex",bean.sex);
        cv.put("signature",bean.signature);
        cv.put("jifen",String.valueOf(bean.jifen));
        db.insert(SQLiteHelper.U_USERINFO,null,cv);
    }
    /**
     * 获取个人资料信息
     */
    public User getUserInfo(String userName){
        String sql="SELECT * FROM "+ SQLiteHelper.U_USERINFO+" WHERE userName=?";
        Cursor cursor=db.rawQuery(sql,new String[]{userName});
        User bean=null;
        while (cursor.moveToNext()){
            bean=new User();
            bean.userName=cursor.getString(cursor.getColumnIndex("userName"));
            bean.nickName=cursor.getString(cursor.getColumnIndex("nickName"));
            bean.sex=cursor.getString(cursor.getColumnIndex("sex"));
            bean.signature=cursor.getString(cursor.getColumnIndex("signature"));
            bean.jifen=Integer.valueOf(cursor.getString(cursor.getColumnIndex("jifen")));
        }
        cursor.close();
        return bean;
    }
    /**
     * 修改个人资料
     */
    public void updateUserInfo(String key,String value,String userName){
        ContentValues cv=new ContentValues();
        cv.put(key,value);
        db.update(SQLiteHelper.U_USERINFO,cv,"userName=?",new String[]{userName});
    }
}