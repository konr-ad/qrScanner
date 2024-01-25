package com.raspberry.qrscanner;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@Service
public class QRCodeScannerService {

    static {
        nu.pattern.OpenCV.loadShared();
    }

    public void scanQRCode() {
        VideoCapture capture = new VideoCapture(0); // Use 0 for the default camera

        if (!capture.isOpened()) {
            System.out.println("Could not open video device.");
            return;
        } else {
            System.out.println("Camera started successfully.");
        }

        Mat frame = new Mat();
        while (true) {
            if (capture.read(frame)) {
                System.out.println("Frame captured.");
                try {
                    Thread.sleep(3000);  // Wait for 5 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // rest of your code for decoding...
            } else {
                System.out.println("No frame captured from camera.");
                break;
            };
            capture.read(frame);

            if (!frame.empty()) {
                String decodedText = decodeQRCode(matToBufferedImage(frame));
                if (decodedText != null) {
                    System.out.println("Decoded QR Code: " + decodedText);
                    sendDecodedTextToApi(decodedText); // Send to API
                }
            }
        }
    }

    private void sendDecodedTextToApi(String decodedText) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8080/api/digital-key/validate";

        // Assuming the API expects a POST request with a plain text body
        restTemplate.postForObject(apiUrl, decodedText, String.class);
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        // Encode the mat to a byte array
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".bmp", mat, matOfByte);

        // Convert byte array to BufferedImage
        try {
            byte[] byteArray = matOfByte.toArray();
            return ImageIO.read(new ByteArrayInputStream(byteArray));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decodeQRCode(BufferedImage bufferedImage) {
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            Result result = new MultiFormatReader().decode(bitmap);
            System.out.println("QR Code decoded: " + result.getText());
            return result.getText();
        } catch (Exception e) {
            System.out.println("Decoding failed: " + e.getMessage());
            return null;
        }
}
}
