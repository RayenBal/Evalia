package com.example.evaliaproject.controller;
import com.example.evaliaproject.entity.User;
import com.example.evaliaproject.repository.ReclamationRepository;
import com.example.evaliaproject.service.ReclamationService;
import org.springframework.security.core.Authentication;


import com.example.evaliaproject.entity.Reclamation;
import com.example.evaliaproject.repository.UserRepository;
import com.example.evaliaproject.service.IReclamationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin("http://localhost:4200")
@RequestMapping("/reclamation")
@RestController
public class ReclamationController {
    @Autowired
    IReclamationService iReclamationService;
    @Autowired
    ReclamationRepository repo;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/addReclamation")
    public Reclamation add(@RequestBody Reclamation body, Authentication auth) {
        User u = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // on ignore l’ID et un éventuel user envoyé par le client
        Reclamation r = Reclamation.builder()
                .content(body.getContent())
                .motif(body.getMotif())
                .user(u)
                .build();

        return repo.save(r);
    }

    /** Mes réclamations */
    @GetMapping("/mine")
    public List<Reclamation> mine() {
        return ((ReclamationService) iReclamationService).listMine();
    }

    /** Détails (autorisé si propriétaire) */
    @GetMapping("/getDetailsReclamation/{id}")
    public Reclamation getOne(@PathVariable String id, Authentication auth) {
        Reclamation r = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkOwner(r, auth);
        return r;
    }

    /** Update (autorisé si propriétaire) */
    @PutMapping("/updateReclamation/{id}")
    public Reclamation update(@PathVariable String id, @RequestBody Reclamation body, Authentication auth) {
        Reclamation r = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkOwner(r, auth);
        r.setContent(body.getContent());
        r.setMotif(body.getMotif());
        return repo.save(r);
    }

    /** Delete (autorisé si propriétaire) */
    @DeleteMapping("/deleteReclamation/{id}")
    public void delete(@PathVariable String id, Authentication auth) {
        Reclamation r = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        checkOwner(r, auth);
        repo.delete(r);
    }

    private void checkOwner(Reclamation r, Authentication auth) {
        String email = auth.getName();
        if (r.getUser() == null || !email.equals(r.getUser().getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n’êtes pas le propriétaire.");
        }
    }
}
















//    @PostMapping("/addReclamation")
//    public Reclamation addReclamation(@RequestBody Reclamation reclamation) {
//
//        return iReclamationService.addReclamation(reclamation);
//
//    }
//
//    @GetMapping("/getAllReclamation")
//    public List<Reclamation> getAllReclamation(){
//        return iReclamationService.getAllReclamations();
//
//    }
//
//    @GetMapping("/getDetailsReclamation/{id}")
//    public Reclamation getDetailsReclamation(@PathVariable("id") String id){
//        return iReclamationService.DetailsReclamation(id);
//    }
//
//    @PutMapping("/updateReclamation/{id}")
//    public Reclamation updateReclamation (@RequestBody Reclamation reclamation,@PathVariable("id") String id){
//        return iReclamationService.updateReclamation(reclamation,id);
//    }
//
//    @DeleteMapping("/deleteReclamation/{id}")
//    public String deleteReclamation(@PathVariable("id") String id){
//        iReclamationService.deleteReclamation(id);
//        return "Reclamation deleted";
//    }

