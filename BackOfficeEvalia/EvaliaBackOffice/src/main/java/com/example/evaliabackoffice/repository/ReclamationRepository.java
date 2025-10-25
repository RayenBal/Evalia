package com.example.evaliabackoffice.repository;

import com.example.evaliabackoffice.dto.DailyCount;
import com.example.evaliabackoffice.dto.MotifCount;
import com.example.evaliabackoffice.dto.UserTypeCount;
import com.example.evaliabackoffice.entity.Reclamation;
import com.example.evaliabackoffice.entity.TypeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation,String> {
//    @Query("select r from Reclamation r where r.user.id_user = :uid")
//    List<Reclamation> findAllByUserId(@Param("uid") Long uid);


    List<Reclamation> findAllByUserType(TypeUser type);

    // ---- Stats ----
//    @Query("""
//    select r.motif as motif, count(r) as total
//    from Reclamation r
//    group by r.motif
//    order by count(r) desc
//  """)
//    List<MotifCount> countByMotif();
    @Query("SELECT r.motif AS motif, COUNT(r) AS total FROM Reclamation r GROUP BY r.motif")
    List<MotifCount> countByMotif();
    @Query("""
    select r.userType as userType, count(r) as total
    from Reclamation r
    group by r.userType
    order by count(r) desc
  """)
    List<UserTypeCount> countByUserType();

    @Query("""
    select r.createdAt as day, count(r) as total
    from Reclamation r
    where r.createdAt between :from and :to
    group by r.createdAt
    order by r.createdAt asc
  """)
    List<DailyCount> dailyCounts(LocalDate from, LocalDate to);

    // variantes filtrées
    @Query("""
    select r.motif as motif, count(r) as total
    from Reclamation r
    where (:type is null or r.userType = :type)
    group by r.motif
    order by count(r) desc
  """)
    List<MotifCount> countByMotifFiltered(TypeUser type);

    @Query("""
    select r.createdAt as day, count(r) as total
    from Reclamation r
    where r.createdAt between :from and :to
      and (:type is null or r.userType = :type)
    group by r.createdAt
    order by r.createdAt asc
  """)
    List<DailyCount> dailyCountsFiltered(LocalDate from, LocalDate to, TypeUser type);}