package com.floowroom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI floowRoomOpenAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
            .info(new Info()
                .title("FloowRoom API")
                .version("1.0.0")
                .description("""
                    **Sistema de Controle de Disponibilidade e Agendamento de Salas**
                    
                    Funcionalidades:
                    - Cadastro de usuários e autenticação JWT
                    - Cadastro de salas (tbSalas)
                    - Cadastro de pessoas/locatários (tbPessoas)
                    - Agendamento com verificação de conflito de horário (tbAgendaSala)
                    - Tipos de evento e tipos de pessoa
                    """)
                .contact(new Contact().name("FloowRoom").email("contato@floowroom.com.br")))
            .addSecurityItem(new SecurityRequirement().addList(schemeName))
            .components(new Components()
                .addSecuritySchemes(schemeName, new SecurityScheme()
                    .name(schemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
