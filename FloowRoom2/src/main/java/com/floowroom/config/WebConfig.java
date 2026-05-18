package com.floowroom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configura o servidor de recursos estáticos e redireciona
 * a raiz "/" para o index.html do frontend SPA.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redireciona raiz para o frontend
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/css/**", "/js/**", "/images/**", "/favicon.ico")
            .addResourceLocations("classpath:/static/css/",
                                  "classpath:/static/js/",
                                  "classpath:/static/images/",
                                  "classpath:/static/");
    }
}
