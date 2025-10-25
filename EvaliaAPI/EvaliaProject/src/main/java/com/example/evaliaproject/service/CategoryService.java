package com.example.evaliaproject.service;


import com.example.evaliaproject.entity.Category;
import com.example.evaliaproject.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryService implements ICategoryService{

    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public Category DetailsCategory(String idCategory) {
        return categoryRepository.findById(Long.valueOf(idCategory)).get();

    }

    @Override
    public List<Category> getAllCategorys() {
        List<Category> cat =categoryRepository.findAll();
        System.out.println(cat.size());
        return cat;
    }
}
