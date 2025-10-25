package com.example.evaliabackoffice.controller;


import com.example.evaliabackoffice.entity.Category;
import com.example.evaliabackoffice.repository.CategoryRepository;
import com.example.evaliabackoffice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CategoryService categoryService;

//    @GetMapping("/create")
//    public String showCreateForm(Model model) {
//        model.addAttribute("category", new Category());
//        return "create_category";
//    }
@PostMapping("/addCategory")
public String addCategory(@ModelAttribute Category category) {
    categoryService.addCategory(category);
    return "redirect:/categories"; // ✅ redirection vers la liste
}

    @GetMapping
    public String listCategory(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "categories";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "create_category";
    }






//    @GetMapping
//    public String listCategory(Model model) {
//        model.addAttribute("categories", categoryRepository.findAll());
//        return "categories";
//    }

    @GetMapping("/getAllCategory")
    public List<Category> getAllCampagne(){
        return categoryService.getAllCategorys();

    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée"));
        model.addAttribute("category", category);
        return "edit_category";
    }

    @PostMapping("/update")
    public String updateCategory(@ModelAttribute Category category) {
        categoryService.addCategory(category); // `save()` agit comme insert/update
        return "redirect:/categories";
    }
}
