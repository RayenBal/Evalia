package com.example.evaliaproject.repository;

import com.example.evaliaproject.entity.Announce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announce,String> {
    // AnnouncementRepository.java
    @Query("select a from Announce a where a.user.id_user = :uid")
    List<Announce> findAllByUserId(@Param("uid") Long uid);

    @Query("""
    select distinct a
    from Announce a
    left join fetch a.quizList q
    where a.idAnnouncement = :id
""")
//    @Query("""
//select distinct a
//from Announce a
//left join fetch a.quizList q
//left join fetch q.questions qu
//left join fetch qu.responses r
//where a.idAnnouncement = :id
//""")left join fetch a.recompensesList r
    Optional<Announce> fetchDetails(@Param("id") String id);
    List<Announce> findByCategory_Idcategory(Long idcategory);

 }
