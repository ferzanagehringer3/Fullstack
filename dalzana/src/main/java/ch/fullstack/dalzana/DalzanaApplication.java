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
import ch.fullstack.dalzana.repo.SkillRepository;

@SpringBootApplication
public class DalzanaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DalzanaApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(
            SkillRepository skillRepo,
            AppUserRepository userRepo,
            RequestRepository requestRepo
    ) {
        return args -> {

            // 👉 Seed nur einmal ausführen
            if (requestRepo.count() > 0) {
                System.out.println("ℹ️ Seed übersprungen – Daten existieren bereits.");
                return;
            }

            // ---- Skills (idempotent) ----
            Skill java = getOrCreateSkill(skillRepo, "Java");
            Skill spring = getOrCreateSkill(skillRepo, "Spring");
            Skill sql = getOrCreateSkill(skillRepo, "SQL");
            Skill ui = getOrCreateSkill(skillRepo, "UI");

            // ---- Users ----
            AppUser manager = new AppUser(
                    "Manager Mia",
                    "manager@dalzana.ch",
                    role.MANAGER
            );
            manager.addSkill(java);
            manager.addSkill(spring);

            AppUser u1 = new AppUser(
                    "Ali",
                    "ali@dalzana.ch",
                    role.USER
            );
            u1.addSkill(java);
            u1.addSkill(sql);

            AppUser u2 = new AppUser(
                    "Sara",
                    "sara@dalzana.ch",
                    role.USER
            );
            u2.addSkill(ui);

            userRepo.save(manager);
            userRepo.save(u1);
            userRepo.save(u2);

            // ---- Requests ----
            Request r1 = new Request(
                    "Website Bugfix",
                    "Login Button funktioniert nicht"
            );
            r1.addRequiredSkill(ui);

            Request r2 = new Request(
                    "API bauen",
                    "REST API für Requests"
            );
            r2.addRequiredSkill(java);
            r2.addRequiredSkill(spring);
            r2.addRequiredSkill(sql);

            requestRepo.save(r1);
            requestRepo.save(r2);

            System.out.println("✅ Seed fertig. Users=" + userRepo.count()
                    + " Skills=" + skillRepo.count()
                    + " Requests=" + requestRepo.count());
        };
    }

    // ---- Helper: verhindert Duplicate-Fehler ----
    private Skill getOrCreateSkill(SkillRepository repo, String name) {
        return repo.findByName(name)
                .orElseGet(() -> repo.save(new Skill(name)));
    }
}
