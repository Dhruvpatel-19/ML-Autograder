package com.example.mlautogradecodetool.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.mlautogradecodetool.model.PredictionRequest;
import com.example.mlautogradecodetool.model.PredictionResponse;
import com.example.mlautogradecodetool.service.CodeEvaluationService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api")
public class FeedbackController {

    private final CodeEvaluationService codeEvaluationService;

    public FeedbackController(CodeEvaluationService codeEvaluationService) {
        this.codeEvaluationService = codeEvaluationService;
    }

   @PostMapping("/feedback")
public String getFeedback(@ModelAttribute PredictionRequest request, RedirectAttributes redirectAttributes) {
    // Evaluate the code and get the prediction response
    PredictionResponse response = codeEvaluationService.evaluateCode(request);

    // Use flash attributes to pass data temporarily
    redirectAttributes.addFlashAttribute("response", response);
    redirectAttributes.addFlashAttribute("instruction", request.getInstruction());
    redirectAttributes.addFlashAttribute("output", request.getOutput());
    redirectAttributes.addFlashAttribute("code_style", request.getCode_style());

    // Redirect to the home page
    return "redirect:/home";
}


@RequestMapping("/home")
public String homePage(Model model) {
    // Clear the form fields by not adding any attributes
    model.addAttribute("response", null);
    model.addAttribute("instruction", "");
    model.addAttribute("output", "");
    model.addAttribute("code_style", 0);
    return "home";
}

}

