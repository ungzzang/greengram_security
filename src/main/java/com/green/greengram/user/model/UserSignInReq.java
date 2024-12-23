package com.green.greengram.user.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignInReq {
    @Size(min = 3, max = 30, message = "아이디는 3~30자 사이만 가능합니다.")
    @NotNull(message = "아이디를 입력하셔야 합니다.")
    private String uid;

    @Size(min = 3, max = 50, message = "비밀번호는 4~50자 사이만 가능합니다.")
    @NotNull(message = "비밀번호를 입력하셔야 합니다.")
    private String upw;
}
