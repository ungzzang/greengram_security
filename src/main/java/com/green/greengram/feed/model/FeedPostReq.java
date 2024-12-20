package com.green.greengram.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "피드 등록 요청")
public class FeedPostReq {
    @JsonIgnore
    private long feedId;

    //@Schema(title = "로그인 유저 pk", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonIgnore //토큰값만 보내면 되어서
    private long writerUserId;

    @Size(max = 1000, message = "내용은 1,000자 이하만 가능합니다.")
    @Schema(title = "피드 내용", example = "피드 내용 테스트")
    private String contents;

    @Size(max = 30, message = "위치는 30자 이하만 가능합니다.")
    @Schema(title = "피드 위치", example = "서울특별시")
    private String location;

}
