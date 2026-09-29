package com.sdcems.sdcems.controller;

import com.sdcems.sdcems.service.AIAnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ai")
public class AIController {

    private final AIAnalysisService aiService;

    public AIController(AIAnalysisService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/summary/{caseId}")
    public String summary(
            @PathVariable Integer caseId,
            Model model) {

        String result =
                aiService.generateSummary(caseId);

        model.addAttribute("summary", result);
        model.addAttribute("caseId", caseId);

        return "ai-summary";
    }
}