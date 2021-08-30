package br.com.jvsf.banco.banco.config;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.classmate.TypeResolver;

import br.com.jvsf.banco.banco.dto.ContaDTO;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Autowired
    private TypeResolver typeResolver;

    private List<ResponseMessage> responseMessageForGET()
    {
        return new ArrayList<>() {{
        }};
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.jvsf.banco.banco.controller"))
                .paths(PathSelectors.any())
                .build().useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, responseMessageForGET())
                .directModelSubstitute(Pageable.class, SwaggerPageable.class)
                .ignoredParameterTypes()
                .alternateTypeRules(
                		newRule(typeResolver.resolve(Page.class,
                				typeResolver.resolve(ResponseEntity.class, ContaDTO.class)),
                				typeResolver.resolve(ContaDTO.class)))
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Api de Banco")
                .version("1.0.0")
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                        .contact(new Contact("Valdomiro Filho", "https://github.com/jvaldomirofilho", "jvaldomirofilho@gmail.com"))
                        .build();
    }

}