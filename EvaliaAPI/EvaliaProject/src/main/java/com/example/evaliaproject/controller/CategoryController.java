package com.example.evaliaproject.controller;


import com.example.evaliaproject.entity.Announce;
import com.example.evaliaproject.entity.Category;
import com.example.evaliaproject.repository.AnnouncementRepository;
import com.example.evaliaproject.repository.CategoryRepository;
import com.example.evaliaproject.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CategoryService categoryService;

@Autowired
    AnnouncementRepository announcementRepository;





    @GetMapping("/getDetailsCategory/{id}")
    public Category getDetailsCategory(@PathVariable("id") String id){
        return categoryService.DetailsCategory(id);
    }

    @GetMapping("/getAllCategory")
    public List<Category> getAllCategory(){
        return categoryService.getAllCategorys();

    }

    @GetMapping("/{id}/announces")
    public List<Announce> getAnnounces(@PathVariable Long id) {
        return announcementRepository.findByCategory_Idcategory(id);
    }

    @PostMapping("/init")
    public String initCategories() {
        // Vérifier si des catégories existent déjà
        if (categoryRepository.count() > 0) {
            return "Catégories déjà initialisées (" + categoryRepository.count() + " catégories)";
        }

        // Créer des catégories par défaut
        String[] categories = {
            "Cosmétique",
            "Véhicule", 
            "Alimentation",
            "Électronique",
            "Mode",
            "Santé",
            "Sport"
        };

        for (String name : categories) {
            Category cat = new Category();
            cat.setNameCategory(name);
            categoryRepository.save(cat);
        }

        return categories.length + " catégories créées avec succès !";
    }
}
