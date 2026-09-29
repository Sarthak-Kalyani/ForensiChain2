package com.sdcems.sdcems.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import com.sdcems.sdcems.model.AuditLog;
import com.sdcems.sdcems.model.Evidence;
import com.sdcems.sdcems.model.InvestigationCase;
import com.sdcems.sdcems.repository.AuditLogRepository;
import com.sdcems.sdcems.repository.EvidenceRepository;
import com.sdcems.sdcems.repository.InvestigationCaseRepository;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@Service
public class PdfReportService {

    private final InvestigationCaseRepository caseRepo;
    private final EvidenceRepository evidenceRepo;
    private final AuditLogRepository auditRepo;

    public PdfReportService(
            InvestigationCaseRepository caseRepo,
            EvidenceRepository evidenceRepo,
            AuditLogRepository auditRepo) {

        this.caseRepo = caseRepo;
        this.evidenceRepo = evidenceRepo;
        this.auditRepo = auditRepo;
    }

    public void exportCaseReport(
            Integer caseId,
            HttpServletResponse response) throws IOException {

        InvestigationCase investigationCase =
                caseRepo.findById(caseId).orElseThrow();

        List<Evidence> evidenceList =
                evidenceRepo.findByInvestigationCase_Id(caseId);

        Document document = new Document(PageSize.A4);

        PdfWriter.getInstance(
                document,
                response.getOutputStream()
        );

        document.open();

        Font titleFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        22,
                        Color.BLUE
                );

        Font headingFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        15
                );

        Font normalFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        10
                );

        // =====================================================
        // TITLE
        // =====================================================

        Paragraph title =
                new Paragraph(
                        "FORENSICHAIN\n" +
                        "AI Powered Digital Evidence Investigation Platform",
                        titleFont
                );

        title.setAlignment(Element.ALIGN_CENTER);

        document.add(title);

        document.add(new Paragraph(" "));
        document.add(
                new Paragraph(
                        "DIGITAL FORENSIC INVESTIGATION REPORT",
                        headingFont
                )
        );

        document.add(new Paragraph(" "));

        // =====================================================
        // CASE INFORMATION
        // =====================================================

        document.add(
                new Paragraph(
                        "1. CASE INFORMATION",
                        headingFont
                )
        );

        document.add(new Paragraph(" "));

        PdfPTable caseTable = new PdfPTable(2);
        caseTable.setWidthPercentage(100);

        caseTable.addCell("Case Number");
        caseTable.addCell(investigationCase.getCaseNumber());

        caseTable.addCell("Title");
        caseTable.addCell(investigationCase.getTitle());

        caseTable.addCell("Description");
        caseTable.addCell(investigationCase.getDescription());

        caseTable.addCell("Case Type");
        caseTable.addCell(investigationCase.getCaseType());

        caseTable.addCell("Priority");
        caseTable.addCell(investigationCase.getPriority());

        caseTable.addCell("Status");
        caseTable.addCell(investigationCase.getStatus());

        caseTable.addCell("Assigned Officer");
        caseTable.addCell(
                investigationCase.getAssignedOfficer()
        );

        caseTable.addCell("Investigator");
        caseTable.addCell(
                investigationCase.getInvestigator()
        );

        caseTable.addCell("Created");
        caseTable.addCell(
                investigationCase.getCreatedAt() != null
                        ? investigationCase.getCreatedAt().toString()
                        : "-"
        );

        document.add(caseTable);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // =====================================================
        // EVIDENCE SUMMARY
        // =====================================================

        document.add(
                new Paragraph(
                        "2. EVIDENCE SUMMARY",
                        headingFont
                )
        );

        document.add(new Paragraph(" "));

        PdfPTable evidenceTable = new PdfPTable(6);
        evidenceTable.setWidthPercentage(100);

        evidenceTable.addCell("ID");
        evidenceTable.addCell("File");
        evidenceTable.addCell("Type");
        evidenceTable.addCell("Size");
        evidenceTable.addCell("Status");
        evidenceTable.addCell("Owner");

        for (Evidence e : evidenceList) {

            evidenceTable.addCell(
                    String.valueOf(e.getId())
            );

            evidenceTable.addCell(
                    e.getFileName()
            );

            evidenceTable.addCell(
                    e.getFileExtension()
            );

            evidenceTable.addCell(
                    e.getFileSize() + " bytes"
            );

            evidenceTable.addCell(
                    e.getStatus()
            );

            evidenceTable.addCell(
                    "User " + e.getUserId()
            );
        }

        document.add(evidenceTable);

        document.add(new Paragraph(" "));

        // =====================================================
        // FORENSIC ANALYSIS
        // =====================================================

        document.add(
                new Paragraph(
                        "3. FORENSIC ANALYSIS",
                        headingFont
                )
        );

        document.add(new Paragraph(" "));

        for (Evidence e : evidenceList) {

            document.add(
                    new Paragraph(
                            "Evidence #" + e.getId(),
                            headingFont
                    )
            );

            PdfPTable info = new PdfPTable(2);
            info.setWidthPercentage(100);

            info.addCell("File Name");
            info.addCell(e.getFileName());

            info.addCell("Extension");
            info.addCell(e.getFileExtension());

            info.addCell("Content Type");
            info.addCell(e.getContentType());

            info.addCell("File Size");
            info.addCell(e.getFileSize() + " bytes");

            info.addCell("Uploaded");
            info.addCell(
                    e.getUploadedAt() != null
                            ? e.getUploadedAt().toString()
                            : "-"
            );

            info.addCell("Status");
            info.addCell(e.getStatus());

            document.add(info);

            document.add(new Paragraph(" "));

            // Metadata
            if (e.getMetadata() != null
                    && !e.getMetadata().isBlank()) {

                document.add(
                        new Paragraph(
                                "Forensic Metadata",
                                headingFont
                        )
                );

                document.add(
                        new Paragraph(
                                e.getMetadata(),
                                normalFont
                        )
                );

                document.add(new Paragraph(" "));
            }

            // OCR
            if (e.getExtractedText() != null
                    && !e.getExtractedText().isBlank()) {

                document.add(
                        new Paragraph(
                                "OCR / Extracted Content",
                                headingFont
                        )
                );

                String ocr =
                        e.getExtractedText();

                if (ocr.length() > 5000) {
                    ocr = ocr.substring(0, 5000)
                            + "\n[OCR output truncated]";
                }

                document.add(
                        new Paragraph(
                                ocr,
                                normalFont
                        )
                );

                document.add(new Paragraph(" "));
            }
        }

        // =====================================================
        // INTEGRITY
        // =====================================================

        document.add(
                new Paragraph(
                        "4. INTEGRITY VERIFICATION",
                        headingFont
                )
        );

        document.add(new Paragraph(" "));

        for (Evidence e : evidenceList) {

            document.add(
                    new Paragraph(
                            "Evidence #" + e.getId(),
                            headingFont
                    )
            );

            document.add(
                    new Paragraph(
                            "Hash Algorithm: SHA-256",
                            normalFont
                    )
            );

            document.add(
                    new Paragraph(
                            "Evidence Fingerprint:",
                            normalFont
                    )
            );

            document.add(
                    new Paragraph(
                            e.getHashValue(),
                            normalFont
                    )
            );

            document.add(
                    new Paragraph(
                            "The SHA-256 fingerprint is used to detect " +
                            "unauthorized modification of the stored " +
                            "digital evidence.",
                            normalFont
                    )
            );

            document.add(new Paragraph(" "));
        }

        // =====================================================
        // CHAIN OF CUSTODY
        // =====================================================

        document.add(
                new Paragraph(
                        "5. CHAIN OF CUSTODY",
                        headingFont
                )
        );

        document.add(new Paragraph(" "));

        document.add(
                new Paragraph(
                        "The following audit records represent the " +
                        "evidence handling history recorded by ForensiChain.",
                        normalFont
                )
        );

        document.add(new Paragraph(" "));

        for (Evidence e : evidenceList) {

            document.add(
                    new Paragraph(
                            "Evidence #" + e.getId(),
                            headingFont
                    )
            );

            List<AuditLog> logs =
                    auditRepo
                            .findByEvidenceIdOrderByTimestampAsc(
                                    e.getId()
                            );

            if (logs == null || logs.isEmpty()) {

                document.add(
                        new Paragraph(
                                "No custody records available.",
                                normalFont
                        )
                );

            } else {

                PdfPTable custodyTable =
                        new PdfPTable(3);

                custodyTable.setWidthPercentage(100);

                custodyTable.addCell("ACTION");
                custodyTable.addCell("USER");
                custodyTable.addCell("DATE & TIME");

                for (AuditLog log : logs) {

                    custodyTable.addCell(
                            log.getAction()
                    );

                    custodyTable.addCell(
                            "User " + log.getUserId()
                    );

                    custodyTable.addCell(
                            log.getTimestamp() != null
                                    ? log.getTimestamp().toString()
                                    : "-"
                    );
                }

                document.add(custodyTable);
            }

            document.add(new Paragraph(" "));
        }

        // =====================================================
        // AI INVESTIGATION
        // =====================================================

        document.add(
                new Paragraph(
                        "6. AI-ASSISTED INVESTIGATION",
                        headingFont
                )
        );

        document.add(new Paragraph(" "));

        document.add(
                new Paragraph(
                        "ForensiChain performs automated preliminary " +
                        "analysis using OCR-extracted content and " +
                        "evidence characteristics.",
                        normalFont
                )
        );

        document.add(
                new Paragraph(
                        "AI analysis may identify:",
                        headingFont
                )
        );

        document.add(
                new Paragraph(
                        "• Potential sensitive information\n" +
                        "• Credential-related indicators\n" +
                        "• API / Postman activity\n" +
                        "• Localhost/internal endpoints\n" +
                        "• HTTP errors\n" +
                        "• Other technical indicators",
                        normalFont
                )
        );

        document.add(new Paragraph(" "));

        // =====================================================
        // SECURITY SUMMARY
        // =====================================================

        document.add(
                new Paragraph(
                        "7. SECURITY SUMMARY",
                        headingFont
                )
        );

        document.add(new Paragraph(" "));

        PdfPTable securityTable =
                new PdfPTable(2);

        securityTable.setWidthPercentage(100);

        securityTable.addCell("Total Evidence");
        securityTable.addCell(
                String.valueOf(evidenceList.size())
        );

        securityTable.addCell("Hash Algorithm");
        securityTable.addCell("SHA-256");

        securityTable.addCell("Integrity Mechanism");
        securityTable.addCell(
                "Cryptographic Hash Verification"
        );

        securityTable.addCell("OCR");
        securityTable.addCell("Tesseract OCR");

        securityTable.addCell("Metadata");
        securityTable.addCell(
                "Forensic Metadata Extraction"
        );

        securityTable.addCell("Audit Logging");
        securityTable.addCell(
                "Chain-of-Custody Tracking"
        );

        securityTable.addCell("Database");
        securityTable.addCell("MySQL");

        securityTable.addCell("Backend");
        securityTable.addCell("Spring Boot");

        securityTable.addCell("Platform");
        securityTable.addCell("ForensiChain");

        securityTable.addCell("Generated On");
        securityTable.addCell(
                java.time.LocalDateTime.now().toString()
        );

        document.add(securityTable);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // =====================================================
        // CONCLUSION
        // =====================================================

        document.add(
                new Paragraph(
                        "8. INVESTIGATION CONCLUSION",
                        headingFont
                )
        );

        document.add(new Paragraph(" "));

        document.add(
                new Paragraph(
                        "ForensiChain provides a centralized digital " +
                        "evidence investigation workflow combining " +
                        "evidence registration, cryptographic integrity " +
                        "verification, forensic metadata extraction, OCR, " +
                        "audit logging, chain-of-custody tracking and " +
                        "AI-assisted preliminary investigation.",
                        normalFont
                )
        );

        document.add(new Paragraph(" "));

        document.add(
                new Paragraph(
                        "This report is system generated and should be " +
                        "used as an investigative support document. Final " +
                        "conclusions must be made by authorized investigators " +
                        "after reviewing the complete evidence.",
                        normalFont
                )
        );

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Paragraph footer =
                new Paragraph(
                        "ForensiChain\n" +
                        "AI-Powered Digital Evidence Investigation Platform\n" +
                        "Spring Boot • MySQL • Digital Forensics • Cybersecurity • AI",
                        normalFont
                );

        footer.setAlignment(Element.ALIGN_CENTER);

        document.add(footer);

        document.close();
    }
}