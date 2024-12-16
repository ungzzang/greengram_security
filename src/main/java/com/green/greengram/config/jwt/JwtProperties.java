package com.green.greengram.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("jwt") //yaml에 있는 jwt
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
