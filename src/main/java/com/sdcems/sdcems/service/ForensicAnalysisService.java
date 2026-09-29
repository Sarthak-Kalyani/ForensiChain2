package com.sdcems.sdcems.service;

import com.sdcems.sdcems.model.Evidence;
import org.springframework.stereotype.Service;

@Service
public class ForensicAnalysisService {

    public String classify(Evidence evidence) {

        String ext = evidence.getFileExtension();

        if (ext == null) return "UNKNOWN";

        return switch (ext.toUpperCase()) {
            case "PNG", "JPG", "JPEG", "GIF", "BMP" ->
                    "IMAGE EVIDENCE";
            case "PDF" ->
                    "DOCUMENT EVIDENCE";
            case "DOC", "DOCX", "TXT" ->
                    "TEXT / DOCUMENT EVIDENCE";
            case "MP4", "AVI", "MOV", "MKV" ->
                    "VIDEO EVIDENCE";
            case "MP3", "WAV", "AAC" ->
                    "AUDIO EVIDENCE";
            case "ZIP", "RAR", "7Z" ->
                    "ARCHIVE EVIDENCE";
            default ->
                    "GENERAL DIGITAL EVIDENCE";
        };
    }

    public String assessRisk(Evidence evidence) {

        StringBuilder reasons = new StringBuilder();

        if (evidence.getFileSize() != null &&
                evidence.getFileSize() > 50 * 1024 * 1024) {

            reasons.append("Large file; ");
        }

        if (evidence.getExtractedText() != null &&
                !evidence.getExtractedText().isBlank()) {

            String text = evidence.getExtractedText().toLowerCase();

            if (text.contains("password") ||
                text.contains("token") ||
                text.contains("api key") ||
                text.contains("secret")) {

                reasons.append("Potential sensitive information detected; ");
            }
        }

        if (reasons.length() > 0) {
            return "MEDIUM RISK - " + reasons;
        }

        return "LOW RISK - No obvious anomalies detected";
    }
}