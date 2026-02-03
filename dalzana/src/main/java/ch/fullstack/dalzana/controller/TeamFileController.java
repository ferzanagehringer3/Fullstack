package ch.fullstack.dalzana.controller;

import ch.fullstack.dalzana.model.TeamFile;
import ch.fullstack.dalzana.repo.TeamFileRepository;
import ch.fullstack.dalzana.repo.TeamRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/teams/{teamId}/files")
public class TeamFileController {

    private final TeamRepository teamRepo;
    private final TeamFileRepository fileRepo;

    public TeamFileController(TeamRepository teamRepo,
                              TeamFileRepository fileRepo) {
        this.teamRepo = teamRepo;
        this.fileRepo = fileRepo;
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
            int count = 0;
            for (MultipartFile f : files) {
                if (f == null || f.isEmpty()) continue;

                String original = Path.of(f.getOriginalFilename()).getFileName().toString();
                String stored = UUID.randomUUID() + "_" + original;
                byte[] fileData = f.getBytes();

                fileRepo.save(new TeamFile(team, original, stored, f.getSize(), fileData));
                count++;
            }

            redirectAttributes.addFlashAttribute("successMessage", "✅ " + count + " Datei(en) hochgeladen.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Upload fehlgeschlagen: " + e.getMessage());
        }

        return "redirect:/teams/" + teamId + "/files";
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long teamId,
                                          @PathVariable Long fileId) {
        TeamFile tf = fileRepo.findById(fileId).orElseThrow();

        // Sicherstellen, dass Datei zu diesem Team gehört
        if (!tf.getTeam().getId().equals(teamId)) {
            return ResponseEntity.notFound().build();
        }

        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        
        // Versuche, den MIME-Type zu erkennen
        if (tf.getOriginalName().endsWith(".pdf")) {
            contentType = MediaType.APPLICATION_PDF_VALUE;
        } else if (tf.getOriginalName().endsWith(".png")) {
            contentType = "image/png";
        } else if (tf.getOriginalName().endsWith(".jpg") || tf.getOriginalName().endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (tf.getOriginalName().endsWith(".txt")) {
            contentType = MediaType.TEXT_PLAIN_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + tf.getOriginalName().replace("\"", "") + "\"")
                .body(tf.getFileData());
    }

@PostMapping("/{fileId}/delete")
    public String delete(@PathVariable Long teamId,
                         @PathVariable Long fileId,
                         RedirectAttributes redirectAttributes) {
        TeamFile tf = fileRepo.findById(fileId).orElseThrow();

        // Sicherstellen, dass Datei zu diesem Team gehört
        if (!tf.getTeam().getId().equals(teamId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Datei gehört nicht zu diesem Team.");
            return "redirect:/teams/" + teamId + "/files";
        }

        try {
            // Datei aus der Datenbank löschen (fileData wird mitgelöscht)
            fileRepo.deleteById(fileId);

            redirectAttributes.addFlashAttribute("successMessage", "✅ Datei gelöscht.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Löschen fehlgeschlagen: " + e.getMessage());
        }

        return "redirect:/teams/" + teamId + "/files";
    }
}
