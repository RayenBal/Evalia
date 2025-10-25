package com.example.evaliabackoffice.repository;

import com.example.evaliabackoffice.entity.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Category findByNameCategory(String nameCategory);
}
