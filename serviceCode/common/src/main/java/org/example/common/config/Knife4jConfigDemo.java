package org.example.common.config;

import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * 仅供参考,请根据实际情况修改
 * 请复制该文件到需要生成接口文档的模块上,进行修改, 然后启用@Configuration, @EnableSwagger2WebMvc
 */
//@Configuration
//@EnableSwagger2WebMvc
public class Knife4jConfigDemo {
    private final OpenApiExtensionResolver openApiExtensionResolver;

    @Autowired
    public Knife4jConfigDemo(OpenApiExtensionResolver openApiExtensionResolver) {
        this.openApiExtensionResolver = openApiExtensionResolver;
    }


    @Bean(value = "appApiDemo")
    public Docket appApiDemo() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("xxx API")
                        .description("# swagger UI  APIs;")
                        .termsOfServiceUrl("http://www.xx.com/")
                        .version("1.0")
                        .build())

                .groupName("app接口")
                .select()
                //Controller扫描路径
                .apis(RequestHandlerSelectors.basePackage("org.example.benshanai.app.app.controller"))
                .paths(PathSelectors.any())
                .build()
                .extensions(openApiExtensionResolver.buildExtensions("app接口"));

        return docket;
    }


}