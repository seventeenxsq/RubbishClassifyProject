package com.example.rubbishclassifywork.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.rubbishclassifywork.HelperClass.AnalysisUtils;
import com.example.rubbishclassifywork.HelperClass.ContentAdapter;
import com.example.rubbishclassifywork.HelperClass.ContentModel;
import com.example.rubbishclassifywork.HelperClass.DBUtils;
import com.example.rubbishclassifywork.HelperClass.ListInfoAdapter;
import com.example.rubbishclassifywork.HelperClass.PhotoPopupWindow;
import com.example.rubbishclassifywork.HelperClass.User;
import com.example.rubbishclassifywork.LoginActivity;
import com.example.rubbishclassifywork.R;
import com.example.rubbishclassifywork.StoreActivity;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;

public class MeFgment extends Fragment implements View.OnClickListener {
    private ImageView blurImageView,avatarImageView;
    private List<ContentModel> mlist;
    private List<ContentModel> listInfoAdapters;
    private ListView listView,listView_head;
    private ContentAdapter madapter;
    private ListInfoAdapter listInfoAdapter;
    private TextView tv_login,tv_phonenumber;
    private String spphonenumber,spusername;

    @Override//将fragment与布局文件联系起来
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        View view=inflater.inflate(R.layout.fragment_me,container,false);
        initData();
        initView(view);
        if(readLoginStatus()){
            tv_login.setText("见圾行事");
            tv_phonenumber.setText("手机号:"+AnalysisUtils.readLoginUserName(getContext()));
        }
        Glide.with(getContext()).load(R.drawable.lemonicon)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(avatarImageView);
        return view;
    }

    private void initView(View view) {
        spphonenumber= AnalysisUtils.readLoginUserName(getContext());
        blurImageView=view.findViewById(R.id.blur_head);
        tv_phonenumber=view.findViewById(R.id.tv_phonenumber);
        tv_login=view.findViewById(R.id.nick_name);
        avatarImageView=view.findViewById(R.id.h_head);
        listView_head=view.findViewById(R.id.lv_nobtn);
        listView=view.findViewById(R.id.lv_info);
        madapter=new ContentAdapter(getActivity(),mlist);
        listView.setAdapter(madapter);
        listInfoAdapter=new ListInfoAdapter(getActivity(),listInfoAdapters);
        listView_head.setAdapter(listInfoAdapter);
        avatarImageView.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch ((int)id){
                    case 0:
                        break;
                    case 1:
                        Intent intent=new Intent(getContext(),StoreActivity.class);
                        startActivity(intent);
                        break;
                    case 2:

                        break;
                    case 3:
                    default:
                        break;
                }
            }
        });
    }

    private boolean readLoginStatus(){
        SharedPreferences sp= (SharedPreferences) getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        boolean isLogin=sp.getBoolean("isLogin",false);
        return isLogin;
    }


    private void initUserData(){
        User bean=null;
        bean= DBUtils.getInstance(getContext()).getUserInfo(spphonenumber);
        if (bean==null){

            bean =new User();
            bean.userName=spphonenumber;
            bean.nickName="环保小卫士";
            bean.sex="男";
            bean.signature="创意生活 文明分类";
            bean.jifen=0;
            DBUtils.getInstance(getContext()).saveUserInfo(bean);
        }

    }
    /**
     * 为界面控件设置值
     */
    private void setValue(User bean){
        //String url = "http://106.13.235.119:8080/Server/MyJifenServlet?username=" + bean.userName;
//        tv_user_name.setText(bean.userName);
//        tv_nickName.setText(bean.nickName);
//        tv_sex.setText(bean.sex);
//        tv_signature.setText(bean.signature);
//        new UserInfoActivity.GetjifenTask().execute(url);
        tv_login.setText(bean.userName);
        tv_phonenumber.setText("手机号:"+bean.userName);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            String textData = data.getStringExtra("textData");
            //Toast.makeText(getContext(),textData,Toast.LENGTH_SHORT).show();
            tv_login.setText("见圾行事");
            tv_phonenumber.setText("手机号:"+textData);

        }

    }


    private void initData(){
        mlist=new ArrayList();
        mlist.add(new ContentModel(R.mipmap.qiandao,"签到",R.mipmap.next,0));
        mlist.add(new ContentModel(R.mipmap.shangcheng,"商城",R.mipmap.next,1));
        mlist.add(new ContentModel(R.mipmap.shezhi,"设置",R.mipmap.next,2));
        mlist.add(new ContentModel(R.mipmap.gerenxinxi,"个人信息",R.mipmap.next,3));
        listInfoAdapters=new ArrayList();
        listInfoAdapters.add(new ContentModel(R.mipmap.qiandao,"积分","100",0));
        listInfoAdapters.add(new ContentModel(R.mipmap.jifens,"称号","分类小能手",1));
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.h_head:
                PhotoPopupWindow mPhotoPopupWindow = new PhotoPopupWindow(getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 进入相册选择
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 拍照
                    }
                });
                View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_main, null);
                mPhotoPopupWindow.showAtLocation(rootView,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.nick_name:
                Intent intent=new Intent(getContext(), LoginActivity.class);
                startActivityForResult(intent,1);
                break;
        }


    }
}
