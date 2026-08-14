package Fullsound.Fullsound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@SpringBootApplication
public class FullsoundApplication {
	private static final Logger log = LoggerFactory.getLogger(FullsoundApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(FullsoundApplication.class, args);
		log.info("========================================");
		log.info("FullSound Frontend Server iniciado");
		log.info("Accede a: http://localhost:8080");
		log.info("Health Check: http://localhost:8080/actuator/health");
		log.info("========================================");
	}
	@Bean
	public WebMvcConfigurer resourceConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/**")
						.addResourceLocations("classpath:/static/")
						.setCachePeriod(0);
			}
		};
	}
}
