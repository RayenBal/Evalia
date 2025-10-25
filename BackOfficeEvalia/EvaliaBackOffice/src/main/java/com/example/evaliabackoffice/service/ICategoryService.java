package com.example.evaliabackoffice.service;



import com.example.evaliabackoffice.entity.Category;

import java.util.List;

public interface ICategoryService {
    public Category addCategory(Category category);


    void deleteCategory(Long idCategory);

    public List<Category> getAllCategorys();
}
