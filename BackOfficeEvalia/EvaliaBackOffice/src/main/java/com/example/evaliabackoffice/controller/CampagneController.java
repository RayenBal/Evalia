package com.example.evaliabackoffice.controller;

import com.example.evaliabackoffice.entity.Campagne;

import com.example.evaliabackoffice.repository.CampagneRepository;
import com.example.evaliabackoffice.service.ICampagneService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:4200")
@RequestMapping("/campagnes")
@Controller
public class CampagneController {

    @Autowired
    ICampagneService iCampagneService;

    @Autowired
    CampagneRepository campagneRepository;



    @PostMapping("/save")
    public String saveCampagne(@ModelAttribute Campagne campagne) {
        iCampagneService.addCampagne(campagne);
        return "redirect:/campagnes";
    }

    @GetMapping
    public String listCampagnes(Model model) {

        model.addAttribute("campagnes", campagneRepository.findAll());
        return "campagnes"; // correspond à campagnes.html
    }

    //  Vue HTML afficher formulaire

    @GetMapping("/create")
    public String showCreateCampagneForm(Model model) {
        model.addAttribute("campagne", new Campagne());
        return "create_campagne"; // 🔁 nom du fichier .html
    }



    @GetMapping("/getAllCampagne")
    public List<Campagne> getAllCampagnes() {
        return iCampagneService.getAllCampagnes();
    }


    @GetMapping("/delete/{id}")
    public String deleteCampagne(@PathVariable Long id) {
        iCampagneService.deleteCampagne(id);
        return "redirect:/campagnes";
    }



    @GetMapping("/{id}")
    public Campagne getCampagneById(@PathVariable Long id) {
        return iCampagneService.DetailsCampagne(id);
    }







    @GetMapping("/edit/{id}")
    public String editCampagneForm(@PathVariable Long id, Model model) {
        Campagne campagne = campagneRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Campagne non trouvée"));
        model.addAttribute("campagne", campagne);
        return "edit_campagne";
    }

    @PostMapping("/update")
    public String updateCampagne(@ModelAttribute Campagne campagne) {
        iCampagneService.addCampagne(campagne); // `save()` agit comme insert/update
        return "redirect:/campagnes";
    }
}
