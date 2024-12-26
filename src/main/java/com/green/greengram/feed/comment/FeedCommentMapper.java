package com.green.greengram.feed.comment;

import com.green.greengram.feed.comment.model.FeedCommentDelReq;
import com.green.greengram.feed.comment.model.FeedCommentDto;
import com.green.greengram.feed.comment.model.FeedCommentGetReq;
import com.green.greengram.feed.comment.model.FeedCommentPostReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedCommentMapper {
    //insert, delete, update는 무조건 int임(영향받은 행값이라서)
    //insert를 void로 해도 되긴한다.
    int insFeedComment(FeedCommentPostReq p);

    List<FeedCommentDto> selFeedCommentList(FeedCommentGetReq p);

    int delFeedComment(FeedCommentDelReq p);

    List<FeedCommentDto> selFeedCommentListByFeedIdsLimit4(List<Long> feedIds);
    List<FeedCommentDto> selFeedCommentListByFeedIdsLimit4Ver2(List<Long> feedIds);
}
