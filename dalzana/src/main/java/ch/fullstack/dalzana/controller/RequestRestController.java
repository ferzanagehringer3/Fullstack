package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.model.Request;
import ch.fullstack.dalzana.repo.RequestRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestRestController {

    private final RequestRepository requestRepo;

    public RequestRestController(RequestRepository requestRepo) {
        this.requestRepo = requestRepo;
    }

    @GetMapping
    public List<Request> getAllRequests() {
        return requestRepo.findAll();
    }
}
