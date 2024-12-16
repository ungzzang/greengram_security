package com.green.greengram.config.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.config.security.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Service
public class TokenProvider {
    private final ObjectMapper objectMapper; //Jackson 라이브러리
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    //외부에서 주소값 집어넣는거 자체를 DI라고 함.
    //빈등록이 되어서 스프링컨테이너가 객체화함.(스프링이 데이터 넣어줄수있다.),  secretKey는 내가 직접 넣어줄꺼다.(빈등록안되어있어서)
    public TokenProvider(ObjectMapper objectMapper, JwtProperties jwtProperties, WebSocketServletAutoConfiguration webSocketServletAutoConfiguration) {
        this.objectMapper = objectMapper;
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));

    }

    // JWT 생성
    public String generateToken(JwtUser jwtUser, Duration expiredAt) {
        Date now = new Date(); //현재시간
        return makeToken(jwtUser, new Date(now.getTime() + expiredAt.toMillis()));//만료시간세팅
    }

    private String makeToken(JwtUser jwtUser, Date expiry){
        //JWT 암호화
        return Jwts.builder()
                .header().add("typ", "JWT")
                         .add("alg", "HS256")
                .and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(new Date())
                .expiration(expiry)
                .claim("signedUser", makeClaimByUserToString(jwtUser))
                .signWith(secretKey)
                .compact();
    }

    private String makeClaimByUserToString(JwtUser jwtUser){
        //객체 자체를 JWT에 담고 싶어서 객체를 직렬화
        //jwtUser에 담고있는 데이터를 JSON형태의 문자열로 변환 - 이것을 직렬화라 함.
        try {
            return objectMapper.writeValueAsString(jwtUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validToken(String token){
        try {
            //JWT 복호화
            getClaims(token);
            return  true;
        } catch (Exception e) {
            return false;
        }
    }

    //userDetails.getAuthorities() - 권한 뭐 들고 있는지
    public Authentication getAuthentication(String token){
        UserDetails userDetails = getUserDetailsFromToken(token);
        return userDetails == null
                ? null
                : new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    } // UsernamePasswordAuthenticationToken은 Authentication 얘 상속받음

    public UserDetails getUserDetailsFromToken(String token) {
        Claims claims = getClaims(token);
        String json = (String)claims.get("signedUser");
        JwtUser jwtUser = objectMapper.convertValue(json, JwtUser.class);
        MyUserDetails userDetails = new MyUserDetails();
        userDetails.setJwtUser(jwtUser);
        return userDetails;
    }

    private Claims getClaims(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}
