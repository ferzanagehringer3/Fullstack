package ch.fullstack.dalzana.service;

import ch.fullstack.dalzana.model.Request;
import ch.fullstack.dalzana.repo.RequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestService {

    private final RequestRepository repo;

    public RequestService(RequestRepository repo) {
        this.repo = repo;
    }

    public List<Request> findAll() {
        return repo.findAll();
    }

    public Request findById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Request save(Request request) {
        return repo.save(request);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
