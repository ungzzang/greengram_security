package com.green.greengram.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
//tokenProvider - 여기에 알아서 주소값들어갈꺼다.
//OncePerRequestFilter - 있어야 필터에 끼울수 있다, 요청당 한번만 실행됨.
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";


    @Override
    //FilterChain filterChain - 다음필터에 넘길게
    //request - 요청한 자의 모든 정보가 담겨있다.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("ip Address: {}", request.getRemoteAddr());
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION); //authorizationHeader 여기에 "Bearer 토큰값" 들어가 있을꺼다.
        log.info("authorizationHeader: {}", authorizationHeader);

        String token = getAccessToken(authorizationHeader); //토큰얻어온다.("Bearer"빼고 "토큰값" 만 가져옴)
        log.info("token: {}", token);

        if(tokenProvider.validToken(token)){//토큰이 정상이라면
            Authentication auth = tokenProvider.getAuthentication(token); //set하면 어디서 get할꺼다.
            SecurityContextHolder.getContext().setAuthentication(auth); //지금들어온 요청 로그인으로 인정하는 과정
            // 그 후 다음 필터로 전달
        }
        filterChain.doFilter(request, response); //다음필터에 request, response 넘겨준다.(모든 필터가 같은 요청, 응답을 갖게 된다.)
    }

    public String getAccessToken(String authorizationHeader) {
        //authorizationHeader.startsWith(TOKEN_PREFIX) - authorizationHeader문자열이 (TOKEN_PREFIX)이걸로 시작하냐
        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring((TOKEN_PREFIX.length()));//TOKEN_PREFIX.length() = 7
        }
        return null;
    }


}
