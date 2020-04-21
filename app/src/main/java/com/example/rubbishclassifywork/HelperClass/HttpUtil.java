package com.example.rubbishclassifywork.HelperClass;


import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

    public static String doPost(URL url) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
                ;
            }
        }
        return null;
    }




    //用于解析字符串
    public static String doJsonPost(URL url, String jsonstr) {
        HttpURLConnection urlConnection = null;
        System.out.println("tag"+"这只从客户端得到的数据"+jsonstr);

        try {

            urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
            urlConnection.setConnectTimeout(8000);//连接的超时时间
            urlConnection.setUseCaches(false);//不使用缓存
            //urlConnection.setFollowRedirects(false);是static函数，作用于所有的URLConnection对象。
            urlConnection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
            urlConnection.setReadTimeout(8000);//响应的超时时间
            urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
            urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据
            urlConnection.setRequestMethod("POST" );//设置请求的方式
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置消息的类型
            urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接

            OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
            bw.write(jsonstr);//把json字符串写入缓冲区中
            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
            out.close();
            bw.close();//使用完关闭
            if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){//得到服务端的返回码是否连接成功

                InputStream in = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        }catch (Exception e) {
        }finally{
            urlConnection.disconnect();//使用完关闭TCP连接，释放资源
        }
        return null;
    }

    //用于解析字符串
    public static String doImgPost(URL url, String mfilepath) {
        HttpURLConnection urlConnection = null;
        Log.d("tag","这只从客户端得到的数据"+mfilepath);
        final String filepath = mfilepath;

        try {

            urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
            //urlConnection.setConnectTimeout(8000);//连接的超时时间
            urlConnection.setUseCaches(false);//不使用缓存
            //urlConnection.setFollowRedirects(false);是static函数，作用于所有的URLConnection对象。
            //urlConnection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
            //urlConnection.setReadTimeout(8000);//响应的超时时间
            urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
            urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据
            urlConnection.setRequestMethod("POST" );//设置请求的方式

            urlConnection.setRequestProperty("Connection", "Keep-Alive");//设置消息的类型
            urlConnection.setRequestProperty("Content-Type","image/jpeg");
            urlConnection.setRequestProperty("Content-Type", "application;charset=UTF-8");
            System.out.println("正在连接");
            urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接
            System.out.println("连接成功");
            //OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
            //第二步：打开数据通道
            DataOutputStream dop=new DataOutputStream(urlConnection.getOutputStream());

            //第三步：准备好发送的数据
            File f=new File(filepath);
            FileInputStream fis=new FileInputStream(f);
            if(f.isFile()){
                byte[] buffer=new byte[1024];
                int length=-1;
                while ((length=fis.read(buffer))!=-1){
                    dop.write(buffer,0,length);
                    Log.v("dos",String.valueOf(length));
                }
            }

            //第四步：将准备的数据发送给服务器
            Log.v("dos",mfilepath);

            dop.flush();
            dop.close();
            Log.v("发送","发送成功");


            //bw.write(picture);//把字符串写入缓冲区中
            //bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
            //out.close();
           // bw.close();//使用完关闭
//            if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){//得到服务端的返回码是否连接成功
//
//                InputStream in = urlConnection.getInputStream();
//                BufferedReader br = new BufferedReader(new InputStreamReader(in));
//
//                StringBuilder response = new StringBuilder();
//                String line;
//                while ((line = br.readLine()) != null) {
//                    response.append(line);
//                }
//                return response.toString();
//            }
        }catch (Exception e) {
        }finally{
            urlConnection.disconnect();//使用完关闭TCP连接，释放资源
        }
        return null;
    }


}
