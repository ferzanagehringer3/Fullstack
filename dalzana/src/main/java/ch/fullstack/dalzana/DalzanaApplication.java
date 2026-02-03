package ch.fullstack.dalzana;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ch.fullstack.dalzana.model.AppUser;
import ch.fullstack.dalzana.model.Request;
import ch.fullstack.dalzana.model.Skill;
import ch.fullstack.dalzana.model.role;
import ch.fullstack.dalzana.repo.AppUserRepository;
import ch.fullstack.dalzana.repo.RequestRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@SpringBootApplication
public class DalzanaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DalzanaApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(
            AppUserRepository userRepo,
            RequestRepository requestRepo
    ) {
        return args -> {

                            Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

            // 👉 Seed nur einmal ausführen
            if (requestRepo.count() > 0) {
                System.out.println("ℹ️ Seed übersprungen – Daten existieren bereits.");
                return;
            }

            // ---- Users ----
            AppUser manager = new AppUser(
                    "Manager Mia",
                    "manager@dalzana.ch",
                    role.MANAGER,
                    encoder.encode("manager123")
            );
            manager.addSkill(Skill.JAVA);
            manager.addSkill(Skill.SPRING_BOOT);

            AppUser u1 = new AppUser(
                    "Ali",
                    "ali@dalzana.ch",
                    role.USER,
                    encoder.encode("user123")
            );
            u1.addSkill(Skill.JAVA);
            u1.addSkill(Skill.SQL);

            AppUser u2 = new AppUser(
                    "Sara",
                    "sara@dalzana.ch",
                    role.USER,
                    encoder.encode("user123")
            );
            u2.addSkill(Skill.FIGMA);
            u2.addSkill(Skill.UX_UI);

            userRepo.save(manager);
            userRepo.save(u1);
            userRepo.save(u2);

            // ---- Requests ----
            Request r1 = new Request(
                    "Website Bugfix",
                    "Login Button funktioniert nicht"
            );
            r1.addRequiredSkill(Skill.FIGMA);
            r1.addRequiredSkill(Skill.JAVASCRIPT);

            Request r2 = new Request(
                    "API bauen",
                    "REST API für Requests"
            );
            r2.addRequiredSkill(Skill.JAVA);
            r2.addRequiredSkill(Skill.SPRING_BOOT);
            r2.addRequiredSkill(Skill.SQL);

            requestRepo.save(r1);
            requestRepo.save(r2);

            System.out.println("✅ Seed fertig. Users=" + userRepo.count()
                    + " Requests=" + requestRepo.count());
        };
    }
}
