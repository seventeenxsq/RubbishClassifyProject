package com.example.rubbishclassifywork.Adaper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import static android.content.ContentValues.TAG;

/**
 * 通过MotionEvent获得手指触碰更改位置坐标
 * 移动控件
 */
public class MoveScrollView extends NestedScrollView {

    int lastX,lastY;

    //接口对象
    private getTopListener getTopListener = null;

    public MoveScrollView(Context context) {
        super(context);
    }
    public MoveScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 重写手势方法
     * @param event
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //x,y是当时view得绝对坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        //判断的绝对坐标
        int scaleY=getTopListener.stopView().getHeight();

        //判断动作并分发
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                lastY=y;
                Log.e(TAG, "onTouchEvent:ACTION_DOWN getTop"+getTop());
                break;

            case MotionEvent.ACTION_MOVE:
                int offsetY = y - lastY;

                if (getTop()+offsetY>scaleY){
//                    layout(getLeft(),
//                            scaleY,
//                            getRight(),
//                            scaleY+getHeight());
//                    Log.e(TAG, "画的getHeight(): "+String.valueOf(getHeight()));
//                    Log.e(TAG, "scaleY "+scaleY);
//                    Log.e(TAG, "getTop(): "+String.valueOf(getTop()));
                break;
            } else if(getTop()<=scaleY) {
                    layout(getLeft(),
                            getTop()+offsetY,
                            getRight(),
                            getBottom()+offsetY);
                    Log.e(TAG, "getTop "+getTop()+"   getBottom"+getBottom());
                    Log.e(TAG, "scaleY "+scaleY);
                    Log.e(TAG, ",y轴的偏移量为" + offsetY + ",移动的过程中y坐标为"+y);
                }

                //展示标题判断
                if (getTopListener != null) {
                    // 在这里将方法暴露出去
                    getTopListener.showTitle(this);
                }
                break;

            case MotionEvent.ACTION_UP:
                this.performClick();
                Log.e(TAG,"ACTION_UP");
                break;
        }
        return true;
    }


    //将监听scroll活动的接口暴露出去
    //展示标题的接口
    public interface getTopListener  {
        //接口中的方法,参数只要y周坐标就行
        void showTitle(View scrollView);
        //让它返回view
        View stopView();
    }


    public void setScrollViewListener(getTopListener listener) {
        getTopListener = listener;
    }
}
