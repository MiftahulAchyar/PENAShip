package com.penship.bismillahsukses;

/**
 * Created by asus on 10/25/2016.
 */
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import org.opencv.android.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;


import java.util.ArrayList;
import java.util.List;

public class BismillahContourFrame extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private CameraBridgeViewBase mOpenCvCameraView;

    private Mat                    mRgba;
    private Mat                    mIntermediateMat;
//    private Mat                    mGray;
    Mat hierarchy;


    List<MatOfPoint> contours;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contour_frame);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this,
                mLoaderCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }



    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
      //  mGray = new Mat(height, width, CvType.CV_8UC1);
        hierarchy = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        //mGray.release();
        mIntermediateMat.release();
        hierarchy.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.gray();
        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();

        Imgproc.Canny(mRgba, mIntermediateMat, 80, 100);
        Imgproc.findContours(mIntermediateMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

    /* Mat drawing = Mat.zeros( mIntermediateMat.size(), CvType.CV_8UC3 );
     for( int i = 0; i< contours.size(); i++ )
     {
    Scalar color =new Scalar(Math.random()*255, Math.random()*255, Math.random()*255);
     Imgproc.drawContours( drawing, contours, i, color, 2, 8, hierarchy, 0, new Point() );
     }*/


        hierarchy.release();
        Point offset = new Point();
        Imgproc.drawContours(mRgba, contours, -1, new Scalar(Math.random()*255, Math.random()*0, Math.random()*0), 3);//, 2, 8, hierarchy, 0, new Point());
        // Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);


        return mRgba;
    }

}