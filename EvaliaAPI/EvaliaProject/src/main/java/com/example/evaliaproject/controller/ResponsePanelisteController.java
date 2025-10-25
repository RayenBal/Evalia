package com.example.evaliaproject.controller;

import com.example.evaliaproject.entity.ResponsePaneliste;
import com.example.evaliaproject.service.IResponsePanelisteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:4200")
@RequestMapping("/reponsepaneliste")
@RestController
public class ResponsePanelisteController {
    @Autowired
    private IResponsePanelisteService service;

    @PostMapping("/add")
    public ResponsePaneliste add(@RequestBody ResponsePaneliste r) {
        return service.addReponse(r);
    }

    @PutMapping("/update/{id}")
    public ResponsePaneliste update(@PathVariable String id, @RequestBody ResponsePaneliste r) {
        return service.updateReponse(id, r);
    }

    @GetMapping("/get/{id}")
    public ResponsePaneliste get(@PathVariable String id) {
        return service.getReponseById(id);
    }

    @GetMapping("/all")
    public List<ResponsePaneliste> all() {
        return service.getAllReponses();
    }

//    @GetMapping("/byQuestion/{qid}")
//    public List<ResponsePaneliste> getByQuestion(@PathVariable String idQuestion) {
//        return service.getByQuestionId(idQuestion);
//    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        service.deleteReponse(id);
    }

    @GetMapping("/byQuestion/{qid}")
    public List<ResponsePaneliste> getByQuestion(@PathVariable("qid") String idQuestion) {
        return service.getByQuestionId(idQuestion);
    }
}
