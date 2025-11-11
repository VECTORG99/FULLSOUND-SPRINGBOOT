package Fullsound.Fullsound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class FullsoundApplication {

	public static void main(String[] args) {
		SpringApplication.run(FullsoundApplication.class, args);
		System.out.println("\n========================================");
		System.out.println("FullSound Frontend Server iniciado");
		System.out.println("Accede a: http://localhost:8080");
		System.out.println("Health Check: http://localhost:8080/actuator/health");
		System.out.println("========================================\n");
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
