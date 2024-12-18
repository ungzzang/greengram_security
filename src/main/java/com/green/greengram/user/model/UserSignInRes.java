package com.green.greengram.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignInRes {
    private long userId;
    private String nickName;
    private String pic;

    private String accessToken;

    @JsonIgnore //swagger 표시 안 되지만, 응답 때 빼는 역할도 한다.
    private String upw;
    @JsonIgnore
    private String message;

}
