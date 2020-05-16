

package com.example.rubbishclassifywork.detection.detection.customview;

import com.example.rubbishclassifywork.detection.tflite.Classifier.Recognition;

import java.util.List;

//单独领出来一个接口用设置返回的数据
public interface ResultsView {
  //它传进去的是分类器返回的列表
  public void setResults(final List<Recognition> results);
}
