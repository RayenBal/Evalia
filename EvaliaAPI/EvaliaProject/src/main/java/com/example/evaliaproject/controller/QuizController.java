package com.example.evaliaproject.controller;



import com.example.evaliaproject.entity.Quiz;
import com.example.evaliaproject.entity.ResponsePaneliste;
import com.example.evaliaproject.repository.QuizRepository;
import com.example.evaliaproject.service.IQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("http://localhost:4200")
@RequestMapping("/quiz")
@RestController
public class QuizController {
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    IQuizService iQuizService;


    @PostMapping("/addQuiz")
    public Quiz addQuiz(@RequestBody Quiz quiz, @RequestParam(name = "announceId", required = false) String announceId) {

        return iQuizService.addQuiz(quiz, announceId);

    }

    @GetMapping("/getAllQuiz")
    public List<Quiz> getAllQuiz(){
        return iQuizService.getAllQuiz();

    }

    @GetMapping("/getDetailsQuiz/{id}")
    public Quiz getDetailsQuiz(@PathVariable("id") String id){
        return iQuizService.DetailsQuiz(id);
    }

    @PutMapping("/updateQuiz/{id}")
    public Quiz updateQuiz (@RequestBody Quiz quiz,@PathVariable("id") String id){
        return iQuizService.updateQuiz(quiz,id);
    }
//    @GetMapping("/getDetailsQuiz/{id}")
//    public Quiz getDetailsQuiz(@PathVariable("id") String id) {
//        return quizRepository.fetchDetails(id)
//                .orElseThrow(() -> new RuntimeException("Quiz introuvable: " + id));
//    }

    @DeleteMapping("/deleteQuiz/{id}")
    public String deleteQuiz(@PathVariable("id") String id){
        iQuizService.deleteQuiz(id);
        return "Quiz deleted";
    }
    @PostMapping("/submitQuiz/{id}")
    public String submitQuiz(@PathVariable("id") String id, @RequestBody List<ResponsePaneliste> responses) {
        iQuizService.submitResponses(id, responses);
        return "Réponses enregistrées avec succès";
    }
    @GetMapping("/byAnnouncement/{id}")
    public List<Quiz> getQuizzesByAnnouncement(@PathVariable("id") String id) {
        return quizRepository.findByAnnouncementIdAnnouncement(id);
    }

}
