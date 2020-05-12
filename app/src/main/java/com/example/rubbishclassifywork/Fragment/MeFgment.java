package com.example.rubbishclassifywork.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.rubbishclassifywork.HelperClass.ContentAdapter;
import com.example.rubbishclassifywork.HelperClass.ContentModel;
import com.example.rubbishclassifywork.HelperClass.PhotoPopupWindow;
import com.example.rubbishclassifywork.MainActivity;
import com.example.rubbishclassifywork.R;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MeFgment extends Fragment implements View.OnClickListener {
    private ImageView blurImageView,avatarImageView;
    private List<ContentModel> mlist;
    private ListView listView;
    private ContentAdapter madapter;

    @Override//将fragment与布局文件联系起来
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        View view=inflater.inflate(R.layout.fragment_me,container,false);
        initData();
        initView(view);

        Glide.with(getContext()).load(R.drawable.lemonicon)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(avatarImageView);
        return view;
    }

    private void initView(View view) {
        blurImageView=view.findViewById(R.id.blur_head);
        avatarImageView=view.findViewById(R.id.h_head);
        listView=view.findViewById(R.id.lv_info);
        madapter=new ContentAdapter(getActivity(),mlist);
        listView.setAdapter(madapter);
        avatarImageView.setOnClickListener(this);
    }

    private void initData(){
        mlist=new ArrayList();
        mlist.add(new ContentModel(R.mipmap.qiandao,"签到",R.mipmap.next,0));
        mlist.add(new ContentModel(R.mipmap.jifens,"积分",R.mipmap.next,1));
        mlist.add(new ContentModel(R.mipmap.dengji,"称号",R.mipmap.next,2));
        mlist.add(new ContentModel(R.mipmap.shangcheng,"商城",R.mipmap.next,3));
        mlist.add(new ContentModel(R.mipmap.shezhi,"设置",R.mipmap.next,4));
        mlist.add(new ContentModel(R.mipmap.gerenxinxi,"个人信息",R.mipmap.next,5));
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
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch ((int)id){
                    case 1:


                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    default:
                        break;
                }
            }
        });

    }
}
