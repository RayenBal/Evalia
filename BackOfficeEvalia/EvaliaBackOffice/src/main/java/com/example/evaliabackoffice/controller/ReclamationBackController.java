package com.example.evaliabackoffice.controller;

import com.example.evaliabackoffice.dto.DailyCount;
import com.example.evaliabackoffice.entity.TypeUser;
import com.example.evaliabackoffice.repository.ReclamationRepository;
import com.example.evaliabackoffice.service.ReclamationAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/bo/reclamations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN_RECLAMATION','ROLE_SUPER_ADMIN')")
public class ReclamationBackController {
    private final ReclamationAdminService svc;
    private final ReclamationRepository repo;

    @GetMapping("/list")
    public String list(@RequestParam(required = false) TypeUser type,
                       @RequestParam(required = false, defaultValue = "") String q,
                       Model model) {
        model.addAttribute("items", svc.list(type, q));
        model.addAttribute("selectedType", type);
        model.addAttribute("query", q);
        model.addAttribute("types", TypeUser.values());
        return "reclamations/list";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable String id, Model model) {
        model.addAttribute("item", svc.details(id));
        return "reclamations/details";
    }

    @GetMapping("/stats")
    public String stats(@RequestParam(required = false) TypeUser type,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                        Model model) {
        var today = LocalDate.now();
        if (to == null) to = today;
        if (from == null) from = to.minusDays(29);

        var overview = svc.overview();
        var byMotif  = (type == null) ? repo.countByMotif() : repo.countByMotifFiltered(type);
        List<DailyCount> daily = (type == null) ? repo.dailyCounts(from, to) : repo.dailyCountsFiltered(from, to, type);

        model.addAttribute("overview", overview);
        model.addAttribute("byType", repo.countByUserType());
        model.addAttribute("byMotif", byMotif);
        model.addAttribute("daily", daily);
        model.addAttribute("types", TypeUser.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "reclamations/stats";
    }
}
