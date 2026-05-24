package com.fjconde.taskmanager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura la documentación OpenAPI (Swagger).
 * Define los metadatos de la API y el esquema de autenticación Bearer.
 *
 * Accesible en: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("API REST para gestión de tareas con autenticación JWT. " +
                                "Regístrate, inicia sesión y obtén un token para usar los endpoints de tareas.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Francisco Javier Conde")
                                .url("https://github.com/JaviConde97")))
                // Define el esquema de seguridad Bearer para que Swagger permita enviar el token
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Introduce el token JWT obtenido en /api/auth/login")));
    }
}
