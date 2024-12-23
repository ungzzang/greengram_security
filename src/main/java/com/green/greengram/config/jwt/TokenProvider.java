package com.green.greengram.config.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.common.exception.CustomException;
import com.green.greengram.common.exception.UserErrorCode;
import com.green.greengram.config.security.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;


@Service
public class TokenProvider {//JWT담당
    private final ObjectMapper objectMapper; //Jackson 라이브러리
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public TokenProvider(ObjectMapper objectMapper, JwtProperties jwtProperties) {
        this.objectMapper = objectMapper; //여기서 디버깅해봤다.
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));
        //secretKey 약 42자 이상적어야 에러 안터짐.(한글자당 2byte, 총 80byte 이상되야함)
    }

    // JWT 생성
    public String generateToken(JwtUser jwtUser, Duration expiredAt) {
        Date now = new Date(); //현재시간
        return makeToken(jwtUser, new Date(now.getTime() + expiredAt.toMillis()));//만료시간세팅
    } //new Date() - 파라미터 없으면 현재시간, 있으면 그 값대로.

    private String makeToken(JwtUser jwtUser, Date expiry){ //expiry - 만료일시

        JwtBuilder builder = Jwts.builder();
        JwtBuilder.BuilderHeader header = builder.header();
        header.type("JWT");

        builder.issuer(jwtProperties.getIssuer());

        //JWT 암호화
        return Jwts.builder()
                .header().type("JWT")
                .and()
                .issuer(jwtProperties.getIssuer())//green@green.kr
                .issuedAt(new Date())//언제발행되었는지
                .expiration(expiry)//만료시간
                .claim("signedUser", makeClaimByUserToString(jwtUser))//내가넣은 클래임(비공개)
                .signWith(secretKey)
                .compact(); //결과(리턴타입 String), JWT에서 씀(build() 안쓰고)
    }

    private String makeClaimByUserToString(JwtUser jwtUser){//객체를 String으로 바꾸는 과정(알면좋다)
        //객체 자체를 JWT에 담고 싶어서 객체를 직렬화
        //jwtUser에 담고있는 데이터를 JSON형태의 문자열로 변환 - 이것을 직렬화라 함.
        try {
            return objectMapper.writeValueAsString(jwtUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /*public boolean validToken(String token){
        try {
            //JWT 복호화
            getClaims(token);
            return  true;
        } catch (Exception e) {
           throw new CustomException(UserErrorCode.EXPIRED_TOKEN);
        }
    }*/

    //Spring Security에서 인증 처리를 해주어야 한다. 그때 Authentication 객체가 필요.
    //userDetails.getAuthorities() - 권한 뭐 들고 있는지
    public Authentication getAuthentication(String token){
        UserDetails userDetails = getUserDetailsFromToken(token); //디버깅했음
        return userDetails == null
                ? null
                : new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    } // UsernamePasswordAuthenticationToken은 Authentication 얘 상속받음

    public JwtUser getJwtUserFromToken(String token) {
        Claims claims = getClaims(token);
        String json = (String)claims.get("signedUser");
        JwtUser jwtUser = null;
        try {
            jwtUser = objectMapper.readValue(json, JwtUser.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jwtUser;
    }

    public UserDetails getUserDetailsFromToken(String token) {
        JwtUser jwtUser = getJwtUserFromToken(token);
        MyUserDetails userDetails = new MyUserDetails();
        userDetails.setJwtUser(jwtUser);
        return userDetails;
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
