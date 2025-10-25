package com.example.evaliaproject.repository;

import com.example.evaliaproject.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,String> {
    List<Question> findByQuiz_idQuiz(String idQuiz);

}
