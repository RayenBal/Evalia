package com.example.evaliaproject.service;
import com.example.evaliaproject.entity.*;
import com.example.evaliaproject.repository.*;
import org.springframework.transaction.annotation.Transactional;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnnouncementService implements IAnnouncementService{
    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RecompensesRepository recompensesRepository; //
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AttemptAnswerRepository attemptAnswerRepository;
    @Autowired private QuizAttemptRepository quizAttemptRepository;
    @Autowired private  FeedbackRepository feedbackRepository;
    @Autowired private EarnedRewardRepository earnedRewardRepository;
    @Autowired private NotificationRepository notificationRepository;
//    @Value("${upload.dir}")
//    private String uploadDir;
    @Override
    public Announce addAnnouncement(Announce announcement) {
        return announcementRepository.save(announcement);
    }

//    @Override
//    public void deleteAnnouncement(String idAnnouncement) {
//        announcementRepository.deleteById(idAnnouncement);
//    }
@Override
@Transactional
public void deleteAnnouncement(String idAnnouncement) {
    // 1) supprimer tout ce qui référence l’annonce
    attemptAnswerRepository.deleteByAnnouncementId(idAnnouncement);
    quizAttemptRepository.deleteByAnnouncementId(idAnnouncement);
    feedbackRepository.deleteByAnnouncementId(idAnnouncement);
    earnedRewardRepository.deleteByAnnouncementId(idAnnouncement);
    notificationRepository.deleteByAnnouncementId(idAnnouncement);

    // 2) supprimer l’annonce (Quiz/Question/ResponsePaneliste tomberont via cascade)
    announcementRepository.deleteById(idAnnouncement);
}
    @Override
    public List<Announce> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)

    public Announce DetailsAnnouncement(String idAnnouncement) {
       // return announcementRepository.findById(idAnnouncement).get();
        Announce a = announcementRepository.fetchDetails(idAnnouncement).orElseThrow();
        a.getQuizList().forEach(q -> q.getQuestions().forEach(qu -> qu.getResponses().size()));
        return a;}


    @Override
    public Announce updateAnnouncement(Announce announcement, String id) {
        Announce existingAnnouncement = announcementRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Impossible de trouver l'announcement avec l'ID : " + id));

        existingAnnouncement.setContent(announcement.getContent());
        existingAnnouncement.setImage(announcement.getImage());

        return announcementRepository.save(existingAnnouncement);
    }

//
//    public Announce uploadImage(MultipartFile file, String announcementId) {
//        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
//        String newFileName = generateNewFileName(originalFileName);
//
//        Path uploadPath = Paths.get(uploadDir);
//        try {
//            if (Files.notExists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//            }
//
//            Path filePath = uploadPath.resolve(newFileName);
//            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//            Announce announcement = announcementRepository.findById(announcementId)
//                    .orElseThrow(() -> new IllegalArgumentException("Annonce non trouvée"));
//
//            announcement.setImage(newFileName);
//            return announcementRepository.save(announcement);
//
//        } catch (IOException e) {
//            throw new RuntimeException("Échec du téléchargement du fichier : " + newFileName, e);}
//
//    }
//
//    public Resource loadImage(String filename) {
//        try {
//            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//
//            if (resource.exists()) {
//                return resource;
//            } else {
//                throw new RuntimeException("Fichier introuvable : " + filename);
//            }
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("Fichier invalide : " + filename, e);
//        }
//    }

    private String generateNewFileName(String originalName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return timestamp + "_" + originalName;
    }
    @Override
    public Announce saveAnnouncementWithQuizzes(
            MultipartFile image,
            MultipartFile[] productImages,
            String announceData,String email
    ) throws IOException {
        System.out.println(">>> Début de saveAnnouncementWithQuizzes()");
        System.out.println("Contenu reçu de announceData : " + announceData);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

       Announce announcement = mapper.readValue(announceData, Announce.class);

       // Announce announcement;
//        try {
//            announcement = mapper.readValue(announceData, Announce.class);
//        } catch (Exception e) {
//            System.err.println("❌ Erreur lors du parsing de announceData JSON");
//            e.printStackTrace();
//            throw new RuntimeException("Erreur de parsing JSON : " + e.getMessage());
//        }

        // 🔗 Vérifier la catégorie et attacher une entité gérée
        if (announcement.getCategory() == null || announcement.getCategory().getIdcategory() == null) {
            throw new IllegalArgumentException("La catégorie est obligatoire (category.idcategory manquant).");
        }
        Long catId = announcement.getCategory().getIdcategory();
        Category managedCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable (id=" + catId + ")"));
        announcement.setCategory(managedCategory);

        User current = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
        if (current.getTypeUser() != TypeUser.Announceur) {
            throw new org.springframework.security.access.AccessDeniedException("Seuls les annonceurs peuvent créer une annonce.");
        }
        announcement.setUser(current);
        Path uploadPath = Paths.get("uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Upload image principale
        if (image != null && !image.isEmpty()) {
            String imageName = UUID.randomUUID() + "_" + sanitizeFileName(image.getOriginalFilename());
            Path imagePath = uploadPath.resolve(imageName);

            Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            announcement.setImage(imageName);
        }

        // Upload images produits
        List<String> productImageNames = new ArrayList<>();
        if (productImages != null) {
            for (MultipartFile file : productImages) {
                if (!file.isEmpty()) {
                String name = UUID.randomUUID() + "_" + sanitizeFileName(file.getOriginalFilename());
                Files.copy(file.getInputStream(), uploadPath.resolve(name), StandardCopyOption.REPLACE_EXISTING);
                productImageNames.add(name);
            }
            }

        }
        announcement.setProductImages(String.join(",", productImageNames));

        // Lier chaque quiz à l’annonce
        if (announcement.getQuizList() != null) {
            for (Quiz quiz : announcement.getQuizList()) {
                quiz.setAnnouncement(announcement);
                if (quiz.getQuestions() != null) {
                    for (Question question : quiz.getQuestions()) {
                        question.setQuiz(quiz);
                        if (question.getResponses() != null) {
                            for (ResponsePaneliste response : question.getResponses()) {
                                response.setQuestion(question);
                            }
                        }
                    }
                }
            }
//            User current = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
//
//            if (current.getTypeUser() != TypeUser.Announceur) {
//                throw new org.springframework.security.access.AccessDeniedException(
//                        "Seuls les annonceurs peuvent créer une annonce.");
//            }

//            announcement.setUser(current);      // 👈 remplit la FK user_id

//            // 4) ⚡️ NOUVEAU : gérer recompensesList au moment de l’ajout
//            if (announcement.getRecompensesList() != null && !announcement.getRecompensesList().isEmpty()) {
//
//                // a) Validation métier simple (optionnelle mais recommandé)
//                validateRewards(announcement.getRecompensesList());
//
//                // b) Séparer celles avec id (existantes) et sans id (nouvelles)
//                List<Recompenses> incoming = announcement.getRecompensesList();
//
//                List<String> existingIds = incoming.stream()
//                        .map(Recompenses::getIdRecompense)
//                        .filter(Objects::nonNull)
//                        .toList();
//
//                // b1) Charger les existantes depuis la BDD
//                List<Recompenses> managedExisting = existingIds.isEmpty()
//                        ? List.of()
//                        : recompensesRepository.findAllById(existingIds);
//
//                // b2) Conserver les nouvelles (sans id) telles quelles (cascade PERSIST les créera)
//                List<Recompenses> newOnes = incoming.stream()
//                        .filter(r -> r.getIdRecompense() == null)
//                        .collect(Collectors.toList());
//
//                // c) Remplacer la liste par (existantes gérées + nouvelles)
//                List<Recompenses> merged = new ArrayList<>();
//                merged.addAll(managedExisting);
//                merged.addAll(newOnes);
//
//                announcement.setRecompensesList(merged);
//            }

            if (announcement.getRecompensesList() != null && !announcement.getRecompensesList().isEmpty()) {
                validateRewards(announcement.getRecompensesList());

                List<Recompenses> fresh = new ArrayList<>();
                for (Recompenses r : announcement.getRecompensesList()) {
                    Recompenses rr = new Recompenses();
                    rr.setTypeRecompenses(r.getTypeRecompenses());
                    rr.setAmount(r.getAmount());
                    rr.setLabel(r.getLabel());
                    rr.setAnnouncement(announcement); // ⬅️ clé: remplit announcement_id_announcement
                    fresh.add(rr);
                }
                announcement.setRecompensesList(fresh);
            }
        }

        //return announcementRepository.save(announcement);
        Announce saved = announcementRepository.save(announcement);
        System.out.println("✅ Annonce enregistrée avec succès : " + saved.getIdAnnouncement());

        return saved;

    }
    private String sanitizeFileName(String originalName) {
        return originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

    }

    @Override
    public Announce updateAnnouncementWithImages(
            String id,
            String announceData,
            MultipartFile image,
            MultipartFile[] productImages
    ) throws IOException {

        Announce existingAnnounce = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée avec l’ID : " + id));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Announce newData = mapper.readValue(announceData, Announce.class);

        // Champs simples
        existingAnnounce.setAnnounceName(newData.getAnnounceName());
        existingAnnounce.setContent(newData.getContent());
        existingAnnounce.setOfficeAddress(newData.getOfficeAddress());
        existingAnnounce.setDeliveryAddress(newData.getDeliveryAddress());
        existingAnnounce.setTestModes(newData.getTestModes());

        // 🔗 Catégorie : ne changer que si fournie et non nulle
        if (newData.getCategory() != null && newData.getCategory().getIdcategory() != null) {
            Long catId = newData.getCategory().getIdcategory();
            Category managedCategory = categoryRepository.findById(catId)
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable (id=" + catId + ")"));
            existingAnnounce.setCategory(managedCategory);
        }

        Path uploadPath = Paths.get("uploads");
        if (Files.notExists(uploadPath)) Files.createDirectories(uploadPath);

        // Image principale (optionnelle)
        if (image != null && !image.isEmpty()) {
            String imageName = UUID.randomUUID() + "_" + sanitizeFileName(image.getOriginalFilename());
            Files.copy(image.getInputStream(), uploadPath.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
            existingAnnounce.setImage(imageName);
        }

        // Images produits (optionnelles)
        if (productImages != null && productImages.length > 0) {
            List<String> productImageNames = new ArrayList<>();
            for (MultipartFile file : productImages) {
                if (!file.isEmpty()) {
                    String name = UUID.randomUUID() + "_" + sanitizeFileName(file.getOriginalFilename());
                    Files.copy(file.getInputStream(), uploadPath.resolve(name), StandardCopyOption.REPLACE_EXISTING);
                    productImageNames.add(name);
                }
            }
            existingAnnounce.setProductImages(String.join(",", productImageNames));
        }

        // ✅ Ne remplace les quizzes QUE si le payload en contient
//        if (newData.getQuizList() != null) {                 // <-- clé du correctif
//            existingAnnounce.getQuizList().clear();          // remplace complètement
//            for (Quiz q : newData.getQuizList()) {
//                q.setAnnouncement(existingAnnounce);
//                if (q.getQuestions() != null) {
//                    for (Question qu : q.getQuestions()) {
//                        qu.setQuiz(q);
//                        if (qu.getResponses() != null) {
//                            for (ResponsePaneliste r : qu.getResponses()) {
//                                r.setQuestion(qu);
//                            }
//                        }
//                    }
//                }
//                existingAnnounce.getQuizList().add(q);
//            }
//        }
        // sinon: on ne touche pas aux quiz existants
        if (newData.getQuizList() != null) {
            // Si tu veux ignorer le cas [] pour ne rien toucher :
            if (!newData.getQuizList().isEmpty()) {
                existingAnnounce.getQuizList().clear();
                for (Quiz q : newData.getQuizList()) {
                    q.setAnnouncement(existingAnnounce);
                    if (q.getQuestions() != null) {
                        for (Question qu : q.getQuestions()) {
                            qu.setQuiz(q);
                            if (qu.getResponses() != null) {
                                for (ResponsePaneliste r : qu.getResponses()) {
                                    r.setQuestion(qu);
                                }
                            }
                        }
                    }
                    existingAnnounce.getQuizList().add(q);
                }
            }
            // Si c'est vide -> on ne touche pas aux quiz existants
        }
        // ✅ Récompenses : remplacement total (plus simple)
        if (newData.getRecompensesList() != null) {
            validateRewards(newData.getRecompensesList());

            existingAnnounce.getRecompensesList().clear();
            for (Recompenses inc : newData.getRecompensesList()) {
                Recompenses r = new Recompenses();
                r.setTypeRecompenses(inc.getTypeRecompenses());
                r.setAmount(inc.getAmount());
                r.setLabel(inc.getLabel());
                r.setAnnouncement(existingAnnounce); // back-ref
                existingAnnounce.getRecompensesList().add(r);
            }
        }


        return announcementRepository.save(existingAnnounce);
    }

//    public Announce updateAnnouncementWithImages(
//            String id,
//            String announceData,
//            MultipartFile image,
//            MultipartFile[] productImages
//    ) throws IOException {
//
//        Optional<Announce> optionalAnnounce = announcementRepository.findById(id);
//        if (optionalAnnounce.isEmpty()) {
//            throw new RuntimeException("Annonce non trouvée avec l’ID : " + id);
//        }
//
//        Announce existingAnnounce = optionalAnnounce.get();
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        Announce newData = mapper.readValue(announceData, Announce.class);
//
//        // ✅ Mettre à jour les champs simples
//        existingAnnounce.setAnnounceName(newData.getAnnounceName());
//        existingAnnounce.setContent(newData.getContent());
//        existingAnnounce.setOfficeAddress(newData.getOfficeAddress());
//        existingAnnounce.setDeliveryAddress(newData.getDeliveryAddress());
//        existingAnnounce.setTestModes(newData.getTestModes());
//
//
//        Path uploadPath = Paths.get("uploads");
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        // ✅ Image principale (si fournie)
//        if (image != null && !image.isEmpty()) {
//            String imageName = UUID.randomUUID() + "_" + sanitizeFileName(image.getOriginalFilename());
//            Files.copy(image.getInputStream(), uploadPath.resolve(imageName), StandardCopyOption.REPLACE_EXISTING);
//            existingAnnounce.setImage(imageName);
//        }
//
//        // ✅ Images produits (si fournies)
//        if (productImages != null && productImages.length > 0) {
//            List<String> productImageNames = new ArrayList<>();
//            for (MultipartFile file : productImages) {
//                if (!file.isEmpty()) {
//                    String name = UUID.randomUUID() + "_" + sanitizeFileName(file.getOriginalFilename());
//                    Files.copy(file.getInputStream(), uploadPath.resolve(name), StandardCopyOption.REPLACE_EXISTING);
//                    productImageNames.add(name);
//                }
//            }
//            existingAnnounce.setProductImages(String.join(",", productImageNames));
//        }
//        // ✅ Remplace totalement les quizzes par ceux reçus
//        existingAnnounce.getQuizList().clear(); // orphanRemoval => supprime les anciens en DB
//        if (newData.getQuizList() != null) {
//            for (Quiz q : newData.getQuizList()) {
//                q.setAnnouncement(existingAnnounce);            // back-ref
//                if (q.getQuestions() != null) {
//                    for (Question qu : q.getQuestions()) {
//                        qu.setQuiz(q);                          // back-ref
//                        if (qu.getResponses() != null) {
//                            for (ResponsePaneliste r : qu.getResponses()) {
//                                r.setQuestion(qu);              // back-ref
//                            }
//                        }
//                    }
//                }
//                existingAnnounce.getQuizList().add(q);
//            }
//        }
//        return announcementRepository.save(existingAnnounce);
//    }

    // petite validation : Points entier >= 0 ; autres > 0
    private void validateRewards(List<Recompenses> rewards) {
        if (rewards == null) return;
        for (Recompenses r : rewards) {
            if (r.getTypeRecompenses() == null)
                throw new IllegalArgumentException("typeRecompenses obligatoire pour une récompense.");

            BigDecimal amt = r.getAmount();
            if (r.getTypeRecompenses() == typeRecompenses.Points) {
                if (amt == null || amt.signum() < 0 || amt.scale() > 0)
                    throw new IllegalArgumentException("Points doit être un entier >= 0.");
            } else {
                if (amt == null || amt.compareTo(BigDecimal.ZERO) <= 0)
                    throw new IllegalArgumentException("Montant > 0 requis pour " + r.getTypeRecompenses());
            }
        }
    }
}

