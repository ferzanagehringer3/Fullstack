package ch.fullstack.dalzana.repo;

import ch.fullstack.dalzana.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> { }
