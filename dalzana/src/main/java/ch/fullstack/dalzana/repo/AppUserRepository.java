package ch.fullstack.dalzana.repo;

import ch.fullstack.dalzana.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> { }
