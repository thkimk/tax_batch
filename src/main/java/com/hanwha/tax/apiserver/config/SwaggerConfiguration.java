package com.hanwha.tax.apiserver.config;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.data.domain.Pageable;

import java.util.*;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration{

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(swaggerInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.hanwha.tax.apiserver.controller"))
                // basePackage : controller 하단의 Controller 내용을 읽어
                // mapping된 resource들을 문서화 시킴
                .paths(PathSelectors.any())
                .build()
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .directModelSubstitute(Pageable.class, SwaggerPageable.class)
                .useDefaultResponseMessages(false); // 기본으로 세팅되는 200,401,403,404 메시지 표시 x
    }

    private ApiInfo swaggerInfo() {
        return new ApiInfoBuilder().title("Spring API Documentation")
                .description("앱 개발시 사용되는 서버 API에 대한 연동 문서입니다.")
                .version("1")
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "jwt", "header");
    }

    private SecurityContext securityContext() {
        return springfox
                .documentation
                .spi.service
                .contexts
                .SecurityContext
                .builder()
                .securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }

    @Getter
    private static class SwaggerPageable {

        @ApiParam(value = "페이지 번호(0..N)", example = "0")
        @Nullable
        private Integer size;

        @ApiParam(value = "페이지 크기", example = "0")
        @Nullable
        private Integer page;

        @ApiParam(value = "정렬(사용법: 컬럼명,ASC|DESC)")
        @Nullable
        private String sort;

    }


}
