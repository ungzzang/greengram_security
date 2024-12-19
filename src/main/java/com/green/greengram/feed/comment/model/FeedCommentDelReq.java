package com.green.greengram.feed.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

import java.beans.ConstructorProperties;

@ToString
@Getter
public class FeedCommentDelReq {
    @Schema(name = "feed_comment_id") //프론트한테 이 이름으로 날린다는거 알리기
    private long feedCommentId;

    //@Schema(name = "signed_user_id")
    @JsonIgnore
    private long userId;


    //@ConstructorProperties - 다 수정할때
    @ConstructorProperties({"feed_comment_id"}) //파라미터 이름 맞춰주려고 생성자로 만들었다.
    public FeedCommentDelReq(long feedCommentId){
        this.feedCommentId = feedCommentId;
    }

    public void setSignedUserId(long signedUserId) {
        this.userId = signedUserId;
    }
}
