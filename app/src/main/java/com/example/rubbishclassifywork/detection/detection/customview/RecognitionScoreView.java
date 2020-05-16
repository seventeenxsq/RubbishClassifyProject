
package com.example.rubbishclassifywork.detection.detection.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.rubbishclassifywork.detection.tflite.Classifier.Recognition;

import java.util.List;

public class RecognitionScoreView extends View implements ResultsView {
  private static final float TEXT_SIZE_DIP = 14;
  private final float textSizePx;

  //#######两个paint对象  前、后两个层两个paint对象
  private final Paint fgPaint;
  private final Paint bgPaint;
  private List<Recognition> results;


//##############绘制结果的函数#################
  public RecognitionScoreView(final Context context, final AttributeSet set) {
    super(context, set);

    textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    fgPaint = new Paint();
    fgPaint.setTextSize(1000);

    bgPaint = new Paint();
    //bgPaint.setColor(0xcc4285f4);
  }

  //result返回
  @Override
  public void setResults(final List<Recognition> results) {
    //将别的类中的结果给复制到本类中
    this.results = results;
 //####postInvalidate()是重绘的，也就是调用postInvalidate()后系统会重新调用onDraw方法画一次
    postInvalidate();
  }

  @Override
  //绘制矩形图案
  public void onDraw(final Canvas canvas) {
    final int x = 10;
    int y = (int) (fgPaint.getTextSize() * 1.5f);

    canvas.drawPaint(bgPaint);

    if (results != null) {
     // 循环绘制
      for (final Recognition recog : results) {
        canvas.drawText(recog.getTitle() + ": " + recog.getConfidence(), x, y, fgPaint);
        y += (int) (fgPaint.getTextSize() * 1.5f);
      }
    }
  }
}
