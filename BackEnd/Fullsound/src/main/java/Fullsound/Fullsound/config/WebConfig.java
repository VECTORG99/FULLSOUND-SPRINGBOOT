package Fullsound.Fullsound.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Configuración de recursos estáticos (SPA fallback).
 *
 * Nota: La configuración CORS se maneja centralizadamente en SecurityConfig
 * (corsConfigurationSource) para evitar duplicaciones y conflictos.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(true)
            .addResolver(new PathResourceResolver() {
                @Override
                protected Resource getResource(String resourcePath, Resource location) throws IOException {
                    Resource requestedResource = location.createRelative(resourcePath);

                    if (requestedResource.exists() && requestedResource.isReadable()) {
                        return requestedResource;
                    }

                    if (!resourcePath.startsWith("api/")) {
                        return new ClassPathResource("/static/index.html");
                    }

                    return null;
                }
            });
    }
}
