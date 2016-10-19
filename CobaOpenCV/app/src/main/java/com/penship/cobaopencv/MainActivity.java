package com.penship.cobaopencv;

import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    // Untuk mode portrait
    // PortraitCameraView javaCameraView;

    // Untuk mode landscape
    JavaCameraView javaCameraView;
    Mat mRgba;
    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:{
                    javaCameraView.enableView();
                    break;
                }
                default: {super.onManagerConnected(status); break;}
            }


        }
    };

    private static final String TAG = "MainAct";
    static {
        if (!OpenCVLoader.initDebug())
            Log.e(TAG, "OPEN CV Not Loaded");
        else
            Log.e(TAG, "OPEN CV Loaded");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Untuk mode Portrain
//        setContentView(R.layout.activity_main);

        //Untuk mode Landscape
        setContentView(R.layout.activity_main2);

        javaCameraView = (JavaCameraView) findViewById(R.id.cameraview);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.setPivotX(90.0f);

//        android.hardware.Camera camera = javaCameraView;
//        setDisplayOrientation(javaCameraView,90);
    }


    protected void setDisplayOrientation(Camera camera, int angle){
        Method downPolymorphic;
        try
        {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[] { angle });
        }
        catch (Exception e1)
        {
            Log.e(TAG, e1.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView!=null)
            javaCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView!=null)
            javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "Open cv loaded");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else {
            Log.i(TAG, "Open cv NOT loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, baseLoaderCallback);
        }
    }

    private Mat mRotated;

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height,width, CvType.CV_8UC4);

    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }
//
//    @Override
//    public Mat onCameraFrame(Mat inputFrame) {
//        return null;
//    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        return mRgba;
    }

    public Mat getmRgba() {

        return mRgba;
    }
}
