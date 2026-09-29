package com.sdcems.sdcems.controller;

import com.sdcems.sdcems.repository.InvestigationCaseRepository;
import com.sdcems.sdcems.service.PdfReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final PdfReportService pdfService;
    private final InvestigationCaseRepository caseRepo;

    public ReportController(PdfReportService pdfService,
                            InvestigationCaseRepository caseRepo) {

        this.pdfService = pdfService;
        this.caseRepo = caseRepo;
    }

    @GetMapping("/case/{id}")
    public void generateReport(@PathVariable Integer id,
                               HttpServletResponse response) {

        try {

            var investigationCase =
                    caseRepo.findById(id).orElseThrow();

            response.setContentType("application/pdf");

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=" +
                            investigationCase.getCaseNumber() +
                            "_Investigation_Report.pdf"
            );

            pdfService.exportCaseReport(id, response);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

}