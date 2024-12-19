package com.green.greengram.user;

import com.green.greengram.common.CookieUtils;
import com.green.greengram.common.MyFileUtils;
import com.green.greengram.config.jwt.JwtUser;
import com.green.greengram.config.jwt.TokenProvider;
import com.green.greengram.user.follow.model.UserPicPatchReq;
import com.green.greengram.user.model.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final MyFileUtils myFileUtils;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CookieUtils cookieUtils;

    public int signUp(MultipartFile pic, UserSignUpReq p){
        String savedPicName = (pic != null ? myFileUtils.makeRandomFileName(pic) : null);

        //String hashedPassWord = BCrypt.hashpw(p.getUpw(), BCrypt.gensalt());
        String hashedPassWord = passwordEncoder.encode(p.getUpw());

        p.setUpw(hashedPassWord);
        p.setPic(savedPicName);

        int result = mapper.insUser(p);

        if(pic == null){
            return result;
        }

        long userId = p.getUserId();
        String middlePath = String.format("user/%d", userId);
        myFileUtils.makeFolders(middlePath);
        String filePath = String.format("%s/%s", middlePath, savedPicName);

        try{
            myFileUtils.transferTo(pic, filePath); //file이 임시파일에 있는데 우리가 관리하는 폴더로 옮기는거
        }catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }

    public UserSignInRes signIn(UserSignInReq p, HttpServletResponse response){
        UserSignInRes res = mapper.selUserByUid(p.getUid());

        if(res == null) { //아이디없음
            res = new UserSignInRes();
            res.setMessage("아이디를 확인하시오");
            return res;
        }else if(!passwordEncoder.matches(p.getUpw(), res.getUpw())){ //비밀번호 다를시
        //}else if(!BCrypt.checkpw(p.getUpw(), res.getUpw())){ //비밀번호 다를시
            res = new UserSignInRes();
            res.setMessage("비밀번호를 확인하시오");
            return res;
        }

        /*
            JWT 토큰 생성 2개 (AccessToken 20분, RefreshToken 15일)
         */

        JwtUser jwtUser = new JwtUser();
        jwtUser.setSignedUserId(res.getUserId());
        jwtUser.setRoles(new ArrayList<>(2));

        jwtUser.getRoles().add("ROLE_USER");
        jwtUser.getRoles().add("ROLE_ADMIN");

        String accessToken = tokenProvider.generateToken(jwtUser, Duration.ofMinutes(20)); //액세스토큰
        String refreshToken = tokenProvider.generateToken(jwtUser, Duration.ofDays(15)); //리프레쉬토큰

        //refreshToken은 쿠키에 담는다.
        int maxAge = 1_296_000; //15 * 24 * 60 * 60, 15일의 초(second)값
        cookieUtils.setCookie(response, "refreshToken", refreshToken, maxAge);

        res.setMessage("로그인 성공");
        res.setAccessToken(accessToken);
        return res;
    }

    public UserInfoGetRes getUserInfo(UserInfoGetReq p){
        return mapper.selUserInfo2(p);
    }

    public String getAccessToken(HttpServletRequest req){
        Cookie cookie = cookieUtils.getCookie(req, "refreshToken");
        String refreshToken = cookie.getValue();
        log.info("refreshToken: {}", refreshToken);
        return refreshToken;
    }

    public String patchUserPic(UserPicPatchReq p){
        // 1. 저장할 파일명(랜덤명 파일명) 생성한다. 이때, 확장자는 오리지날 파일명과 일치하게 한다.
        String savedPicName = (p.getPic() != null ? myFileUtils.makeRandomFileName(p.getPic()) : null);

        // 2. 폴더 만들기 (최초에 프로필 사진이 없었다면 폴더가 없기 때문)
        String folderPath = String.format("user/%d", p.getSignedUserId());
        myFileUtils.makeFolders(folderPath);

        // 3. 기존 파일 삭제(이거 안하면 안쓰는 파일이 쌓여서)
        // (방법 3가지 [1] 폴더를 지운다. [2] select해서 기존 파일명을 얻어온다. [3]기존 파일명을 FE에서 받는다.)
        String deletePath = String.format("%s/user/%d", myFileUtils.getUploadPath(), p.getSignedUserId());
        myFileUtils.deleteFolder(deletePath, false);

        // 4. DB에 튜플을 수정(Update)한다.
        p.setPicName(savedPicName);
        int result = mapper.updUserPic(p);

        if(p.getPic() == null){
            return null;
        }

        // 5. 원하는 위치에 저장할 파일명으로 파일을 이동(transferTo)한다.
        long userId = p.getSignedUserId();
        String filePath = String.format("user/%d/%s", userId, savedPicName);

        try {
            myFileUtils.transferTo(p.getPic(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savedPicName;
    }

}
