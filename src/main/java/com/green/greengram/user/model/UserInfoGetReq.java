package com.green.greengram.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.beans.ConstructorProperties;

@Getter
@Schema(title = "유저 정보 GET 요청")
@ToString
public class UserInfoGetReq {
    //@Schema(description = "로그인한 유저 PK", name = "signed_user_id", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonIgnore
    private long signedUserId;

    @Schema(description = "프로필 유저 PK", name = "profile_user_id"
            , example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long profileUserId;

    @ConstructorProperties({"signed_user_id", "profile_user_id"})
    public UserInfoGetReq(long profileUserId) {
        this.profileUserId = profileUserId;
    }

    public void setSignedUserId(long signedUserId) {
        this.signedUserId = signedUserId;
    }

}
