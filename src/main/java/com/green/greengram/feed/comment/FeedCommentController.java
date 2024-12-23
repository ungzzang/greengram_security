package com.green.greengram.feed.comment;

import com.green.greengram.common.model.ResultResponse;
import com.green.greengram.feed.comment.model.FeedCommentDelReq;
import com.green.greengram.feed.comment.model.FeedCommentGetReq;
import com.green.greengram.feed.comment.model.FeedCommentGetRes;
import com.green.greengram.feed.comment.model.FeedCommentPostReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("feed/comment")
@Tag(name = "4. 피드 댓글", description = "피드 댓글 관리")
public class FeedCommentController {
    private final FeedCommentService service;

    @PostMapping
    @Operation(summary = "피드 댓글 등록", description = "")
    public ResultResponse<Long> FeedComment(@RequestBody FeedCommentPostReq p){
        log.info("FeedCommentController > postFeedComment > p: {}", p);
        long result = service.postFeedComment(p);
        log.info("+++++++++++++++++++++++++++ {}", result);
        return ResultResponse.<Long>builder()
                .resultMessage("댓글 등록 완료")
                .resultData(result)
                .build();
    }// 프론트에서 원하는 데이터 주면됨. 여기서는 pk를 모르기때문에 long타입인 pk를 주는거다.


    // 쿼리스트링(프론트에서 백엔드로 주는 방식)
    // @ModelAttribute는 프론트가 값을 알아서 넣어라라고 하는데 지금 feedId가 프론트에서는 feed_id로 되어 있어서 안맞아서 생성자에서 @BindParam사용했음.
    // @RequestParam은 컨트롤러에서 쓰는거, 변수 하나하나 맞춰줄수 있음. 근데 변수 많아지면 불리해져서 객체로 보내는게 더 낫다.


    @GetMapping
    @Operation(summary = "피드 댓글 리스트", description = "댓글 더보기 처리 - 파라미터를 ModelAttribute를 이용해서 받음")
    public ResultResponse<FeedCommentGetRes> getFeedCommentList(@Valid @ParameterObject @ModelAttribute FeedCommentGetReq p){ //쌤픽
        log.info("FeedCommentController > getFeedComment > p: {}", p);
        FeedCommentGetRes res = service.getFeedComment(p);
        return ResultResponse.<FeedCommentGetRes>builder()
                .resultMessage(String.format("%d rows", res.getCommentList().size()))
                .resultData(res)
                .build();
    }//@Valid - 이거 사용했다.

    /*
        생성자와 @BindParam 안쓸꺼면 밑에 처럼 쓰면된다. (프론트랑 맞춰주려고 이런 과정이 있는데 프론트랑 카멜케이스 쓰는거에 대해 잘 의논해서 해보자)
        (@RequestParam("feed_id") long feedId, @RequestParam int page)
        FeedCommentGetReq p = new FeedCommentGetReq(); 객체화도 해줘야함
    */

    @GetMapping("/request_param")
    @Operation(summary = "피드 댓글 리스트", description = "댓글 더보기 처리 - 파라미터를 RequestParam을 이용해서 받음")
    public ResultResponse<FeedCommentGetRes> getFeedComment2(@Parameter(description = "피드 PK", example = "12") @RequestParam("feed_id") long feedId
                                                            , @Parameter(description = "튜플 시작 index", example = "3") @RequestParam("start_idx") int startIdx
                                                            , @Parameter(description = "페이지 당 아이템 수", example = "20") @RequestParam(required = false, defaultValue = "20") int size) {
        FeedCommentGetReq p = new FeedCommentGetReq(feedId, startIdx, size);
        log.info("FeedCommentController > getFeedComment > p: {}", p);
        FeedCommentGetRes res = service.getFeedComment(p);
        return ResultResponse.<FeedCommentGetRes>builder()
                .resultMessage(String.format("%d rows", res.getCommentList().size()))
                .resultData(res)
                .build();
    }

    //삭제시 받아야 할 데이터 feedCommentId + 로그인한 사용자의 PK (feed_comment_id, signed_user_id)
    //FE - data 전달방식 : query-String
    @DeleteMapping
    @Operation(summary = "피드 댓글 삭제")
    public ResultResponse<Integer> delFeedComment(@ParameterObject @ModelAttribute  FeedCommentDelReq p){
        log.info("FeedCommentController > delFeedComment > P: {}", p);
        int result = service.delFeedComment(p);
        return ResultResponse.<Integer>builder()
                .resultMessage("댓글 삭제 완료")
                .resultData(result)
                .build();
    }
}
