package com.green.greengram.feed.comment;

import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.feed.comment.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCommentService {
    private final FeedCommentMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public long postFeedComment(FeedCommentPostReq p){
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        int result = mapper.insFeedComment(p);
        //mapper.insFeedComment(p); 도 가능
        return p.getFeedCommentId();
    }

    public FeedCommentGetRes getFeedComment(FeedCommentGetReq p){
        FeedCommentGetRes res = new FeedCommentGetRes();//객체안의 멤버필드에 값 담으려고 객체선언함.
        if(p.getStartIdx() < 0) { //startIdx 값이 0보다 작으면 에러가 발생해서 if로 조건걸었다.
            res.setCommentList(new ArrayList<>());
            return res;
        }
        List<FeedCommentDto> commentDtoList = mapper.selFeedCommentList(p); //1~21사이 //쿼리문으로 튜플 뽑아서 담기

        res.setCommentList(commentDtoList); // 담아진 튜플들을 res객체의 멤버필드로 담기

        //p에서 사이즈값을 21개 가져온다고 정했기에 p.getSize()=21이라서 commentDtoList의 사이즈가 같다면 더보기 생긴거.
        res.setMoreComment(commentDtoList.size() == p.getSize()); //삼항식으로 할때는 메소드 호출이 안되기에 이렇게 바로하면됨

        if(res.isMoreComment()){
            commentDtoList.remove(commentDtoList.size() - 1);
        }
        return res;
    }

    public int delFeedComment(FeedCommentDelReq p){
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        int result = mapper.delFeedComment(p);
        return result;
    }

}
