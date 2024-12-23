package com.green.greengram.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode{
    INCORRECT_ID_PW(HttpStatus.BAD_REQUEST, "아이디, 비밀번호를 확인해 주세요.")
    , USER_NOT_FOUNT(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.")
    , UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "로그인을 해주세요.")
    , EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.")
    , INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
