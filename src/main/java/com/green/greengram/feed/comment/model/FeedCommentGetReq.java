package com.green.greengram.feed.comment.model;

import com.green.greengram.common.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.ToString;

import java.beans.ConstructorProperties;

@Getter
@Schema(title = "피드 댓글 리스트 요청")
@ToString
public class FeedCommentGetReq {
    private final static int FIRST_COMMENT_SIZE = 3;

    @Positive
    @Schema(title = "피드 pk", description = "피드 PK" , name = "feed_id"
            , example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;

    @PositiveOrZero
    @Schema(title= "튜플 시작 index", description = "댓글 Element 갯수를 보내주면 된다.", name="start_idx"
            , example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private int startIdx;

    @Min(value = 21, message = "사이즈는 20이상이어야 합니다.")
    @Schema(title= "페이지 당 아이템 수", description = "default: 20", example = "20")
    private int size;

    @ConstructorProperties({"feed_id", "start_idx", "size"})
    public FeedCommentGetReq(long feedId, int startIdx, Integer size) {
        this.feedId = feedId;
        this.startIdx = startIdx;
        this.size = (size == null ? Constants.getDefault_page_size() : size) + 1;
    }
}


//밑에는 버그 수정하기 전 코드들
/* //@BindParam 이용해서 프론트의 feed_id를 feedId로 맵핑시키려고 생성자 만들어서 사용한거(생성자에서 @BindParam 사용가능해서)
    public FeedCommentGetReq(@BindParam("feed_id") long feedId, int page){
        this.feedId = feedId;
        setPage(page);
    }*/

   /* public void setPage(int page) {
        this.page = page;
        if(page < 1) { return; }//튕겨서 작업안한다는뜻
        if(page == 1) {//첫페이지
            startIdx = 0;
            size = FIRST_COMMENT_SIZE + 1; // +1은 isMore처리용
            return;
        }
        startIdx = ((page - 2) * DEFAULT_PAGE_SIZE) + FIRST_COMMENT_SIZE ;//다음페이지부터
        size = DEFAULT_PAGE_SIZE + 1; // +1은 isMore처리용
    }
    //다음댓글이 있는지 확인하려고 튜플 1개씩 더 보는거다.
    //+1해서 확인한거는 보지않고 다음 페이지 처음에 나타나는거다.(말그대로 확인용)*/
