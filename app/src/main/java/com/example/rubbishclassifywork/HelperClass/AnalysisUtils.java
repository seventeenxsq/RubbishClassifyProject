package com.example.rubbishclassifywork.HelperClass;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 创建一个工具类
 * 获取登录时的用户名的方法
 */
public class AnalysisUtils {
    public static String readLoginUserName(Context context){
        SharedPreferences sp = context.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
        String userName=sp.getString("loginUserName","");
        return userName;
    }
}
