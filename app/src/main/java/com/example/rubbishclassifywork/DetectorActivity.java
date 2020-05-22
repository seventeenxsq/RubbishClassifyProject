package com.example.rubbishclassifywork;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rubbishclassifywork.HelperClass.ImageUtils;
import com.example.rubbishclassifywork.HelperClass.result;
import com.example.rubbishclassifywork.classification.CameraActivity;
import com.example.rubbishclassifywork.classification.env.Logger;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();

  //预览尺寸
  private static final Size DESIRED_PREVIEW_SIZE = new Size(720, 720);
  private static final float TEXT_SIZE_DIP = 10;
  private Bitmap rgbFrameBitmap = null;
  private long lastProcessingTimeMs;
  private Integer sensorOrientation;

  /** Input image size of the model along x axis. */
  private int imageSizeX;
  /** Input image size of the model along y axis. */
  private int imageSizeY;
  List<result> resultlist=new ArrayList<result>();
//////////////////////////////////////////////////////////////////////////////////

//########################加载tensorflow静态库#######################################
//Load the tensorflow inference library
  static {
    System.loadLibrary("tensorflow_inference");
  }
  //#####与神经层有关的字符串，因为安卓中是按神经层的名字来找寻数据#####
  private String INPUT_NAME = "input";//input_1,input
  private String OUTPUT_NAME = "final_result";//,final_result,output_1

  private final int CLASS_NUM =42;

  float[] PREDICTIONS = new float[CLASS_NUM];    //返回预测的精度数组
  float[] PREDICTIONS2=null;

  private float[] floatValues;               //输入数据
  private int[] INPUT_SIZE = {224, 224, 3};    //输入尺寸
//####################################################################################


  @Override
  protected int getLayoutId() {
    return R.layout.tfe_ic_camera_connection_fragment;
  }
  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }
  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();
    LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
 //////初始化Bitmap//////
    //
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
  }


//################################执行预测过程########################################
  @Override
  //处理图像并传入到模型接口的过程
  protected void processImage() {
    //rgbFrameBitmap将byte
    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
    final int cropSize = Math.min(previewWidth, previewHeight);
    //rgbFrameBitmap
    //在后台处理图像和预测
    runInBackground(

        new Runnable() {
          @Override
          public void run() {
//            ///让程序停止一会儿每次执行时候显示什么都没有
            try {
              Thread.sleep(500);
            } catch (InterruptedException e) {
              return;
            }
///////////////////////////运行pb模型/////////////////////////////////////////////////////////////////////
            {
              ////
              Bitmap resized_image = ImageUtils.processBitmap(rgbFrameBitmap,224);

              //归一化后的图片数据也就是模型的输入数据
              floatValues = ImageUtils.normalizeBitmap(resized_image,224,127.5f,1.0f);

//              //这一步加载模型
              tf.feed(INPUT_NAME,floatValues,1,224,224,3);
              //compute predictions
              tf.run(new String[]{OUTPUT_NAME});
              //copy the output into the PREDICTIONS array
              //将output中的
              tf.fetch(OUTPUT_NAME,PREDICTIONS);

              //复制一份数组
              PREDICTIONS2 = Arrays.copyOf(PREDICTIONS, PREDICTIONS.length);
              //Obtained highest prediction
              final Object[] results = argmax(PREDICTIONS);
              //预测下标
              int class_index = (Integer) results[0];
              //正确率
              float confidence = (Float) results[1];
              try {
               getResult();
              } catch (JSONException e) {
                e.printStackTrace();
              }

              Log.e( "排序后的数组", String.valueOf(PREDICTIONS[1])+" "+String.valueOf(PREDICTIONS[2]));

            }

//              //结果反馈给UI线程
              runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      ///判断阈值反馈结果
                      if (resultlist.get(0).getConfidence()>0.1){
                        setResult(resultlist);
                        setImage(transNameToint(resultlist.get(0).getKind()));
                      }
                      else{
                        meurenchulai();
                      }
                    }
                  });
            readyForNextImage();
          }
        });
  }

  //返回显示结果的列表
  private List<result> getResult() throws JSONException {
    //先把列表情空
    resultlist.clear();
    //给数组排序
    bubbleSort(PREDICTIONS,CLASS_NUM);
    System.out.println("PREDICTIONS排序完成");
    for (int i=0;i<3;i++){

      result result=new result("","",0);

      Log.e( "PREDICTIONS[0] ",String.valueOf(PREDICTIONS[1]));

      result.setConfidence(PREDICTIONS[i]);
      //这个是获取到的下标
       int index=returnIndex(PREDICTIONS[i]);
      //yingshebiao[index]这个是映射出的对应的json中的索引值
      //这个是得到的真实标签的描述
      String describe=jsonbiao.getString(String.valueOf(yingshebiao[index]));
      Log.e("真实标签的描述",describe );
      String[] jieguo=describe.split("/");
      result.setKind(jieguo[0]);
      result.setName(jieguo[1]);
      //这个就是最后的结果列表
      resultlist.add(result);
    }
    for (int i=0;i<3;i++){
      Log.e("################", i +resultlist.get(i).getName()+resultlist.get(i).getKind());
    }
    return  resultlist;
  }


//####################################################################################################

  @Override//监听配置栏有无改变
  protected void onInferenceConfigurationChanged() {
    if (rgbFrameBitmap == null) {
      // Defer creation until we're getting camera frames.
      return ;
    }
  }


  @Override
  public void onClick(View v) {
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

  }

  //返回结果的Object[]{best,best_confidence}
  public Object[] argmax(float[] array){

    int best = -1;
    float best_confidence = 0.0f;

    for(int i = 0;i < array.length;i++){

      float value = array[i];

      if (value > best_confidence){

        best_confidence = value;
        best = i;
      }
    }
    //返回对象数组
    return new Object[]{best,best_confidence};
  }

  //冒泡排序
  public void bubbleSort(float[] arr, int n) {
    if (n <= 1) return;       //如果只有一个元素就不用排序了

    for (int i = 0; i < n; ++i) {
      // 提前退出冒泡循环的标志位,即一次比较中没有交换任何元素，这个数组就已经是有序的了
      boolean flag = false;
      for (int j = 0; j < n - i - 1; ++j) {        //此处你可能会疑问的j<n-i-1，因为冒泡是把每轮循环中较大的数飘到后面，
        // 数组下标又是从0开始的，i下标后面已经排序的个数就得多减1，总结就是i增多少，j的循环位置减多少
        if (arr[j] < arr[j + 1]) {        //即这两个相邻的数是逆序的，交换
          float temp = arr[j];
          arr[j] = arr[j + 1];
          arr[j + 1] = temp;
          flag = true;
        }
      }
      if (!flag) break;//没有数据交换，数组已经有序，退出排序
    }
  }

  public int returnIndex(float confidence){
    for(int i=0;i<PREDICTIONS2.length;i++){
      if (confidence==PREDICTIONS2[i])
        return i;
    }
    return -1;
  }

}
