package com.green.greengram.feed;

import com.green.greengram.common.MyFileUtils;
import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.feed.comment.FeedCommentMapper;
import com.green.greengram.feed.comment.model.FeedCommentDto;
import com.green.greengram.feed.comment.model.FeedCommentGetReq;
import com.green.greengram.feed.comment.model.FeedCommentGetRes;
import com.green.greengram.feed.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;
    private final FeedPicMapper feedPicsMapper;
    private final FeedCommentMapper feedCommentMapper;
    private final MyFileUtils myFileUtils;
    private final FeedPicMapper feedPicMapper;
    private final AuthenticationFacade authenticationFacade;

    //자동커밋종료
    @Transactional //트랜잭션은 하나의 작업 단위로 간주되어, 모든 작업이 성공하면 커밋(Commit)되고, 하나라도 실패하면 롤백(Rollback)되어 이전 상태로 복구된다.
    public FeedPostRes postFeed(List<MultipartFile> pics, FeedPostReq p){
        p.setWriterUserId(authenticationFacade.getSignedUserId());
        int result = feedMapper.insFeed(p);

        //----------------파일 등록
        long feedId = p.getFeedId();

        //저장 폴더 만들기, 저장위치/feed/${feedId}/파일들을 저장한다.
        String middlePath = String.format("feed/%d",feedId);
        myFileUtils.makeFolders(middlePath);

        //랜덤 파일명 저장용 >> feed_pics 테이블에 저장할 때 사용
        List<String> picNameList = new ArrayList<>(pics.size()); //pics.size()없어도 결과는 같음

        for(MultipartFile pic : pics){
            //각 파일 랜덤파일명 만들기
            String savedFileName = myFileUtils.makeRandomFileName(pic);
            picNameList.add(savedFileName);
            String filePath = String.format("%s/%s", middlePath, savedFileName);

            try {
                myFileUtils.transferTo(pic, filePath);
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        FeedPicDto feedPicDto = new FeedPicDto();
        feedPicDto.setFeedId(feedId);
        feedPicDto.setPics(picNameList);//리스트에 담은 사진들을 통째로 넘긴다. ver1에서는 for문으로 사진하나하나 집어넣음.
        int resultPics = feedPicsMapper.insFeedPic(feedPicDto); //버전2에서는 for문 밖에서 한번만 쿼리문 호출함(이게 더 효율이 좋음), 그래서 xml에서 foreach사용했다
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>resultPics : {}", resultPics); // 사진갯수

        //@Setter 사용했을때
        /*FeedPostRes res = new FeedPostRes();
        res.setFeedId(feedId);
        res.setPics(picNameList);*/

        //@Builder 사용했을때
        return FeedPostRes.builder()
                .feedId(feedId)
                .pics(picNameList)
                .build();
    } // 이 하나의 업무를 하나의 트랜젝션이라고 한다.
    // 에러터짐 - Rollback
    // 에러안터짐 - Commit

    public List<FeedGetRes> getFeedList(FeedGetReq p) { //피드 20개 있으면 총 41번 셀렉트한다.
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        // N + 1 이슈 발생
        List<FeedGetRes> list = feedMapper.selFeedList(p); //여기서 한 번 셀렉트 //최대 20개 넘어온다.(디폴트 size를 20으로 설정해서)

        /*for(int i = 0; i < list.size(); i++){
         FeedGetRes item = list.get(i);
         ...} 와 같다.
         */
        for(FeedGetRes item : list) { //item에는 feed의 튜플들이 담긴다고 생각, feed_id 1번부터 쭉 반복한다.
            //피드 당 사진 리스트
            item.setPics(feedPicsMapper.selFeedPic(item.getFeedId())); //여기서 (튜플 4개라면) 4 번 셀렉트 //피드의 사진이 넘어옴

            //피드 당 댓글 4개(첫페이지) 가져오기
            FeedCommentGetReq commentGetReq = new FeedCommentGetReq(item.getFeedId(), 0, 3);// 맵퍼한테 보낼꺼다.(feedId, startIdx, size), 생성자에서 feed_id랑 feedId 맵핑해놨다.

            /* 얘네는 bindParam, 생성자 쓸 때 필요없음.
            commentGetReq.setPage(1); //피드당 댓글 3개 들고오는 첫번째 페이지(startIdx, size 다 세팅됨)
            commentGetReq.setFeedId(item.getFeedId());//피드당 피드아이디 넣기, feed테이블을 반복을 하니까 feedId 위에서 부터 담긴다. ex)feed_id 1번부터*/

            //commentList 사이즈는 최소 0, 최대 4 (limit로 잘라서), 첫페이지의 댓글이 담긴다.
            //FeedCommentDto에는 쿼리문 실행되면 나오는 결과를 담는다.(튜플이 여러개 나오니까 List 쓴거다)
            List<FeedCommentDto> commentList = feedCommentMapper.selFeedCommentList(commentGetReq); //0 ~ 4개, 위에서 객체선언하며 startIdx = 0, size = 3을 보내서

            FeedCommentGetRes commentGetRes = new FeedCommentGetRes();
            commentGetRes.setCommentList(commentList);//commentList에 담긴 댓글을 저 객체의 commentList에 담는다.
            commentGetRes.setMoreComment(commentList.size() == commentGetReq.getSize()); // 4개면 true, 4개 아니면 false, (4 = commentGetReq.getSize() 와 같다)

            if(commentGetRes.isMoreComment()) {
                commentList.remove(commentList.size() - 1); //index값(몇번방)으로 지운다.
            }
            item.setComment(commentGetRes);
        }
        return list;
    }

    //select 2번
    public List<FeedGetRes> getFeedList2(FeedGetReq p) {
        return null;
    }

    //select 3번, 피드 5,000개 있음, 페이지당 20개씩 가져온다.
    public List<FeedGetRes> getFeedList3(FeedGetReq p) {
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        //피드 리스트
        List<FeedGetRes> list = feedMapper.selFeedList(p); // 여기서 pic은 null이다.아직, select 한 번.
        if(list.size() == 0) { //list 없으면 바로 리턴(피드 없음)
            return list;
        }

        //feed_id를 골라내야 한다.
        List<Long> feedIds = new ArrayList<>(list.size());
        for(FeedGetRes item : list){ //feedId 정리하려고
            feedIds.add(item.getFeedId());
        }
        log.info("feedIds: {}", feedIds);

        //피드와 관련된 사진 리스트
        List<FeedPicSel> feedPicList = feedPicMapper.selFeedPicListByFeedIds(feedIds); //피드Id와 사진 (맵핑하려고), select 한 번.
        log.info("feedPicList: {}", feedPicList);

        // 순서 신경안쓰고 가능(해쉬맵)
        Map<Long, List<String>> picHashMap = new HashMap<>(); //키 타입과, value 타입 설정한 해쉬맵
        for(FeedPicSel item : feedPicList){
            long feedId = item.getFeedId();
            if(!picHashMap.containsKey(feedId)) { // !있어서 키값 없는게 true
                picHashMap.put(feedId, new ArrayList<>(3)); //키값과 Array공간 주소값(value) 저장, 사진이 보통 3장정도라서 공간을 3으로 해놓았다.(없어도 됨)
            }
            List<String> pics = picHashMap.get(feedId); // feedId 키값에 대응되는 ArrayList공간 주소값이 pics에 담는다.
            pics.add(item.getPic());//ArrayList 주소값을 가진 pics에 사진을 넣는다.(그러면 ArrayList에 사진들어간다.)
        }


        /*int lastIndex = 0;
        for(FeedGetRes res : list){
            List<String> pics = new ArrayList<>(2);
            for(int i = lastIndex; i < feedPicList.size(); i++) {
                FeedPicSel feedPicSel = feedPicList.get(i);
                if(res.getFeedId() == feedPicSel.getFeedId()){
                    pics.add(feedPicSel.getPic());
                } else {
                    res.setPics(pics);
                    lastIndex = i;
                    break;
                }
            }
        } 이거 어려워서*/


        //피드와 관련된 댓글 리스트
        List<FeedCommentDto> feedCommentList = feedCommentMapper.selFeedCommentListByFeedIdsLimit4(feedIds);
        Map<Long, FeedCommentGetRes> commentHashMap = new HashMap<>(); //해쉬맵 하나 만들었다.
        for(FeedCommentDto item : feedCommentList) {
            long feedId = item.getFeedId();
            if(!commentHashMap.containsKey(feedId)) {
                FeedCommentGetRes feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>(4));
                commentHashMap.put(feedId, feedCommentGetRes); //key값, value값 넣어줌
            }
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(feedId); //value(객체주소값) 넣어줌
            feedCommentGetRes.getCommentList().add(item); // 여기까지가 new ArrayList<>() 객체주소값 넘어오는 과정
        }

        for(FeedGetRes res : list) { //여기서부터 댓글정리 (위에서 정리한거 집어넣으려고)
            res.setPics(picHashMap.get(res.getFeedId())); //feedId 맞게 사진(ArrayList<String> 객체의 주소값) 넣어준다.
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(res.getFeedId());

            if(feedCommentGetRes == null) { //댓글이 null인지 체크
                feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>());
                //res.setComment(feedCommentGetRes); 밑에 중복이라서 빼도됨
            } else if (feedCommentGetRes.getCommentList().size() == 4) {
                feedCommentGetRes.setMoreComment(true);
                feedCommentGetRes.getCommentList().remove(feedCommentGetRes.getCommentList().size() - 1); //마지막 하나 지우는과정
            }
            res.setComment(feedCommentGetRes);//더보기와 commentList
        }
        log.info("list: {}", list);
        return list;
    }


    @Transactional
    public int deleteFeed(FeedDeleteReq p) {
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        //피드 댓글, 좋아요 삭제
        int affectedRowsEtc = feedMapper.delFeedLikeAndFeedCommentAndFeedPic(p);
        log.info("deleteFeed > affectedRows: {}", affectedRowsEtc);


        //피드 삭제
        int affectedRowsFeed = feedMapper.delFeed(p);
        log.info("deleteFeed > affectedRowsFeed: {}", affectedRowsFeed);

        //피드 사진 삭제 (폴더 삭제)
        String deletePath = String.format("%s/feed/%d", myFileUtils.getUploadPath(), p.getFeedId());
        myFileUtils.deleteFolder(deletePath, true);

        return 1;
    }

}
//내가 뽑은 튜플이 0개, 1개면 그냥 클래스, 2개 이상이면 List로 만든다.
//내가 뽑은 튜플과 객체의 멤버필드명이 같아야 값을 넣어준다.