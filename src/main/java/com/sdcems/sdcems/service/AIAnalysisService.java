package com.sdcems.sdcems.service;

import com.sdcems.sdcems.model.Evidence;
import com.sdcems.sdcems.repository.EvidenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIAnalysisService {

    private final EvidenceRepository evidenceRepo;

    public AIAnalysisService(EvidenceRepository evidenceRepo) {
        this.evidenceRepo = evidenceRepo;
    }

    // AI SUMMARY FOR COMPLETE INVESTIGATION CASE
    public String generateSummary(Integer caseId) {

        List<Evidence> evidences =
                evidenceRepo.findByInvestigationCase_IdAndStatus(
                        caseId,
                        "ACTIVE"
                );

        if (evidences.isEmpty()) {
            return """
                    AI INVESTIGATION SUMMARY

                    No active evidence is currently registered
                    for this investigation case.

                    Recommendation:
                    Upload and analyze evidence before drawing
                    investigative conclusions.
                    """;
        }

        StringBuilder result = new StringBuilder();

        result.append("AI INVESTIGATION SUMMARY\n\n");

        result.append("CASE ID: ")
                .append(caseId)
                .append("\n\n");

        result.append("TOTAL ACTIVE EVIDENCE: ")
                .append(evidences.size())
                .append("\n\n");

        int highRisk = 0;
        int mediumRisk = 0;
        int lowRisk = 0;

        // =====================================================
        // ANALYZE EACH EVIDENCE
        // =====================================================

        for (Evidence evidence : evidences) {

            String text = evidence.getExtractedText();

            if (text == null) {
                text = "";
            }

            String lower = text.toLowerCase();

            int risk = 0;

            if (lower.contains("password")
                    || lower.contains("credential")
                    || lower.contains("secret")) {
                risk += 3;
            }

            if (lower.contains("token")
                    || lower.contains("authorization")) {
                risk += 2;
            }

            if (lower.contains("api")
                    || lower.contains("postman")
                    || lower.contains("localhost")
                    || lower.contains("http")) {
                risk += 1;
            }

            if (lower.contains("error")
                    || lower.contains("failed")
                    || lower.contains("404")
                    || lower.contains("500")) {
                risk += 1;
            }

            String riskLevel;

            if (risk >= 3) {
                riskLevel = "HIGH RISK";
                highRisk++;
            }
            else if (risk >= 1) {
                riskLevel = "MEDIUM RISK";
                mediumRisk++;
            }
            else {
                riskLevel = "LOW RISK";
                lowRisk++;
            }

            result.append("====================================\n");

            result.append("EVIDENCE #")
                    .append(evidence.getId())
                    .append("\n");

            result.append("File: ")
                    .append(evidence.getFileName())
                    .append("\n");

            result.append("Type: ")
                    .append(evidence.getFileExtension())
                    .append("\n");

            result.append("Risk: ")
                    .append(riskLevel)
                    .append("\n");

            result.append("OCR Characters: ")
                    .append(text.length())
                    .append("\n");

            result.append("SHA-256: ")
                    .append(evidence.getHashValue())
                    .append("\n\n");

            // Findings

            result.append("KEY FINDINGS:\n");

            boolean finding = false;

            if (lower.contains("postman")) {
                result.append("- Postman/API testing activity detected.\n");
                finding = true;
            }

            if (lower.contains("api")) {
                result.append("- API-related activity detected.\n");
                finding = true;
            }

            if (lower.contains("localhost")) {
                result.append("- Local/internal application endpoint detected.\n");
                finding = true;
            }

            if (lower.contains("password")
                    || lower.contains("credential")) {

                result.append(
                        "- Possible credential-related information detected.\n"
                );

                finding = true;
            }

            if (lower.contains("404")) {
                result.append("- HTTP 404 error detected.\n");
                finding = true;
            }

            if (lower.contains("500")) {
                result.append("- HTTP 500 server error detected.\n");
                finding = true;
            }

            if (!finding) {
                result.append(
                        "- No obvious technical indicators detected.\n"
                );
            }

            result.append("\n");
        }

        // =====================================================
        // OVERALL ASSESSMENT
        // =====================================================

        result.append("\n====================================\n");
        result.append("OVERALL AI ASSESSMENT\n");
        result.append("====================================\n\n");

        result.append("High Risk Evidence: ")
                .append(highRisk)
                .append("\n");

        result.append("Medium Risk Evidence: ")
                .append(mediumRisk)
                .append("\n");

        result.append("Low Risk Evidence: ")
                .append(lowRisk)
                .append("\n\n");

        if (highRisk > 0) {

            result.append(
                    "Overall Assessment: HIGH INVESTIGATIVE ATTENTION REQUIRED.\n\n"
            );

        } else if (mediumRisk > 0) {

            result.append(
                    "Overall Assessment: MODERATE INVESTIGATIVE ATTENTION REQUIRED.\n\n"
            );

        } else {

            result.append(
                    "Overall Assessment: NO OBVIOUS HIGH-RISK INDICATORS.\n\n"
            );
        }

        result.append("RECOMMENDATION:\n");

        result.append(
                "Correlate the identified evidence with metadata, "
                + "OCR results, SHA-256 integrity records, timestamps "
                + "and chain-of-custody logs before reaching a final "
                + "investigative conclusion."
        );

        return result.toString();
    }
}