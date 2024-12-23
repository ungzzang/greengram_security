package com.green.greengram.config.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

//시큐리티에 등록 필요
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final HandlerExceptionResolver resolver;

    //@Qualifier - 스프링 컨테이너로 DI를 받을 때 여러빈 중 하나를 선택할 수 있음, ID값을 적으면 됨.
    public JwtAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request
                       , HttpServletResponse response
                       , AuthenticationException authException) throws IOException, ServletException {
        //GlobalExceptionHandler에서 exception을 잡을 수 있도록 연결하는 작업.
        resolver.resolveException(request, response, null, (Exception)request.getAttribute("exception"));
    }


}
