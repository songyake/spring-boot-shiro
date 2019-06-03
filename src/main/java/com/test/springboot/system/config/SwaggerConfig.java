package com.test.springboot.system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author songyake
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(name = "swagger.show", havingValue = "true")
public class SwaggerConfig {

    private Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

    @Value("${swagger.show}")
    private String show;

    @Value("${swagger.title}")
    private String title;

    @Value("${swagger.description}")
    private String description;

    @Value("${swagger.scanPackage}")
    private String scanPackage;

    @Bean
    public Docket createRestApi() {
        boolean isShow = isShow();
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(isShow)
                .select()
                .apis(RequestHandlerSelectors.basePackage(scanPackage))
                .paths(isShow ? PathSelectors.any() : PathSelectors.none())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version("1.0")
                .build();
    }

    private boolean isShow() {
        boolean isShow = false;
        try {
            isShow = Boolean.valueOf(show);
        } catch (Exception e) {
            logger.error("[System Config]-[Swagger2 Config]", e);
        }
        return isShow;
    }
}
