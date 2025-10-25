package com.example.evaliaproject.repository;

import com.example.evaliaproject.entity.Campagne;
import com.example.evaliaproject.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz,String> {
    List<Quiz> findByAnnouncementIdAnnouncement(String idAnnouncement);

    @Query("""
  select distinct q
  from Quiz q
  left join fetch q.questions qu
  left join fetch qu.responses r
  where q.idQuiz = :id
""")
    Optional<Quiz> fetchDetails(@Param("id") String id);

    @Query("""
select q
from Quiz q
left join fetch q.questions qu
where q.idQuiz = :id
""")
    Optional<Quiz> fetchWithQuestions(@Param("id") String id);
}
