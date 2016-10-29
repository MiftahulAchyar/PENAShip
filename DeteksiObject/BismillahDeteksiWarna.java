package com.penship.bismillahsukses;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

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

import static org.opencv.core.Core.addWeighted;
import static org.opencv.core.Core.inRange;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2GRAY;
import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.HoughCircles;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.line;
import static org.opencv.imgproc.Imgproc.medianBlur;
import static org.opencv.imgproc.Imgproc.putText;
import static org.opencv.imgproc.Imgproc.rectangle;

/**
 * Created by asus on 10/25/2016.
 */

public class BismillahDeteksiWarna extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private CameraBridgeViewBase cameraView;

    private Mat mat;
    private Mat hsvMat;
    private int frameWidth, frameHeight;

    private boolean				 modoReconocimiento = false; //Modo de reconocimiento colores. Preciso(true) o Rango(false). por defecto empezamos en preciso(true)
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        hsvMat = new Mat(height, width, CvType.CV_8UC1);

        frameHeight = height;
        frameWidth = width;

    }

    @Override
    public void onCameraViewStopped() {
        mat.release();
        //mGray.release();
        hsvMat.release();
//        hierarchy.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mat = inputFrame.rgba();
//        hsvMat= inputFrame.gray();

        /*
        * KIRI 2
        * */
        int alto = frameHeight/ 2;	//camera.getHeight() / 2;
        int ancho = frameWidth /2;	//camera.getWidth() / 2;
        double[] color = mat.get(alto, ancho);
//        Log.i(TAG , "WARNA KIRI -->"+ color[0] +";"+ color[1] +";"+ color[2] +"");
        String nombreColor = getColorName(color[0], color[1], color[2]);
        Log.i(TAG , "WARNA KIRI -->"+nombreColor);
        putText(mat, nombreColor, new Point(ancho, 50), 3, 1, new Scalar(255, 255, 255, 255), 2);

        double[] colorInverso = { 255 - color[0], 255 - color[1], 255 - color[2], 255};
        //Lineas Horizontales
//        line(mat, new Point(0, frameHeight), new Point(frameWidth - 25, frameHeight), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Izquierda
//        line(mat, new Point(frameWidth + 25, frameHeight), new Point(frameWidth + frameWidth, frameHeight), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Derecha
//
//	    	//Lineas Verticales
//        line(mat, new Point(frameWidth, 0), new Point(frameWidth, frameHeight - 25), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Top
//        line(mat, new Point(frameWidth, frameHeight + 25), new Point(frameWidth, frameHeight + frameHeight), new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1, 1, 1); //Bottom

        //Circulo interno
        circle(mat, new Point(ancho, alto), 3, new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), -1);

        //Circulo externo
        circle(mat, new Point(ancho, alto), 50, new Scalar(colorInverso[0], colorInverso[1], colorInverso[2]), 1);
        String texto = "RGB: " + color[0] + " " + color[1] + " " + color[2];
        putText(mat, texto, new Point(10, 50), 3, 1, new Scalar(255, 255, 255, 255), 2);
        rectangle(mat, new Point( 10 , 80), new Point(frameWidth - 10, 100), new Scalar(color[0], color[1], color[2], 255), -1); //Al pintar, usamos RGBA




        return mat;
    }



//    public native void Nativecleshpdetect(long matAddrRgba);
    public String getColorName(double r, double g, double b){

    String nombreColor = null;

    if(modoReconocimiento){ //Modo Preciso
        //Putih
        if(r > 140.0 && g > 140.0 && b > 140.0){
            if(r > 200.0 && g > 200.0 && b > 200.0){
                nombreColor = "Putih Murni";
            }else{
                nombreColor = "Putih";
            }
        }

        //Negro
        if(r < 50.0 && g < 50.0 && b < 50.0){
            nombreColor = "Negro";
        }

        //Merah
        if(r > 100.0 && g < 100.0 && b < 100.0){
            nombreColor = "Merah";
        }

        //Hijau
        if(r < 100.0 && g > 100.0 && b < 100.0){
            nombreColor = "Hijau";
        }

        //Biru
        if(r < 100.0 && g < 100.0 && b > 100.0){
            nombreColor = "Biru";
        }

        //Kuning
        if(r > 180.0 && r < 230.0 && g > 200.0 && g < 230.0 && b < 30.0){
            nombreColor = "Kuning";
        }
        //Cyan
        if(r < 10.0 && g > 200.0 && g < 230.0 && b > 230.0 && b < 240.0){
            nombreColor = "Cyan";
        }
        //Magenta
        if(r > 200.0 && r < 220.0 && g > 30.0 && g < 50.0 && b > 220.0 && b < 240.0){
            nombreColor = "Magenta";
        }

    }else{ //Modo Rangos de Colores

        // Calculamos a partir del Hue, en vez del valor... As� tomamos rangos
        // http://en.wikipedia.org/wiki/Hue

        //Merah
        if(r >= g && g >= b){
            nombreColor = "Nada Merah";
        }

        //Kuning
        if(g > r && r >= b){
            nombreColor = "Nada Kuning";
        }

        //Hijau
        if(g >= b && b > r){
            nombreColor = "Nada Hijau";
        }

        //Cyan
        if(b > g && g > r){
            nombreColor = "Nada Cyan";
        }

        //Biru
        if(b > r && r >= g){
            nombreColor = "Nada Biru";
        }

        //Magenta
        if(r >= b && b > g){
            nombreColor = "Nada Magenta";
        }

        //Negro
        if(r < 10.0 && g < 10.0 && b < 10.0){
            nombreColor = "Nada Negro";
        }

        //Putih
        if(r > 140.0 && g > 140.0 && b > 140.0){
            if(r > 200.0 && g > 200.0 && b > 200.0){
                nombreColor = "Putih Murni";
            }else{
                nombreColor = "Nada Putih";
            }
        }

    }

    return nombreColor;
}

}