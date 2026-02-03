package ch.fullstack.dalzana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/files")
public class FileController {

    // Uploads Verzeichnis (kann in application.properties konfiguriert werden)
    private static final String UPLOAD_DIR = "uploads";

    public FileController() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Uploads-Verzeichnis konnte nicht erstellt werden", e);
        }
    }

    @GetMapping
    public String showUploadPage() {
        return "files";
    }

    @PostMapping("/upload")
    public String uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            Model model) {

        List<String> uploadedFiles = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (files.length == 0) {
            model.addAttribute("errorMessage", "Bitte wählen Sie mindestens eine Datei aus.");
            return "files";
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                errors.add(file.getOriginalFilename() + " - Datei ist leer");
                continue;
            }

            try {
                // Sicherer Dateiname erstellen
                String originalFilename = file.getOriginalFilename();
                String fileExtension = getFileExtension(originalFilename);
                String uniqueFilename = UUID.randomUUID() + (fileExtension.isEmpty() ? "" : "." + fileExtension);

                // Datei speichern
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Path filePath = uploadPath.resolve(uniqueFilename);

                Files.write(filePath, file.getBytes());

                uploadedFiles.add(originalFilename + " (" + formatFileSize(file.getSize()) + ")");

            } catch (IOException e) {
                errors.add(file.getOriginalFilename() + " - Fehler beim Hochladen: " + e.getMessage());
            }
        }

        // Meldungen zusammenstellen
        if (!uploadedFiles.isEmpty()) {
            String successMsg = "✓ " + uploadedFiles.size() + " Datei(en) erfolgreich hochgeladen: " +
                    String.join(", ", uploadedFiles);
            model.addAttribute("successMessage", successMsg);
        }

        if (!errors.isEmpty()) {
            String errorMsg = "✗ Fehler: " + String.join("; ", errors);
            model.addAttribute("errorMessage", errorMsg);
        }

        return "files";
    }

    // Hilfsmethoden
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.2f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}
