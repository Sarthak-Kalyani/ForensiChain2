package com.sdcems.sdcems.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OCRService {

    private final ITesseract tesseract;
    private boolean ocrAvailable = false;

    public OCRService() {

        tesseract = new Tesseract();

        String tessDataPath = System.getenv().getOrDefault("TESSDATA_PREFIX", "/usr/share/tesseract-ocr/5/tessdata");

        File tessData = new File(tessDataPath);

        if (tessData.exists() && tessData.isDirectory()) {

            tesseract.setDatapath(tessDataPath);
            tesseract.setLanguage("eng");

            ocrAvailable = true;

            System.out.println("✅ Tesseract OCR initialized successfully.");

        } else {

            System.out.println(
                    "⚠️ Tesseract tessdata folder not found."
            );

            System.out.println(
                    "OCR will be skipped. Evidence upload will continue."
            );

        }
    }

    public String extractText(File file) {

        if (!ocrAvailable) {

            return "OCR NOT AVAILABLE";
        }

        try {

            System.out.println(
                    "Reading File : " + file.getAbsolutePath()
            );

            String text = tesseract.doOCR(file);

            System.out.println("===== OCR RESULT =====");
            System.out.println(text);
            System.out.println("======================");

            return text;

        } catch (Throwable e) {

            System.err.println(
                    "⚠️ OCR failed. Continuing without OCR."
            );

            e.printStackTrace();

            return "OCR FAILED";
        }
    }
}