package com.green.greengram.feed.model;

import com.green.greengram.feed.comment.model.FeedCommentDto;
import com.green.greengram.feed.comment.model.FeedCommentGetRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Schema(title = "피드 정보")
@NoArgsConstructor //기본생성자 만들어줌
public class FeedGetRes {
        @Schema(title = "피드 PK")
        private long feedId;
        @Schema(title = "피드 내용")
        private String contents;
        @Schema(title = "피드 위치")
        private String location;
        @Schema(title = "피드 생성일시")
        private String createdAt;
        @Schema(title = "작성자 유저 PK")
        private long writerUserId;
        @Schema(title = "작성자 유저 이름")
        private String writerNm;
        @Schema(title = "작성자 유저 프로필 사진파일명")
        private String writerPic;
        @Schema(title = "좋아요", description = "1: 좋아요, 0: 좋아요 아님")
        private int isLike;

        @Schema(title = "피드 사진 리스트")
        private List<String> pics;
        @Schema(title = "피드 댓글")
        private FeedCommentGetRes comment; //댓글정보(주솟값)

        public FeedGetRes(FeedWithPicCommentDto dto) {
                this.feedId = dto.getFeedId();
                this.contents = dto.getContents();
                this.location = dto.getLocation();
                this.createdAt = dto.getCreatedAt();
                this.writerUserId = dto.getWriterUserId();
                this.writerNm = dto.getWriterNm();
                this.writerPic = dto.getWriterPic();
                this.isLike = dto.getIsLike();
                this.pics = dto.getPics();
                this.comment = new FeedCommentGetRes();
                //TODO: 댓글 moreComment, list 컨버트


                //dto.getCommentList().size()값이 4라면
                List<FeedCommentDto> list = dto.getCommentDtoList();
                if(list == null){
                        comment.setCommentList(new ArrayList<>());
                } else if(list.size() >= 4){
                        comment.setMoreComment(true);
                        list.remove(list.size()-1);
                }
                comment.setCommentList(list);
        }

}
