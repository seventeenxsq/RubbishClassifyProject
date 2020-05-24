package com.example.rubbishclassifywork.classification;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.os.Trace;
import android.os.Vibrator;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rubbishclassifywork.HelperClass.result;
import com.example.rubbishclassifywork.R;
import com.example.rubbishclassifywork.classification.env.ImageUtils;
import com.example.rubbishclassifywork.classification.env.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public abstract class CameraActivity extends AppCompatActivity
    implements OnImageAvailableListener,
        Camera.PreviewCallback,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {
  private static final Logger LOGGER = new Logger();

  private static final int PERMISSIONS_REQUEST = 1;

  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  protected int previewWidth = 0;
  protected int previewHeight = 0;
  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;
  private Runnable postInferenceCallback;
  private Runnable imageConverter;
  protected Button btnFankui;

  private TextView rubbishname1, rubbishname2, rubbishname3, rubbishkind1, rubbishkind2, rubbishkind3;
  private TextView rubbishconfidence1, rubbishconfidence2, rubbishconfidence3;
  private ImageView rubbishpic;

  //////////////////////////文件转换////////////////////////
  public JSONObject jsonbiao;
  public int[] yingshebiao = new int[42];
/////////////////////////////////////////////////////


  //################################################################
  String MODEL_PATH = "file:///android_asset/retrained_graph_ali.pb";
//#################################################################

  //在这里申明tensorflowInterface接口
  protected TensorFlowInferenceInterface tf;


  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    LOGGER.d("onCreate " + this);
    super.onCreate(null);
    StatusBarUtil.transparencyBar(this);
    StatusBarUtil.BlackFontStatusBar(this.getWindow());
    if (Build.VERSION.SDK_INT > 9) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.tfe_ic_activity_camera);

    initView();

    if (hasPermission()) {
      setFragment();
    } else {
      requestPermission();
    }
//###########################实例化接口#################################
    tf = new TensorFlowInferenceInterface(getAssets(), MODEL_PATH);
    //#####################################################################
  }

  private void initView(){

    rubbishpic = findViewById(R.id.im_rubbishpic);
    btnFankui = findViewById(R.id.btn_fankui);
    rubbishpic.setImageResource(R.mipmap.meirenchulai);
    rubbishname1=findViewById(R.id.laji_name1);
    rubbishname2=findViewById(R.id.laji_name2);
    rubbishname3=findViewById(R.id.laji_name3);
    rubbishkind1=findViewById(R.id.laji_kind1);
    rubbishkind2=findViewById(R.id.laji_kind2);
    rubbishkind3=findViewById(R.id.laji_kind3);
    rubbishconfidence1=findViewById(R.id.laji_confidence1);
    rubbishconfidence2=findViewById(R.id.laji_confidence2);
    rubbishconfidence3=findViewById(R.id.laji_confidence3);


    btnFankui.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Vibrator vibrator = (Vibrator) Objects.requireNonNull(getBaseContext()).getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(30);
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(CameraActivity.this);
        builder1.setTitle("上传垃圾信息");
//
        //用布局填充器填充
        LayoutInflater factory = LayoutInflater.from(CameraActivity.this);//提示框
        //用布局充斥填充器对象使成为一个view
        final View view = factory.inflate(R.layout.twoedittext, null);//这里必须是final的
        builder1.setCancelable(false);
        builder1.setView(view);
        builder1.setNegativeButton("我也不知道", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(getBaseContext(),"感谢反馈，此图片将作为未定图片供他人参考",Toast.LENGTH_LONG).show();
          }
        });
        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            EditText editText1 = view.findViewById(R.id.tv_name);
            EditText editText2 = view.findViewById(R.id.tv_kind);
            if (!((editText1.getText().toString().equals("")) || (editText2.getText().toString().equals("")))) {
              AlertDialog.Builder builder2 = new AlertDialog.Builder(CameraActivity.this);
              builder2.setTitle("上传成功");
              builder2.setMessage("感谢您的矫正支持！\n核实成功后，将获得积分+10");
              builder2.setPositiveButton("确定", null);
              builder2.show();
            } else {
              Toast.makeText(getBaseContext(), "请输入有效内容", Toast.LENGTH_LONG).show();
            }
          }
        });
        builder1.show();
      }
    });

    //////////////////////解析json文件////////////////////////////
    try {
      InputStream isjson = Objects.requireNonNull(CameraActivity.this.getClass().getClassLoader()).
              getResourceAsStream("assets/" + "garbage_classify_rule.json");
      InputStreamReader streamReader = new InputStreamReader(isjson);
      BufferedReader reader = new BufferedReader(streamReader);
      String line;
      StringBuilder stringBuilder = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
      }
      jsonbiao = new JSONObject(String.valueOf(stringBuilder));
      String category = jsonbiao.getString("0");
      Log.e("##################", category);
      reader.close();
      isjson.close();
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }

    readAssetsTxt();


  }

  //获得像素的rgb值
  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  /**
   * Callback for android.hardware.Camera API
   */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) {
    if (isProcessingFrame) {
      LOGGER.w("Dropping frame!");
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      return;
    }

    isProcessingFrame = true;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
            new Runnable() {
              @Override
              public void run() {
                ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
              }
            };

    postInferenceCallback =
            new Runnable() {
              @Override
              public void run() {
                camera.addCallbackBuffer(bytes);
                isProcessingFrame = false;
              }
            };
    processImage();
  }

  /**
   * Callback for Camera2 API
   */
  @Override
  public void onImageAvailable(final ImageReader reader) {
    // We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
              new Runnable() {
                @Override
                public void run() {
                  ImageUtils.convertYUV420ToARGB8888(
                          yuvBytes[0],
                          yuvBytes[1],
                          yuvBytes[2],
                          previewWidth,
                          previewHeight,
                          yRowStride,
                          uvRowStride,
                          uvPixelStride,
                          rgbBytes);
                }
              };

      postInferenceCallback =
              new Runnable() {
                @Override
                public void run() {
                  image.close();
                  isProcessingFrame = false;
                }
              };

      processImage();
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      Trace.endSection();
      return;
    }
    Trace.endSection();
  }

  @Override
  public synchronized void onStart() {
    LOGGER.d("onStart " + this);
    super.onStart();
  }

  @Override
  public synchronized void onResume() {
    LOGGER.e("Camera onResume " + this);
    super.onResume();

    //开启一个叫interface的线程
    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  @Override
  public synchronized void onPause() {
    LOGGER.d("onPause " + this);

    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (final InterruptedException e) {
      LOGGER.e(e, "Exception!");
    }

    super.onPause();
  }

  @Override
  public synchronized void onStop() {
    LOGGER.d("onStop " + this);
    super.onStop();
  }

  @Override
  public synchronized void onDestroy() {
    LOGGER.d("onDestroy " + this);
    super.onDestroy();
  }

  //异步的
  protected synchronized void runInBackground(final Runnable r) {
    if (handler != null) {
      //handler将这个线程给传递出去
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult(
          final int requestCode, final String[] permissions, final int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST) {
      if (allPermissionsGranted(grantResults)) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private static boolean allPermissionsGranted(final int[] grantResults) {
    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
        Toast.makeText(
                CameraActivity.this,
                "Camera permission is required for this demo",
                Toast.LENGTH_LONG)
                .show();
      }
      requestPermissions(new String[]{PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
          CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        // We don't use a front facing camera in this sample.
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
                characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API =
                (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                        || isHardwareLevelSupported(
                        characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        LOGGER.i("Camera API lv2?: %s", useCamera2API);
        return cameraId;
      }
    } catch (CameraAccessException e) {
      LOGGER.e(e, "Not allowed to access camera");
    }
    return null;
  }

  protected void setFragment() {
    String cameraId = chooseCamera();

    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
              CameraConnectionFragment.newInstance(
                      new CameraConnectionFragment.ConnectionCallback() {
                        @Override
                        public void onPreviewSizeChosen(final Size size, final int rotation) {
                          previewHeight = size.getHeight();
                          previewWidth = size.getWidth();
                          CameraActivity.this.onPreviewSizeChosen(size, rotation);
                        }
                      },
                      this,
                      getLayoutId(),
                      getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
              new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
  }

  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  //准备好进行下一张图预测
  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {
    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }

  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

  protected abstract int getLayoutId();

  protected abstract Size getDesiredPreviewFrameSize();

  protected abstract void onInferenceConfigurationChanged();

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  //设置返回结果
  public void setResult(List<result> result) {

    rubbishname1.setText(result.get(0).getName());
    rubbishname2.setText(result.get(1).getName());
    rubbishname3.setText(result.get(2).getName());

    rubbishkind1.setText(result.get(0).getKind());
    rubbishkind2.setText(result.get(1).getKind());
    rubbishkind3.setText(result.get(2).getKind());

    String confidence1 = String.format("%1.2f", result.get(0).getConfidence()*100);
    String confidence2 = String.format("%1.2f", result.get(1).getConfidence()*100);
    String confidence3 = String.format("%1.2f", result.get(2).getConfidence()*100);

    rubbishconfidence1.setText(confidence1+"%");
    rubbishconfidence2.setText(confidence2+"%");
    rubbishconfidence3.setText(confidence3+"%");
  }

  //名字装换成int
  public int transNameToint(String category) {
    int i;
    switch (category) {
      case "可回收物":
        i = 0;
        break;
      case "其他垃圾":
        i = 1;
        break;
      case "厨余垃圾":
        i = 2;
        break;
      case "有害垃圾":
        i = 3;
        break;
      default:
        i=4;
    }
    return i;
  }

  //设置图片
  public void setImage(int kind) {
    switch (kind) {
      case 0:
        rubbishpic.setImageResource(R.drawable.recyc1);
        break;
      case 1:
        rubbishpic.setImageResource(R.drawable.other1);
        break;
      case 2:
        rubbishpic.setImageResource(R.drawable.kitc1);
        break;
      case 3:
        rubbishpic.setImageResource(R.drawable.harm1);
        break;
      case 4:
        rubbishpic.setImageResource(R.mipmap.meirenchulai);
        break;
    }
  }

  public void readAssetsTxt() {
    //Return an AssetManager instance for your application's package
    InputStream is = null;
    try {
      is = this.getAssets().open("retrained_labels.txt");
      InputStreamReader streamReader = new InputStreamReader(is);
      BufferedReader reader = new BufferedReader(streamReader);
      Scanner in = new Scanner(reader);


      for (int i = 0; i < 42; i++) {
        yingshebiao[i] = in.nextInt() - 1;
        Log.d("readAssetsTxt: ", String.valueOf(yingshebiao[i]));
      }
      is.close();streamReader.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void meurenchulai(){

    rubbishname1.setText("");
    rubbishname2.setText("");
    rubbishname3.setText("");
    rubbishkind1.setText("");
    rubbishkind2.setText("");
    rubbishkind3.setText("");
    rubbishconfidence1.setText("");
    rubbishconfidence2.setText("");
    rubbishconfidence3.setText("");
    setImage(4);
  }


}
