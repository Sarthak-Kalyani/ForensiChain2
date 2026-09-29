package com.sdcems.sdcems.controller;

import com.sdcems.sdcems.model.Evidence;
import com.sdcems.sdcems.model.InvestigationCase;

import com.sdcems.sdcems.repository.EvidenceRepository;
import com.sdcems.sdcems.repository.InvestigationCaseRepository;
import com.sdcems.sdcems.repository.AuditLogRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    private final EvidenceRepository evidenceRepo;
    private final InvestigationCaseRepository caseRepo;
    private final AuditLogRepository auditRepo;
    private final com.sdcems.sdcems.service.ForensicAnalysisService forensicService;

    public PageController(
        EvidenceRepository evidenceRepo,
        InvestigationCaseRepository caseRepo,
        AuditLogRepository auditRepo,
        com.sdcems.sdcems.service.ForensicAnalysisService forensicService) {

    this.evidenceRepo = evidenceRepo;
    this.caseRepo = caseRepo;
    this.auditRepo = auditRepo;
    this.forensicService = forensicService;
}

    // ==========================================
    // LOGIN PAGE
    // ==========================================

    @GetMapping("/")
    public String home() {
        return "login";
    }

    // ==========================================
    // UPLOAD PAGE
    // ==========================================

    @GetMapping("/uploadPage")
    public String uploadPage(Model model) {

        model.addAttribute(
                "cases",
                caseRepo.findAll()
        );

        return "upload";
    }

    // ==========================================
    // VERIFY PAGE
    // ==========================================

    @GetMapping("/verifyPage")
    public String verifyPage() {
        return "verify";
    }

    // ==========================================
    // DASHBOARD
    // ==========================================

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute(
                "evidences",
                evidenceRepo.findAll()
        );

        model.addAttribute(
                "totalEvidence",
                evidenceRepo.count()
        );

        model.addAttribute(
                "totalCases",
                caseRepo.count()
        );

        model.addAttribute(
                "activeEvidence",
                evidenceRepo.countByStatus("ACTIVE")
        );

        model.addAttribute(
                "deletedEvidence",
                evidenceRepo.countByStatus("DELETED")
        );

        Long storage = evidenceRepo.getTotalStorage();

        if (storage == null) {
            storage = 0L;
        }

        double storageMB =
                storage / (1024.0 * 1024.0);

        model.addAttribute(
                "storageUsed",
                String.format("%.2f MB", storageMB)
        );

        model.addAttribute(
                "jpgFiles",
                evidenceRepo.countJpgFiles()
        );

        model.addAttribute(
                "pngFiles",
                evidenceRepo.countPngFiles()
        );

        model.addAttribute(
                "pdfFiles",
                evidenceRepo.countPdfFiles()
        );

        return "dashboard";
    }

    // ==========================================
    // CASE LIST PAGE
    // ==========================================

    @GetMapping("/cases")
    public String cases(Model model) {

        model.addAttribute(
                "cases",
                caseRepo.findAll()
        );

        return "cases";
    }

    // ==========================================
    // CREATE CASE PAGE
    // ==========================================

    @GetMapping("/cases/new")
    public String createCasePage(Model model) {

        model.addAttribute(
                "caseObj",
                new InvestigationCase()
        );

        return "create-case";
    }

    // ==========================================
    // SAVE CASE FROM HTML FORM
    // ==========================================

    @PostMapping("/cases/save")
    public String saveCase(
            @ModelAttribute("caseObj")
            InvestigationCase investigationCase) {

        // Generate unique case number
        investigationCase.setCaseNumber(
                "CASE-" + System.currentTimeMillis()
        );

        // Default values
        investigationCase.setStatus("OPEN");

        if (investigationCase.getPriority() == null
                || investigationCase.getPriority().isBlank()) {

            investigationCase.setPriority("MEDIUM");
        }

        InvestigationCase saved =
                caseRepo.save(investigationCase);

        return "redirect:/cases/" + saved.getId();
    }

    // ==========================================
    // CASE DETAILS / INVESTIGATION WORKSPACE
    // ==========================================

    @GetMapping("/cases/{id}")
    public String caseDetails(
            @PathVariable Integer id,
            Model model) {

        InvestigationCase investigationCase =
                caseRepo.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Investigation Case not found"
                                )
                        );

        model.addAttribute(
                "case",
                investigationCase
        );

        var evidenceList =
                evidenceRepo
                        .findByInvestigationCase_IdAndStatus(
                                id,
                                "ACTIVE"
                        );

        model.addAttribute(
                "evidenceList",
                evidenceList
        );

        model.addAttribute(
                "totalEvidence",
                evidenceList.size()
        );

        return "case-details";
    }

    // ==========================================
    // EVIDENCE DETAILS
    // ==========================================

    @GetMapping("/evidence/{id}")
public String evidenceDetails(
        @PathVariable int id,
        Model model) {

    Evidence evidence = evidenceRepo.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Evidence not found"));

    InvestigationCase investigationCase =
            evidence.getInvestigationCase();

    model.addAttribute("evidence", evidence);
    model.addAttribute("case", investigationCase);

    model.addAttribute(
            "auditLogs",
            auditRepo.findByEvidenceIdOrderByTimestampAsc(id)
    );

    return "evidence-details";
}
}