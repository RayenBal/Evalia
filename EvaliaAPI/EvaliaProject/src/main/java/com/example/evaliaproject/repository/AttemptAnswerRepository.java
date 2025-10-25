package com.example.evaliaproject.repository;
import com.example.evaliaproject.entity.AttemptAnswer;
import com.example.evaliaproject.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, String> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
     delete from AttemptAnswer aa
     where aa.question.quiz.announcement.idAnnouncement = :annId
  """)
    int deleteByAnnouncementId(@Param("annId") String annId);
}
