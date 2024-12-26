package com.green.greengram.feed;

import com.green.greengram.feed.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeedMapper {
    int insFeed(FeedPostReq p);
    List<FeedGetRes> selFeedList(FeedGetReq p);
    List<FeedAndPicDto> selFeedWithPicList(FeedGetReq p); // SELECT 두번용
    List<FeedWithPicCommentDto> selFeedWithPicAndCommentLimit4List(FeedGetReq p);


    int delFeedLikeAndFeedCommentAndFeedPic(FeedDeleteReq p);
    int delFeed(FeedDeleteReq p);
}
