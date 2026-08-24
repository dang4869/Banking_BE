package com.me.base.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration.
 * Provides API documentation and interactive UI for testing endpoints.
 * <p>
 * Access Swagger UI at: <a href="http://localhost:8000/swagger-ui.html">...</a>
 * Access API docs at: <a href="http://localhost:8000/v3/api-docs">...</a>
 *
 * @author Base Project
 * @version 1.0
 */
@Configuration
public class SwaggerConfig {
    
    /**
     * Configures OpenAPI documentation.
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Base Spring Boot API")
                        .description("REST API documentation for Base Spring Boot project with JWT Authentication and Internationalization (i18n)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Base Project Team")
                                .email("support@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token from /api/auth/login or /api/auth/register")));
    }
    
    /**
     * Configures global parameters for all API operations.
     * Adds Accept-Language header parameter for i18n support.
     * 
     * @return GroupedOpenApi with global parameters
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-apis")
                .pathsToMatch("/api/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    // Add Accept-Language header parameter
                    Parameter languageParam = new Parameter()
                            .in("header")
                            .name("Accept-Language")
                            .description("Language preference (en for English, vi for Vietnamese)")
                            .required(false)
                            .schema(new StringSchema()
                                    ._enum(java.util.Arrays.asList("en", "vi"))
                                    ._default("en"));
                    
                    operation.addParametersItem(languageParam);
                    return operation;
                })
                .build();
    }
}
