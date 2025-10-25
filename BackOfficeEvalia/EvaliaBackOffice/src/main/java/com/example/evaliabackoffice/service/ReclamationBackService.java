package com.example.evaliabackoffice.service;


import com.example.evaliabackoffice.dto.*;
import com.example.evaliabackoffice.entity.Reclamation;
import com.example.evaliabackoffice.entity.TypeUser;
import com.example.evaliabackoffice.repository.ReclamationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReclamationBackService {
    private final ReclamationRepository repo;

    public List<ReclamationAdminDTO> list(TypeUser type, String q) {
        List<Reclamation> base = (type == null) ? repo.findAll() : repo.findAllByUserType(type);

        return base.stream()
                .filter(r -> {
                    if (q == null || q.isBlank()) return true;
                    String s = q.toLowerCase();
                    var u = r.getUser();
                    return (r.getContent() != null && r.getContent().toLowerCase().contains(s))
                            || (u != null && (
                            (u.getEmail() != null && u.getEmail().toLowerCase().contains(s))
                                    || (u.fullName() != null && u.fullName().toLowerCase().contains(s))
                    ));
                })
                .map(ReclamationAdminDTO::new)
                .toList();
    }

    public ReclamationAdminDTO details(String id) {
        return repo.findById(id).map(ReclamationAdminDTO::new).orElse(null);
    }

    public OverviewStatsDTO overview() {
        long total = repo.count();
        var byMotif = repo.countByMotif();
        var byType  = repo.countByUserType();
        return new OverviewStatsDTO(total, byMotif, byType);
    }
}