package com.example.controller;

import com.example.model.Attraction;
import com.example.model.Food;
import com.example.model.RouteResponse;
import com.example.service.OpenAIService;
import com.example.service.TourService;
import com.example.util.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class TourController {

    private final TourService tourService;
    private final OpenAIService openAIService;
    private final SessionManager sessionManager;

    @Autowired
    public TourController(TourService tourService,
                          OpenAIService openAIService,
                          SessionManager sessionManager) {
        this.tourService = tourService;
        this.openAIService = openAIService;
        this.sessionManager = sessionManager;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("attractions", tourService.getAttractions());
        model.addAttribute("foods", tourService.getFoods());
        return "index";
    }

    @PostMapping("/generateRoute")
    @ResponseBody
    public RouteResponse generateRoute(@RequestParam int budget,
                                       @RequestParam int days) {
        try {
            return openAIService.generateLocalRoute(budget, days);
        } catch (Exception e) {
            return openAIService.getFallbackRoute(budget, days);
        }
    }

    @PostMapping("/api/ai/ask")
    @ResponseBody
    public Map<String, String> askAI(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String question = request.get("question");
        String history = sessionManager.getHistory(sessionId);

        Map<String, String> response = openAIService.askAI(question, history);
        sessionManager.saveHistory(sessionId, response.get("newHistory"));

        return Map.of("answer", response.get("answer"));
    }

    @PostMapping("/api/ai/clear")
    @ResponseBody
    public void clearHistory(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        sessionManager.clearHistory(sessionId);
    }
}