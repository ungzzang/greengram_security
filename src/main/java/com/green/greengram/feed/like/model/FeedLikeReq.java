package com.green.greengram.feed.like.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "피드 좋아요 Toggle") //토글 방식(insert와 delete가 번갈아가면서)
public class FeedLikeReq {
    @Schema(title = "피드 pk", example = "1"
            , requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;

    //@Schema(title = "유저 pk", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonIgnore
    private long userId;

    public void setSignedUserId(long signedUserId) {
        this.userId = signedUserId;
    }
}
