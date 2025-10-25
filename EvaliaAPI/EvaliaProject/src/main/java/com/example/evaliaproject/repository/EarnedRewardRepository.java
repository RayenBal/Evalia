// src/main/java/com/example/evaliaproject/repository/EarnedRewardRepository.java
package com.example.evaliaproject.repository;

import com.example.evaliaproject.dto.PanelistRewardItemDto;
import com.example.evaliaproject.entity.EarnedReward;
import com.example.evaliaproject.entity.typeRecompenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EarnedRewardRepository extends JpaRepository<EarnedReward, String> {

    @Query("""
    select er from EarnedReward er
    where er.panelist.id_user = :panelistId
    order by er.createdAt desc
  """)
    List<EarnedReward> listForPanelist(@Param("panelistId") Long panelistId);

    @Query("""
    select er.rewardType, sum(er.amount)
    from EarnedReward er
    where er.panelist.id_user = :panelistId
    group by er.rewardType
  """)
    List<Object[]> totalsByType(@Param("panelistId") Long panelistId);

    @Query("""
    select case when count(er) > 0 then true else false end
    from EarnedReward er
    where er.announcement.idAnnouncement = :annId
      and er.panelist.id_user = :panelistId
      and er.rewardType = :type
  """)
    boolean existsForPanelistInAnnouncement(
            @Param("annId") String annId,
            @Param("panelistId") Long panelistId,
            @Param("type") typeRecompenses type
    );


    @Query("""
select new com.example.evaliaproject.dto.OwnerRewardItemDto(
  er.id,
  a.idAnnouncement, a.announceName,
  p.id_user, p.firstname, p.lastname, p.email,p.iban,
  er.rewardType, er.amount, er.status, er.createdAt
)
from EarnedReward er
 join er.announcement a
 join er.panelist p
where a.user.id_user = :ownerId
order by er.createdAt desc
""")
    List<com.example.evaliaproject.dto.OwnerRewardItemDto> ownerRewards(@Param("ownerId") Long ownerId);

    @Query("""
select new com.example.evaliaproject.dto.OwnerRewardItemDto(
  er.id,
  a.idAnnouncement, a.announceName,
  p.id_user, p.firstname, p.lastname, p.email,p.iban,
  er.rewardType, er.amount, er.status, er.createdAt
)
from EarnedReward er
 join er.announcement a
 join er.panelist p
where a.user.id_user = :ownerId
  and a.idAnnouncement = :annId
order by er.createdAt desc
""")
    List<com.example.evaliaproject.dto.OwnerRewardItemDto> ownerRewardsForAnnounce(
            @Param("ownerId") Long ownerId, @Param("annId") String announcementId);

    @Query("""
select er from EarnedReward er
 join fetch er.announcement a
 join fetch a.user u
where er.id = :id
""")
    Optional<EarnedReward> findByIdWithOwner(@Param("id") String id);




    @Query("""
select new com.example.evaliaproject.dto.PanelistRewardItemDto(
  er.announcement.idAnnouncement,
  er.announcement.announceName,
  er.rewardType,
  er.amount,
  er.status,
  er.createdAt
)
from EarnedReward er
where er.panelist.id_user = :panelistId
order by er.createdAt desc
""")
    List<PanelistRewardItemDto> rewardsDtoForPanelist(@Param("panelistId") Long panelistId);



    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
     delete from EarnedReward er
     where er.announcement.idAnnouncement = :annId
  """)
    int deleteByAnnouncementId(@Param("annId") String annId);
}
