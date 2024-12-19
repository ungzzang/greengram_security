package com.green.greengram.common;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

// @OpenAPIDefinition 스웨거 바꾸는거
@OpenAPIDefinition(
        info = @Info(
                title = "GreenGram",
                description = "그린그램 SNS",
                version = "v3"
        )
        , security = @SecurityRequirement(name = "Authorization")
)

@SecurityScheme(
        type = SecuritySchemeType.HTTP
        , name = "Authorization"
        , in = SecuritySchemeIn.HEADER
        , bearerFormat = "JWT"
        , scheme = "Bearer"
)
public class SwaggerConfiguration {
}

