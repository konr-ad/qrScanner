package com.raspberry.qrscanner;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgcodecs.Imgcodecs;

public class QRCodeScanner {
    static {
        System.loadLibrary("opencv_java451"); // Załaduj bibliotekę OpenCV 4.5.5
    }
    private VideoCapture camera;

    public QRCodeScanner() {
        this.camera = new VideoCapture(0, Videoio.CAP_V4L);
    }

    public void openCamera(int cameraIndex) {
        camera.open(cameraIndex);
    }

    public Mat captureImage() {
        if (!camera.isOpened()) {
            System.err.println("Error: Camera is not opened.");
            return null;
        }

        Mat frame = new Mat();
        boolean success = camera.read(frame);

        if (!success) {
            System.err.println("Error: Cannot capture frame from camera.");
            return null;
        }
        return frame;
    }

    public void releaseCamera() {
        if (camera.isOpened()) {
            camera.release();
        }
    }
}
