package com.example.evaliaproject.service;




import com.example.evaliaproject.entity.Category;


import java.util.List;

public interface ICategoryService {

    Category DetailsCategory(String idCategory);

    public List<Category> getAllCategorys();
}
