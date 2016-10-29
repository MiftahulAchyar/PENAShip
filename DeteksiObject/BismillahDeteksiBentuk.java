package com.penship.bismillahsukses;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.rectangle;

/**
 * Created by asus on 10/25/2016.
 */

public class BismillahDeteksiBentuk  extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private CameraBridgeViewBase cameraView;

    private Mat mat;
    private Mat grayMat;
    private int frameWidth, frameHeigth;
    //    private Mat                    mGray;
//    Mat hierarchy;


    List<MatOfPoint> contours;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    cameraView.enableView();
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

        cameraView = (CameraBridgeViewBase) findViewById(R.id.java_surface_view);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV Sukses");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this,
                    mLoaderCallback);
        }else {
            Log.e(TAG, "OpenCV GAGAL");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraView != null)
            cameraView.disableView();
    }



    @Override
    public void onCameraViewStarted(int width, int height) {

        mat = new Mat(height, width, CvType.CV_8UC1);
        grayMat = new Mat(height, width, CvType.CV_8UC1);

        frameHeigth = height;
        frameWidth = width;

    }

    @Override
    public void onCameraViewStopped() {
        mat.release();
        //mGray.release();
        grayMat.release();
//        hierarchy.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mat = inputFrame.rgba();
        grayMat= inputFrame.gray();

        // Convert to grayscale
        int colorChannels = (mat.channels()==3)? Imgproc.COLOR_BGR2GRAY :
                ((mat.channels()==4)?Imgproc.COLOR_BGR2GRAY:1);
        Log.e("colorChannels", colorChannels+"");
        if (mat!=null && grayMat!=null){
            Log.e("MAT", "GAK NULL");
            Imgproc.cvtColor(mat, grayMat, colorChannels);

            // Reduce the noise so we avoid false circle detection
            Imgproc.GaussianBlur(grayMat, grayMat, new Size(9,9),2,2);

            // acumulator value
            double dp = 1.2d;
            // minimum distance between the center coordinates of detected circles in pixels
            double minDist=100;

            // min & max radius (set these values as you desire)
            int minRadius =20, maxRadius=0;

            // param1 = gradient value used to handle edge detection
            // param2 = Accumulator threshold value for the
            // cv2.CV_HOUGH_GRADIENT method.
            // The smaller the threshold is, the more circles will be
            // detected (including false circles).
            // The larger the threshold is, the more circles will
            // potentially be returned.
            double param1 = 70, param2 = 72;

        /* create a Mat object to store the circles detected */
            Mat circles = new Mat(frameWidth, frameHeigth
                    , CvType.CV_8UC1);

/* find the circle in the image */
            Imgproc.HoughCircles(grayMat, circles,
                    Imgproc.CV_HOUGH_GRADIENT, dp, minDist, param1,
                    param2, minRadius, maxRadius );

            /* get the number of circles detected */
            int numberOfCircles = (circles.rows() == 0) ? 0 : circles.cols();
            /* draw the circles found on the image */
            //            for (int i=0; i<numberOfCircles; i++) {
            //            if (numberOfCircles<=2)
                for (int i=0; i<2; i++) {

                    /* get the circle details, circleCoordinates[0, 1, 2] = (x,y,r)
                     * (x,y) are the coordinates of the circle's center
                     */
                    double[] circleCoordinates = circles.get(0, i);
                    if(circleCoordinates!=null){
                        int x = (int) circleCoordinates[0], y = (int) circleCoordinates[1];
                        Point center = new Point(x, y);
//                    int radius = (int) circleCoordinates[2];
                        int radius = 100;
            /* circle's outline */
                        //            circle()
                         circle(mat, center, radius, new Scalar(0, 255, 0), 4);

        /* circle's center outline */
                        rectangle(mat, new Point(x - 5, y - 5),
                                new Point(x + 5, y + 5),
                                new Scalar(0, 128, 255), -1);
                    }


                }

        }


//        Nativecleshpdetect(mat.getNativeObjAddr()); // native method call to perform color and object detection// the method getNativeObjAddr gets the address of the Mat object(camera frame) and passes it to native side as long object so that you dont have to create and destroy Mat object on each frame
        return mat;
    }

//    public native void Nativecleshpdetect(long matAddrRgba);


}