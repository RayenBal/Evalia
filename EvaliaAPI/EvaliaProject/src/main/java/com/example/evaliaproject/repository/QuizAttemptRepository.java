package com.example.evaliaproject.repository;

import com.example.evaliaproject.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, String> {
    // ⬇️ ramène tout ce qu’il faut en une requête, sans LazyProxy
//    @Query("""
//     select distinct qa
//     from QuizAttempt qa
//       join fetch qa.panelist p
//       join fetch qa.quiz q
//       join fetch qa.announcement a
//       left join fetch qa.answers ans
//       left join fetch ans.question qu
//       left join fetch ans.selectedResponse resp
//     where a.idAnnouncement = :announceId
//       and qa.status = com.example.evaliaproject.entity.AttemptStatus.SUBMITTED
//     order by qa.submittedAt desc
//  """)
//    List<QuizAttempt> findSubmittedWithAnswersByAnnouncement(@Param("announceId") String announceId);
//
//    @Query("""
// select distinct qa
// from QuizAttempt qa
//   join fetch qa.panelist p
//   join fetch qa.quiz q
//   join fetch qa.announcement a
//   left join fetch qa.answers ans
//   left join fetch ans.question qu
//   left join fetch ans.selectedResponse resp
// where a.idAnnouncement = :announceId
//   and q.idQuiz = :quizId
//   and qa.status = com.example.evaliaproject.entity.AttemptStatus.SUBMITTED
// order by qa.submittedAt desc
//""")
//    List<QuizAttempt> findSubmittedWithAnswersByAnnouncementAndQuiz(
//            @Param("announceId") String announceId,
//            @Param("quizId") String quizId
//    );



    @Query("""
     select distinct qa
     from QuizAttempt qa
       join fetch qa.panelist p
       join fetch qa.quiz q
       join fetch qa.announcement a
       left join fetch qa.answers ans
       left join fetch ans.question qu
       left join fetch ans.selectedResponse resp
     where a.idAnnouncement = :announceId
       and qa.status = com.example.evaliaproject.entity.AttemptStatus.SUBMITTED
     order by qa.submittedAt desc
  """)
    List<QuizAttempt> findSubmittedWithAnswersByAnnouncement(@Param("announceId") String announceId);

    // ⬇️ AJOUTER CECI
    @Query("""
     select distinct qa
     from QuizAttempt qa
       join fetch qa.panelist p
       join fetch qa.quiz q
       join fetch qa.announcement a
       left join fetch qa.answers ans
       left join fetch ans.question qu
       left join fetch ans.selectedResponse resp
     where a.idAnnouncement = :announceId
       and q.idQuiz = :quizId
       and qa.status = com.example.evaliaproject.entity.AttemptStatus.SUBMITTED
     order by qa.submittedAt desc
  """)
    List<QuizAttempt> findSubmittedWithAnswersByAnnouncementAndQuiz(
            @Param("announceId") String announceId,
            @Param("quizId") String quizId
    );



    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
     delete from QuizAttempt qa
     where qa.announcement.idAnnouncement = :annId
  """)
    int deleteByAnnouncementId(@Param("annId") String annId);
}
