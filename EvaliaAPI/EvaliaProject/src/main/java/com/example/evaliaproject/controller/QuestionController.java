package com.example.evaliaproject.controller;

import com.example.evaliaproject.entity.Question;
import com.example.evaliaproject.service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:4200")
@RequestMapping("/question")
@RestController
public class QuestionController {

    @Autowired
    private IQuestionService questionService;

    @PostMapping("/add")
    public Question addQuestion(@RequestBody Question question) {
        return questionService.addQuestion(question);
    }

    @PutMapping("/update/{id}")
    public Question updateQuestion(@PathVariable String id, @RequestBody Question question) {
        return questionService.updateQuestion(id, question);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteQuestion(@PathVariable String id) {
        questionService.deleteQuestion(id);
    }

    @GetMapping("/get/{id}")
    public Question getQuestion(@PathVariable String id) {
        return questionService.getQuestionById(id);
    }

    @GetMapping("/all")
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/byQuiz/{idQuiz}")
    public List<Question> getByQuiz(@PathVariable String idQuiz) {
        return questionService.getQuestionsByQuizId(idQuiz);
    }
}
