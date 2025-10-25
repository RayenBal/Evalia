package com.example.evaliaproject.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.autoconfigure.web.embedded.JettyVirtualThreadsWebServerFactoryCustomizer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
//    private static final String SECRET_KEY = "372E5BFA545129FBE82D69638D3AD";
private static final String SECRET_B64 = System.getenv().getOrDefault(
        "JWT_SECRET_B64",
        "ZkQ2bWlYQ0p3ZkxqS1BWeWk3bG9YVUNuRk9mY0Z3b2dMR0hVb2xLcW1DV2x2S1Jk" // exemple
);
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject );
    }

//    public String generateToken (UserDetails userDetails){
//
//        return generateToken(new HashMap<>(), userDetails);
//    }

    public String generateToken (UserDetails userDetails){
        Map<String, Object> extra = new HashMap<>();
        if (userDetails instanceof com.example.evaliaproject.entity.User u) {
            extra.put("id_user", u.getId_user());
            extra.put("typeUser", u.getTypeUser().name());
            extra.put("email", u.getEmail());
        }
        return generateToken(extra, userDetails);
    }




    public String generateToken(
            Map<String, Object> extraClaims, UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())//email
                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis()* 1000 * 60 * 24))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)) // 24h

                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();//generate and return the token

    }

//    public boolean isTokenValid(String token, UserDetails userDetails){
//        final String userename= extractUsername(token);
//        return (userename.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
       }

       private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token)
    {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())//to generate,create,decode we need a signiNkEY(create the signature part of the jwt which user to verify the senderof the JWT is who it claims to be and the message wasn't change on the way )
                .build()
                .parseClaimsJws(token)//to parse our token
                .getBody();
    }

    private Key getSignInKey() {
            byte[] keybytes = Decoders.BASE64.decode(SECRET_B64);
        return Keys.hmacShaKeyFor(keybytes);
    }
    public boolean isTokenValid(String token, UserDetails userDetails){
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException ex) {
            // token expiré / mal formé -> considéré invalide, surtout ne pas throw
            return false;
        }

}}
