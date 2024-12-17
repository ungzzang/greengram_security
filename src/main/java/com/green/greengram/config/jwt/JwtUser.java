package com.green.greengram.config.jwt;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class JwtUser {
    //로그인한 유저 아이디
    private long signedUserId;

    //인가(권한)처리 때 사용, (ROLE_이름, ROLE_USER, ROLE_ADMIN), 권한 하나이상 하려면 List
    private List<String> roles;
}
