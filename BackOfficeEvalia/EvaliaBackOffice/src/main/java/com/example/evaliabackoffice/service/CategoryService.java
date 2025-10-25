package com.example.evaliabackoffice.service;

import com.example.evaliabackoffice.entity.Category;
import com.example.evaliabackoffice.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryService implements ICategoryService{

    @Autowired
    private CategoryRepository categoryRepository;
    @Override
    public Category addCategory(Category category) {
        return categoryRepository.save(category) ;
    }

    @Override
    public void deleteCategory(Long idCategory) {
        categoryRepository.deleteById(idCategory);
    }

    @Override
    public List<Category> getAllCategorys() {
        return categoryRepository.findAll();
    }
}
