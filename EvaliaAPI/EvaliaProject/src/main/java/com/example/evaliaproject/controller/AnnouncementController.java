package com.example.evaliaproject.controller;

import com.example.evaliaproject.repository.UserRepository;
import org.springframework.security.core.Authentication;

import com.example.evaliaproject.entity.Announce;
import com.example.evaliaproject.entity.Category;
import com.example.evaliaproject.repository.AnnouncementRepository;
import com.example.evaliaproject.repository.CategoryRepository;
import com.example.evaliaproject.service.IAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
@CrossOrigin(origins ="http://localhost:4200", allowCredentials = "true")
@RequestMapping("/Announcement")
@RestController

public class AnnouncementController {
    @Autowired
    AnnouncementRepository announcementRepository;
    @Autowired
    IAnnouncementService iAnnouncementService;
    // src/main/java/com/example/evaliaproject/controller/AnnouncementController.java
    @Autowired
    CategoryRepository categoryRepository;
@Autowired
    UserRepository userRepository;


    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Category> listCategories() {
        // Grâce à @JsonIgnore sur Category.announces, ça renverra un JSON propre
        return categoryRepository.findAll();
    }



//    @PostMapping("/addAnnouncement")
//    public Announce addAnnouncement(@RequestBody Announce announcement) {
//        System.out.println("REÇU : " + announcement);
//        return iAnnouncementService.addAnnouncement(announcement);
//
//    }

//    @GetMapping("/getAllAnnouncements")
//    public List<Announce> getAllAnnouncements(){
//        return iAnnouncementService.getAllAnnouncements();
//
//    }
@GetMapping("/getAllAnnounces")
public List<Announce> getAllAnnouncements() {
    return iAnnouncementService.getAllAnnouncements();
}

    @GetMapping("/getDetailsAnnouncement/{id}")
    public Announce getDetailsAnnouncement(@PathVariable("id") String id){
        return iAnnouncementService.DetailsAnnouncement(id);
    }

//    @PutMapping("/updateAnnouncement/{id}")
//    public Announce updateAnnouncement (@RequestBody Announce announcement, @PathVariable("id") String id){
//        return iAnnouncementService.updateAnnouncement(announcement,id);
//    }

    @DeleteMapping("/deleteAnnouncement/{id}")
    public String deleteAnnouncement(@PathVariable("id") String id){
        iAnnouncementService.deleteAnnouncement(id);
        return "Announcement deleted";
    }

//    @PostMapping("/uploadannounce/{id}")
//    public Announce handleFileUpload(@RequestParam("photo") MultipartFile file, @PathVariable("id") String announcementId) {
//        return iAnnouncementService.uploadImage(file,announcementId);
//
//    }
//
//    @GetMapping("/downloadannounce/{fileName}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName) {
//        Resource resource = iAnnouncementService.loadImage(fileName);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
//
//    }
//    @GetMapping("/images/{filename}")
//    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
//        Resource file = iAnnouncementService.loadImage(filename);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
//                .body(file);
//    }
    @PostMapping(value = "/addAnnounce", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAnnounce(
            @RequestPart("announceData") String announceData,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "productImages", required = false) MultipartFile[] productImages,
            Authentication authentication

    ) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Non authentifié"));
            }
            String email = authentication.getName();
            Announce saved = iAnnouncementService.saveAnnouncementWithQuizzes(
                    image, productImages, announceData,email
            );
//            return ResponseEntity.ok(saved);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new Announce()); // ou une classe d’erreur dédiée
//        }
            // ✅ Bonne pratique : retourner une réponse claire
            return ResponseEntity.ok(Map.of(
                    "message", "Annonce créée avec succès",
                    "id", saved.getIdAnnouncement()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Erreur lors de la création de l'annonce",
                            "details", e.getMessage()
                    ));
        }

    }
    // AnnouncementController.java
    @GetMapping("/downloadannounce/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping(value = "/updateAnnounceWithImages/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAnnounceWithImages(
            @PathVariable("id") String id,
            @RequestPart("announceData") String announceData,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "productImages", required = false) MultipartFile[] productImages
    ) {
        try {
            Announce updated = iAnnouncementService.updateAnnouncementWithImages(
                    id, announceData, image, productImages
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Annonce mise à jour avec succès",
                    "id", updated.getIdAnnouncement()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Erreur lors de la mise à jour de l'annonce",
                            "details", e.getMessage()
                    ));
        }
    }
    @GetMapping("/mine")
    public ResponseEntity<?> myAnnouncements(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Non authentifié"));
        }
        // selon ton SecurityConfig, auth.getPrincipal() peut être ton User
        String email = auth.getName();
        Long uid = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email))
                .getId_user();

        return ResponseEntity.ok(announcementRepository.findAllByUserId(uid));
    }
}
