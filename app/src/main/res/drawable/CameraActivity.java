package com.sunday.myapplication.Activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunday.myapplication.Bean.Image;
import com.sunday.myapplication.R;
import com.sunday.myapplication.Util.Base64Util;
import com.sunday.myapplication.Util.HttpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

public class CameraActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    private ImageView roundImageView;
    private Button button,buttonUpphoto;
    private Uri imageUri;
    private String upphoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        roundImageView=findViewById(R.id.picture);
        button=findViewById(R.id.take_photo);
        buttonUpphoto=findViewById(R.id.up_photo);

        //上传服务器测试
        buttonUpphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new upPicture().execute(uri,upphoto);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(CameraActivity.this,"com.example.cameraalbumtest.fileprovider",outputImage);
                }else {
                    imageUri=Uri.fromFile(outputImage);
                }
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });

        roundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CameraActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });
    }

    private void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(CameraActivity.this,"您拒绝了权限",Toast.LENGTH_SHORT).show();
                }break;
             default:
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        File file;
        OutputStream outputStream;

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHOTO:
                if(requestCode==1){
                    try {
                        Bitmap mbitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        //上传服务器
                        upphoto=new Base64Util().bitmapToBase64(mbitmap);
                        System.out.println(imageUri.toString());
                        roundImageView.setImageBitmap(mbitmap);

                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(requestCode==2){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else {
                        handleImageBeforeKitKat(data);
                    }
                }break;
            default:
                break;
        }
    }

    //从相册中选择，版本号大于19
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.download.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            displayImage(imagePath);
        }

    }

    //版本号低于19，，使用该方式获取路径
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }

    //获取照片路径
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    //在imageview上展示图片
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayImage(String imagePath){
        if(imagePath!=null){
            String uri="http://106.13.235.119:8080/Server/ImageServlet";
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            String picture=new Base64Util().bitmapToBase64(bitmap);

            Image image=new Image();
            image.setImage(picture);
            image.setImagename("photo.jpg");
            Gson gson = new Gson();
            Type type = new TypeToken<Image>(){}.getType();
            String jsonstr = gson.toJson(image,type);
            //mtest(imagePath);

            new CameraActivity.upPicture().execute(uri,jsonstr);
            Log.v("上传图片","成功");
            roundImageView.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }





    //上传异步类
    class upPicture extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            String par  = params[0];//url
            String picturejson = params[1];//string
            URL url = null;
            try {
                url = new URL(par);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String result = HttpUtil.doJsonPost(url,picturejson);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int i = Integer.parseInt(result);
            if(i==1){
                Toast.makeText(getApplicationContext(),"上传成功", Toast.LENGTH_SHORT).show();
                return;

            }else {
                if (i == -1) {
                    Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }
}
