package tn.esprit.autoloc.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI autolocOpenApi() {
        return new OpenAPI().info(new Info().title("AutoLoc API")
                .description("API REST de gestion de location de vehicules")
                .version("1.0.0"));
    }

    @Bean
    public OpenApiCustomizer openApiVersion() {
        return openApi -> openApi.setOpenapi("3.1.1");
    }
}
