package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.model.TeamFile;
import ch.fullstack.dalzana.repo.TeamFileRepository;
import ch.fullstack.dalzana.repo.TeamRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/teams/{teamId}/files")
public class TeamFileController {

    private final Path uploadDir;

    private final TeamRepository teamRepo;
    private final TeamFileRepository fileRepo;

    public TeamFileController(TeamRepository teamRepo,
                              TeamFileRepository fileRepo,
                              @Value("${app.upload-dir:uploads}") String uploadDir) {
        this.teamRepo = teamRepo;
        this.fileRepo = fileRepo;
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @GetMapping
    public String page(@PathVariable Long teamId, Model model) {
        model.addAttribute("teamId", teamId);
        model.addAttribute("files", fileRepo.findByTeamIdOrderByUploadedAtDesc(teamId));
        return "files";
    }

    @PostMapping("/upload")
    public String upload(@PathVariable Long teamId,
                         @RequestParam("files") MultipartFile[] files,
                         RedirectAttributes redirectAttributes) {
        var team = teamRepo.findById(teamId).orElseThrow();

        try {
            Files.createDirectories(uploadDir);

            int count = 0;
            for (MultipartFile f : files) {
                if (f == null || f.isEmpty()) continue;

                String original = Path.of(f.getOriginalFilename()).getFileName().toString();
                String stored = UUID.randomUUID() + "_" + original;

                Path target = uploadDir.resolve(stored);
                Files.copy(f.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                fileRepo.save(new TeamFile(team, original, stored, f.getSize()));
                count++;
            }

            redirectAttributes.addFlashAttribute("successMessage", "✅ " + count + " Datei(en) hochgeladen.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Upload fehlgeschlagen: " + e.getMessage());
        }

        return "redirect:/teams/" + teamId + "/files";
    }

    @GetMapping("/{fileId}/download")
public ResponseEntity<Resource> download(@PathVariable Long teamId,
                                        @PathVariable Long fileId) throws Exception {

    TeamFile tf = fileRepo.findById(fileId).orElseThrow();

    // Safety: sicherstellen, dass File wirklich zu diesem Team gehört
    if (!tf.getTeam().getId().equals(teamId)) {
        return ResponseEntity.notFound().build();
    }

    Path filePath = uploadDir.resolve(tf.getStoredName()).normalize();
    Resource resource = new UrlResource(filePath.toUri());

    if (!resource.exists()) {
        return ResponseEntity.notFound().build();
    }

    String contentType = Files.probeContentType(filePath);
    if (contentType == null) {
        contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + tf.getOriginalName().replace("\"", "") + "\"")
            .body(resource);
}

@PostMapping("/{fileId}/delete")
    public String delete(@PathVariable Long teamId,
                         @PathVariable Long fileId,
                         RedirectAttributes redirectAttributes) {
        TeamFile tf = fileRepo.findById(fileId).orElseThrow();

        // Safety: sicherstellen, dass File wirklich zu diesem Team gehört
        if (!tf.getTeam().getId().equals(teamId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Datei gehört nicht zu diesem Team.");
            return "redirect:/teams/" + teamId + "/files";
        }

        try {
            // Datei vom Dateisystem löschen
            Path filePath = uploadDir.resolve(tf.getStoredName());
            Files.deleteIfExists(filePath);

            // Datei aus DB löschen
            fileRepo.deleteById(fileId);

            redirectAttributes.addFlashAttribute("successMessage", "✅ Datei gelöscht.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Löschen fehlgeschlagen: " + e.getMessage());
        }

        return "redirect:/teams/" + teamId + "/files";
    }
}
