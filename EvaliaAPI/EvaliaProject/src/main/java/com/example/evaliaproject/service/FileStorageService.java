package com.example.evaliaproject.service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${upload.dir:uploads}")
    private String uploadDir;

    public String savePdf(MultipartFile file, String subDir, String prefix) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("obligation de copie de registre de commerce");
        }
        // tolérer certains proxies/navigateurs qui ne posent pas exactement application/pdf
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        String original = file.getOriginalFilename() == null ? "document.pdf" : file.getOriginalFilename();
        String lower = original.toLowerCase();
        if (!contentType.contains("pdf") && !lower.endsWith(".pdf")) {
            throw new IllegalArgumentException("Le fichier doit être un PDF.");
        }

        Path root = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();
        Files.createDirectories(root);

        String cleanName = StringUtils.cleanPath(original).replaceAll("\\s+", "_");
        String filename = (prefix == null ? "" : prefix + "_") + UUID.randomUUID() + ".pdf";
        Path target = root.resolve(filename);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return root.getFileName().resolve(filename).toString(); // ex: "registre/uuid.pdf"
    }
}
