package com.example.evaliaproject.repository;
import com.example.evaliaproject.entity.Planning;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public interface PlanningRepository extends JpaRepository<Planning, String> {

    @Query("select p from Planning p where p.owner.id_user = :ownerId")
    List<Planning> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("select p from Planning p where p.panelist.id_user = :panelistId")
    List<Planning> findByPanelistId(@Param("panelistId") Long panelistId);

    @Query("select p from Planning p where p.announcement.idAnnouncement = :annId")
    List<Planning> findByAnnouncementId(@Param("annId") String annId);

    @Query("""
    select p from Planning p
     where p.panelist.id_user = :panelistId
       and p.startsAt < :end
       and p.endsAt   > :start
  """)
    List<Planning> findConflicts(@Param("panelistId") Long panelistId,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);
}
//    List<Planning> findByOwner_Id_user(Long ownerId);
//    List<Planning> findByPanelist_Id_user(Long panelistId);
//    List<Planning> findByAnnouncement_IdAnnouncement(String annId);
//
//    // pour empêcher conflits quand on assigne un paneliste
//    @Query("""
//    select a from Planning a
//     where a.panelist.id_user = :panelistId
//       and a.startsAt < :end
//       and a.endsAt   > :start
//  """)
//    List<Planning> findConflicts(Long panelistId, LocalDateTime start, LocalDateTime end);
//}
//
















//    /** Tous les rendez-vous d’un annonceur (owner) */
//    @Query("""
//    select p from Planning p
//    left join fetch p.panelist
//    where p.owner.id_user = :ownerId
//    order by p.startAt desc
//  """)
//    List<Planning> ownerPlans(@Param("ownerId") Long ownerId);
//
//    /** Tous les rendez-vous d’un paneliste (pour son agenda) */
//    @Query("""
//    select p from Planning p
//    left join fetch p.owner
//    where p.panelist.id_user = :panelistId
//    order by p.startAt desc
//  """)
//    List<Planning> panelistPlans(@Param("panelistId") Long panelistId);
//
//    /** Détecter un chevauchement de créneau pour un paneliste */
//    @Query("""
//    select (count(p)>0) from Planning p
//    where p.panelist.id_user = :panelistId
//      and (:planningId is null or p.id <> :planningId)
//      and p.startAt < :endAt
//      and (p.endAt is null or p.endAt > :startAt)
//  """)
//    boolean existsOverlapForPanelist(@Param("planningId") String planningId,
//                                     @Param("panelistId") Long panelistId,
//                                     @Param("startAt") OffsetDateTime startAt,
//                                     @Param("endAt")   OffsetDateTime endAt);
//}
