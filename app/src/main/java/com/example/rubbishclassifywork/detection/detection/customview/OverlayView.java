package com.example.rubbishclassifywork.detection.detection.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/** A simple View providing a render callback to other classes. */
//一个给其他VIew提供接口回调的help类
public class OverlayView extends View {

  //这个里面有一系列的画图回调函数 DrawCallback是接口对象
  private final List<DrawCallback> callbacks = new LinkedList<DrawCallback>();

  public OverlayView(final Context context, final AttributeSet attrs) {
    super(context, attrs);
  }

  //添加接口，将其他类的参数传到自己的类中来
  public void addCallback(final DrawCallback callback) {
    callbacks.add(callback);
  }

  @Override
  //绘制重写了绘制方法 （View会在显示前使用draw方法）
  public synchronized void draw(final Canvas canvas) {
    for (final DrawCallback callback : callbacks) {
      callback.drawCallback(canvas);
    }
    super.draw(canvas);
  }

  /** Interface defining the callback for client classes. */
  public interface DrawCallback {
    public void drawCallback(final Canvas canvas);
  }
}
