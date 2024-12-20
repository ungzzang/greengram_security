package com.green.greengram.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

//@Bean 이거 있어야 @Configuration 얘가 의미있음
//@Bean이랑 @Configuration 이거 같이 있으면 싱글톤으로 쓸수있다.
@Configuration //빈등록 (@Componeent 써도됨 상속이라서.)
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final String uploadPath; //final 붙은 멤버필드는 = "ㅁㅁ" 이렇게 하거나 생성자로 값넣기 가능

    //final 쓰고 싶어서 밑에 방식으로 넣음. 생성자로(final은 무조건)
    public WebMvcConfiguration(@Value("${file.directory}")String uploadPath) {
        this.uploadPath = uploadPath;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/pic/**")
                .addResourceLocations("file:" + uploadPath + "/");

        //새로고침시 화면이 나타날 수 있도록 세팅
        //여기 코드는 잘 안바껴서 그대로 쓰면된다.
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/**") //여기 폴더명은 바뀔수 있다.
                .resourceChain(true)
                .addResolver(new PathResourceResolver(){
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource resource = location.createRelative(resourcePath);

                        if(resource.exists() && resource.isReadable()) {
                            return resource;
                        }

                        return new ClassPathResource("/static/index.html");
                    }

                });



    }//스프링이 얘네 알아서 호출함.

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer){
        // RestController의 모든 URL에 "/api" prefix를 생성, api가 앞에 붙게된다. ex) localhost:8080/api/feed?page=10&size=20
        configurer.addPathPrefix("api", HandlerTypePredicate.forAnnotation(RestController.class));
        //컨트롤러에서만 api붙인다. 다른곳은 안붙여도됨
    }

}
