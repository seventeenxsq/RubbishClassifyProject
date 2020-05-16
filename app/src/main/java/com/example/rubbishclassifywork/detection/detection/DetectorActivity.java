package com.example.rubbishclassifywork.detection.detection;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;


import com.example.rubbishclassifywork.R;
import com.example.rubbishclassifywork.detection.detection.customview.OverlayView;
import com.example.rubbishclassifywork.detection.detection.customview.OverlayView.DrawCallback;
import com.example.rubbishclassifywork.detection.env.BorderedText;
import com.example.rubbishclassifywork.detection.env.ImageUtils;
import com.example.rubbishclassifywork.detection.env.Logger;
import com.example.rubbishclassifywork.detection.env.MultiBoxTracker;
import com.example.rubbishclassifywork.detection.tflite.Classifier;
import com.example.rubbishclassifywork.detection.tflite.TFLiteObjectDetectionAPIModel;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/// 它继承了CameraActivity所以他才是真正的Activity
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();
  //////////////////////////////////////////////////////////
  // Configuration values for the prepackaged SSD model.
  //网络有关的参数TF_OD_API_INPUT_SIZE网络输入尺寸
  private static final int TF_OD_API_INPUT_SIZE = 300;

  //////模型文件名TF_OD_API_MODEL_FILE 标签名TF_OD_API_LABELS_FILEs
  private static final String TF_OD_API_MODEL_FILE = "detector.tflite";
  private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/rubbishlabel.txt";
  private static final DetectorMode MODE = DetectorMode.TF_OD_API;
  //最小确信度的阈值
  private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.6f;
  private static final float MAXMUM_CONFIDENCE_TF_OD_API = 1f;
///表示模型是否量化 不造啥意思
  private static final boolean TF_OD_API_IS_QUANTIZED = false;


  private static final boolean MAINTAIN_ASPECT = false;
  private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
  //是否保留图片
  private static final boolean SAVE_PREVIEW_BITMAP = false;
  private static final float TEXT_SIZE_DIP = 10;

  OverlayView trackingOverlay;
  private Integer sensorOrientation;

  //##################分类器接口###############
  private Classifier detector;

  //每次的测试时间
  private long lastProcessingTimeMs;

  //rgb的Bitmap
  private Bitmap rgbFrameBitmap = null;
  //裁剪的 bitmap
  private Bitmap croppedBitmap = null;
  //裁剪的bitmap复制
  private Bitmap cropCopyBitmap = null;

  //侦测
  private boolean computingDetection = false;
  //时间戳
  private long timestamp = 0;

  //两个矩阵
  private Matrix frameToCropTransform;
  private Matrix cropToFrameTransform;

  private MultiBoxTracker tracker;

  private BorderedText borderedText;

  @Override  public void onPreviewSizeChosen(final Size size, final int rotation) {
    final float textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    tracker = new MultiBoxTracker(this);

    //cropSize裁剪的大小
    int cropSize = TF_OD_API_INPUT_SIZE;

    //创建分类器
    try {
      detector =
          TFLiteObjectDetectionAPIModel.create(
              getAssets(),
              TF_OD_API_MODEL_FILE,
              TF_OD_API_LABELS_FILE,
              TF_OD_API_INPUT_SIZE,
              TF_OD_API_IS_QUANTIZED);
      cropSize = TF_OD_API_INPUT_SIZE;
    } catch (final IOException e) {
      e.printStackTrace();
      LOGGER.e(e, "Exception initializing classifier!");
      Toast toast =
          Toast.makeText(
              getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
      toast.show();
      finish();
    }

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();
    LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
    croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

    frameToCropTransform =
        ImageUtils.getTransformationMatrix(
            previewWidth, previewHeight,
            cropSize, cropSize,
            sensorOrientation, MAINTAIN_ASPECT);

    cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);

    //////关联结果的布局tracking_overlay里面监听回调接口
    trackingOverlay = findViewById(R.id.tracking_overlay);
    /////###########添加回调接口##################让trcker去绘制
    trackingOverlay.addCallback(
        new DrawCallback() {
          @Override
          public void drawCallback(final Canvas canvas) {
            tracker.draw(canvas);
          }
        });

    tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
  }

  /**
   * 回调接口处理图像
   */
  @Override
  protected void processImage() {
    ++timestamp;
    final long currTimestamp = timestamp;
    trackingOverlay.postInvalidate();

    // No mutex needed as this method is not reentrant.
    if (computingDetection) {
      readyForNextImage();
      return;
    }
    computingDetection = true;
    LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

    readyForNextImage();

    final Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    // For examining the actual TF input.
    if (SAVE_PREVIEW_BITMAP) {
      ImageUtils.saveBitmap(croppedBitmap);
    }

    //#######后台进程中运行的图像检测########
    runInBackground(
        new Runnable() {
          @Override
          public void run() {
            LOGGER.i("Running detection on image " + currTimestamp);
            final long startTime = SystemClock.uptimeMillis();

            final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);

            lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
            //cropCopyBitmap剪裁后的图片的备份
            cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
            final Canvas canvas = new Canvas(cropCopyBitmap);
            final Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Style.STROKE);
            //画笔的粗细
            paint.setStrokeWidth(1.0f);

            ////确信度阈值（默认是MINIMUM_CONFIDENCE_TF_OD_API）
            float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
            switch (MODE) {
              case TF_OD_API:
                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                break;
            }

            //mappedRecognitions是过滤掉小于阈值的那些预测
            final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();

            //####这里执行过滤预测######
            for (final Classifier.Recognition result : results) {
              final RectF location = result.getLocation();
              if (location != null && result.getConfidence() >= minimumConfidence && result.getConfidence()<=MAXMUM_CONFIDENCE_TF_OD_API) {
                canvas.drawRect(location, paint);

                cropToFrameTransform.mapRect(location);

                result.setLocation(location);
                mappedRecognitions.add(result);
              }
            }

        ///############这里是
            tracker.trackResults(mappedRecognitions, currTimestamp);
            trackingOverlay.postInvalidate();
            computingDetection = false;

          }
        });
  }

  @Override
  protected int getLayoutId() {
    return R.layout.tfe_od_camera_connection_fragment_tracking;
  }

  @Override
  /**
   * DESIRED_PREVIEW_SIZE （640，480）
   */
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  @Override
  public void onClick(View v) {

  }


  // Which detection model to use: by default uses Tensorflow Object Detection API frozen
  // checkpoints.
  private enum DetectorMode {
    TF_OD_API;
  }


  @Override
  protected void setUseNNAPI(final boolean isChecked) {
    runInBackground(() -> detector.setUseNNAPI(isChecked));
  }


  @Override
  protected void setNumThreads(final int numThreads) {
    runInBackground(() -> detector.setNumThreads(numThreads));
  }
}
