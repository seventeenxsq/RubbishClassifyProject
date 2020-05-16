package com.example.rubbishclassifywork.detection.tflite;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Trace;

import com.example.rubbishclassifywork.detection.env.Logger;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

//目标识别分类器类
public class TFLiteObjectDetectionAPIModel implements Classifier {
  private static final Logger LOGGER = new Logger();

  // Only return this many results.
  //这个NUM_DETECTIONS 是作为输入尺寸的一个维度，所以不能随便更改
  private static final int NUM_DETECTIONS = 10;
  // Float model
  private static final float IMAGE_MEAN = 128.0f;
  private static final float IMAGE_STD = 128.0f;
  // Number of threads in the java app
  private static final int NUM_THREADS = 4;

  private boolean isModelQuantized;
  // Config values.
  private int inputSize;
  // Pre-allocated buffers.
  private Vector<String> labels = new Vector<String>();
  private int[] intValues;
  // outputLocations: array of shape [Batchsize, NUM_DETECTIONS,4]
  // contains the location of detected boxes
  private float[][][] outputLocations;
  // outputClasses: array of shape [Batchsize, NUM_DETECTIONS]
  // contains the classes of detected boxes
  private float[][] outputClasses;
  // outputScores: array of shape [Batchsize, NUM_DETECTIONS]
  // contains the scores of detected boxes
  private float[][] outputScores;
  // numDetections: array of shape [Batchsize]
  // contains the number of detected boxes
  private float[] numDetections;

  private ByteBuffer imgData;

  private Interpreter tfLite;

  private TFLiteObjectDetectionAPIModel() {}

  /** Memory-map the model file in Assets. */
  //MappedByteBuffer继承ByteBuffer
  /**
   * 用来加载模型
   * @param assets   asset管理器
   * @param modelFilename 模型的名字
   * @return 返回的是bytebuffer 字节buffer
   * @throws IOException
   */
  private static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename)
      throws IOException {

    AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);

    FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());

    FileChannel fileChannel = inputStream.getChannel();
    long startOffset = fileDescriptor.getStartOffset();
    long declaredLength = fileDescriptor.getDeclaredLength();
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
  }

  /**
   * Initializes a native TensorFlow session for classifying images.
   *##########################################################################
   * @param assetManager The asset manager to be used to load assets.
   * @param modelFilename The filepath of the model GraphDef protocol buffer. pb模型名称
   * @param labelFilename The filepath of label.txt file for classes.     类别标签名
   * @param inputSize The size of image input       输入尺寸
   * @param isQuantized Boolean representing model is quantized or not
   */
  public static Classifier create(
      final AssetManager assetManager,
      final String modelFilename,
      final String labelFilename,
      final int inputSize,
      final boolean isQuantized)
      throws IOException {
    //我用我自己
    final TFLiteObjectDetectionAPIModel apimodel = new TFLiteObjectDetectionAPIModel();

    //上面那句代码的原因所以在输入labelFilename的时候必须加上"file:///android_asset/"路径
    String actualFilename = labelFilename.split("file:///android_asset/")[1];
    //labelsInput标签输入
    InputStream labelsInput = assetManager.open(actualFilename);
    BufferedReader br = new BufferedReader(new InputStreamReader(labelsInput));
    String line;
    while ((line = br.readLine()) != null) {
      LOGGER.w(line);
      apimodel.labels.add(line);
    }
    br.close();

    apimodel.inputSize = inputSize;

    try {
      apimodel.tfLite = new Interpreter(loadModelFile(assetManager, modelFilename));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    apimodel.isModelQuantized = isQuantized;
    // Pre-allocate buffers.
    //numBytesPerChannel根据是否量化模型来判断numBytesPerChannel=1还是4
    int numBytesPerChannel;
    //如果是量化模型numBytesPerChanne=l否则numBytesPerChannel=4
    if (isQuantized) {
      numBytesPerChannel = 1; // Quantized
    } else {
      numBytesPerChannel = 4; // Floating point
    }

///////////////////////imgData///////////////////
    apimodel.imgData = ByteBuffer.allocateDirect(1* apimodel.inputSize * apimodel.inputSize * 3 * numBytesPerChannel);
    apimodel.imgData.order(ByteOrder.nativeOrder());
    apimodel.intValues = new int[apimodel.inputSize * apimodel.inputSize];

    apimodel.tfLite.setNumThreads(NUM_THREADS);

    //这是一个接受池
    apimodel.outputLocations = new float[1][NUM_DETECTIONS][4];
    apimodel.outputClasses = new float[1][NUM_DETECTIONS];
    apimodel.outputScores = new float[1][NUM_DETECTIONS];
    apimodel.numDetections = new float[1];
    return apimodel;
  }

  @Override
//########################传入像素#####################
  //###############结果返回的是recognition的列表##########
  public List<Recognition> recognizeImage(final Bitmap bitmap) {
    // Log this method so that it can be analyzed with systrace.
    Trace.beginSection("recognizeImage");

    Trace.beginSection("preprocessBitmap");
    // Preprocess the image data from 0-255 int to normalized float based
    // on the provided parameters.
    bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

    imgData.rewind();
    //将输入的bitmap，取红绿蓝通道转换成byteValues 作为输入Tensor
    for (int i = 0; i < inputSize; ++i) {
      for (int j = 0; j < inputSize; ++j) {
        int pixelValue = intValues[i * inputSize + j];

    /////////在这里评判是否量化模型/////////
        if (isModelQuantized) {
          // Quantized model
          imgData.put((byte) ((pixelValue >> 16) & 0xFF));
          imgData.put((byte) ((pixelValue >> 8) & 0xFF));
          imgData.put((byte) (pixelValue & 0xFF));
        } else { // Float model
          imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
          imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
          imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
        }
      }
    }
    Trace.endSection(); // preprocessBitmap

    // Copy the input data into TensorFlow.
    //feed喂入Tensor  -
    Trace.beginSection("feed");
    outputLocations = new float[1][NUM_DETECTIONS][4];
    outputClasses = new float[1][NUM_DETECTIONS];
    outputScores = new float[1][NUM_DETECTIONS];
    numDetections = new float[1];

    Object[] inputArray = {imgData};
    Map<Integer, Object> outputMap = new HashMap<>();
    outputMap.put(0, outputLocations);
    outputMap.put(1, outputClasses);
    outputMap.put(2, outputScores);
    outputMap.put(3, numDetections);
    Trace.endSection();

    // Run the inference call.
    //Run 运行模型
    Trace.beginSection("run");
    //outputMap就是被转换成tensor的bitmap图像
    tfLite.runForMultipleInputsOutputs(inputArray, outputMap);
    Trace.endSection();

    // Show the best detections.
    // after scaling them back to the input size.
      
    // You need to use the number of detections from the output and not the NUM_DETECTONS variable declared on top
      // because on some models, they don't always output the same total number of detections
      // For example, your model's NUM_DETECTIONS = 20, but sometimes it only outputs 16 predictions
      // If you don't use the output's numDetections, you'll get nonsensical data
    int numDetectionsOutput = Math.min(NUM_DETECTIONS, (int) numDetections[0]); // cast from float to integer, use min for safety

////#########获取返回的结果#############
    final ArrayList<Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
    for (int i = 0; i < numDetectionsOutput; ++i) {
      //返回值是比例不是确切值，所以*上inputsize
      final RectF detection =
          new RectF(
              outputLocations[0][i][1] * inputSize,
              outputLocations[0][i][0] * inputSize,
              outputLocations[0][i][3] * inputSize,
              outputLocations[0][i][2] * inputSize);
      // SSD Mobilenet V1 Model assumes class 0 is background class
      // in label.txt file and class labels start from 1 to number_of_classes+1,
      // while outputClasses correspond to class index from 0 to number_of_classes
      //这里给得出的结果标签整体加1
      //就是是说，我的标签集还是从0开始就行了
      int labelOffset = 1;
  ////得到Recognition对象 添加到列表中
      recognitions.add(
          new Recognition(
              "" + i,
              labels.get((int) outputClasses[0][i] + labelOffset),   //
              outputScores[0][i],
              detection));
    }
    Trace.endSection(); // "recognizeImage"
    return recognitions;
  }


  @Override
  public void enableStatLogging(final boolean logStats) {}

  @Override
  public String getStatString() {
    return "";
  }

  @Override
  public void close() {}


  public void setNumThreads(int num_threads) {
    if (tfLite != null) tfLite.setNumThreads(num_threads);
  }

  @Override
  public void setUseNNAPI(boolean isChecked) {
    if (tfLite != null) tfLite.setUseNNAPI(isChecked);
  }
}
