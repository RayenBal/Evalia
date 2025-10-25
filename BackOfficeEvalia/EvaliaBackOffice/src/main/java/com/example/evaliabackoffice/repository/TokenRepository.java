package com.example.evaliabackoffice.repository;


import com.example.evaliabackoffice.entity.Token;
import com.example.evaliabackoffice.entity.TokenType;
import com.example.evaliabackoffice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {

     Optional<Token> findByToken(String token) ;
     Optional<Token> findByTokenAndType(String token, TokenType type);
     List<Token> findAllByUser(User user);
     @Modifying(clearAutomatically = true, flushAutomatically = true)
     @Query("""
       UPDATE Token t
          SET t.revoked = TRUE,
              t.expiresAt = CURRENT_TIMESTAMP
        WHERE t.user.id_user = :userId
       """)
     int revokeAllByUserId(@Param("userId") Long userId);

}
