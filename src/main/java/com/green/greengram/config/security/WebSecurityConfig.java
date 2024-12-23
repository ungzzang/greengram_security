package com.green.greengram.config.security;
//Spring security 세팅

import com.green.greengram.config.jwt.JwtAuthenticationEntryPoint;
import com.green.greengram.config.jwt.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration //빈등록도 함, 메소드 빈등록이 있어야 의미가 있다.(없으면 그냥 빈등록), 메소드 빈등록이 싱글톤이 됨.
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

   /* //스프링 시큐리티 기능 비활성화 (스프링 시큐리티가 관여하지 않았으면 하는 부분)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring() //인터페이스를 객체화하는것처럼 보이는거(익명클래스), 익명클래스 만드는거 줄인게 람다식, 인터페이스에 메서드 하나만 있어야함
                        .requestMatchers(new AntPathRequestMatcher("/static/**")); //static이하는 시큐리티 관여안하겠다.
    }*/ //작동안되서 껐음.


    @Bean //스프링이 메소드 호출을 하고 리턴할 객체의 주소값을 관리한다. (빈등록)
    // http는 빈등록 되어있을꺼다.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {//HttpSecurity http 주소 넣어줘야 스프링이 동작함
        return http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //시큐리티가 세션을 사용하지 않는다.
                .httpBasic(h -> h.disable()) //SSR(Server Side Rendering)이 아니다. 화면을 만들지 않을꺼기 때문에 비활성화 시킨다. 시큐리티 로그인창 나타나지 않을 것이다.
                .formLogin(form -> form.disable()) //SSR(Server Side Rendering)이 아니다. 폼로그인 기능 자체를 비활성화
                .csrf(csrf -> csrf.disable()) //SSR(Server Side Rendering)이 아니다. 보안관련 SSR 이 아니면 보안이슈가 없기 때문에 기능을 끈다.
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/api/feed", "/api/feed/**").authenticated() //로그인이 되어 있어야만 사용 가능
                                .requestMatchers(HttpMethod.GET,"/api/user").authenticated()
                                .requestMatchers(HttpMethod.PATCH,"/api/user/pic").authenticated()
                                .anyRequest().permitAll() //나머지 요청은 모두 허용
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint) )//jwtAuthenticationEntryPoint 너가 담당자야.
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() { //DI받으려고
        return new BCryptPasswordEncoder(); //BCryptPasswordEncoder()얘는 상속관계 (PasswordEncoder 얘랑)
    }
}
