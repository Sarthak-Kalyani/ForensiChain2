package com.sdcems.sdcems.controller;

import com.sdcems.sdcems.model.AuditLog;
import com.sdcems.sdcems.model.Evidence;
import com.sdcems.sdcems.model.InvestigationCase;

import com.sdcems.sdcems.repository.AuditLogRepository;
import com.sdcems.sdcems.repository.EvidenceRepository;
import com.sdcems.sdcems.repository.InvestigationCaseRepository;

import com.sdcems.sdcems.service.HashService;
import com.sdcems.sdcems.service.MetadataService;
import com.sdcems.sdcems.service.OCRService;

import jakarta.servlet.http.HttpSession;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/evidence")
public class EvidenceController {

    private final EvidenceRepository repo;
    private final AuditLogRepository auditRepo;
    private final OCRService ocrService;
    private final InvestigationCaseRepository caseRepo;
    private final MetadataService metadataService;

    public EvidenceController(
        EvidenceRepository repo,
        AuditLogRepository auditRepo,
        OCRService ocrService,
        InvestigationCaseRepository caseRepo,
        MetadataService metadataService) {

    this.repo = repo;
    this.auditRepo = auditRepo;
    this.ocrService = ocrService;
    this.caseRepo = caseRepo;
    this.metadataService = metadataService;
}

    // ==========================================
    // GET LOGGED-IN USER
    // ==========================================

    private Integer getLoggedInUserId(HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            return null;
        }

        if (userId instanceof Integer) {
            return (Integer) userId;
        }

        return Integer.valueOf(userId.toString());
    }

    // ==========================================
    // UPLOAD EVIDENCE
    // ==========================================

    @PostMapping("/upload")
    @ResponseBody
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("caseId") int caseId,
            HttpSession session) {

        try {

            // ------------------------------------------
            // Authentication check
            // ------------------------------------------

            Integer userId = getLoggedInUserId(session);

            if (userId == null) {

                return "<h2>ACCESS DENIED</h2>"
                        + "<p>Please login first.</p>"
                        + "<a href='/'>Login</a>";
            }

            // ------------------------------------------
            // File validation
            // ------------------------------------------

            if (file == null || file.isEmpty()) {

                return "UPLOAD FAILED : No file selected";
            }

            // ------------------------------------------
            // Find investigation case
            // ------------------------------------------

            InvestigationCase investigationCase =
                    caseRepo.findById(caseId)
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Investigation Case not found"
                                    )
                            );

            // ------------------------------------------
            // Create uploads directory
            // ------------------------------------------

            Path uploadPath =
                    Paths.get(
                            System.getProperty("user.dir"),
                            "uploads"
                    );

            Files.createDirectories(uploadPath);

            // ------------------------------------------
            // Sanitize original filename
            // ------------------------------------------

            String originalName =
                    file.getOriginalFilename();

            if (originalName == null
                    || originalName.isBlank()) {

                originalName = "evidence";
            }

            String safeName =
                    Paths.get(originalName)
                            .getFileName()
                            .toString();

            // ------------------------------------------
            // Generate unique stored filename
            // ------------------------------------------

            String storedFileName =
                    UUID.randomUUID()
                            + "_" + safeName;

            Path storedPath =
                    uploadPath.resolve(storedFileName);

            // ------------------------------------------
            // Save file
            // ------------------------------------------

            file.transferTo(storedPath.toFile());

            File savedFile =
                    storedPath.toFile();

            // ------------------------------------------
            // Generate SHA-256 hash
            // ------------------------------------------

            String hash;

            try (FileInputStream inputStream =
                         new FileInputStream(savedFile)) {

                hash =
                        HashService.generateFileHash(
                                inputStream
                        );
            }

            // ------------------------------------------
            // OCR
            // ------------------------------------------

            String extractedText = "";

            String contentType =
                    file.getContentType();

            if (contentType != null
                    && contentType.startsWith("image/")) {

                extractedText =
                        ocrService.extractText(
                                savedFile
                        );
            }
            String metadata =
            metadataService.extractMetadata(savedFile);

            // ------------------------------------------
            // Create Evidence object
            // ------------------------------------------

            Evidence evidence =
                    new Evidence();

            evidence.setInvestigationCase(
                    investigationCase
            );

            evidence.setUserId(userId);

            // Store actual physical filename
            evidence.setFileName(
                    storedFileName
            );

            evidence.setHashValue(hash);

            evidence.setStatus("ACTIVE");

            evidence.setUploadedAt(
                    LocalDateTime.now()
            );

            // ------------------------------------------
            // File metadata
            // ------------------------------------------

            evidence.setFileSize(
                    savedFile.length()
            );

            evidence.setContentType(
                    contentType
            );

            String extension = "";

            if (safeName.contains(".")) {

                extension =
                        safeName.substring(
                                safeName.lastIndexOf(".") + 1
                        );
            }

            evidence.setFileExtension(
                    extension.toUpperCase()
            );

            // ------------------------------------------
            // OCR result
            // ------------------------------------------

            evidence.setExtractedText(
                    extractedText
            );

            evidence.setMetadata(metadata);

            // ------------------------------------------
            // Save evidence
            // ------------------------------------------

            Evidence saved =
                    repo.save(evidence);

            // ------------------------------------------
            // Audit log
            // ------------------------------------------

            AuditLog log =
                    new AuditLog();

            log.setUserId(userId);

            log.setEvidenceId(
                    saved.getId()
            );

            log.setAction("UPLOAD");

            auditRepo.save(log);

            // ------------------------------------------
            // SUCCESS PAGE
            // ------------------------------------------

            return buildUploadSuccessPage(
                    saved.getId(),
                    investigationCase.getId(),
                    investigationCase.getCaseNumber(),
                    safeName,
                    hash
            );

        } catch (Exception e) {

            e.printStackTrace();

            return "UPLOAD FAILED : "
                    + e.getMessage();
        }
    }

    // ==========================================
    // UPLOAD SUCCESS PAGE
    // ==========================================

    private String buildUploadSuccessPage(
            Integer evidenceId,
            Integer caseId,
            String caseNumber,
            String originalFile,
            String hash) {

        return "<!DOCTYPE html>"

                + "<html lang='en'>"

                + "<head>"

                + "<meta charset='UTF-8'>"

                + "<meta name='viewport' "
                + "content='width=device-width, initial-scale=1.0'>"

                + "<title>Upload Successful | ForensiChain</title>"

                + "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' "
                + "rel='stylesheet'>"

                + "<link href='https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css' "
                + "rel='stylesheet'>"

                + "<style>"

                + "body{"
                + "background:#f4f7fb;"
                + "font-family:'Segoe UI',Arial,sans-serif;"
                + "}"

                + ".navbar{"
                + "background:linear-gradient(135deg,#111827,#1e3a8a);"
                + "padding:16px 0;"
                + "}"

                + ".success-card{"
                + "max-width:850px;"
                + "margin:60px auto;"
                + "border:none;"
                + "border-radius:20px;"
                + "overflow:hidden;"
                + "box-shadow:0 15px 40px rgba(0,0,0,.10);"
                + "}"

                + ".success-header{"
                + "background:linear-gradient(135deg,#198754,#20c997);"
                + "color:white;"
                + "text-align:center;"
                + "padding:35px;"
                + "}"

                + ".success-icon{"
                + "font-size:65px;"
                + "}"

                + ".success-header h2{"
                + "font-weight:700;"
                + "margin-top:10px;"
                + "}"

                + ".info-box{"
                + "background:#f8fafc;"
                + "border-radius:12px;"
                + "padding:18px;"
                + "margin-bottom:15px;"
                + "}"

                + ".label{"
                + "color:#6b7280;"
                + "font-size:.8rem;"
                + "font-weight:700;"
                + "text-transform:uppercase;"
                + "}"

                + ".value{"
                + "font-weight:600;"
                + "font-size:1.05rem;"
                + "word-break:break-word;"
                + "}"

                + ".hash-box{"
                + "background:#111827;"
                + "color:#7dd3fc;"
                + "padding:20px;"
                + "border-radius:12px;"
                + "font-family:monospace;"
                + "word-break:break-all;"
                + "font-size:.9rem;"
                + "}"

                + ".btn{"
                + "border-radius:9px;"
                + "font-weight:500;"
                + "padding:10px 18px;"
                + "}"

                + ".footer{"
                + "text-align:center;"
                + "color:#6b7280;"
                + "margin-top:30px;"
                + "padding-bottom:30px;"
                + "}"

                + "</style>"

                + "</head>"

                + "<body>"

                // NAVBAR
                + "<nav class='navbar navbar-dark shadow'>"

                + "<div class='container'>"

                + "<a class='navbar-brand' href='/dashboard'>"

                + "<i class='bi bi-shield-lock-fill me-2'></i>"

                + "<b>ForensiChain</b>"

                + "</a>"

                + "<span class='text-light'>"
                + "Digital Evidence Investigation Platform"
                + "</span>"

                + "</div>"

                + "</nav>"

                // MAIN CONTAINER
                + "<div class='container'>"

                + "<div class='card success-card'>"

                // SUCCESS HEADER
                + "<div class='success-header'>"

                + "<i class='bi bi-check-circle-fill success-icon'></i>"

                + "<h2>Evidence Uploaded Successfully</h2>"

                + "<p class='mb-0'>"
                + "Digital evidence has been securely registered."
                + "</p>"

                + "</div>"

                // BODY
                + "<div class='card-body p-4 p-md-5'>"

                + "<div class='row'>"

                // EVIDENCE ID
                + "<div class='col-md-6'>"

                + "<div class='info-box'>"

                + "<div class='label'>Evidence ID</div>"

                + "<div class='value text-primary'>"
                + evidenceId
                + "</div>"

                + "</div>"

                + "</div>"

                // CASE
                + "<div class='col-md-6'>"

                + "<div class='info-box'>"

                + "<div class='label'>Investigation Case</div>"

                + "<div class='value'>"
                + caseNumber
                + "</div>"

                + "</div>"

                + "</div>"

                // ORIGINAL FILE
                + "<div class='col-12'>"

                + "<div class='info-box'>"

                + "<div class='label'>Original File</div>"

                + "<div class='value'>"

                + "<i class='bi bi-file-earmark-image me-2 text-primary'></i>"

                + originalFile

                + "</div>"

                + "</div>"

                + "</div>"

                // HASH
                + "<div class='col-12 mt-2'>"

                + "<div class='label mb-2'>"
                + "SHA-256 Evidence Fingerprint"
                + "</div>"

                + "<div class='hash-box'>"
                + hash
                + "</div>"

                + "<small class='text-muted'>"
                + "This fingerprint is securely stored for future integrity verification."
                + "</small>"

                + "</div>"

                + "</div>"

                + "<hr class='my-4'>"

                // BUTTONS
                + "<div class='d-flex flex-wrap gap-2 justify-content-center'>"

                + "<a href='/evidence/"
                + evidenceId
                + "' class='btn btn-primary'>"

                + "<i class='bi bi-eye me-1'></i>"
                + "View Evidence"

                + "</a>"

                + "<a href='/cases/"
                + caseId
                + "' class='btn btn-success'>"

                + "<i class='bi bi-folder2-open me-1'></i>"
                + "Investigation Workspace"

                + "</a>"

                + "<a href='/uploadPage' "
                + "class='btn btn-outline-primary'>"

                + "<i class='bi bi-upload me-1'></i>"
                + "Upload Another"

                + "</a>"

                + "<a href='/dashboard' "
                + "class='btn btn-dark'>"

                + "<i class='bi bi-speedometer2 me-1'></i>"
                + "Dashboard"

                + "</a>"

                + "</div>"

                + "</div>"

                + "</div>"

                // FOOTER
                + "<div class='footer'>"

                + "<h5>"
                + "<i class='bi bi-shield-lock-fill me-1'></i>"
                + "ForensiChain"
                + "</h5>"

                + "<p>"
                + "AI-Powered Digital Evidence Investigation Platform"
                + "</p>"

                + "<p>"
                + "Spring Boot • MySQL • Digital Forensics • Cybersecurity • AI"
                + "</p>"

                + "</div>"

                + "</div>"

                + "</body>"

                + "</html>";
    }


    // ==========================================
// VERIFY EVIDENCE
// ==========================================

@GetMapping("/verify/{id}")
public String verify(
        @PathVariable Integer id,
        HttpSession session,
        org.springframework.ui.Model model) {

    try {

        Integer userId = getLoggedInUserId(session);

        if (userId == null) {
            model.addAttribute("error", "ACCESS DENIED - Please login");
            return "verify-result";
        }

        Evidence evidence = repo.findById(id).orElseThrow();

        // Ownership check
        if (!evidence.getUserId().equals(userId)) {
            model.addAttribute("error", "ACCESS DENIED - Not Owner");
            return "verify-result";
        }

        // Deleted evidence cannot be verified
        if ("DELETED".equalsIgnoreCase(evidence.getStatus())) {
            model.addAttribute("error", "EVIDENCE DELETED");
            return "verify-result";
        }

        // Locate physical file
        Path filePath = Paths.get(
                System.getProperty("user.dir"),
                "uploads",
                evidence.getFileName()
        );

        File file = filePath.toFile();

        if (!file.exists()) {
            model.addAttribute("error", "FILE MISSING");
            return "verify-result";
        }

        // Generate current SHA-256
        String newHash;

        try (FileInputStream inputStream =
                     new FileInputStream(file)) {

            newHash = HashService.generateFileHash(inputStream);
        }

        // Audit verification
        AuditLog log = new AuditLog();

        log.setUserId(userId);
        log.setEvidenceId(evidence.getId());
        log.setAction("VERIFY");

        auditRepo.save(log);

        // Send information to HTML
        model.addAttribute("fileName", evidence.getFileName());
        model.addAttribute("evidenceId", evidence.getId());
        model.addAttribute("originalHash", evidence.getHashValue());
        model.addAttribute("currentHash", newHash);

        // Compare hashes
        if (newHash.equals(evidence.getHashValue())) {

            model.addAttribute("valid", true);
            model.addAttribute(
                    "message",
                    "VALID - File Not Modified"
            );

        } else {

            model.addAttribute("valid", false);
            model.addAttribute(
                    "message",
                    "TAMPERED - File Changed"
            );
        }

        return "verify-result";

    } catch (Exception e) {

        e.printStackTrace();

        model.addAttribute(
                "error",
                "ERROR VERIFYING FILE"
        );

        return "verify-result";
    }
}


    // ==========================================
    // DOWNLOAD EVIDENCE
    // ==========================================

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(
            @PathVariable Integer id,
            HttpSession session) {

        try {

            Integer userId =
                    getLoggedInUserId(session);

            if (userId == null) {

                return ResponseEntity
                        .status(401)
                        .build();
            }

            Evidence evidence =
                    repo.findById(id)
                            .orElseThrow();

            // Ownership

            if (!evidence.getUserId()
                    .equals(userId)) {

                return ResponseEntity
                        .status(403)
                        .build();
            }

            // Deleted evidence

            if ("DELETED".equalsIgnoreCase(
                    evidence.getStatus())) {

                return ResponseEntity
                        .status(410)
                        .build();
            }

            // Locate file

            Path filePath =
                    Paths.get(
                            System.getProperty("user.dir"),
                            "uploads",
                            evidence.getFileName()
                    );

            File file =
                    filePath.toFile();

            if (!file.exists()) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            // Audit

            AuditLog log =
                    new AuditLog();

            log.setUserId(userId);

            log.setEvidenceId(
                    evidence.getId()
            );

            log.setAction("DOWNLOAD");

            auditRepo.save(log);

            // Return file

            Resource resource =
                    new FileSystemResource(file);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\""
                                    + file.getName()
                                    + "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }


    // ==========================================
    // SOFT DELETE EVIDENCE
    // ==========================================

    @GetMapping("/delete/{id}")
    @ResponseBody
    public String deleteEvidence(
            @PathVariable Integer id,
            HttpSession session) {

        try {

            Integer userId =
                    getLoggedInUserId(session);

            if (userId == null) {

                return "ACCESS DENIED - Please login";
            }

            Evidence evidence =
                    repo.findById(id)
                            .orElseThrow();

            // Ownership

            if (!evidence.getUserId()
                    .equals(userId)) {

                return "ACCESS DENIED - Not Owner";
            }

            // Already deleted

            if ("DELETED".equalsIgnoreCase(
                    evidence.getStatus())) {

                return "Evidence already deleted";
            }

            // Soft delete

            evidence.setStatus("DELETED");

            repo.save(evidence);

            // Audit

            AuditLog log =
                    new AuditLog();

            log.setUserId(userId);

            log.setEvidenceId(
                    evidence.getId()
            );

            log.setAction("DELETE");

            auditRepo.save(log);

            // Return to workspace

            Integer caseId =
                    evidence.getInvestigationCase()
                            .getId();

            return "<h2>✅ Evidence Deleted Successfully</h2>"
                    + "<br>"
                    + "<a href='/cases/"
                    + caseId
                    + "'>⬅ Back to Investigation Workspace</a>";

        } catch (Exception e) {

            e.printStackTrace();

            return "DELETE FAILED : "
                    + e.getMessage();
        }
    }
}