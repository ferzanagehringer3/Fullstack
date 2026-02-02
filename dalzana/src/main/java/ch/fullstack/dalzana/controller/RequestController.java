package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.service.RequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    // Liste
    @GetMapping
    public String list(Model model) {
        model.addAttribute("requests", requestService.findAll());
        return "requests";
    }

    // Detail
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("request", requestService.findById(id));
        return "request-detail";
    }
}
