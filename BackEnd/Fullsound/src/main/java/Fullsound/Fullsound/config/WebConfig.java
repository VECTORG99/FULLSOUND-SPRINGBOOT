package Fullsound.Fullsound.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final String FORWARD_INDEX = "forward:/index.html";
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
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
