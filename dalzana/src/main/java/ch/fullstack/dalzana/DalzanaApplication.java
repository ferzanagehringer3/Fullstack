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
CommandLineRunner seedData(SkillRepository skillRepo,
                           AppUserRepository userRepo,
                           RequestRepository requestRepo) {
    return args -> {
        Skill java = skillRepo.save(new Skill("Java"));
        Skill spring = skillRepo.save(new Skill("Spring"));
        Skill sql = skillRepo.save(new Skill("SQL"));
        Skill ui = skillRepo.save(new Skill("UI"));

        var manager = new AppUser("Manager Mia", "manager@dalzana.ch", role.MANAGER);
        manager.addSkill(java); manager.addSkill(spring);

        var u1 = new AppUser("Ali", "ali@dalzana.ch", role.USER);
        u1.addSkill(java); u1.addSkill(sql);

        var u2 = new AppUser("Sara", "sara@dalzana.ch", role.USER);
        u2.addSkill(ui);

        userRepo.save(manager);
        userRepo.save(u1);
        userRepo.save(u2);

        Request r1 = new Request("Website Bugfix", "Login Button funktioniert nicht");
        r1.addRequiredSkill(ui);

        Request r2 = new Request("API bauen", "REST API für Requests");
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

}
