package com.green.greengram.feed.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "피드 댓글 등록 요청")
public class FeedCommentPostReq { //누가 어느 피드에 어떤 코멘트를 담았는지
    @JsonIgnore
    private long feedCommentId; //pk값 갖기위한 용도

    @Schema(title = "피드 pk", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;

    //@Schema(title = "로그인한 유저 pk", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonIgnore
    private long userId;

    @Schema(title = "댓글내용", example = "댓글입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String comment;

    public void setSignedUserId(long signedUserId) {
        this.userId = signedUserId;
    }
}
//feedId랑 userId를 복합키로 묶지 않았다.(한 피드에 하나의 id가 한 댓글만 달게 되니까)
//fk를 걸었는데 없는 피드에 없는 id가 댓글 달게 안하려고
