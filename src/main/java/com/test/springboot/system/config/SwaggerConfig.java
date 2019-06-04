package com.test.springboot.system.config;

import com.github.xiaoymin.swaggerbootstrapui.filter.SecurityBasicAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
public class SwaggerConfig {

    @Value("${swagger.title}")
    private String title;

    @Value("${swagger.description}")
    private String description;

    @Value("${swagger.scanPackage}")
    private String scanPackage;

    @Value("${swagger.basic.enable}")
    private String enableBasicAuth;

    @Value("${swagger.basic.userName}")
    private String userName;

    @Value("${swagger.basic.password}")
    private String password;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage(scanPackage))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version("1.0")
                .build();
    }

    @Bean
    public FilterRegistrationBean securityBasicAuthFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new SecurityBasicAuthFilter());
        bean.addInitParameter("enableBasicAuth",enableBasicAuth);
        bean.addInitParameter("userName",userName);
        bean.addInitParameter("password",password);
        bean.addUrlPatterns("/*");
        return bean;
    }
}
