package com.example.evaliaproject.controller;


import com.example.evaliaproject.entity.Recompenses;
import com.example.evaliaproject.repository.AnnouncementRepository;
import com.example.evaliaproject.repository.UserRepository;
import com.example.evaliaproject.service.IRecompensesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin("http://localhost:4200")
@RequestMapping("recompenses")
@RestController
public class RecompensesController {
    @Autowired
    IRecompensesService iRecompensesService;
@Autowired
    AnnouncementRepository announcementRepo;
@Autowired
    UserRepository  userRepo;

//    @GetMapping("/byAnnouncement/{id}")
//    public List<Recompenses> byAnnouncement(@PathVariable String id, Authentication auth) {
//        // Optionnel mais recommandé: vérifier que l’annonce appartient bien à l’annonceur connecté
//        var ann = announcementRepo.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//        var email = auth.getName();
//        if (ann.getUser() != null && !email.equals(ann.getUser().getEmail())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
//        }
//        return iRecompensesService.byAnnouncement(id);
//    }

    @GetMapping("/byAnnouncement/{id}")
    public List<Recompenses> byAnnouncement(@PathVariable String id) {
        // on vérifie juste que l'annonce existe
        announcementRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return iRecompensesService.byAnnouncement(id);
    }
    @PostMapping("/addRecomponse")
    public Recompenses addRecompenses(@RequestBody Recompenses recompenses) {

        return iRecompensesService.addRecompenses(recompenses);

    }

    @GetMapping("/getAllRecompenses")
    public List<Recompenses> getAllRecompenses(){
        return iRecompensesService.getAllRecompenses();

    }

    @GetMapping("/getDetailsRecompenses/{id}")
    public Recompenses getDetailsRecompenses(@PathVariable("id") String id){
        return iRecompensesService.DetailsRecompenses(id);
    }

    @PutMapping("/updateRecompenses/{id}")
    public Recompenses updateRecompenses (@RequestBody Recompenses recompenses,@PathVariable("id") String id){
        return iRecompensesService.updateRecompenses(recompenses,id);
    }

    @DeleteMapping("/deleteRecompenses/{id}")
    public String deleteRecompenses(@PathVariable("id") String id){
        iRecompensesService.deleteRecompenses(id);
        return "Recompenses deleted";
    }
}
