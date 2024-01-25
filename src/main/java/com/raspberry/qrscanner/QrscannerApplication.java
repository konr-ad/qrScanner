package com.raspberry.qrscanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QrscannerApplication {


	public static void main(String[] args) {
		SpringApplication.run(QrscannerApplication.class, args);
		new QRCodeScannerService().scanQRCode();
	}

}
