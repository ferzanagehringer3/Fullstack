package ch.fullstack.dalzana.controller;

import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HelloController {

    @GetMapping("/")
public String root() {
    return "redirect:/login";
}

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
