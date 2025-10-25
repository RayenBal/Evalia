package com.example.evaliaproject.repository;

import com.example.evaliaproject.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository

public interface NotificationRepository extends JpaRepository<Notification, String> {
   // List<Notification> findByRecipient_Id_userOrderByCreatedAtDesc(Long userId);
    //List<Notification> findByRecipient_IdUserOrderByCreatedAtDesc(Long recipientId);

//    @Query("""
//     select n
//     from Notification n
//     where n.recipient.id_user = :recipientId
//     order by n.createdAt desc
//  """)
//    List<Notification> findByRecipientOrderByCreatedAtDesc(@Param("recipientId") Long recipientId);

//    @Query("""
//     select n
//     from Notification n
//     left join fetch n.announcement a
//     where n.recipient.id_user = :recipientId
//     order by n.createdAt desc
//  """)
//   // List<Notification> findByRecipientOrderByCreatedAtDesc(Long recipientId);
//    List<Notification> findByRecipientOrderByCreatedAtDesc(@Param("recipientId") Long recipientId);
//
//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Transactional
//    @Query("""
//     delete from Notification n
//     where n.announcement.idAnnouncement = :annId
//  """)
//    int deleteByAnnouncementId(@Param("annId") String annId);
//
//




    @Query("""
      select n
      from Notification n
      left join fetch n.announcement a
      where n.recipient.id_user = :recipientId
      order by n.createdAt desc
    """)
    List<Notification> findByRecipientOrderByCreatedAtDesc(@Param("recipientId") Long recipientId);

    @Query("""
      select count(n)
      from Notification n
      where n.recipient.id_user = :recipientId
        and n.seen = false
    """)
    long countUnseen(@Param("recipientId") Long recipientId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
      delete from Notification n
      where n.announcement.idAnnouncement = :annId
    """)
    int deleteByAnnouncementId(@Param("annId") String annId);
}

