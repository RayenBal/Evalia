package com.example.evaliaproject.service;

import com.example.evaliaproject.entity.Announce;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


import java.util.List;

public interface IAnnouncementService {
    public Announce addAnnouncement(Announce announcement);


    void deleteAnnouncement(String idAnnouncement);

    public List<Announce> getAllAnnouncements();


    Announce DetailsAnnouncement(String idAnnouncement);

    Announce updateAnnouncement(Announce announcement, String id);
//    Announce uploadImage(MultipartFile file, String announcementId);
//    Resource loadImage(String filename);
    Announce saveAnnouncementWithQuizzes(
            MultipartFile image,
            MultipartFile[] productImages,
            String announceDataJson,String email
    ) throws IOException;
     Announce updateAnnouncementWithImages(
            String id,
            String announceData,
            MultipartFile image,
            MultipartFile[] productImages
    )   throws IOException;
}
