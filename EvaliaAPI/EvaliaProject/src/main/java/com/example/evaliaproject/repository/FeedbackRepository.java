package com.example.evaliaproject.repository;

import com.example.evaliaproject.dto.OwnerFeedbackItemDto;
import com.example.evaliaproject.entity.Feedback;
import com.example.evaliaproject.entity.FeedbackOwnerDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,String> {
    @Query("""
  select new com.example.evaliaproject.dto.MyFeedbackItemDto(
    a.idAnnouncement, a.announceName, f.rating, f.comment, f.createdAt
  )
  from Feedback f
    join f.announcement a
  where f.panelist.id_user = :panelistId
  order by f.createdAt desc
""")
    List<com.example.evaliaproject.dto.MyFeedbackItemDto> findAllMine(@Param("panelistId") Long panelistId);

//    @Query("""
//  select new com.example.evaliaproject.dto.PanelistRewardItemDto(
//    a.idAnnouncement, a.announceName, r.typeRecompenses, r.amount, f.createdAt
//  )
//  from Feedback f
//    join f.announcement a
//    join a.recompensesList r
//  where f.panelist.id_user = :panelistId
//  order by f.createdAt desc
//""")
//    List<com.example.evaliaproject.dto.PanelistRewardItemDto> rewardsForPanelist(@Param("panelistId") Long panelistId);
    @Query("""
  select f from Feedback f
  where f.announcement.idAnnouncement = :announcementId
    and f.panelist.id_user = :panelistId
""")
    java.util.Optional<Feedback> findOneByAnnouncementAndPanelist(
            @Param("announcementId") String announcementId,
            @Param("panelistId") Long panelistId
    );
    List<Feedback> findByAnnouncement_IdAnnouncement(String announcementId);
 //   List<Feedback> findByPanelist_Id(Long panelistId);


    long countByAnnouncement_IdAnnouncement(String announcementId);

    long countByAnnouncement_IdAnnouncementAndRating(String announcementId, Integer rating);

    @Query("select avg(f.rating) from Feedback f where f.announcement.idAnnouncement = :annId")
    Double averageRating(@Param("annId") String announcementId);

    @Query("""
    select f from Feedback f
    where f.announcement.idAnnouncement = :annId
      and f.panelist.id_user = :panelistId
  """)
    Optional<Feedback> findMine(@Param("annId") String announcementId,
                                @Param("panelistId") Long panelistId);

    // Liste “Owner” en DTO (pas d’entités Hibernate dans la réponse)
    @Query("""
    select new com.example.evaliaproject.dto.OwnerFeedbackItemDto(
      f.idFeedback, f.createdAt, f.rating, f.comment,
      p.id_user, p.firstname, p.lastname
    )
    from Feedback f
      join f.panelist p
    where f.announcement.idAnnouncement = :annId
    order by f.createdAt desc
  """)
    List<OwnerFeedbackItemDto> ownerList(@Param("annId") String announcementId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
     delete from Feedback f
     where f.announcement.idAnnouncement = :annId
  """)
    int deleteByAnnouncementId(@Param("annId") String annId);













// Feedbacks d’une annonce (vue annonceur)
// @Query("""
//    select new com.example.evaliaproject.dto.FeedbackOwnerDto(
//      f.idFeedback, f.rating, f.comment, f.createdAt,
//      p.id_user, concat(p.firstname,' ',p.lastname), p.email,
//      a.idAnnouncement
//    )
//    from Feedback f
//      join f.panelist p
//      join f.announcement a
//    where a.idAnnouncement = :announcementId
//    order by f.createdAt desc
//  """)
// List<FeedbackOwnerDto> ownerViewByAnnouncement(@Param("announcementId") String announcementId);
//
//    // Feedbacks de toutes les annonces d’un annonceur
//    @Query("""
//    select new com.example.evaliaproject.dto.FeedbackOwnerDto(
//      f.idFeedback, f.rating, f.comment, f.createdAt,
//      p.id_user, concat(p.firstname,' ',p.lastname), p.email,
//      a.idAnnouncement
//    )
//    from Feedback f
//      join f.panelist p
//      join f.announcement a
//    where a.user.id_user = :ownerId
//    order by f.createdAt desc
//  """)
//    List<FeedbackOwnerDto> ownerViewAllForOwner(@Param("ownerId") Long ownerId);
//
//    // Stats d’une annonce
//    @Query("""
//    select new com.example.evaliaproject.dto.FeedbackStatsDto(
//      count(f), avg(f.rating)
//    )
//    from Feedback f
//    where f.announcement.idAnnouncement = :announcementId
//  """)
//    FeedbackStatsDto statsForAnnouncement(@Param("announcementId") String announcementId);
}
