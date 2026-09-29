package com.sdcems.sdcems.controller;

import com.sdcems.sdcems.model.InvestigationCase;
import com.sdcems.sdcems.repository.InvestigationCaseRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cases")
public class InvestigationCaseController {

    private final InvestigationCaseRepository repo;

    public InvestigationCaseController(InvestigationCaseRepository repo) {
        this.repo = repo;
    }

    // ==========================================
    // CREATE CASE - REST API
    // ==========================================

    @PostMapping("/create")
    public InvestigationCase createCase(
            @RequestBody InvestigationCase investigationCase) {

        investigationCase.setCaseNumber(
                "CASE-" + System.currentTimeMillis()
        );

        if (investigationCase.getStatus() == null
                || investigationCase.getStatus().isBlank()) {

            investigationCase.setStatus("OPEN");
        }

        if (investigationCase.getPriority() == null
                || investigationCase.getPriority().isBlank()) {

            investigationCase.setPriority("MEDIUM");
        }

        return repo.save(investigationCase);
    }

    // ==========================================
    // GET ALL CASES - REST API
    // ==========================================
    //
    // NOTE:
    // The browser UI /cases route is handled by
    // PageController.
    //
    // Therefore this API uses:
    // GET /cases/api
    //
    // ==========================================

    @GetMapping("/api")
    public List<InvestigationCase> getAllCases() {
        return repo.findAll();
    }
}