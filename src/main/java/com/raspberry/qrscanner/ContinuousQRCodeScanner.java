package com.raspberry.qrscanner;

import org.opencv.core.Mat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.Reader;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.Result;
import com.google.zxing.NotFoundException;

import java.awt.image.BufferedImage;
import org.opencv.imgcodecs.Imgcodecs;

public class ContinuousQRCodeScanner {
    static {
        System.loadLibrary("opencv_java451"); // Załaduj bibliotekę OpenCV 4.5.5
    }
    private QRCodeScanner qrCodeScanner;
    private QRCodeValidator qrCodeValidator;

    public ContinuousQRCodeScanner() {
        this.qrCodeScanner = new QRCodeScanner();
        this.qrCodeValidator = new QRCodeValidator();
    }

    public void continuousScan() {
        qrCodeScanner.openCamera(0);

        while (true) {
            Mat frame = qrCodeScanner.captureImage();
            if (frame != null) {
                BufferedImage image = convertMatToBufferedImage(frame);
                String qrCodeData = decodeQRCode(image);

                if (qrCodeData != null) {
                    boolean isValid = qrCodeValidator.validateQRCode(qrCodeData);
                    System.out.println("Wynik walidacji: " + (isValid ? "Valid QR Code" : "Invalid or Expired QR Code"));
                }
            }

            try {
                Thread.sleep(1000); // Czekanie na następne skanowanie
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                qrCodeScanner.releaseCamera();
            }
        }
    }

    private BufferedImage convertMatToBufferedImage(Mat mat) {
        // Konwersja Mat na BufferedImage
        byte[] data = new byte[mat.rows() * mat.cols() * (int)(mat.elemSize())];
        mat.get(0, 0, data);

        int type = mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;

        if (type == BufferedImage.TYPE_3BYTE_BGR) {
            for (int i = 0; i < data.length; i += 3) {
                byte temp = data[i];
                data[i] = data[i + 2];
                data[i + 2] = temp;
            }
        }

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);

        return image;
    }


    private String decodeQRCode(BufferedImage image) {
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bitmap);
            return result.getText();
        } catch (Exception e) {
            return null;
        }
    }
}
