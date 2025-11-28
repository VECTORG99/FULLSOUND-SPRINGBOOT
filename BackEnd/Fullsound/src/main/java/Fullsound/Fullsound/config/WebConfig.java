package Fullsound.Fullsound.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web para manejar:
 * - Enrutamiento SPA (Single Page Application)
 * - CORS para APIs externas
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String FORWARD_INDEX = "forward:/index.html";

    /**
     * Configura CORS para permitir peticiones desde cualquier origen
     * Necesario para desarrollo y para integración con APIs en AWS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    /**
     * Configura el enrutamiento para React Router
     * Redirige todas las rutas no-API a index.html
     * Esto permite que React Router maneje las rutas del frontend
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirigir rutas del frontend a index.html
        registry.addViewController("/").setViewName(FORWARD_INDEX);
        registry.addViewController("/beats").setViewName(FORWARD_INDEX);
        registry.addViewController("/carrito").setViewName(FORWARD_INDEX);
        registry.addViewController("/admin").setViewName(FORWARD_INDEX);
        registry.addViewController("/login").setViewName(FORWARD_INDEX);
        registry.addViewController("/registro").setViewName(FORWARD_INDEX);
        registry.addViewController("/terminos").setViewName(FORWARD_INDEX);
        registry.addViewController("/creditos").setViewName(FORWARD_INDEX);
        registry.addViewController("/main").setViewName(FORWARD_INDEX);
        registry.addViewController("/producto/{id}").setViewName(FORWARD_INDEX);
    }
}

